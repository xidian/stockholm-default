package com.stockholm.factory.di;

import android.app.Application;
import android.content.Context;

import com.stockholm.common.utils.WeakHandler;
import com.stockholm.factory.FactoryApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private Application application;

    public ApplicationModule(FactoryApplication application) {
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

}

