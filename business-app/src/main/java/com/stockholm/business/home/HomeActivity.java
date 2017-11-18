package com.stockholm.business.home;

import android.support.v4.view.ViewPager;

import com.stockholm.api.business.DisplayBean;
import com.stockholm.business.BusinessApplication;
import com.stockholm.business.R;
import com.stockholm.business.di.component.ApplicationComponent;
import com.stockholm.business.di.component.DaggerActivityComponent;
import com.stockholm.common.view.ReleaseBaseActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;


public class HomeActivity extends ReleaseBaseActivity implements HomeView {

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @Inject
    HomePresenter homePresenter;

    private DisplayAdapter adapter;
    private boolean isLoading;

    @Override
    protected void initInject() {
        ApplicationComponent component = ((BusinessApplication) getApplication()).getApplicationComponent();
        DaggerActivityComponent.builder().applicationComponent(component).build().inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    protected void init() {
        homePresenter.attachView(this);
        adapter = new DisplayAdapter(this, null);
        viewPager.setAdapter(adapter);
        homePresenter.getDisplayList();
        isLoading = true;
    }

    @Override
    public void getDataSuccess(List<DisplayBean> data) {
        isLoading = false;
        adapter.setNewData(data);
    }

    @Override
    public void getDataFail() {
        isLoading = false;
    }

    @Override
    public void onControlUpClick() {
        if (isLoading) return;
        int index = viewPager.getCurrentItem() - 1;
        viewPager.setCurrentItem(index < 0 ? 0 : index);
    }

    @Override
    public void onControlDownClick() {
        if (isLoading) return;
        int maxIndex = adapter.getData().size();
        int index = viewPager.getCurrentItem() + 1;
        viewPager.setCurrentItem(index == maxIndex ? maxIndex - 1 : index);
    }

    @Override
    public void onControlOKClick() {
        if (!isLoading) {
            homePresenter.getDisplayList();
            isLoading = true;
        }
    }

}