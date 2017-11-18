package com.stockholm.factory.bluetooth;


import com.stockholm.common.view.MvpView;


public interface BluetoothView extends MvpView {
    void onBluetoothFound(MeowBluetoothDevice device);
    void onBluetoothScanOver(boolean success);
}
