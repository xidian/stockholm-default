package com.stockholm.factory.di;

import android.content.Context;

import com.stockholm.common.utils.WeakHandler;
import com.stockholm.factory.FactoryApplication;
import com.stockholm.factory.LogUtils;
import com.stockholm.factory.PathUtils;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(FactoryApplication appApplication);

    Context context();

    WeakHandler weakHandler();

    PathUtils pathUtils();

    LogUtils logUtils();

}

