package com.stockholm.business.di.module;

import android.app.Application;
import android.content.Context;

import com.stockholm.api.business.BusinessService;
import com.stockholm.business.BusinessApplication;
import com.stockholm.common.api.BaseUrl;
import com.stockholm.common.api.EnvProvider;
import com.stockholm.common.api.ServiceFactory;
import com.stockholm.common.utils.WeakHandler;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private Application application;

    public ApplicationModule(BusinessApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return this.application;
    }

    @Provides
    @Singleton
    public Application provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    public WeakHandler providerWeakHandler() {
        return new WeakHandler();
    }


    @Provides
    @Singleton
    @Named("meow.api.service.factory")
    public ServiceFactory provideServiceFactory() {
        BaseUrl url = new BaseUrl("http://bd.api.meowtechnology.com/");
        return new ServiceFactory(url);
    }

    @Provides
    @Singleton
    public BusinessService provideBusinessService(@Named("meow.api.service.factory") ServiceFactory factory) {
        return factory.create(BusinessService.class);
    }

}