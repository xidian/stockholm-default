package com.stockholm.bind.di.component;

import android.content.Context;

import com.stockholm.api.bind.BindService;
import com.stockholm.api.push.PushService;
import com.stockholm.bind.BindApplication;
import com.stockholm.bind.di.module.ApplicationModule;
import com.stockholm.bind.wifi.APManager;
import com.stockholm.common.utils.DeviceUUIDFactory;
import com.stockholm.common.utils.PreferenceFactory;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(BindApplication appApplication);

    Context context();

    PreferenceFactory preferenceFactory();

    BindService bindService();

    PushService pushService();

    APManager apManager();

    DeviceUUIDFactory deviceUUIDFactory();
}

