package com.stockholm.bind.view;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.stockholm.api.base.BaseResponse;
import com.stockholm.api.bind.BindService;
import com.stockholm.api.bind.DeviceBindStateBean;
import com.stockholm.api.push.CommonPushReq;
import com.stockholm.api.push.PushMessage;
import com.stockholm.api.push.PushService;
import com.stockholm.bind.BindInfo;
import com.stockholm.bind.Constant;
import com.stockholm.bind.TAG;
import com.stockholm.bind.bluetooth.BluetoothHelper;
import com.stockholm.bind.bluetooth.BluetoothMessage;
import com.stockholm.bind.ios.BluetoothHelperForIOS;
import com.stockholm.bind.utils.KeyTrigger;
import com.stockholm.bind.utils.SoundManager;
import com.stockholm.bind.wifi.WiFiHelper;
import com.stockholm.bind.wifi.WifiMessage;
import com.stockholm.common.IntentExtraKey;
import com.stockholm.common.JPushOrder;
import com.stockholm.common.utils.NetworkTestUtil;
import com.stockholm.common.utils.ProviderUtil;
import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.common.view.BasePresenter;
import com.wx.lib.Connector;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BindPresenter extends BasePresenter<HomeView> {

    private static final String ACTION_GET_SN = "com.stockholm.action.serialnumber.request";
    private static final String ACTION_RECEIVE_SN = "com.stockholm.action.serialnumber.response";
    private static final String KEY_SN = "com.stockholm.key.serialnumber";
    private static final String DEFAULT_PCB_SN = "0#0#0#";
    private Context context;
    private BindService bindService;
    private PushService pushService;
    private BluetoothHelper bluetoothHelper;
    private WiFiHelper wiFiHelper;
    private Connector connector;
    private BluetoothHelperForIOS bluetoothHelperIOS;
    private SoundManager soundManager;
    private SnReceiver snReceiver;
    private KeyTrigger keyTrigger;

    private int connectType;
    private boolean iosStartConnect = false;
    private long iosConnectTime = 0;
    private String pcbSN = DEFAULT_PCB_SN;

    @Inject
    public BindPresenter(Context context,
                         BindService bindService,
                         PushService pushService,
                         BluetoothHelper bluetoothHelper,
                         WiFiHelper wiFiHelper,
                         BluetoothHelperForIOS bluetoothHelperForIOS,
                         SoundManager soundManager,
                         KeyTrigger keyTrigger) {
        this.context = context;
        this.bindService = bindService;
        this.pushService = pushService;
        this.bluetoothHelper = bluetoothHelper;
        this.wiFiHelper = wiFiHelper;
        this.bluetoothHelperIOS = bluetoothHelperForIOS;
        this.soundManager = soundManager;
        this.keyTrigger = keyTrigger;
        this.connector = new Connector(context);
    }

    public void init() {
        soundManager.play(SoundManager.SOUND_BIND_START);
    }

    public void setKeyTrigger() {
        keyTrigger.setKeyTriggerCallBack(new KeyTriggerCallback());
    }

    public void start() {
        soundManager.play(SoundManager.SOUND_BIND_ENTER);
    }

    public void startBind() {
        new Thread(() -> {
            startApServer();
            startBluetoothServer();
            startBluetoothForIOS();
        }).start();
        registerSnReceiver(context);
        getSn(context);
    }

    private void startApServer() {
        StockholmLogger.d(TAG.AP, "start ap server.");
        wiFiHelper.createAP();
        wiFiHelper.init(new WiFiHelper.WiFiListener() {
            @Override
            public void onConnect(long sessionId) {
                StockholmLogger.d(TAG.WIFI, "onConnect:" + sessionId);
                connectType = Constant.CONNECT_TYPE_AP;
                getMvpView().onUpdateView(HomeView.VIEW_PAIR_SUCCESS);
                soundManager.play(SoundManager.SOUND_BIND_CONNECTED);
            }

            @Override
            public void onMessageReceive(String message) {
                StockholmLogger.d(TAG.AP, "onMessageReceive:" + message);
                WifiMessage msg = WifiMessage.get(message);
                if (msg != null && msg.getCommand() == WifiMessage.CMD_MOBILE_SEND) {
                    getMvpView().onUpdateView(HomeView.VIEW_CONNECT_NETWORK);
                    WifiMessage responseMsg = new WifiMessage(true, WifiMessage.CMD_DEVICE_RESPONSE, "ok");
                    wiFiHelper.getIoSession().write(responseMsg).addListener(ioFuture -> {
                        StockholmLogger.d(TAG.AP, "onMessageReceive: write success");
                        BindInfo bindInfo = BindInfo.toBindInfo(msg.getData());
                        connectNetwork(bindInfo, true);
                    });
                }
            }
        });
        wiFiHelper.start();
    }

    private void startBluetoothServer() {
        bluetoothHelper.init(new BluetoothHelper.BluetoothListener() {
            @Override
            public void onDeviceDiscovery(BluetoothDevice device) {
                StockholmLogger.d(TAG.BLE, "onDeviceDiscovery");
            }

            @Override
            public void onDeviceDiscoveryFinish(List<BluetoothDevice> devices) {
                StockholmLogger.d(TAG.BLE, "onDeviceDiscoveryFinish");
            }

            @Override
            public void onDeviceConnecting() {
                StockholmLogger.d(TAG.BLE, "onDeviceConnecting");
            }

            @Override
            public void onDeviceConnected() {
                StockholmLogger.d(TAG.BLE, "onDeviceConnected");
                connectType = Constant.CONNECT_TYPE_BLE_ANDROID;
                getMvpView().onUpdateView(HomeView.VIEW_PAIR_SUCCESS);
                soundManager.play(SoundManager.SOUND_BIND_CONNECTED);
            }

            @Override
            public void onMessageRead(String msg) {
                StockholmLogger.d(TAG.BLE, "onMessageRead:" + msg);
                BluetoothMessage message = new Gson().fromJson(msg, BluetoothMessage.class);
                if (message != null && message.getCmd() == BluetoothMessage.CMD_SEND_BIND) {
                    getMvpView().onUpdateView(HomeView.VIEW_CONNECT_NETWORK);
                    BindInfo bindInfo = BindInfo.toBindInfo(message.getContent());
                    connectNetwork(bindInfo, false);
                }
            }

            @Override
            public void onMessageWrite(String msg) {
                StockholmLogger.d(TAG.BLE, "onMessageWrite:" + msg);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startBluetoothForIOS() {
        bluetoothHelperIOS.init(new BluetoothHelperForIOS.Listener() {
            @Override
            public void onConnected() {
                StockholmLogger.d(TAG.BLE_IOS, "ios connect.");
                iosStartConnect = false;
                connectType = Constant.CONNECT_TYPE_BLE_IOS;
                getMvpView().onUpdateView(HomeView.VIEW_PAIR_SUCCESS);
                long time = System.currentTimeMillis();
                if (time - iosConnectTime > 1000) {
                    soundManager.play(SoundManager.SOUND_BIND_CONNECTED);
                }
                iosConnectTime = time;
            }

            @Override
            public void onDisconnected() {
                StockholmLogger.d(TAG.BLE_IOS, "ios disconnect.");
                if (!iosStartConnect) {
                    getMvpView().onUpdateView(HomeView.VIEW_BIND_START);
                }
            }

            @Override
            public void onMessageRead(String msg) {
                StockholmLogger.d(TAG.BLE_IOS, "message read:" + msg);
                if (!TextUtils.isEmpty(msg)) {
                    getMvpView().onUpdateView(HomeView.VIEW_CONNECT_NETWORK);
                    BindInfo bindInfo = BindInfo.toBindInfo(msg);
                    iosStartConnect = true;
                    connectNetwork(bindInfo, false);
                }
            }
        });
    }

    private void openWiFi() {
        new Thread(() -> {
            Log.d(TAG.BIND, "WiFiHelper release.");
            wiFiHelper.release();
            Log.d(TAG.BIND, "WiFiHelper open wifi");
            wiFiHelper.openWiFi();
            int retry = 5;
            while (retry > 0) {
                if (wiFiHelper.isWiFiEnable()) {
                    Log.d(TAG.BIND, "wifi has enable.");
                    break;
                } else {
                    Log.d(TAG.BIND, "check wifi enable, " + retry);
                    wiFiHelper.openWiFi();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    retry--;
                }
            }
        }).start();
    }

    private void connectNetwork(final BindInfo bindInfo, final boolean restart) {
        openWiFi();
        StockholmLogger.d(TAG.BIND, "connectNetwork name: " + bindInfo.getWifiName() + ", pwd: " + bindInfo.getWifiPassword());
        CountDownTimer countDown = new CountDownTimer(55000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG.BIND, "connect network count down:" + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                Log.d(TAG.BIND, "connect network count down finish.");
                handleFail(restart);
            }
        };
        countDown.start();
        connector.connect(bindInfo.getWifiName(), bindInfo.getWifiPassword(), new Connector.Listener() {
            @Override
            public void onNotFountHotspot() {
                Log.d(TAG.BIND, "not found hotspot." + bindInfo.getWifiName());
                handleFail(restart);
                countDown.cancel();
            }

            @Override
            public void onFoundHotspot(ScanResult scanResult) {
                Log.d(TAG.BIND, "found hotspot, " + scanResult);
            }

            @Override
            public void onFoundAdHoc(ScanResult scanResult) {
                Log.d(TAG.BIND, "found AdHoc, " + scanResult);
                handleFail(restart);
                countDown.cancel();
            }

            @Override
            public void onConnectResult(boolean result) {
                countDown.cancel();
                if (result) {
                    reqServer(bindInfo, restart, true);
                } else {
                    handleFail(restart);
                }
            }
        });
    }

    private void handleFail(boolean restart) {
        tellMobileConnectFail();
        getMvpView().onBindFail(restart);
        soundManager.play(SoundManager.SOUND_BIND_FAIL);
    }

    private void reqServer(BindInfo bindInfo, boolean restart, boolean delay) {
        StockholmLogger.d(TAG.BIND, bindInfo.toString() + "-" + restart + "-" + delay);
        if (delay) {
            int delayReqServer = 20_000;
            int delayTime = 0;
            while (delayTime <= delayReqServer) {
                boolean available = NetworkTestUtil.isNetworkAvailable(context);
                if (available) {
                    break;
                } else {
                    delayTime += 2_000;
                    try {
                        StockholmLogger.d(TAG.BIND, "delay to req bind.");
                        Thread.sleep(2_000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        boolean available = NetworkTestUtil.isNetworkAvailable(context);
        if (available) {
            if (bindInfo.isBindDevice()) {
                bind(bindInfo.getAccessToken(), restart);
            } else {
                reportWifiConnect();
            }
        } else {
            bindFail(new Exception("network is not available."), restart);
        }
    }

    private void bind(final String accessToken, final boolean restart) {
        if (TextUtils.isEmpty(pcbSN)) {
            pcbSN = DEFAULT_PCB_SN;
        }
        bindService.bind(accessToken, pcbSN)
                .timeout(15, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> bindSuccess(resp, restart), e -> bindFail(e, restart));
    }

    private void bindSuccess(Response<BaseResponse<DeviceBindStateBean>> bindResp, boolean restart) {
        StockholmLogger.d(TAG.API, "handle bind resp success.");
        if (bindResp.isSuccessful()) {
            DeviceBindStateBean bindStateBean = bindResp.body().getData();
            StockholmLogger.d(TAG.API, "bindSuccess userCount: " + bindStateBean.getUsersCount()
                    + " -- " + "showGuide " + bindStateBean.isShowGuide());
            ProviderUtil.updateShowGuide(context, bindStateBean.isShowGuide());
            if (bindStateBean.isEnableAutoDisplay()) {
                context.sendBroadcast(new Intent(IntentExtraKey.ACTION_ENABLE_AUTO_DISPLAY));
            }
            tellMobileConnectOk();
            getMvpView().onUpdateView(HomeView.VIEW_BIND_SUCCESS);
            soundManager.play(SoundManager.SOUND_BIND_OK);
        } else {
            StockholmLogger.d(TAG.API, "bind resp is fail.");
            handleFail(restart);
        }
    }

    private void bindFail(Throwable e, boolean restart) {
        StockholmLogger.e(TAG.API, "bind fail. e:" + e.getMessage());
        handleFail(restart);
    }

    // TODO: 13/07/2017 restart
    private void reportWifiConnect() {
        pushService.sendPush(new CommonPushReq(new PushMessage(JPushOrder.REPORT_WIFI_CONNECT)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    StockholmLogger.d(TAG.API, "report wifi connect success.");
                    getMvpView().onReportWifiSuccess();
                }, e -> {
                    e.printStackTrace();
                    StockholmLogger.e(TAG.API, "report wifi connect fail. e:" + e.getMessage());
                });
    }

    private void tellMobileConnectOk() {
        StockholmLogger.d(TAG.BIND, "tellMobileConnectOk. connect type:" + connectType);
        if (connectType == Constant.CONNECT_TYPE_BLE_ANDROID) {
            BluetoothMessage msg = new BluetoothMessage(BluetoothMessage.CMD_CONNECT_OK, "connect network success.");
            bluetoothHelper.sendMessage(msg.toString());
        } else if (connectType == Constant.CONNECT_TYPE_BLE_IOS) {
            bluetoothHelperIOS.sendMessage(Constant.MSG_IOS_OK);
        }
    }

    private void tellMobileConnectFail() {
        StockholmLogger.d(TAG.BIND, "tellMobileConnectFail. connect type:" + connectType);
        if (connectType == Constant.CONNECT_TYPE_BLE_ANDROID) {
            BluetoothMessage msg = new BluetoothMessage(BluetoothMessage.CMD_CONNECT_FAIL, "connect network fail.");
            bluetoothHelper.sendMessage(msg.toString());
        } else if (connectType == Constant.CONNECT_TYPE_BLE_IOS) {
            bluetoothHelperIOS.sendMessage(Constant.MSG_IOS_FAIL);
        }
    }

    public void getSn(Context context) {
        Intent intent = new Intent(ACTION_GET_SN);
        context.sendBroadcast(intent);
    }

    public void registerSnReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_RECEIVE_SN);
        snReceiver = new SnReceiver();
        context.registerReceiver(snReceiver, intentFilter);
    }

    public void unRegisterReceiver(Context context) {
        if (null != snReceiver) {
            context.unregisterReceiver(snReceiver);
        }
    }

    public void destroy() {
        try {
            bluetoothHelper.release();
            wiFiHelper.release();
            bluetoothHelperIOS.stopAdvertise();
            soundManager.release();
            unRegisterReceiver(context);
        } catch (Exception e) {
            StockholmLogger.e(TAG.BIND, "destroy error:" + e.getMessage());
        }
    }

    public void keyInput(int keycode) {
        keyTrigger.input(keycode);
    }

    class KeyTriggerCallback implements KeyTrigger.KeyTriggerCallBack {

        @Override
        public void keyEvent(int keyEvent) {
            if (keyEvent == KeyTrigger.KEY_EVENT_FACTORY) {
                context.sendBroadcast(new Intent(IntentExtraKey.ACTION_OPEN_FACTORY));
            } else if (keyEvent == KeyTrigger.KEY_EVENT_STABLE) {
                context.sendBroadcast(new Intent(IntentExtraKey.ACTION_OPEN_STABLE_TEST));
            }
        }
    }

    class SnReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            pcbSN = intent.getStringExtra(KEY_SN);
        }
    }

}