package com.stockholm.speech.di.module;

import android.app.Application;
import android.content.Context;

import com.stockholm.common.utils.WeakHandler;
import com.stockholm.speech.SpeechApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private Application application;

    public ApplicationModule(SpeechApplication application) {
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