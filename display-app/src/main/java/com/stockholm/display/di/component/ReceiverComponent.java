package com.stockholm.display.di.component;


import com.stockholm.display.di.Scopes;
import com.stockholm.display.di.module.ReceiverModule;
import com.stockholm.display.receiver.DisplayStateReceiver;
import com.stockholm.display.receiver.EnableDisplayReceiver;

import dagger.Component;

@Scopes.Receiver
@Component(dependencies = ApplicationComponent.class, modules = {ReceiverModule.class})
public interface ReceiverComponent {

    void inject(EnableDisplayReceiver receiver);

    void inject(DisplayStateReceiver receiver);

}