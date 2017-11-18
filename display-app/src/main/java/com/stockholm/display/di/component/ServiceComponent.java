package com.stockholm.display.di.component;


import com.stockholm.display.di.Scopes;
import com.stockholm.display.di.module.ServiceModule;
import com.stockholm.display.push.PushMessageService;
import com.stockholm.display.service.AutoDisplayControlService;

import dagger.Component;

@Scopes.Service
@Component(dependencies = ApplicationComponent.class, modules = {ServiceModule.class})
public interface ServiceComponent {

    void inject(PushMessageService service);

    void inject(AutoDisplayControlService service);

}