package com.stockholm.display.push;


import android.content.Context;
import android.content.Intent;

import com.stockholm.api.display.DisplayService;
import com.stockholm.common.IntentExtraKey;
import com.stockholm.common.JPushOrder;
import com.stockholm.common.utils.PreferenceFactory;
import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.display.DisplayPreference;
import com.stockholm.display.service.AutoDisplayControlService;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PushPresenter {

    private static final String TAG = PushPresenter.class.getSimpleName();

    private Context context;
    private DisplayService displayService;
    private DisplayPreference displayPreference;

    @Inject
    public PushPresenter(Context context,
                         DisplayService displayService,
                         PreferenceFactory preferenceFactory) {
        this.context = context;
        this.displayService = displayService;
        this.displayPreference = preferenceFactory.create(DisplayPreference.class);
    }

    void handlePushMessage(int order) {
        if (order == JPushOrder.GET_APP_CONFIG) {
            displayPreference.enableAutoDisplay(true);
            getConfig();
        }
    }

    private void getConfig() {
        displayService.getDisplayConfig()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    if (resp.isSuccessful() && resp.body().isSuccess()) {
                        boolean openAutoDisplay = resp.body().getData().isOpenAutoDisplay();
                        displayPreference.openAutoDisplay(openAutoDisplay);
                        if (openAutoDisplay) {
                            startCountDown();
                        } else {
                            context.sendBroadcast(new Intent(IntentExtraKey.ACTION_DISMISS_AUTO_DISPLAY));
                        }
                    }
                }, e -> {
                    StockholmLogger.e(TAG, "getConfig: " + e.toString());
                    startCountDown();
                });
    }

    private void startCountDown() {
        Intent service = new Intent(context, AutoDisplayControlService.class);
        context.startService(service);
    }

}
