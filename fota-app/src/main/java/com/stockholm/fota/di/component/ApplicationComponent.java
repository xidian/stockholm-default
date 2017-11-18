package com.stockholm.fota.di.component;


import android.content.Context;

import com.stockholm.api.rom.RomService;
import com.stockholm.common.bus.RxEventBus;
import com.stockholm.common.utils.PreferenceFactory;
import com.stockholm.fota.FotaApplication;
import com.stockholm.fota.di.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(FotaApplication appApplication);

    RxEventBus rxEventBus();

    RomService romService();

    PreferenceFactory preferenceFactory();

    Context context();
}