package com.stockholm.fota.ota;

import android.content.Context;

import com.stockholm.common.bus.RxEventBus;
import com.stockholm.fota.engine.AlarmService;
import com.stockholm.fota.engine.UpdateEngine;
import com.stockholm.fota.event.ReconnectOtaEvent;

import javax.inject.Inject;

public class FotaPresenter implements FotaManager.FotaManagerCallback, UpdateEngine.UpdateCallback {

    private static final String TAG = "FotaService";

    @Inject
    Context context;
    @Inject
    FotaManager fotaManager;
    @Inject
    RxEventBus eventBus;

    @Inject
    public FotaPresenter() {

    }

    public void init() {
        fotaManager.setFotaManagerCallback(this);
        UpdateEngine.getInstance(context).setUpdateCallback(this);
        eventBus.subscribe(ReconnectOtaEvent.class, event -> fotaManager.reconnectOta());
        fotaManager.init();
    }

    public void destroy() {
        fotaManager.destroy();
        eventBus.unsubscribe();
    }

    @Override
    public void onRegisterFail() {
        AlarmService.startAlarmService(context);
    }

    @Override
    public void onCheckUpdateFail() {
        AlarmService.startAlarmService(context);
    }
}
