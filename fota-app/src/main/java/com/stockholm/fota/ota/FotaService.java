package com.stockholm.fota.ota;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.adups.iot_libs.constant.BroadcastConsts;
import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.fota.FotaApplication;
import com.stockholm.fota.di.component.ApplicationComponent;
import com.stockholm.fota.di.component.DaggerServiceComponent;

import javax.inject.Inject;

public class FotaService extends Service {

    private static final String TAG = "FotaService";

    @Inject
    FotaPresenter fotaPresenter;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY_COMPATIBILITY;
        }
        StockholmLogger.d(TAG, "FotaService onStartCommand");
        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationComponent component = ((FotaApplication) getApplication()).getApplicationComponent();
        DaggerServiceComponent.builder().applicationComponent(component).build().inject(this);
        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        StockholmLogger.d(TAG, "FotaService onDestory");
        fotaPresenter.destroy();
    }

    private void init() {
        initFotaReceiver();
        fotaPresenter.init();
    }

    private void initFotaReceiver() {
        OTABroadcastReceiver otaBroadcastReceiver = new OTABroadcastReceiver();
        LocalBroadcastManager.getInstance(this.getApplicationContext())
                .registerReceiver(otaBroadcastReceiver, new IntentFilter(BroadcastConsts.ACTION_FOTA_NOTIFY));
    }


    public class OTABroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BroadcastConsts.ACTION_FOTA_NOTIFY:
                    String body = intent.getStringExtra(BroadcastConsts.KEY_FOTA_NOTIFY);
                    StockholmLogger.d(TAG, "onReceive() notify:" + body);
                    startService(new Intent(FotaService.this, UpdateService.class));
                    break;
                default:
                    break;
            }
        }
    }

}
