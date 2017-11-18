package com.stockholm.business;

import com.stockholm.business.di.component.ApplicationComponent;
import com.stockholm.business.di.component.DaggerApplicationComponent;
import com.stockholm.business.di.module.ApplicationModule;
import com.stockholm.common.BaseApplication;
import com.stockholm.common.utils.StockholmLogger;


public class BusinessApplication extends BaseApplication {

    private ApplicationComponent applicationComponent;

    @Override
    public void initializeThirdService() {
        StockholmLogger.init(this);
    }

    @Override
    public void initializeInjector() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        applicationComponent.inject(this);
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

}