package com.stockholm.stable.di;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {
    private Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    public Activity provideActivity() {
        return this.activity;
    }
}
