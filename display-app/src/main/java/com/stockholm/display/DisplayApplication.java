package com.stockholm.display;

import com.stockholm.common.BaseApplication;
import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.display.di.component.ApplicationComponent;
import com.stockholm.display.di.component.DaggerApplicationComponent;
import com.stockholm.display.di.module.ApplicationModule;


public class DisplayApplication extends BaseApplication {

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
