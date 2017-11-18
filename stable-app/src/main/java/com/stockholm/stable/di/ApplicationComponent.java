package com.stockholm.stable.di;

import android.content.Context;

import com.stockholm.common.utils.WeakHandler;
import com.stockholm.stable.StableApplication;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(StableApplication appApplication);

    Context context();

    WeakHandler weakHandler();

}

