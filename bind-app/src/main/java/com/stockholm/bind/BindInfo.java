package com.stockholm.bind;

import com.google.gson.Gson;

public class BindInfo {

    private String n; //name
    private String p; //password
    private String t; //token
    private boolean b; //bind device

    public BindInfo(String wifiName, String wifiPassword, String accessToken, boolean bindDevice) {
        this.n = wifiName;
        this.p = wifiPassword;
        this.t = accessToken;
        this.b = bindDevice;
    }

    public String getN() {
        return n;
    }

    public String getP() {
        return p;
    }

    public String getT() {
        return t;
    }

    public boolean isB() {
        return b;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static BindInfo toBindInfo(String s) {
        return new Gson().fromJson(s, BindInfo.class);
    }

}
