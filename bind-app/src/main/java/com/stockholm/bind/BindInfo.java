package com.stockholm.bind;

import com.google.gson.Gson;

public class BindInfo {

    private String wifiName;
    private String wifiPassword;
    private String accessToken;
    private boolean bindDevice;

    public BindInfo(String wifiName, String wifiPassword, String accessToken, boolean bindDevice) {
        this.wifiName = wifiName;
        this.wifiPassword = wifiPassword;
        this.accessToken = accessToken;
        this.bindDevice = bindDevice;
    }

    public String getWifiName() {
        return wifiName;
    }

    public String getWifiPassword() {
        return wifiPassword;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public boolean isBindDevice() {
        return bindDevice;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static BindInfo toBindInfo(String s) {
        return new Gson().fromJson(s, BindInfo.class);
    }

}
