package com.stockholm.factory.mic;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.CountDownTimer;

import com.stockholm.common.view.BasePresenter;
import com.stockholm.factory.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.inject.Inject;

public class MicPresenter extends BasePresenter<MicView> {

    boolean played = false;

    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private MediaPlayer mediaPlayer;

    private Context context;
    private CountDownTimer timer;
    private boolean recording = false;

    private AudioManager audioManager;
    private int volumeOrigin;

    @Inject
    public MicPresenter(Context context) {
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        volumeOrigin = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    private void turnVolumeUp() {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10, AudioManager.ADJUST_SAME);
    }

    private void resetVolume() {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeOrigin, AudioManager.ADJUST_SAME);
    }

    void init() {

    }

    private void initMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        audioFilePath = context.getCacheDir().getAbsolutePath() + "/test.wav";
        mediaRecorder.setOutputFile(audioFilePath);
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    void startRecord() {
        if (recording) return;
        try {
            initMediaRecorder();
            mediaRecorder.prepare();
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
        recording = true;
        getMvpView().onUpdateMessage(context.getString(R.string.mic_state_record_start));
        timer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                getMvpView().onCountdown(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                stopRecord();
            }
        };
        timer.start();
    }

    private void stopRecord() {
        try {
            recording = false;
            mediaRecorder.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        play();
        getMvpView().onUpdateMessage(context.getString(R.string.mic_state_record_stop));
        if (timer != null) timer.cancel();
        timer = new CountDownTimer(11000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                getMvpView().onCountdown(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                getMvpView().onCountdownFinish();
            }
        };
        timer.start();
    }

//    void togglePlay() {
//        if (mediaPlayer != null) {
//            if (playing) {
//                pause();
//                getMvpView().onUpdateMessage(context.getString(R.string.mic_state_pause));
//            } else {
//                play();
//                getMvpView().onUpdateMessage(context.getString(R.string.mic_state_playing));
//            }
//        }
//    }

    private void play() {
        try {
            turnVolumeUp();
            initMediaPlayer();
            File file = new File(audioFilePath);
            if (!file.exists()) {
                getMvpView().onToastFailMessage(context.getString(R.string.mic_error_no_record));
                return;
            }
            FileInputStream mFileInputStream = new FileInputStream(file);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(mFileInputStream.getFD());
            mediaPlayer.prepare();
            mediaPlayer.start();
            played = true;
            mediaPlayer.setOnCompletionListener(mPlayer -> {
                resetVolume();
                mPlayer.stop();
                mPlayer.reset();
                mPlayer.release();
                getMvpView().onUpdateMessage(context.getString(R.string.mic_state_stop));
            });
        } catch (Exception e) {
            resetVolume();
            e.printStackTrace();
        }
    }

    private void pause() {
        try {
            mediaPlayer.pause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void release() {
        try {
            resetVolume();
            if (mediaRecorder != null) {
                mediaRecorder.release();
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            if (timer != null) {
                timer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
