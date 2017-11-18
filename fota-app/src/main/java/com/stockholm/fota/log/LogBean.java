package com.stockholm.fota.log;


public class LogBean {

    private int logFileResult;
    private String fileUploadKey;
    private String fileUploadToken;

    public LogBean(int logFileResult, String fileUploadKey, String fileUploadToken) {
        this.logFileResult = logFileResult;
        this.fileUploadKey = fileUploadKey;
        this.fileUploadToken = fileUploadToken;
    }

    public int getLogFileResult() {
        return logFileResult;
    }

    public String getFileUploadKey() {
        return fileUploadKey;
    }

    public String getFileUploadToken() {
        return fileUploadToken;
    }
}
