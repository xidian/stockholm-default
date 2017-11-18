package com.stockholm.bind.bluetooth;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

import com.stockholm.bind.TAG;
import com.stockholm.common.utils.StockholmLogger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class BluetoothHelper {

    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothChatService chatService;

    private BluetoothListener listener;

    private List<BluetoothDevice> scanedDevices = new ArrayList<>();
    private boolean registReceiver = false;

    private final BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    listener.onDeviceDiscovery(device);
                    scanedDevices.add(device);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                listener.onDeviceDiscoveryFinish(scanedDevices);
            }
        }
    };

    private final Handler chatHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Bluetooth.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            listener.onDeviceConnected();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            listener.onDeviceConnecting();
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            break;
                        default:
                    }
                    break;
                case Bluetooth.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    listener.onMessageWrite(writeMessage);
                    break;
                case Bluetooth.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    listener.onMessageRead(readMessage);
                    break;
                case Bluetooth.MESSAGE_DEVICE_NAME:
                    String connectedDeviceName = msg.getData().getString(Bluetooth.DEVICE_NAME);
                    break;
                case Bluetooth.MESSAGE_TOAST:
                    String toast = msg.getData().getString(Bluetooth.TOAST);
                    StockholmLogger.d(TAG.BLE, "toast:" + toast);
                    break;
                default:
            }
        }
    };

    @Inject
    public BluetoothHelper(Context context) {
        this.context = context;
    }

    public void init(BluetoothListener listener) {
        this.listener = listener;
        this.chatService = new BluetoothChatService(context, chatHandler);
        if (chatService.getState() == BluetoothChatService.STATE_NONE) {
            chatService.start();
        }
        if (bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(discoveryReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(discoveryReceiver, filter);
        registReceiver = true;
    }

    public void ensureDiscoverable() {
        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            context.startActivity(discoverableIntent);
        }
    }

    public void doDiscovery() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        scanedDevices.clear();
        bluetoothAdapter.startDiscovery();
    }

    public void cancelDiscovery() {
        bluetoothAdapter.cancelDiscovery();
    }

    public void connectDevice(String address, boolean secure) {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        chatService.connect(device, secure);
    }

    public void sendMessage(String message) {
        if (chatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            chatService.write(send);
        }
    }

    public void release() {
        if (registReceiver) context.unregisterReceiver(discoveryReceiver);
        if (chatService != null) chatService.stop();
    }

    public interface BluetoothListener {
        void onDeviceDiscovery(BluetoothDevice device);
        void onDeviceDiscoveryFinish(List<BluetoothDevice> devices);
        void onDeviceConnecting();
        void onDeviceConnected();
        void onMessageRead(String msg);
        void onMessageWrite(String msg);
    }
}
