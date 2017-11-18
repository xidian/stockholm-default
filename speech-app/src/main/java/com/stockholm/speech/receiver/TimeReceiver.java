package com.stockholm.speech.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.stockholm.speech.UploadService;

import org.joda.time.LocalTime;


public class TimeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
            int hourOfDay = LocalTime.now().getHourOfDay();
            if (hourOfDay == 2) {
                Intent serviceIntent = new Intent(context, UploadService.class);
                context.startService(serviceIntent);
            }
        }
    }

}