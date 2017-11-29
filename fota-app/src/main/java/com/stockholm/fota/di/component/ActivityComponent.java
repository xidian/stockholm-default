package com.stockholm.fota.di.component;


import com.stockholm.fota.di.Scopes;
import com.stockholm.fota.di.module.ActivityModule;

import dagger.Component;

@Scopes.Activity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
}