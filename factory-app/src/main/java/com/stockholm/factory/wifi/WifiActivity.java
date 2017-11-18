package com.stockholm.factory.wifi;


import android.net.wifi.ScanResult;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.stockholm.common.view.ReleaseBaseActivity;
import com.stockholm.factory.FactoryApplication;
import com.stockholm.factory.PathUtils;
import com.stockholm.factory.R;
import com.stockholm.factory.Tips;
import com.stockholm.factory.di.ApplicationComponent;
import com.stockholm.factory.di.DaggerActivityComponent;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class WifiActivity extends ReleaseBaseActivity implements WifiView {

    @BindView(R.id.rv_wifi)
    RecyclerView recyclerView;
    @BindView(R.id.tv_countdown)
    TextView tvCountdown;

    @Inject
    WifiPresenter presenter;

    @Inject
    PathUtils pathUtils;

    private WifiListAdapter adapter;

    @Override
    protected void initInject() {
        ApplicationComponent component = ((FactoryApplication) getApplication()).getApplicationComponent();
        DaggerActivityComponent.builder().applicationComponent(component).build().inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_wifi;
    }

    @Override
    protected void init() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WifiListAdapter(this);
        recyclerView.setAdapter(adapter);
        presenter.attachView(this);
        presenter.startScan();
    }

    @Override
    public void onTestFinish(boolean pass) {
        runOnUiThread(() -> {
            Tips tips = new Tips(this);
            if (pass) {
                tips.setSuccessText(R.string.wifi_over);
                tips.showSuccess();
                pathUtils.goNext(this);
            } else {
//                tips.setFailText(R.string.wifi_fail);
//                tips.showFail();
//                pathUtils.goOver(this);
                tvCountdown.setTextColor(ContextCompat.getColor(this, R.color.red));
                tvCountdown.setText(R.string.fail);
            }
        });
    }

    @Override
    public void onUpdateList(List<ScanResult> list) {
        runOnUiThread(() -> adapter.setData(list));
    }

    @Override
    public void onCountdown(long millisUntilFinished) {
        int display = (int) (millisUntilFinished / 1000);
        tvCountdown.setText(String.valueOf(display));
    }

    @Override
    public void onLineShortDrag() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.release();
    }
}
