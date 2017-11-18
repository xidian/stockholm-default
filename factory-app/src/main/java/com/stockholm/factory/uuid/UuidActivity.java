package com.stockholm.factory.uuid;


import android.bluetooth.BluetoothAdapter;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stockholm.common.view.ReleaseBaseActivity;
import com.stockholm.factory.FactoryApplication;
import com.stockholm.factory.LogUtils;
import com.stockholm.factory.PathUtils;
import com.stockholm.factory.R;
import com.stockholm.factory.di.ApplicationComponent;
import com.stockholm.factory.di.DaggerActivityComponent;

import javax.inject.Inject;

import butterknife.BindView;

public class UuidActivity extends ReleaseBaseActivity implements UuidView {

    @BindView(R.id.iv_sn)
    ImageView ivSn;
    @BindView(R.id.iv_ble_mac)
    ImageView ivBleMac;
    @BindView(R.id.tv_sn)
    TextView tvSn;
    @BindView(R.id.tv_mac)
    TextView tvBleMac;
    @BindView(R.id.tv_fail)
    TextView tvFail;

    @Inject
    PathUtils pathUtils;
    @Inject
    LogUtils logUtils;
    @Inject
    UuidPresenter uuidPresenter;

    private boolean fail = false;

    @Override
    protected void initInject() {
        ApplicationComponent component = ((FactoryApplication) getApplication()).getApplicationComponent();
        DaggerActivityComponent.builder().applicationComponent(component).build().inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_uuid;
    }

    @Override
    protected void init() {
        uuidPresenter.attachView(this);
        uuidPresenter.registerSnReceiver(this);
        uuidPresenter.getSn(this);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            String mac = adapter.getAddress();
            Bitmap macBitmap = ZxingUtil.createQRImage(mac, 300, 300);
            ivBleMac.setImageBitmap(macBitmap);
            tvBleMac.setText(mac);
        }
    }

    @Override
    public void onLineShortDrag() {
//        pathUtils.goOver(this);
        logUtils.write(LogUtils.UUID, LogUtils.FAIL);
        tvFail.setText(R.string.fail);
        tvFail.setVisibility(View.VISIBLE);
        fail = true;
    }

    @Override
    public void onControlOkLongClick() {
        if (fail) return;
        pathUtils.goNext(this);
        logUtils.write(LogUtils.UUID, LogUtils.PASS);
    }

    @Override
    public void onSnGot(String sn) {
        Bitmap snBitmap = ZxingUtil.createQRImage(sn, 300, 300);
        ivSn.setImageBitmap(snBitmap);
        tvSn.setText(sn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uuidPresenter.unRegisterReceiver(this);
    }
}
