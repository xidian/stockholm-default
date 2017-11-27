package com.stockholm.fota.ota;

import android.content.Intent;

import com.stockholm.common.bus.RxEventBus;
import com.stockholm.common.view.ReleaseBaseActivity;
import com.stockholm.fota.FotaApplication;
import com.stockholm.fota.event.TestOtaEvent;
import com.stockholm.fota.di.component.ApplicationComponent;
import com.stockholm.fota.di.component.DaggerActivityComponent;

import javax.inject.Inject;

public class MainActivity extends ReleaseBaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    RxEventBus eventBus;

    @Override
    protected void initInject() {
        ApplicationComponent component = ((FotaApplication) getApplication()).getApplicationComponent();
        DaggerActivityComponent.builder().applicationComponent(component).build().inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return 0;
    }

    @Override
    protected void init() {
        initFotaService();
    }

    @Override
    public void onTestButtonClick() {
        super.onTestButtonClick();
        eventBus.post(new TestOtaEvent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unsubscribe();
    }

    private void initFotaService() {
        Intent fotaService = new Intent(this, FotaService.class);
        startService(fotaService);
        finish();
    }

}