package com.stockholm.fota.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.stockholm.common.bus.RxEventBus;
import com.stockholm.fota.FotaApplication;
import com.stockholm.fota.di.component.ApplicationComponent;
import com.stockholm.fota.di.component.DaggerReceiverComponent;
import com.stockholm.fota.event.ReconnectOtaEvent;
import com.stockholm.fota.ota.FotaManager;

import javax.inject.Inject;

public class FotaReceiver extends BroadcastReceiver {

    private static final String TAG = "FotaReceiver";

    @Inject
    FotaManager fotaManager;
    @Inject
    RxEventBus eventBus;

    @Override
    public void onReceive(Context context, Intent intent) {
        ApplicationComponent component = ((FotaApplication) context.getApplicationContext()).getApplicationComponent();
        DaggerReceiverComponent.builder().applicationComponent(component).build().inject(this);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())
                || "com.stockholm.action.BIND_SUCCESS".equals(intent.getAction())) {
            fotaManager.updateRomVersion();
            eventBus.post(new ReconnectOtaEvent());
        }
    }
}