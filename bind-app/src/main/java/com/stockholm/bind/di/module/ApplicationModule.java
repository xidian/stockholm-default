package com.stockholm.bind.di.module;

import android.app.Application;
import android.content.Context;

import com.stockholm.api.bind.BindService;
import com.stockholm.api.push.PushService;
import com.stockholm.bind.BindApplication;
import com.stockholm.bind.BuildConfig;
import com.stockholm.common.api.BaseUrl;
import com.stockholm.common.api.Env;
import com.stockholm.common.api.EnvProvider;
import com.stockholm.common.api.ServiceFactory;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private Application application;

    public ApplicationModule(BindApplication application) {
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
    @Named("com.meow.api")
    public BaseUrl provideBaseUrl(EnvProvider provider) {
        Env env;
        switch (BuildConfig.ENV_VALUE) {
            case 1:
                env = Env.DEV;
                break;
            case 2:
                env = Env.STG;
                break;
            case 3:
                env = Env.PROD;
                break;
            default:
                env = Env.PROD;
        }
        return new BaseUrl(provider.get(env).getApiUrl());
    }

    @Provides
    @Singleton
    @Named("meow.api.service.factory")
    public ServiceFactory provideServiceFactory(@Named("com.meow.api") BaseUrl baseUrl) {
        return new ServiceFactory(baseUrl);
    }

    @Provides
    @Singleton
    public BindService provideBindService(@Named("meow.api.service.factory") ServiceFactory factory) {
        return factory.create(BindService.class);
    }

    @Provides
    @Singleton
    public PushService providePushService(@Named("meow.api.service.factory") ServiceFactory factory) {
        return factory.create(PushService.class);
    }

}

