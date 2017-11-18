package com.stockholm.factory;


import com.stockholm.common.view.ReleaseBaseActivity;
import com.stockholm.factory.di.ApplicationComponent;
import com.stockholm.factory.di.DaggerActivityComponent;

import javax.inject.Inject;

public class HomeActivity extends ReleaseBaseActivity {

    @Inject
    PathUtils pathUtils;
    @Inject
    LogUtils logUtils;

    @Override
    protected void initInject() {
        ApplicationComponent component = ((FactoryApplication) getApplication()).getApplicationComponent();
        DaggerActivityComponent.builder().applicationComponent(component).build().inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    protected void init() {
        logUtils.init(this);
    }

    @Override
    public void onControlDownClick() {
        super.onControlDownClick();
        pathUtils.goNext(this);
        logUtils.write("home | control down click", LogUtils.CLICK);
    }

    @Override
    public void onLineShortDrag() {
        logUtils.write("home | line drag", LogUtils.CLICK);
    }
}
