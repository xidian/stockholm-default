package com.stockholm.bind.wifi;

import com.google.gson.Gson;

public class WifiMessage {

    public static final int CMD_DEVICE_RESPONSE = 1000;

    public static final int CMD_MOBILE_SEND = 2000;

    private boolean isResponse;
    private int command;
    private String data;

    public WifiMessage(boolean isResponse, int command) {
        this.isResponse = isResponse;
        this.command = command;
    }

    public WifiMessage(boolean isResponse, int command, String data) {
        this.isResponse = isResponse;
        this.command = command;
        this.data = data;
    }

    public boolean isResponse() {
        return isResponse;
    }

    public void setResponse(boolean response) {
        isResponse = response;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public String getData() {
        return data == null ? " " : data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static WifiMessage get(String json) {
        return new Gson().fromJson(json, WifiMessage.class);
    }
}
