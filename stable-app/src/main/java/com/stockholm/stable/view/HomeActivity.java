package com.stockholm.stable.view;

import android.widget.ImageView;

import com.stockholm.common.view.ReleaseBaseActivity;
import com.stockholm.stable.R;
import com.stockholm.stable.StableApplication;
import com.stockholm.stable.di.ApplicationComponent;
import com.stockholm.stable.di.DaggerActivityComponent;

import javax.inject.Inject;

import butterknife.BindView;

public class HomeActivity extends ReleaseBaseActivity implements HomeView {

    @Inject
    HomePresenter homePresenter;
    @BindView(R.id.iv_test_image)
    ImageView ivTestImage;

    @Override
    protected void initInject() {
        ApplicationComponent component = ((StableApplication) getApplication()).getApplicationComponent();
        DaggerActivityComponent.builder().applicationComponent(component).build().inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    protected void init() {
        homePresenter.attachView(this);
        homePresenter.init();
    }

    @Override
    protected void pauseSound() {

    }

    @Override
    public void flashImage(int picId) {
        ivTestImage.setImageDrawable(getDrawable(picId));
    }

    @Override
    public void onControlOkLongClick() {
        super.onControlOkLongClick();
        homePresenter.exit();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
