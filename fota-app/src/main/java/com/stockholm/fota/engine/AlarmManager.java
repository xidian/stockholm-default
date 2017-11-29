package com.stockholm.fota.engine;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.stockholm.common.utils.StockholmLogger;

import java.text.SimpleDateFormat;
import java.util.Locale;

public final class AlarmManager {

    public static final String TAG = "AlarmManager:";
    private static AlarmManager info = null;

    public PendingIntent operation;
    private Context mContext;

    private AlarmManager(Context ctx) {
        mContext = ctx;
    }

    public static AlarmManager getInstance(Context context) {
        if (info == null) {
            info = new AlarmManager(context);
        }
        return info;
    }

    //repeating alarm
    public android.app.AlarmManager setRepeatingAlarm(long repeatTime, String action) {
        operation = PendingIntent.getBroadcast(mContext, 0, new Intent(action), 0);
        android.app.AlarmManager am = (android.app.AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        am.cancel(operation);
        am.setExact(android.app.AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + repeatTime, operation);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        StockholmLogger.d(TAG, "serAlarm enter, current time is: " + format.format(System.currentTimeMillis()));
        StockholmLogger.d(TAG, "setRepeatingAlarm() next time is:" + format.format(System.currentTimeMillis() + repeatTime));
        return am;
    }

}
