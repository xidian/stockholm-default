package com.stockholm.factory.bluetooth;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.stockholm.common.view.ReleaseBaseActivity;
import com.stockholm.factory.FactoryApplication;
import com.stockholm.factory.PathUtils;
import com.stockholm.factory.R;
import com.stockholm.factory.Tips;
import com.stockholm.factory.di.ApplicationComponent;
import com.stockholm.factory.di.DaggerActivityComponent;

import javax.inject.Inject;

import butterknife.BindView;

public class BluetoothActivity extends ReleaseBaseActivity implements BluetoothView {

    @BindView(R.id.rv_ble)
    RecyclerView recyclerView;
    @Inject
    BluetoothPresenter presenter;
    @Inject
    PathUtils pathUtils;

    private BluetoothListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BluetoothListAdapter(this);
        recyclerView.setAdapter(adapter);
        presenter.attachView(this);
        presenter.startScan();
    }

    @Override
    protected void initInject() {
        ApplicationComponent component = ((FactoryApplication) getApplication()).getApplicationComponent();
        DaggerActivityComponent.builder().applicationComponent(component).build().inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_bluetooth;
    }

    @Override
    protected void init() {

    }

    @Override
    public void onBluetoothFound(MeowBluetoothDevice device) {
        adapter.add(device);
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public void onBluetoothScanOver(boolean success) {
        runOnUiThread(() -> {
            Tips tips = new Tips(this);
            tips.setSuccessText(R.string.ble_over);
            tips.showSuccess();
        });
    }

    @Override
    public void onLineShortDrag() {

    }

    @Override
    public void onControlOkLongClick() {
        super.onControlOkLongClick();
        pathUtils.goNext(this);
    }
}
