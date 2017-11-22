package com.stockholm.bind.view;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.stockholm.bind.BindApplication;
import com.stockholm.bind.R;
import com.stockholm.bind.di.component.ApplicationComponent;
import com.stockholm.bind.di.component.DaggerActivityComponent;
import com.stockholm.common.IntentExtraKey;
import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.common.utils.WeakHandler;
import com.stockholm.common.view.ReleaseBaseActivity;

import javax.inject.Inject;

import butterknife.BindView;

public class HomeActivity extends ReleaseBaseActivity implements HomeView {

    private static final String TAG = "HomeActivity";

    @BindView(R.id.iv_qrcode)
    ImageView ivQrCode;
    @BindView(R.id.layout_bind)
    ViewGroup layoutBind;
    @BindView(R.id.iv_center)
    ImageView ivCenter;
    @BindView(R.id.tv_bind_status)
    TextView tvBindStatus;
    @BindView(R.id.tv_bind_info)
    TextView tvBindInfo;

    @Inject
    BindPresenter presenter;

    private WeakHandler handler = new WeakHandler();
    private AnimationDrawable drawable;
    private int lastViewState;
    private boolean clickCombination = false;
    private boolean isQRView = true;

    @Override
    protected void initInject() {
        ApplicationComponent component = ((BindApplication) getApplication()).getApplicationComponent();
        DaggerActivityComponent.builder().applicationComponent(component).build().inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.attachView(this);
        presenter.setKeyTrigger();
        if (getIntent() != null) {
            boolean showQR = getIntent().getBooleanExtra(IntentExtraKey.KEY_SHOW_QR, true);
            StockholmLogger.d(TAG, "showQR:" + showQR);
            if (showQR && !clickCombination) {
                ivQrCode.setBackgroundResource(R.drawable.binding);
                presenter.init();
            } else {
                bindStartView();
                ensureDiscoverable();
                presenter.start();
            }
        } else {
            StockholmLogger.d(TAG, "onResume getIntent is null.");
        }
    }

    @Override
    protected void init() {

    }

    @Override
    public void onConnectWifiButtonClick() {
        Log.d(TAG, "connect wifi button click.");
        clickCombination = true;
        if (lastViewState == VIEW_PAIR_SUCCESS) {
            //已经连接成功，不需要再显示联网模式的第一个页面
            //不需要再重新启动联网模式(重新启动，连接会断开)
        } else {
            bindStartView();
            ensureDiscoverable();
            presenter.start();
        }
    }

