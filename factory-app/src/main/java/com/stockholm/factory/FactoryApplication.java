package com.stockholm.factory;


import com.stockholm.common.BaseApplication;
import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.factory.di.ApplicationComponent;
import com.stockholm.factory.di.ApplicationModule;
import com.stockholm.factory.di.DaggerApplicationComponent;

public class FactoryApplication extends BaseApplication {

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

    @Override
    public String initUMengAppKey() {
        return "59a8c83d65b6d629620002ed";
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
