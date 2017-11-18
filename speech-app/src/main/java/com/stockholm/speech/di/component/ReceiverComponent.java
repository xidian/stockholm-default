package com.stockholm.speech.di.component;


import com.stockholm.speech.di.Scopes;
import com.stockholm.speech.di.module.ReceiverModule;

import dagger.Component;

@Scopes.Receiver
@Component(dependencies = ApplicationComponent.class, modules = ReceiverModule.class)
public interface ReceiverComponent {

}