    public void ensureDiscoverable() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivityForResult(discoverableIntent, 10000);
        } else {
            presenter.startBind();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10000) {
            presenter.startBind();
        }
    }

    @Override
    public void onLineShortDrag() {
        // must have this function. Don't remove it.
    }

    @Override
    public void onControlUpLongPress() {
        // must have this function. Don't remove it.
    }

    @Override
    public void onControlDownLongPress() {
        // must have this function. Don't remove it.
    }

    @Override
    public void onLongPressLowerVolume() {
        // must have this function. Don't remove it.
    }

    @Override
    public void onLongPressRaiseVolume() {
        // must have this function. Don't remove it.
    }

    @Override
    public void onControlOKClick() {
        super.onControlOKClick();
        if (isQRView) {
            presenter.keyInput(KeyEvent.KEYCODE_ENTER);
        }
    }

    @Override
    public void onControlUpClick() {
        super.onControlUpClick();
        if (isQRView) {
            presenter.keyInput(KeyEvent.KEYCODE_DPAD_UP);
        }
    }

    @Override
    public void onControlDownClick() {
        super.onControlDownClick();
        if (isQRView) {
            presenter.keyInput(KeyEvent.KEYCODE_DPAD_DOWN);
        }
    }

    @Override
    public void onFactoryModeButtonClick() {
        super.onFactoryModeButtonClick();
    }

    @Override
    public void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void bindStartView() {
        System.out.println("bind start view.");
        isQRView = false;
        ivQrCode.setVisibility(View.INVISIBLE);
        layoutBind.setVisibility(View.VISIBLE);
        tvBindStatus.setText(R.string.bind_mode_enter);
        ivCenter.setBackgroundResource(R.drawable.phone_remind);
        tvBindInfo.setText(R.string.info_check_phone);
    }

    @Override
    public void onUpdateView(int state) {
        System.out.println("update view state:" + state);
        StockholmLogger.d(TAG, "state:" + state + "\t last state:" + lastViewState);
        if (state == lastViewState) return;
        lastViewState = state;
        switch (state) {
            case VIEW_BIND_START:
                changeCenterImage(R.drawable.phone_remind);
                changeBindStatusText(R.string.bind_mode_enter);
                tvBindInfo.setText(R.string.info_check_phone);
                break;
            case VIEW_PAIR_SUCCESS:
                changeCenterImage(R.drawable.phone_connect);
                changeBindStatusText(R.string.bluetooth_connect);
                tvBindInfo.setText(R.string.info_check_phone);
                break;
            case VIEW_CONNECT_NETWORK:
                runOnUiThread(() -> {
                    Log.d(TAG, "case VIEW_CONNECT_NETWORK.");
                    changeCenterImage(R.drawable.wifi_1);
                    changeBindStatusText(R.string.connect_ing);
                    ivCenter.clearAnimation();
                    ivCenter.setBackgroundResource(R.drawable.anim_wifi);
                    drawable = (AnimationDrawable) ivCenter.getBackground();
                    drawable.start();
                });
                break;
            case VIEW_BIND_SUCCESS:
                if (drawable != null) drawable.stop();
                tvBindStatus.setVisibility(View.VISIBLE);
                tvBindInfo.setVisibility(View.INVISIBLE);
                changeCenterImage(R.drawable.wifi_succeed);
                changeBindStatusText(R.string.connect_finish);
                stopSelf();
                break;
            default:
        }
    }

    @Override
    public void onBindFail(boolean restart) {
        StockholmLogger.d(TAG, "onBindFail, restart:" + restart);
        lastViewState = HomeView.VIEW_BIND_FAIL;
        if (drawable != null) drawable.stop();
        tvBindStatus.setVisibility(View.VISIBLE);
        tvBindInfo.setVisibility(View.VISIBLE);
        changeCenterImage(R.drawable.defeated);
        changeBindStatusText(R.string.connect_fail);
        tvBindInfo.setText(R.string.info_check_wifi);
        if (restart) {
            handler.postDelayed(() -> {
                runOnUiThread(() -> {
                    bindStartView();
                    presenter.startBind();
                });
            }, 5000);
        }
    }

    @Override
    public void onReportWifiSuccess() {
        stopSelf();
    }

    private void stopSelf() {
        handler.postDelayed(() -> {
            Intent intent = new Intent(IntentExtraKey.ACTION_BIND_SUCCESS);
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            sendBroadcast(intent);
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
        }, 5000);
    }

    private void changeCenterImage(final int imageRes) {
        TranslateAnimation in = new TranslateAnimation(500, 0, 0, 0);
        in.setDuration(400);
        TranslateAnimation out = new TranslateAnimation(0, -500, 0, 0);
        out.setDuration(400);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivCenter.setBackgroundResource(imageRes);
                ivCenter.startAnimation(in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        ivCenter.startAnimation(out);
    }

    private void changeBindStatusText(final int textRes) {
        Log.d(TAG, "changeBindStatusText|change bind status text.");
        AlphaAnimation in = new AlphaAnimation(0, 1);
        in.setDuration(400);
        AlphaAnimation out = new AlphaAnimation(1, 0);
        out.setDuration(400);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "changeBindStatusText| onAnimationEnd.");
                tvBindStatus.setText(textRes);
                tvBindStatus.startAnimation(in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        tvBindStatus.startAnimation(out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StockholmLogger.d("Home", "onDestroy.");
        lastViewState = 0;
        presenter.destroy();
    }
}