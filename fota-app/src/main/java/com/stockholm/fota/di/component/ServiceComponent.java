package com.stockholm.fota.di.component;


import com.stockholm.fota.di.Scopes;
import com.stockholm.fota.di.module.ServiceModule;
import com.stockholm.fota.engine.AlarmService;
import com.stockholm.fota.ota.FotaService;

import dagger.Component;

@Scopes.Service
@Component(dependencies = {ApplicationComponent.class}, modules = {ServiceModule.class})
public interface ServiceComponent {
    void inject(FotaService service);

    void inject(AlarmService service);
}