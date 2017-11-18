package com.stockholm.factory.speaker;


import com.stockholm.common.view.MvpView;

public interface SpeakerView extends MvpView {

    int STATE_PLAY = 1;
    int STATE_STOP = 2;

    void onPlayStateChange(int state);
    void onVolumeChange(boolean left, boolean right);
    void onCountdown(long millisUntilFinished);
    void onCountdownFinish();
}
