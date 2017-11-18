package com.stockholm.business.di.component;


import com.stockholm.business.di.Scopes;
import com.stockholm.business.di.module.ReceiverModule;

import dagger.Component;

@Scopes.Receiver
@Component(dependencies = ApplicationComponent.class, modules = {ReceiverModule.class})
public interface ReceiverComponent {

}