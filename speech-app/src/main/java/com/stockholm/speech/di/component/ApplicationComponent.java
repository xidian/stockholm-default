package com.stockholm.speech.di.component;


import com.stockholm.common.bus.RxEventBus;
import com.stockholm.common.utils.WeakHandler;
import com.stockholm.speech.SpeechApplication;
import com.stockholm.speech.di.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(SpeechApplication appApplication);

    WeakHandler weakhandler();

    RxEventBus rxEventBus();

}