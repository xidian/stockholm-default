package com.stockholm.factory;


import android.app.Activity;
import android.content.Intent;

import com.stockholm.factory.line.LineActivity;
import com.stockholm.factory.mic.MicActivity;
import com.stockholm.factory.screen.ScreenActivity;
import com.stockholm.factory.sensor.SensorActivity;
import com.stockholm.factory.speaker.SpeakerActivity;
import com.stockholm.factory.uuid.UuidActivity;
import com.stockholm.factory.wifi.WifiActivity;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PathUtils {

    private Class[] path = new Class[]{
            HomeActivity.class,
            LineActivity.class,
            ScreenActivity.class,
            SensorActivity.class,
            WifiActivity.class,
            SpeakerActivity.class,
            MicActivity.class,
            UuidActivity.class,
            OverActivity.class
    };

    @Inject
    public PathUtils() {

    }

    private void goNext(Activity current, Class next) {
        Intent intent = new Intent(current, next);
        current.startActivity(intent);
    }

    private Class getNext(Activity current) {
        for (int i = 0; i < path.length; i++) {
            Class c = path[i];
            if (c.equals(current.getClass())) {
                if (i + 1 < path.length) return path[i + 1];
                else return null;
            }
        }
        return null;
    }

    public void goNext(Activity current) {
        Class next = getNext(current);
        if (next != null) {
            goNext(current, next);
            current.finish();
        }
    }

    public void goOver(Activity current) {
        goNext(current, OverActivity.class);
    }
}
