package com.stockholm.fota.log;


public class LogConfig {

    private long interval = 3600_000;
    private int backupDays = 15;

    public LogConfig(Builder builder) {
        this.interval = builder.interval;
        this.backupDays = builder.backupDays;
    }

    public long getInterval() {
        return interval;
    }

    public int getBackupDays() {
        return backupDays;
    }

    public static class Builder {

        private long interval;
        private int backupDays;

        public Builder interval(long interval) {
            this.interval = interval;
            return this;
        }

        public Builder backupDays(int backupDays) {
            this.backupDays = backupDays;
            return this;
        }

        public LogConfig build() {
            return new LogConfig(this);
        }
    }


}
