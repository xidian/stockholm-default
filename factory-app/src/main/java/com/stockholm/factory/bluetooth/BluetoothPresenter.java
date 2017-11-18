package com.stockholm.factory.bluetooth;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.stockholm.common.view.BasePresenter;
import com.stockholm.factory.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

public class BluetoothPresenter extends BasePresenter<BluetoothView> {

    private Context context;
    private LogUtils logUtils;

    private Lock lock;
    private Condition condition;
    private List<MeowBluetoothDevice> list = new ArrayList<>();

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                MeowBluetoothDevice mdevice = new MeowBluetoothDevice(device, rssi);
                list.add(mdevice);
                getMvpView().onBluetoothFound(mdevice);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (list.size() > 0) {
                    logUtils.write(LogUtils.BLUETOOTH, LogUtils.PASS);
                } else {
                    logUtils.write(LogUtils.BLUETOOTH, LogUtils.FAIL);
                }
                getMvpView().onBluetoothScanOver(list.size() > 0);
                list.clear();
                context.unregisterReceiver(receiver);
            }
        }
    };

    @Inject
    public BluetoothPresenter(Context context,
                              LogUtils logUtils) {
        this.context = context;
        this.logUtils = logUtils;

        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    public void startScan() {
        new Thread(() -> {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (!adapter.isEnabled()) {
                adapter.enable();
            }
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            context.registerReceiver(receiver, filter);
            adapter.startDiscovery();
            lock.lock();
            try {
                condition.await(5, TimeUnit.SECONDS);
                if (list.size() > 0) {
                    logUtils.write(LogUtils.BLUETOOTH, LogUtils.PASS);
                } else {
                    logUtils.write(LogUtils.BLUETOOTH, LogUtils.FAIL);
                }
                getMvpView().onBluetoothScanOver(list.size() > 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            lock.unlock();
            context.unregisterReceiver(receiver);
        }).start();
    }
}
