package com.stockholm.display.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.stockholm.common.IntentExtraKey;
import com.stockholm.common.utils.PreferenceFactory;
import com.stockholm.display.DisplayApplication;
import com.stockholm.display.DisplayPreference;
import com.stockholm.display.di.component.ApplicationComponent;
import com.stockholm.display.di.component.DaggerReceiverComponent;

import javax.inject.Inject;


public class EnableDisplayReceiver extends BroadcastReceiver {

    @Inject
    PreferenceFactory preferenceFactory;

    @Override
    public void onReceive(Context context, Intent intent) {
        ApplicationComponent component = ((DisplayApplication) context.getApplicationContext()).getApplicationComponent();
        DaggerReceiverComponent.builder().applicationComponent(component).build().inject(this);

        if (IntentExtraKey.ACTION_ENABLE_AUTO_DISPLAY.equals(intent.getAction())) {
            preferenceFactory.create(DisplayPreference.class).enableAutoDisplay(true);
        }
    }

}