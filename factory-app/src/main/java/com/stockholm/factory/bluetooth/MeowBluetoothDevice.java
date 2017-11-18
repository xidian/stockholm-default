package com.stockholm.factory.bluetooth;


import android.bluetooth.BluetoothDevice;

public class MeowBluetoothDevice {

    private BluetoothDevice device;
    private short rssi;

    public MeowBluetoothDevice(BluetoothDevice device, short rssi) {
        this.device = device;
        this.rssi = rssi;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public short getRssi() {
        return rssi;
    }
}
