package com.stockholm.factory.line;


import com.stockholm.common.view.MvpView;

public interface LineView extends MvpView {

    void onCountdown(long millisUntilFinished);
    void onTestSuccess(boolean over);
    void onTestFail(int state);
    void onLineDrag(int times);
    void onUpdateView(int state);
}
