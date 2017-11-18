package com.stockholm.factory.speaker;


import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.stockholm.common.view.ReleaseBaseActivity;
import com.stockholm.factory.FactoryApplication;
import com.stockholm.factory.LogUtils;
import com.stockholm.factory.PathUtils;
import com.stockholm.factory.R;
import com.stockholm.factory.di.ApplicationComponent;
import com.stockholm.factory.di.DaggerActivityComponent;

import javax.inject.Inject;

import butterknife.BindView;

public class SpeakerActivity extends ReleaseBaseActivity implements SpeakerView {

    @BindView(R.id.tv_main_text)
    TextView tvMain;
    @BindView(R.id.tv_play_state)
    TextView tvPlayState;
    @BindView(R.id.tv_volume_state)
    TextView tvVolumeState;
    @BindView(R.id.tv_countdown)
    TextView tvCountdown;

    @Inject
    SpeakerPresenter presenter;
    @Inject
    LogUtils logUtils;
    @Inject
    PathUtils pathUtils;

    private boolean fail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.attachView(this);
    }

    @Override
    protected void initInject() {
        ApplicationComponent component = ((FactoryApplication) getApplication()).getApplicationComponent();
        DaggerActivityComponent.builder().applicationComponent(component).build().inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_speaker;
    }

    @Override
    protected void init() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.release();
    }

    @Override
    public void onPlayStateChange(int state) {
        if (state == STATE_PLAY) {
            tvPlayState.setText(R.string.speaker_playing);
        } else if (state == STATE_STOP) {
            tvPlayState.setText(R.string.speaker_stop);
        }
        YoYo.with(Techniques.ZoomIn).duration(300).playOn(tvPlayState);
    }

    @Override
    public void onVolumeChange(boolean left, boolean right) {
        if (left && right) {
            tvVolumeState.setText(R.string.speaker_both_volume);
        } else if (left) {
            tvVolumeState.setText(R.string.speaker_left_volume);
        } else if (right) {
            tvVolumeState.setText(R.string.speaker_right_volume);
        }
        YoYo.with(Techniques.ZoomIn).duration(300).playOn(tvVolumeState);
    }

    @Override
    public void onCountdown(long millisUntilFinished) {
        int display = (int) (millisUntilFinished / 1000);
        tvCountdown.setText(String.valueOf(display));
    }

    @Override
    public void onCountdownFinish() {
        logUtils.write(LogUtils.SOUND, LogUtils.FAIL);
        fail = true;
        tvCountdown.setTextColor(ContextCompat.getColor(this, R.color.red));
        tvCountdown.setText(R.string.fail);
    }

    @Override
    public void onControlUpClick() {
        super.onControlUpClick();
        presenter.writeLog("speaker | control up click", LogUtils.CLICK);
        if (fail) return;
        presenter.toggle();
        presenter.setVolume(true, false);
    }

    @Override
    public void onControlDownClick() {
        super.onControlDownClick();
        presenter.writeLog("speaker | control down click", LogUtils.CLICK);
        if (fail) return;
        presenter.toggle();
        presenter.setVolume(false, true);
    }

    @Override
    public void onControlOkLongClick() {
        super.onControlOkLongClick();
        presenter.writeLog("speaker | control ok long click", LogUtils.CLICK);
        if (fail) return;
        presenter.toggle();
        if (presenter.getTest() == SpeakerPresenter.TEST_LEFT) {
            tvMain.setText(R.string.speaker_right_test);
        } else if (presenter.getTest() == SpeakerPresenter.TEST_RIGHT) {
            //write pass
            presenter.writeLog(LogUtils.SOUND, LogUtils.PASS);
            pathUtils.goNext(this);
        }
    }

    @Override
    public void onLineShortDrag() {
        if (fail) return;
        presenter.writeLog(LogUtils.SOUND, LogUtils.FAIL);
        tvCountdown.setTextColor(ContextCompat.getColor(this, R.color.red));
        tvCountdown.setText(R.string.fail);
        fail = true;
//        pathUtils.goOver(this);
    }

}
