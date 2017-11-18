package com.stockholm.factory.speaker;


import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;

import com.stockholm.common.view.BasePresenter;
import com.stockholm.factory.LogUtils;


import javax.inject.Inject;

public class SpeakerPresenter extends BasePresenter<SpeakerView> {

    public static final int TEST_LEFT = 1;
    public static final int TEST_RIGHT = 2;

    private Context context;
    private LogUtils logUtils;

    private MediaPlayer mediaPlayer;
    private boolean playing = false;
    private int test;

    private CountDownTimer timer;

    private AudioManager audioManager;
    private int volumeOrigin;

    @Inject
    public SpeakerPresenter(Context context,
                            LogUtils logUtils) {
        this.context = context;
        this.logUtils = logUtils;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        volumeOrigin = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(mp -> {
            playing = false;
            getMvpView().onPlayStateChange(SpeakerView.STATE_STOP);
        });
    }

    private void turnVolumeUp() {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10, AudioManager.ADJUST_SAME);
    }

    private void resetVolume() {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeOrigin, AudioManager.ADJUST_SAME);
    }

    private void play() {
        turnVolumeUp();
        try {
            mediaPlayer.reset();
            AssetFileDescriptor descriptor = context.getAssets().openFd("speaker_test.mp3");
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
            playing = true;
            logUtils.write(LogUtils.SOUND, LogUtils.TESTED);
            getMvpView().onPlayStateChange(SpeakerView.STATE_PLAY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setVolume(boolean left, boolean right) {
        if (left)  test = TEST_LEFT;
        else if (right) test = TEST_RIGHT;
        mediaPlayer.setVolume(left ? 1 : 0, right ? 1 : 0);
        getMvpView().onVolumeChange(left, right);

        if (timer != null) timer.cancel();
        timer = new CountDownTimer(11000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                getMvpView().onCountdown(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                resetVolume();
                getMvpView().onCountdownFinish();
            }
        };
        timer.start();
    }

    private void stop() {
        try {
            playing = false;
            mediaPlayer.stop();
            getMvpView().onPlayStateChange(SpeakerView.STATE_STOP);
            resetVolume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toggle() {
        if (timer != null) timer.cancel();
        if (playing) {
            stop();
        } else {
            play();
        }
    }

    public int getTest() {
        return test;
    }

    public void release() {
        try {
            resetVolume();
            if (mediaPlayer != null) {
                stop();
                mediaPlayer.release();
            }
            if (timer != null) {
                timer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void writeLog(String testItem, String result) {
        logUtils.write(testItem, result);
    }

}
