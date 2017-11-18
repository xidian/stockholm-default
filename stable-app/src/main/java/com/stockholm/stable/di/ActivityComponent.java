package com.stockholm.stable.di;


import com.stockholm.stable.view.HomeActivity;

import dagger.Component;

@Scopes.Activity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(HomeActivity activity);

}