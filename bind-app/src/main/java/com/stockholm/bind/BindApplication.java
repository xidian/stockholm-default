package com.stockholm.bind;

import com.facebook.stetho.Stetho;
import com.stockholm.bind.di.component.ApplicationComponent;
import com.stockholm.bind.di.component.DaggerApplicationComponent;
import com.stockholm.bind.di.module.ApplicationModule;
import com.stockholm.common.BaseApplication;
import com.stockholm.common.utils.StockholmLogger;

public class BindApplication extends BaseApplication {

    private ApplicationComponent applicationComponent;

    @Override
    public void initializeThirdService() {
        StockholmLogger.init(this);
        Stetho.initializeWithDefaults(this);
    }

    @Override
    public void initializeInjector() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        applicationComponent.inject(this);
    }

    @Override
    public String initUMengAppKey() {
        return "59a8c83d65b6d629620002ed";
    }

    public ApplicationComponent getApplicationComponent() {
        return this.applicationComponent;
    }

}
