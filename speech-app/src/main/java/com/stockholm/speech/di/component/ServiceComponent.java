package com.stockholm.speech.di.component;

import com.stockholm.speech.di.Scopes;
import com.stockholm.speech.di.module.ServiceModule;

import dagger.Component;

@Scopes.Service
@Component(dependencies = {ApplicationComponent.class}, modules = {ServiceModule.class})
public interface ServiceComponent {

}