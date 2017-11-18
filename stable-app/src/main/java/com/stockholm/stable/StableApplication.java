package com.stockholm.stable;

import com.stockholm.common.BaseApplication;
import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.stable.di.ApplicationComponent;
import com.stockholm.stable.di.ApplicationModule;
import com.stockholm.stable.di.DaggerApplicationComponent;

public class StableApplication extends BaseApplication {

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
