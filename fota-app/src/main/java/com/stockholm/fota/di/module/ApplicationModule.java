package com.stockholm.fota.di.module;

import android.app.Application;
import android.content.Context;

import com.stockholm.api.rom.RomService;
import com.stockholm.common.api.BaseUrl;
import com.stockholm.common.api.Env;
import com.stockholm.common.api.EnvProvider;
import com.stockholm.common.api.ServiceFactory;
import com.stockholm.common.utils.PreferenceFactory;
import com.stockholm.common.utils.WeakHandler;
import com.stockholm.fota.BuildConfig;
import com.stockholm.fota.FotaApplication;
import com.stockholm.fota.ota.FotaManager;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private Application application;

    public ApplicationModule(FotaApplication application) {
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
    public WeakHandler provideWeakHandler() {
        return new WeakHandler();
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
    public RomService provideRomService(@Named("meow.api.service.factory") ServiceFactory factory) {
        return factory.create(RomService.class);
    }

    @Provides
    @Singleton
    public FotaManager provideFotaManager(Context context,
                                          RomService romService,
                                          PreferenceFactory preferenceFactory) {
        return new FotaManager(context, romService, preferenceFactory);
    }
}