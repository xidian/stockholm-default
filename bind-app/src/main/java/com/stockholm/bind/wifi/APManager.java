package com.stockholm.bind.wifi;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.stockholm.bind.Constant;
import com.stockholm.bind.TAG;
import com.stockholm.common.utils.StockholmLogger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class APManager {

    private WifiManager wifiManager;

    @Inject
    public APManager(Context context) {
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public boolean createAP() {
        boolean disconnect = disconnectCurrentNetwork();
        StockholmLogger.d(TAG.AP, "disconnect current network:" + disconnect);
        closeWifi();
        StockholmLogger.d(TAG.AP, "close wifi");
        WifiConfiguration wifiConfiguration = createWifiConfiguration();
        int count = 10;
        while (!isApOpen() && count > 0) {
            Log.d(TAG.AP, "count:" + count);
            setApEnable(true, wifiConfiguration);
            try {
                Thread.sleep(500);
                count--;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        boolean result = isApOpen();
        StockholmLogger.d(TAG.AP, "ap open result:" + result);
        return result;
    }

    private boolean setApEnable(boolean enable, WifiConfiguration configuration) {
        try {
            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            return (Boolean) method.invoke(wifiManager, configuration, enable);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void disableAp() {
        try {
            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            boolean result = (boolean) method.invoke(wifiManager, null, false);
            StockholmLogger.d(TAG.AP, "disable ap result:" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isWifiEnable() {
        return wifiManager != null && wifiManager.isWifiEnabled();
    }

    public boolean disconnectCurrentNetwork() {
        if (isWifiEnable()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                int netId = wifiInfo.getNetworkId();
                wifiManager.removeNetwork(netId);
                return wifiManager.saveConfiguration();
            }
        }
        return false;
    }

    public boolean openWifi() {
        return wifiManager.setWifiEnabled(true);
    }

    private WifiConfiguration createWifiConfiguration() {
        WifiConfiguration netConfig = new WifiConfiguration();
        netConfig.SSID = Constant.DEFAULT_SSID;
        netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        return netConfig;
    }

    private void closeWifi() {
        while (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isApOpen() {
        try {
            Method method = wifiManager.getClass().getDeclaredMethod("getWifiApState");
            int state = (int) method.invoke(wifiManager);
            Field field = wifiManager.getClass().getDeclaredField("WIFI_AP_STATE_ENABLED");
            int value = (int) field.get(wifiManager);
            if (state == value) {
                return true;
            } else {
                return false;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
