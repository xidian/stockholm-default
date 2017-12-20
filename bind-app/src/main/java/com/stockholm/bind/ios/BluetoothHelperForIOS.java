package com.stockholm.bind.ios;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.util.Log;

import com.stockholm.common.utils.DeviceUUIDFactory;

import java.util.UUID;

import javax.inject.Inject;


public class BluetoothHelperForIOS {

    private static final String LOG_TAG = "BluetoothHelperForIOS";

    private static final ParcelUuid BLE_UUID = ParcelUuid.fromString("00008913-0000-1000-8000-00805f9b34fb");
    private static final String SERVICE_UUID = "1706BBC1-88AB-4B8D-877E-2237916EE929";
    private static final UUID MESSAGE_CHARACTERISTIC_UUID = UUID.fromString("275348FC-C14D-4FD5-B434-7C3F351DEA5F");
    private static final UUID DESCRIPTOR_MESSAGE_UUID = UUID.fromString("45bda094-ff40-4cb8-835d-0da8742bb1eb");
    private static final UUID READ_CHARACTERISTIC_UUID = UUID.fromString("BD28E457-4026-4270-A99F-F9BC20182E15");

    private static final int MSG_CONNECT = 1;
    private static final int MSG_DISCONNECT = 2;
    private static final int MSG_MESSAGE = 3;

    private Context context;
    private DeviceUUIDFactory deviceUUIDFactory;
    private BluetoothGattServer mGattServer;
    private BluetoothGattCharacteristic mMessageCharacteristic;
    private BluetoothLeAdvertiser mBLEAdvertiser;

    private BluetoothDevice mDevice;

    private Listener mListener;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CONNECT:
                    mListener.onConnected();
                    break;
                case MSG_DISCONNECT:
                    mListener.onDisconnected();
                    break;
                case MSG_MESSAGE:
                    String message = (String) msg.obj;
                    mListener.onMessageRead(message);
                    break;
                default:
            }
            return false;
        }
    });

    private final BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            Log.d(LOG_TAG, "onConnectionStateChange| status:" + status + "\tnewState:" + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mDevice = device;
                handler.obtainMessage(MSG_CONNECT).sendToTarget();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED || newState == BluetoothProfile.STATE_DISCONNECTING) {
                mDevice = null;
                handler.obtainMessage(MSG_DISCONNECT).sendToTarget();
            }
        }

        @Override
        public void onMtuChanged(BluetoothDevice device, int mtu) {
            super.onMtuChanged(device, mtu);
            Log.d(LOG_TAG, "onMtuChanged|mtu:" + mtu);
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
            Log.d(LOG_TAG, "onNotificationSent," + device.getAddress() + "-" + status);
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor,
                                             boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
            if (responseNeeded) {
                mGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        offset,
                        value);
            }
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic,
                                                 boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            String message = new String(value);
            Log.d(LOG_TAG, "onCharacteristicWriteRequest value:" + message);
            handler.obtainMessage(MSG_MESSAGE, message).sendToTarget();
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            Log.i(LOG_TAG, "onCharacteristicReadRequest " + characteristic.getUuid().toString());
            byte [] value;
            if (READ_CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
                value = deviceUUIDFactory.getDeviceId().getBytes();
            } else {
                value = new byte[0];
            }

            mGattServer.sendResponse(device,
                    requestId,
                    BluetoothGatt.GATT_SUCCESS,
                    offset,
                    value);
        }
    };

    private final AdvertiseCallback mAdvCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Log.d(LOG_TAG, "Advertise Start Successfully");
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Log.e(LOG_TAG, "Advertise Start Error, Code " + String.valueOf(errorCode));
        }
    };

    @Inject
    public BluetoothHelperForIOS(Context context,
                                 DeviceUUIDFactory deviceUUIDFactory) {
        this.context = context;
        this.deviceUUIDFactory = deviceUUIDFactory;
    }

    public void init(Listener listener) {
        this.mListener = listener;
        initializeBTLE();
        startAdvertise();
    }

    private Boolean initializeBTLE() {
        boolean hasBLE = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        if (!hasBLE) {
            Log.e(LOG_TAG, "Bluetooth Low Energy Not Supported On Phone.");
            return false;
        }
        mBLEAdvertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
        if (mBLEAdvertiser == null) {
            Log.e(LOG_TAG, "Cannot Initialize BTLE Advertiser.");
            return false;
        }
        return createGattService();
    }

    private Boolean createGattService() {
        BluetoothManager mManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (mManager == null) {
            Log.e(LOG_TAG, "Cannot Initialize Bluetooth Manager.");
            return false;
        }
        mGattServer = mManager.openGattServer(context, mGattServerCallback);
        if (mGattServer == null) {
            Log.e(LOG_TAG, "Cannot Create Gatt Server.");
            return false;
        }
        addDeviceService();
        return true;
    }

    private void addDeviceService() {
        // Remove previous Service
        BluetoothGattService previousService = mGattServer.getService(UUID.fromString(SERVICE_UUID));
        if (previousService != null) {
            mGattServer.removeService(previousService);
        }
        mMessageCharacteristic = new BluetoothGattCharacteristic(MESSAGE_CHARACTERISTIC_UUID,
                //Read-write characteristic, supports notifications
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);
        BluetoothGattDescriptor messageDesc = new BluetoothGattDescriptor(DESCRIPTOR_MESSAGE_UUID,
                BluetoothGattDescriptor.PERMISSION_WRITE | BluetoothGattDescriptor.PERMISSION_READ);
        mMessageCharacteristic.addDescriptor(messageDesc);

        BluetoothGattCharacteristic mReadCharacteristic = new BluetoothGattCharacteristic(READ_CHARACTERISTIC_UUID, BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_READ);

        BluetoothGattService service = new BluetoothGattService(UUID.fromString(SERVICE_UUID), BluetoothGattService.SERVICE_TYPE_PRIMARY);
        service.addCharacteristic(mMessageCharacteristic);
        service.addCharacteristic(mReadCharacteristic);
        mGattServer.addService(service);
    }

    private void startAdvertise() {
        AdvertiseSettings advSettings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .setConnectable(true)
                .build();
        AdvertiseData advData = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .addServiceUuid(BLE_UUID)
                .build();
        if (advSettings == null || advData == null || mBLEAdvertiser == null) {
            Log.e(LOG_TAG, "Cannot create AdvertiseSettings or AdvertiseData");
            return;
        }

        mBLEAdvertiser.startAdvertising(advSettings, advData, mAdvCallback);
    }

    public void sendMessage(String message) {
        if (mDevice == null) {
            Log.d(LOG_TAG, "mDevice is null.");
            return;
        }
        if (!TextUtils.isEmpty(message)) {
            byte[] data = message.getBytes();
            mMessageCharacteristic.setValue(data);
            mGattServer.notifyCharacteristicChanged(mDevice, mMessageCharacteristic, false);
        }
    }

    public void stopAdvertise() {
        if (mBLEAdvertiser != null && mAdvCallback != null) {
            mBLEAdvertiser.stopAdvertising(mAdvCallback);
        }
    }

    public interface Listener {
        void onConnected();
        void onDisconnected();
        void onMessageRead(String msg);
    }
}
