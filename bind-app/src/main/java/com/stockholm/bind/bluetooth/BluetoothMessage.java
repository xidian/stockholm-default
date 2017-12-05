package com.stockholm.bind.bluetooth;


import com.google.gson.Gson;

public class BluetoothMessage {

    public static final int CMD_SEND_BIND = 0;
    public static final int CMD_CONNECT_FAIL = 1;
    public static final int CMD_CONNECT_OK = 2;
    public static final int CMD_UUID = 3;

    private int cmd;
    private String content;

    public BluetoothMessage(int cmd, String content) {
        this.cmd = cmd;
        this.content = content;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static BluetoothMessage toMessage(String s) {
        return new Gson().fromJson(s, BluetoothMessage.class);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
