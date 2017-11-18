package com.stockholm.display.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.stockholm.common.utils.PreferenceFactory;
import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.display.DisplayApplication;
import com.stockholm.display.DisplayPreference;
import com.stockholm.display.di.component.ApplicationComponent;
import com.stockholm.display.di.component.DaggerServiceComponent;
import com.stockholm.display.media.DisplayHelper;

import javax.inject.Inject;


public class AutoDisplayControlService extends Service {

    private static final String TAG = AutoDisplayControlService.class.getSimpleName();

    private static final int FREE_TIME_MILLIS = 60 * 1000;

    @Inject
    DisplayHelper displayHelper;
    @Inject
    PreferenceFactory preferenceFactory;

    private CountDownTimer countDownTimer;
    private DisplayPreference preference;

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationComponent component = ((DisplayApplication) getApplication()).getApplicationComponent();
        DaggerServiceComponent.builder().applicationComponent(component).build().inject(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        StockholmLogger.d(TAG, "onStartCommand: start autoDisplay service");
        if (intent == null) return START_STICKY_COMPATIBILITY;

        preference = preferenceFactory.create(DisplayPreference.class);

        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(FREE_TIME_MILLIS, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    System.out.println("count down " + millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    boolean openAutoDisplay = preference.isOpenAutoDisplay();
                    boolean hasMediaPlaying = preference.hasMediaPlaying();
                    if (openAutoDisplay && !hasMediaPlaying) {
                        displayHelper.showWindow();
                        StockholmLogger.d(TAG, "1 min after show auto display");
                    }
                }
            };
        } else countDownTimer.cancel();

        if (preference.isEnableAutoDisplay()) {
            countDownTimer.start();
        }

        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }


}