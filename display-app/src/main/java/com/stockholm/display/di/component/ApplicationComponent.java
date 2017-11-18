package com.stockholm.display.di.component;

import android.content.Context;

import com.stockholm.api.display.DisplayService;
import com.stockholm.common.bus.RxEventBus;
import com.stockholm.common.utils.PreferenceFactory;
import com.stockholm.common.utils.WeakHandler;
import com.stockholm.display.DisplayApplication;
import com.stockholm.display.di.module.ApplicationModule;
import com.stockholm.display.media.DisplayHelper;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(DisplayApplication application);

    Context context();

    PreferenceFactory preferenceFactory();

    RxEventBus rxEventBus();

    WeakHandler weakHandler();

    DisplayService displayService();

    DisplayHelper displayHelper();

}