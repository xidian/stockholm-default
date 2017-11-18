package com.stockholm.business.di.component;

import android.content.Context;

import com.stockholm.api.business.BusinessService;
import com.stockholm.business.BusinessApplication;
import com.stockholm.business.di.module.ApplicationModule;
import com.stockholm.common.bus.RxEventBus;
import com.stockholm.common.utils.PreferenceFactory;
import com.stockholm.common.utils.WeakHandler;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(BusinessApplication application);

    Context context();

    PreferenceFactory preferenceFactory();

    RxEventBus rxEventBus();

    WeakHandler weakHandler();

    BusinessService businessService();

}