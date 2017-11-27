package com.stockholm.fota.engine;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.adups.iot_libs.utils.SPFTool;
import com.adups.trace.Trace;
import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.fota.ota.FotaManager;

import javax.inject.Inject;

public class AlarmService extends Service {

    public static final String CHECK_CYCLE = "checkCycle";
    public static final String ACTION_CHECK_VERSION = "action_check_version";
    public static final String ALARM_CHECK_VERSION_CYCLE = "alarm_check_version_cycle";
    public static final int DEFAULT_ALARM_CHECK_VERSION_CYCLE = 15;
    private static final String TAG = "AlarmService";

    @Inject
    FotaManager fotaManager;

    private CheckVersionReceiver checkVersionReceiver;

    public static void startAlarmService(Context context) {
        long cycle = SPFTool.getLong(AlarmService.ALARM_CHECK_VERSION_CYCLE, AlarmService.DEFAULT_ALARM_CHECK_VERSION_CYCLE);
        StockholmLogger.d("AlarmService", "startCycleCheck: " + cycle);
        Intent intent = new Intent();
        intent.putExtra(AlarmService.CHECK_CYCLE, cycle);
        intent.setClass(context, AlarmService.class);
        context.startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {
            Long cycle = intent.getLongExtra(CHECK_CYCLE, -1);
            StockholmLogger.d(TAG, "onStartCommand() cycle:" + cycle);
            startAlarm(cycle * 60 * 1000);
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        checkVersionReceiver = new CheckVersionReceiver();
        AlarmService.this.registerReceiver(checkVersionReceiver, new IntentFilter(ACTION_CHECK_VERSION));
        Trace.d(TAG, "--onCreate--");
    }

    @Override
    public void onDestroy() {
        AlarmService.this.unregisterReceiver(checkVersionReceiver);
        Trace.d(TAG, "--onDestroy--");
        super.onDestroy();
    }

    private void startAlarm(long cycle) {
        AlarmManager.getInstance(AlarmService.this).setRepeatingAlarm(cycle, ACTION_CHECK_VERSION);
    }

    public class CheckVersionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_CHECK_VERSION:
                    StockholmLogger.d(TAG, "Time up! Check Fota");
                    fotaManager.checkUpdate();
                    fotaManager.reconnectOta();
                    break;
                default:
                    break;
            }
        }
    }

}