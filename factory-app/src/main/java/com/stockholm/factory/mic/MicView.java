package com.stockholm.factory.mic;


import com.stockholm.common.view.MvpView;

public interface MicView extends MvpView {

    void onToastFailMessage(String msg);
    void onUpdateMessage(String msg);
    void onCountdown(long millisUntilFinished);
    void onCountdownFinish();
}
