package com.stockholm.fota.ota;

import android.content.Context;

import com.adups.iot_libs.MqttAgentPolicy;
import com.adups.iot_libs.OtaAgentPolicy;
import com.adups.iot_libs.constant.Error;
import com.adups.iot_libs.info.DeviceInfo;
import com.adups.iot_libs.info.VersionInfo;
import com.adups.iot_libs.inter.ICheckVersionCallback;
import com.adups.iot_libs.inter.IRegisterListener;
import com.adups.iot_libs.inter.IStatusListener;
import com.google.gson.Gson;
import com.stockholm.api.rom.RomInfoReq;
import com.stockholm.api.rom.RomService;
import com.stockholm.common.Constant;
import com.stockholm.common.bus.RxEventBus;
import com.stockholm.common.utils.OsUtils;
import com.stockholm.common.utils.PreferenceFactory;
import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.fota.FotaPreference;
import com.stockholm.fota.RemindOtaEvent;
import com.stockholm.fota.TestOtaEvent;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FotaPresenter {

    private static final String TAG = "FotaService";

    @Inject
    RomService romService;

    @Inject
    RxEventBus eventBus;

    @Inject
    Context context;

    @Inject
    PreferenceFactory preferenceFactory;

    private IStatusListener statusListener;
    private FotaPreference fotaPreference;

    @Inject
    public FotaPresenter() {

    }

    public void init() {
        eventBus.subscribe(RemindOtaEvent.class, event -> reconnectOta());
        eventBus.subscribe(TestOtaEvent.class, event -> checkUpdate());
        fotaPreference = preferenceFactory.create(FotaPreference.class);
        initFotaStatusListener();
        updateRomVersion();
    }

    private void reconnectOta() {
        if (!OsUtils.isNetworkConnected(context)) {
            StockholmLogger.d(TAG, "remind ota service but no network");
            return;
        }
        if (!fotaPreference.getFotaRegistered()) {
            StockholmLogger.d(TAG, "ota not register,so register");
            fotaRegister();
        } else {
            checkUpdate();
        }
        if (!MqttAgentPolicy.isConnected()) {
            MqttAgentPolicy.connect();
        }
    }

    private void fotaRegister() {
        FotaPreference fotaPreference = preferenceFactory.create(FotaPreference.class);
        OtaAgentPolicy.register(new IRegisterListener() {
            @Override
            public void onSuccess() {
                StockholmLogger.d("FotaApplication", "注册设备成功");
                fotaPreference.setFotaRegistered(true);
                checkUpdate();
            }

            @Override
            public void onFailed(int i) {
                StockholmLogger.d("FotaApplication", "注册设备失败");
                fotaPreference.setFotaRegistered(false);
            }
        });
    }

    private void checkUpdate() {
        OtaAgentPolicy.checkVersion(new ICheckVersionCallback() {
            @Override
            public void onCheckSuccess(VersionInfo versionInfo) {
                StockholmLogger.d(TAG, "检测版本成功！ " + new Gson().toJson(versionInfo));
                fotaPreference.setNewVersionName(versionInfo.versionName);
            }

            @Override
            public void onCheckFail(int status) {
                StockholmLogger.d(TAG, "check version error" + status);
            }
        });
    }

    private void updateRomVersion() {
        StockholmLogger.d(TAG, "version info: " + DeviceInfo.getInstance().toString() + DeviceInfo.getInstance().version);
        String launcherVersion = OsUtils.getAppVersionName(context, Constant.APP_PACKAGE_NAME_LAUNCHER);
        RomInfoReq.RomBean romBean = new RomInfoReq.RomBean();
        romBean.setDeviceType(DeviceInfo.getInstance().deviceType);
        romBean.setLauncherVersion(launcherVersion);
        romBean.setMid(DeviceInfo.getInstance().mid);
        romBean.setModels(DeviceInfo.getInstance().models);
        romBean.setOem(DeviceInfo.getInstance().oem);
        romBean.setPlatform(DeviceInfo.getInstance().platform);
        romBean.setVersion(DeviceInfo.getInstance().version);
        romBean.setVersionName(fotaPreference.getNewVersionName());
        RomInfoReq romInfoReq = new RomInfoReq();
        romInfoReq.setRom(romBean);
        romService.updateVersion(romInfoReq)
                .retry(5)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    if (resp.isSuccessful()) {
                        StockholmLogger.d(TAG, "update version info success");
                    }
                }, e -> StockholmLogger.e(TAG, e.toString()));
    }

    private void initFotaStatusListener() {
        statusListener = new IStatusListener() {
            @Override
            public void onConnected() {
                StockholmLogger.d(TAG, "onConnected() ");
            }

            @Override
            public void onDisconnected() {
                StockholmLogger.d(TAG, "onDisconnected() ");
                reconnectOta();
            }

            @Override
            public void onAbnormalDisconnected(int i) {
                StockholmLogger.d(TAG, "onAbnormalDisconnected() error " + Error.getErrorMessage(i));
                reconnectOta();
            }

            @Override
            public void onError(int i) {
                StockholmLogger.d(TAG, "onError() error " + Error.getErrorMessage(i));
//                reconnectOta();
            }
        };
        MqttAgentPolicy.registerStatusListener(statusListener);
        if (MqttAgentPolicy.isConnected()) {
            MqttAgentPolicy.connect();
        }
    }

    public void destroy() {
        if (null != statusListener) {
            MqttAgentPolicy.unregisterStatusListener(statusListener);
        }
        eventBus.unsubscribe();
    }

}
