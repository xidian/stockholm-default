package com.stockholm.bind;

public final class Constant {

    /**
     * 默认的Wifi SSID
     */
    public static final String DEFAULT_SSID = "JUMMO";

    /**
     * 默认的Wifi类型
     */
    public static final int DEFAULT_TYPE = 1;

    /**
     * TCP通信服务 默认端口
     */
    public static final int DEFAULT_SERVER_TCP_PORT = 7890;


    /**
     * 设备端返回给手机端的命令 收到传输过来的绑定信息（包括Wi-Fi名字、密码、token）
     */
    public static final int RESPONSE_RECEIVER_INFO = 1000;

    public static final int DEVICE_SEND_WIFI = 2000;

    public static final int CONNECT_TYPE_BLE_ANDROID = 1;
    public static final int CONNECT_TYPE_BLE_IOS = 2;
    public static final int CONNECT_TYPE_AP = 3;

    /**
     * 设备端发送给iOS设备的消息
     */
    public static final String MSG_IOS_OK = "0";
    public static final String MSG_IOS_FAIL = "1";
    public static final String MSG_IOS_UUID = "UUID@";

    private Constant() {

    }
}
