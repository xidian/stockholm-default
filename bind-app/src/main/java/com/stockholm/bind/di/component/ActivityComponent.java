package com.stockholm.bind.di.component;

import com.stockholm.bind.di.Scopes;
import com.stockholm.bind.di.module.ActivityModule;
import com.stockholm.bind.view.HomeActivity;

import dagger.Component;

@Scopes.Activity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(HomeActivity activity);

}