package com.stockholm.fota;

import com.facebook.stetho.Stetho;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.leakcanary.LeakCanary;
import com.stockholm.common.BaseApplication;
import com.stockholm.common.utils.PreferenceFactory;
import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.fota.di.component.ApplicationComponent;
import com.stockholm.fota.di.component.DaggerApplicationComponent;
import com.stockholm.fota.di.module.ApplicationModule;
import com.stockholm.fota.log.MLog;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

import javax.inject.Inject;


public class FotaApplication extends BaseApplication {

    @Inject
    PreferenceFactory preferenceFactory;

    private ApplicationComponent applicationComponent;

    @Override
    public void initializeThirdService() {
        StockholmLogger.init(this);
        FlowManager.init(new FlowConfig.Builder(this).build());
        Stetho.initializeWithDefaults(this);
        LeakCanary.install(this);
        Bugly.init(this, "8c7c1ea02d", false);
        CrashReport.setIsDevelopmentDevice(this, BuildConfig.DEBUG);
        MLog.init(this);
    }

    @Override
    public void initializeInjector() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();
        applicationComponent.inject(this);
    }

    @Override
    public String initUMengAppKey() {
        return "5a1e1b13b27b0a544b000084";
    }

    public ApplicationComponent getApplicationComponent() {
        return this.applicationComponent;
    }

}