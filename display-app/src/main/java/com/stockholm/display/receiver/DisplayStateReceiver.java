package com.stockholm.display.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.stockholm.common.IntentExtraKey;
import com.stockholm.common.statusbar.StatusAction;
import com.stockholm.common.utils.PreferenceFactory;
import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.display.DisplayApplication;
import com.stockholm.display.DisplayPreference;
import com.stockholm.display.di.component.ApplicationComponent;
import com.stockholm.display.di.component.DaggerReceiverComponent;
import com.stockholm.display.media.DisplayHelper;
import com.stockholm.display.service.AutoDisplayControlService;

import javax.inject.Inject;


public class DisplayStateReceiver extends BroadcastReceiver {

    @Inject
    DisplayHelper displayHelper;
    @Inject
    PreferenceFactory preferenceFactory;

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        ApplicationComponent component = ((DisplayApplication) context.getApplicationContext()).getApplicationComponent();
        DaggerReceiverComponent.builder().applicationComponent(component).build().inject(this);
        DisplayPreference preference = preferenceFactory.create(DisplayPreference.class);
        String action = intent.getAction();
        StockholmLogger.d("DisplayStateReceiver", "onReceive: " + action);
        if (IntentExtraKey.ACTION_DISMISS_AUTO_DISPLAY.equals(action) || StatusAction.ACTION_STATUS_SHOW_MEDIA.equals(action)) {
            if (StatusAction.ACTION_STATUS_SHOW_MEDIA.equals(action)) {
                preference.mediaPlaying(true);
            }
            displayHelper.dismissWindow();
            startCountDown();
        } else if (StatusAction.ACTION_STATUS_DISMISS_MEDIA.equals(action)) {
            preference.mediaPlaying(false);
        }
    }

    private void startCountDown() {
        Intent service = new Intent(context, AutoDisplayControlService.class);
        context.startService(service);
    }

}