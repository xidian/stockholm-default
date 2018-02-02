package com.stockholm.display;

import android.content.Intent;

import com.stockholm.common.Constant;
import com.stockholm.common.JPushOrder;
import com.stockholm.common.utils.PreferenceFactory;
import com.stockholm.common.view.ReleaseBaseActivity;
import com.stockholm.display.di.component.ApplicationComponent;
import com.stockholm.display.di.component.DaggerActivityComponent;
import com.stockholm.display.media.DisplayHelper;

import javax.inject.Inject;

public class HomeActivity extends ReleaseBaseActivity {

    @Inject
    DisplayHelper displayHelper;
    @Inject
    PreferenceFactory preferenceFactory;

    @Override
    protected void initInject() {
        ApplicationComponent component = ((DisplayApplication) getApplication()).getApplicationComponent();
        DaggerActivityComponent.builder().applicationComponent(component).build().inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return 0;
    }

    @Override
    protected void init() {
        preferenceFactory.create(DisplayPreference.class).mediaPlaying(false);
        syncConfig();
        finish();
    }

    @Override
    protected void pauseSound() {

    }

    private void syncConfig() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Constant.ACTION_PUSH_BROADCAST);
        broadcastIntent.putExtra(Constant.JPUSH_ORDER, JPushOrder.GET_APP_CONFIG);
        broadcastIntent.setPackage(this.getPackageName());
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onTestButtonClick() {
        super.onTestButtonClick();
        displayHelper.showWindow();
    }

    @Override
    public void onNotificationButtonClick() {
        super.onNotificationButtonClick();
        displayHelper.dismissWindow();
    }

}