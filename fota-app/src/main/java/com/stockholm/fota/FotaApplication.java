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
        Bugly.init(this, "4a11ae4c63", false);
        CrashReport.setIsDevelopmentDevice(this, BuildConfig.DEBUG);
        MLog.init(this);
    }

    @Override
    public void initializeInjector() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();
        applicationComponent.inject(this);
    }

    public ApplicationComponent getApplicationComponent() {
        return this.applicationComponent;
    }

}