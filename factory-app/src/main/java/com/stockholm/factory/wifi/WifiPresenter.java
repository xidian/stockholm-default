package com.stockholm.factory.wifi;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;

import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.common.view.BasePresenter;
import com.stockholm.factory.LogUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

public class WifiPresenter extends BasePresenter<WifiView> {

    private static final String TAG = "WifiPresenter";
    private static final String SSID_TEST = "Meow1-test";
//    private static final String SSID_TEST = "Meow";

    private Context context;
    private LogUtils logUtils;

    private WifiManager wifiManager;
    private Lock lock;
    private Condition condition;
    private CountDownTimer timer;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            StockholmLogger.d(TAG, "scan wifi over.");
            updateList();
        }
    };

    @Inject
    public WifiPresenter(Context context,
                         LogUtils logUtils) {
        this.context = context;
        this.logUtils = logUtils;

        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    void startScan() {
        new Thread(() -> {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            context.registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifiManager.startScan();
            lock.lock();
            try {
                condition.await(10, TimeUnit.SECONDS);
                updateList();
                getResult();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.unlock();
            context.unregisterReceiver(receiver);
        }).start();
        timer = new CountDownTimer(11000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                getMvpView().onCountdown(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                StockholmLogger.d(TAG, "time finish");
            }
        };
        timer.start();
    }

    private void getResult() {
        List<ScanResult> list = wifiManager.getScanResults();
        for (ScanResult result : list) {
            if (result != null && result.SSID != null && SSID_TEST.equals(result.SSID)) {
                logUtils.write(LogUtils.WIFI, LogUtils.PASS);
                getMvpView().onTestFinish(true);
                return;
            }
        }
        logUtils.write(LogUtils.WIFI, LogUtils.FAIL);
        getMvpView().onTestFinish(false);
        StockholmLogger.d(TAG, "get result.");
    }

    private void updateList() {
        List<ScanResult> list = wifiManager.getScanResults();
        getMvpView().onUpdateList(list);
    }

    void release() {
        if (timer != null) {
            timer.cancel();
        }
    }

}
