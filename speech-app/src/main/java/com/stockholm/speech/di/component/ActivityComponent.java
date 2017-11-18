package com.stockholm.speech.di.component;


import com.stockholm.speech.di.Scopes;
import com.stockholm.speech.di.module.ActivityModule;

import dagger.Component;

@Scopes.Activity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

}