package com.stockholm.factory.wifi;


import android.net.wifi.ScanResult;

import com.stockholm.common.view.MvpView;

import java.util.List;


public interface WifiView extends MvpView {

    void onTestFinish(boolean pass);
    void onCountdown(long millisUntilFinished);
    void onUpdateList(List<ScanResult> list);
}
