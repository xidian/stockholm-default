package com.stockholm.display.media;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.VideoView;

import com.stockholm.common.utils.PreferenceFactory;
import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.display.DisplayPreference;
import com.stockholm.display.R;

import javax.inject.Inject;
import javax.inject.Singleton;

import butterknife.ButterKnife;

@Singleton
public class DisplayHelper implements MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
    private static final String TAG = DisplayHelper.class.getSimpleName();

    private Context context;
    private DisplayPreference preference;
    private WindowManager windowManager;

    private VideoView videoView;
    private View view;

    @Inject
    public DisplayHelper(Context context,
                         PreferenceFactory preferenceFactory) {
        this.context = context;
        this.preference = preferenceFactory.create(DisplayPreference.class);
        this.windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
    }

    public void showWindow() {
        StockholmLogger.d(TAG, "showWindow: ");
        if (view == null) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            params.gravity = Gravity.CENTER;
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
            params.format = PixelFormat.RGBA_8888;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            view = View.inflate(context, R.layout.layout_display_view, null);
            videoView = ButterKnife.findById(view, R.id.video_view);
            String path = "android.resource://" + context.getPackageName() + "/" + R.raw.demo;
            videoView.setVideoURI(Uri.parse(path));
            videoView.setOnErrorListener(this);
            videoView.setOnPreparedListener(this);
            videoView.setOnCompletionListener(this);
            windowManager.addView(view, params);
        }
    }

    public void dismissWindow() {
        StockholmLogger.d(TAG, "dismissWindow: ");
        if (view != null) {
            if (videoView.isPlaying()) {
                videoView.stopPlayback();
                videoView.suspend();
            }
            try {
                windowManager.removeView(view);
            } catch (Exception e) {
                StockholmLogger.e(TAG, "dismissWindow: " + e.toString());
            }
            view = null;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        StockholmLogger.e(TAG, "onError: " + what);
        videoView.stopPlayback();
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        StockholmLogger.d(TAG, "onPrepared: ");
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.start();
        mp.setLooping(true);
    }

}
