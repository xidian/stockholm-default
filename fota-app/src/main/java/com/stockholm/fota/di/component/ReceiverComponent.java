package com.stockholm.fota.di.component;


import com.stockholm.fota.BindReceiver;
import com.stockholm.fota.NetStateReceiver;
import com.stockholm.fota.di.Scopes;
import com.stockholm.fota.di.module.ReceiverModule;

import dagger.Component;

@Scopes.Receiver
@Component(dependencies = ApplicationComponent.class, modules = ReceiverModule.class)
public interface ReceiverComponent {
    void inject(NetStateReceiver receiver);

    void inject(BindReceiver receiver);
}