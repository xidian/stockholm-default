package com.stockholm.display.push;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.stockholm.common.Constant;
import com.stockholm.common.JPushOrder;
import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.display.DisplayApplication;
import com.stockholm.display.di.component.ApplicationComponent;
import com.stockholm.display.di.component.DaggerServiceComponent;

import javax.inject.Inject;

public class PushMessageService extends IntentService {

    private static final String TAG = "PushMessageService";

    @Inject
    PushPresenter presenter;

    public PushMessageService() {
        super("PushMessageService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationComponent component = ((DisplayApplication) getApplication()).getApplicationComponent();
        DaggerServiceComponent.builder().applicationComponent(component).build().inject(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        StockholmLogger.d(TAG, "onHandleIntent");

        if (intent == null) return;

        int order = intent.getIntExtra(Constant.JPUSH_ORDER, JPushOrder.GET_APP_CONFIG);
        presenter.handlePushMessage(order);
    }

}