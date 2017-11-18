package com.stockholm.factory;


import android.content.Context;

import org.joda.time.LocalDateTime;

import java.io.File;
import java.io.FileOutputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LogUtils {

    public static final String LINE_CONTROL_UP  = "line_control_up";
    public static final String LINE_CONTROL_OK  = "line_control_ok";
    public static final String LINE_CONTROL_DOWN  = "line_control_down";
    public static final String LINE_CONTROL  = "line_control";
    public static final String SCREEN = "screen";
    public static final String SENSOR_LIGHT = "sensor_light";
    public static final String SENSOR_TEMPERATURE = "sensor_temperature";
    public static final String SENSOR_HUMITURE = "sensor_humiture";
    public static final String WIFI = "Wi-Fi";
    public static final String BLUETOOTH = "bluetooth";
    public static final String SOUND = "sound";
    public static final String MIC = "mic";

    public static final String NONE = "none";
    public static final String TESTED = "tested";
    public static final String PASS = "pass";
    public static final String FAIL = "fail";
    public static final String CLICK = "click";
    public static final String UUID = "uuid";

    private static final String TAG = "LogUtils";

    private Context context;
    private File file;
    private StringBuilder sb = new StringBuilder();

    @Inject
    public LogUtils() {

    }

    public void init(Context context) {
        this.context = context;
//        file = logFileDir();
//        String s = initTestResult();
//        writeFile(file, s);
    }

    private String initTestResult() {
        StringBuilder sb = new StringBuilder();
        sb.append(LINE_CONTROL_UP).append("=").append(NONE).append("\n");
        sb.append(LINE_CONTROL_OK).append("=").append(NONE).append("\n");
        sb.append(LINE_CONTROL_DOWN).append("=").append(NONE).append("\n");
        sb.append(LINE_CONTROL).append("=").append(NONE).append("\n");
        sb.append(SCREEN).append("=").append(NONE).append("\n");
        sb.append(SENSOR_LIGHT).append("=").append(NONE).append("\n");
        sb.append(SENSOR_TEMPERATURE).append("=").append(NONE).append("\n");
        sb.append(SENSOR_HUMITURE).append("=").append(NONE).append("\n");
        sb.append(WIFI).append("=").append(NONE).append("\n");
        sb.append(BLUETOOTH).append("=").append(NONE).append("\n");
        sb.append(SOUND).append("=").append(NONE).append("\n");
        return sb.toString();
    }

    public void write(String testItem, String result) {
        sb.append(LocalDateTime.now()).append("\t").append(testItem).append("=").append(result).append("\n");
    }

    private void writeFile(File file, String log) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(log.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeFile() {
        File file = logFileDir();
        if (file != null) writeFile(file, sb.toString());
    }

    //确定目录不被删除／文件以时间命名
    private File logFileDir() {
        String filePath = "/data/data/com.stockholm.factory/files/" + LocalDateTime.now() + ".txt";
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

}
