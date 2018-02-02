package com.stockholm.factory;


import android.content.Intent;

import com.stockholm.common.utils.WeakHandler;
import com.stockholm.common.view.ReleaseBaseActivity;
import com.stockholm.factory.di.ApplicationComponent;
import com.stockholm.factory.di.DaggerActivityComponent;

import javax.inject.Inject;

public class OverActivity extends ReleaseBaseActivity {

    @Inject
    WeakHandler handler;

    @Inject
    LogUtils logUtils;

    @Override
    protected void initInject() {
        ApplicationComponent component = ((FactoryApplication) getApplication()).getApplicationComponent();
        DaggerActivityComponent.builder().applicationComponent(component).build().inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_over;
    }

    @Override
    protected void init() {
        logUtils.writeFile();
        handler.postDelayed(this::shutDown, 3000);
    }

    @Override
    protected void pauseSound() {

    }

    @Override
    public void onLineShortDrag() {

    }

    private void shutDown() {
        Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void exit() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
