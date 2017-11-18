package com.stockholm.display.di.component;


import com.stockholm.display.HomeActivity;
import com.stockholm.display.di.Scopes;
import com.stockholm.display.di.module.ActivityModule;

import dagger.Component;

@Scopes.Activity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(HomeActivity activity);

}