package com.stockholm.stable.view;


import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.common.utils.WeakHandler;
import com.stockholm.common.view.BasePresenter;
import com.stockholm.stable.R;
import com.stockholm.stable.utils.OSUtils;

import javax.inject.Inject;

public class HomePresenter extends BasePresenter<HomeView> {

    @Inject
    Context context;

    private int[] picIds = new int[]{R.drawable.pic_1, R.drawable.pic_2, R.drawable.pic_3, R.drawable.pic_4};
    private WeakHandler handler;
    private MediaPlayer mediaPlayer;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int id = (int) (Math.random() * 4);
            StockholmLogger.d("stable-app", "pic index=" + id);
            getMvpView().flashImage(picIds[id]);
            handler.postDelayed(runnable, 3000);
        }
    };

    @Inject
    public HomePresenter() {

    }

    public void init() {
        OSUtils.adjustScreen(context);
        OSUtils.adjustVolume(context);
        OSUtils.adjustWifiBluetooth(context);
        startPlay(context);
        flashImage();
    }

    private void startPlay(Context context) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            AssetFileDescriptor descriptor = context.getAssets().openFd("speaker_test.mp3");
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopPLay() {
        if (null != mediaPlayer) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    private void flashImage() {
        handler = new WeakHandler();
        handler.postDelayed(runnable, 3000);
    }

    public void exit() {
        OSUtils.resetScreen(context);
        OSUtils.resetVolume(context);
        stopPLay();
    }

}
