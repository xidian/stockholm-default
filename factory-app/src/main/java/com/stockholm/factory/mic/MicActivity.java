package com.stockholm.factory.mic;


import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.stockholm.common.view.ReleaseBaseActivity;
import com.stockholm.factory.FactoryApplication;
import com.stockholm.factory.LogUtils;
import com.stockholm.factory.PathUtils;
import com.stockholm.factory.R;
import com.stockholm.factory.Tips;
import com.stockholm.factory.di.ApplicationComponent;
import com.stockholm.factory.di.DaggerActivityComponent;

import javax.inject.Inject;

import butterknife.BindView;

public class MicActivity extends ReleaseBaseActivity implements MicView {

    @BindView(R.id.tv_mic_state)
    TextView tvMicState;
    @BindView(R.id.tv_countdown)
    TextView tvCountdown;

    @Inject
    PathUtils pathUtils;

    @Inject
    MicPresenter presenter;

    @Inject
    LogUtils logUtils;

    private boolean fail = false;

    @Override
    protected void initInject() {
        ApplicationComponent component = ((FactoryApplication) getApplication()).getApplicationComponent();
        DaggerActivityComponent.builder().applicationComponent(component).build().inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_mic;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.attachView(this);
        presenter.init();
    }

    @Override
    public void onToastFailMessage(String msg) {
        Tips tip = new Tips(this);
        tip.setFailText(msg);
        tip.showFail();
    }

    @Override
    public void onUpdateMessage(String msg) {
        tvMicState.setText(msg);
    }

    @Override
    public void onCountdown(long millisUntilFinished) {
        int display = (int) (millisUntilFinished / 1000);
        tvCountdown.setText(String.valueOf(display));
    }

    @Override
    public void onCountdownFinish() {
        logUtils.write(LogUtils.MIC, LogUtils.FAIL);
        fail = true;
        tvCountdown.setTextColor(ContextCompat.getColor(this, R.color.red));
        tvCountdown.setText(R.string.fail);
    }

    @Override
    public void onControlOkLongClick() {
        super.onControlOkLongClick();
        if (fail) return;
        if (presenter.played) {
            logUtils.write(LogUtils.MIC, LogUtils.PASS);
            pathUtils.goNext(this);
        }
    }

    @Override
    public void onLineShortDrag() {

    }

    @Override
    public void onControlDownClick() {
        super.onControlDownClick();
        if (fail) return;
        logUtils.write("mic | control down click", LogUtils.CLICK);
        presenter.startRecord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.release();
    }
}
