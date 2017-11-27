package com.stockholm.fota.engine;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.adups.trace.Trace;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AlarmManager {

    public static final String TAG = "AlarmManager:";
    private static AlarmManager info = null;

    public static final int INIT_TIME = 10;               // unit : min
    private Context mContext;
    public PendingIntent operation;

    public static AlarmManager getInstance(Context context) {
        if (info == null) {
            info = new AlarmManager(context);
        }
        return info;
    }

    private AlarmManager(Context ctx) {
        mContext = ctx;
    }

    //repeating alarm
    public android.app.AlarmManager setRepeatingAlarm(long repeatTime, String action) {
        operation = PendingIntent.getBroadcast(mContext, 0, new Intent(action), 0);
        android.app.AlarmManager am = (android.app.AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        am.cancel(operation);
        am.setInexactRepeating(android.app.AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + repeatTime, repeatTime, operation);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Trace.d(TAG, "serAlarm enter, current time is: " + format.format(System.currentTimeMillis()));
        Trace.d(TAG, "setRepeatingAlarm() first time is:" + format.format(System.currentTimeMillis() + repeatTime));
        return am;
    }

}
