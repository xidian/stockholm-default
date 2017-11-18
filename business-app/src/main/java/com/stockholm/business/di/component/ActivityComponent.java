package com.stockholm.business.di.component;


import com.stockholm.business.home.HomeActivity;
import com.stockholm.business.di.Scopes;
import com.stockholm.business.di.module.ActivityModule;

import dagger.Component;

@Scopes.Activity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(HomeActivity activity);

}