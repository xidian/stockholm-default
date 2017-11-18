package com.stockholm.factory.line;


import android.os.CountDownTimer;

import com.stockholm.common.view.BasePresenter;
import com.stockholm.factory.LogUtils;

import javax.inject.Inject;

public class LinePresenter extends BasePresenter<LineView> {

    static final int STATE_UP_BUTTON = 0;
    static final int STATE_OK_BUTTON = 1;
    static final int STATE_DOWN_BUTTON = 2;
    static final int STATE_LINE_DRAG = 3;
    static final int STATE_OVER = 4;

    private int state = STATE_UP_BUTTON;

    private LogUtils logUtils;
    private int lineDragTimes = 0;
    private CountDownTimer timer;

    @Inject
    public LinePresenter(LogUtils logUtils) {
        this.logUtils = logUtils;
    }

    void start() {
        countdown(STATE_UP_BUTTON, 6000);
    }

    private void countdown(int state, int seconds) {
        if (timer != null) timer.cancel();
        timer = new CountDownTimer(seconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                getMvpView().onCountdown(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                getMvpView().onTestFail(state);
            }
        };
        timer.start();
        getMvpView().onCountdown(seconds);
    }

    public void onControlUpClick() {
        logUtils.write("line | control up click", LogUtils.CLICK);
        if (state == STATE_UP_BUTTON) {
            logUtils.write(LogUtils.LINE_CONTROL_UP, LogUtils.PASS);
            getMvpView().onTestSuccess(false);
            state++;
            getMvpView().onUpdateView(state);
            countdown(state, 6000);
        }
    }

    public void onControlOKClick() {
        logUtils.write("line | control ok click", LogUtils.CLICK);
        if (state == STATE_OK_BUTTON) {
            logUtils.write(LogUtils.LINE_CONTROL_OK, LogUtils.PASS);
            getMvpView().onTestSuccess(false);
            state++;
            getMvpView().onUpdateView(state);
            countdown(state, 6000);
        }
    }

    public void onControlDownClick() {
        logUtils.write("line | control down click", LogUtils.CLICK);
        if (state == STATE_DOWN_BUTTON) {
            logUtils.write(LogUtils.LINE_CONTROL_DOWN, LogUtils.PASS);
            getMvpView().onTestSuccess(false);
            state++;
            getMvpView().onUpdateView(state);
            countdown(state, 9000);
        }
    }

    public void onLineDrag() {
        logUtils.write("line | line drag", LogUtils.CLICK);
        if (state == STATE_LINE_DRAG) {
            lineDragTimes++;
            if (lineDragTimes == 3) {
                logUtils.write(LogUtils.LINE_CONTROL, LogUtils.PASS);
                getMvpView().onLineDrag(lineDragTimes);
                state++;
                getMvpView().onUpdateView(state);
                getMvpView().onTestSuccess(true);
            } else {
                getMvpView().onLineDrag(lineDragTimes);
            }
        }
    }

    void release() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
