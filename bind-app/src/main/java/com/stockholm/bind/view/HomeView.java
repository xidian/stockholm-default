package com.stockholm.bind.view;

import com.stockholm.common.view.MvpView;

public interface HomeView extends MvpView {

    int VIEW_BIND_START = 1;
    int VIEW_PAIR_SUCCESS = 2;
    int VIEW_CONNECT_NETWORK = 3;
    int VIEW_BIND_SUCCESS = 4;
    int VIEW_BIND_FAIL = -1;

    void onUpdateView(int state);

//    void onPairedSuccess();

//    void onConnectNetwork();

//    void onBindSuccess();

    void onBindFail(boolean restart);

    void onReportWifiSuccess();

    void showMsg(String msg);
}
