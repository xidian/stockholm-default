package com.stockholm.bind.utils;


import android.content.Context;
import android.media.MediaPlayer;

import com.stockholm.bind.R;

import javax.inject.Inject;

public class SoundManager {

    public static final int SOUND_BIND_START = 1;

    public static final int SOUND_BIND_ENTER = SOUND_BIND_START + 1;
    //连接成功
    public static final int SOUND_BIND_CONNECTED = SOUND_BIND_ENTER + 1;
    //联网成功
    public static final int SOUND_BIND_OK = SOUND_BIND_CONNECTED + 1;
    //联网失败
    public static final int SOUND_BIND_FAIL = SOUND_BIND_OK + 1;


    private Context context;
    private MediaPlayer mp;

    @Inject
    public SoundManager(Context context) {
        this.context = context;
    }

    public void play(int sound) {
        release();
        switch (sound) {
            case SOUND_BIND_START:
                mp = MediaPlayer.create(context, R.raw.bind_start);
                break;
            case SOUND_BIND_ENTER:
                mp = MediaPlayer.create(context, R.raw.bind_enter);
                break;
            case SOUND_BIND_OK:
                mp = MediaPlayer.create(context, R.raw.bind_ok);
                break;
            case SOUND_BIND_FAIL:
                mp = MediaPlayer.create(context, R.raw.bind_fail);
                break;
            case SOUND_BIND_CONNECTED:
                mp = MediaPlayer.create(context, R.raw.bind_connected);
                break;
            default:
        }
        mp.start();
    }

    public void release() {
        if (mp != null) {
            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }
    }

}
