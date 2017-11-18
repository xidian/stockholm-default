package com.stockholm.fota.log;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.stockholm.api.base.BaseResponse;
import com.stockholm.api.log.LogResp;
import com.stockholm.api.log.LogService;
import com.stockholm.api.log.LogStatusReq;
import com.stockholm.common.api.BaseUrl;
import com.stockholm.common.api.Env;
import com.stockholm.common.api.EnvProvider;
import com.stockholm.common.api.ServiceFactory;
import com.stockholm.common.utils.PreferenceFactory;
import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.common.utils.ZipUtil;
import com.stockholm.fota.BuildConfig;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public final class MLog {

    public static final String ACTION_UPLOAD = "action.stockholm.ACTION_UPLOAD";
    public static final String KEY_UPLOAD_KEY = "KEY_UPLOAD_KEY";

    static LogConfig config;

    private static final String TAG = "MLog";
    private static final String FORMAT = "yyyy-MM-dd";
    private static final int MAX_LEN = 10000;

    private MLog() {

    }

    public static void init(Context context) {
        LogConfig config = new LogConfig.Builder()
                .interval(6000_000)
                .backupDays(15)
                .build();
        init(context, config);
    }

    public static void init(Context context, LogConfig config) {
        MLog.config = config;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long startTime = SystemClock.elapsedRealtime() + config.getInterval();
        Intent intent = new Intent(context, MLogService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, startTime, config.getInterval(), pendingIntent);
    }

    public static void writeLogCat(Context context) {
        try {
            Process process = Runtime.getRuntime().exec("logcat -d -v time");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder log = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
                log.append("\n");
            }
            new ProcessBuilder().command("logcat", "-c").redirectErrorStream(true).start();
            File file = logFile(context);
            RandomAccessFile randomFile = new RandomAccessFile(file.getAbsolutePath(), "rw");
            while (log.length() > MAX_LEN) {
                long fileLength = randomFile.length();
                randomFile.seek(fileLength);
                String content = log.substring(0, MAX_LEN);
                log = log.delete(0, MAX_LEN);
                randomFile.writeUTF(content);
            }
            long fileLength = randomFile.length();
            randomFile.seek(fileLength);
            randomFile.writeUTF(log.toString());
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
            StockholmLogger.e(TAG, "write logcat IOE.", e);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            StockholmLogger.e(TAG, "write logcat OOM.", e);
        } catch (Exception e) {
            e.printStackTrace();
            StockholmLogger.e(TAG, "Exception", e);
        }
    }

    private static File logFileDir(Context context) {
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + File.separator + "Meow"
                + File.separator + context.getApplicationContext().getPackageName());
        Log.d("MLog", "log dir:" + dir.getPath());
        if (!dir.exists()) {
            boolean mkdirs = dir.mkdirs();
            Log.d("MLog", "mkdirs:" + mkdirs);
        }
        return dir;
    }

    private static File logFile(Context context) {
        File dir = logFileDir(context);
        return new File(dir, LocalDate.now().toString(FORMAT) + ".log");
    }

    public static void processBackup(Context context) {
        File dir = logFileDir(context);
        File[] files = dir.listFiles();
        if (files != null && files.length > config.getBackupDays()) {
            Arrays.sort(files, (f1, f2) -> f1.getName().compareTo(f2.getName()));
            StockholmLogger.d(TAG, "delete file:" + files[0].getName());
            files[0].delete();
        }
    }

    public static void uploadLog(Context context, String addition) {
        Intent intent = new Intent(context, MLogService.class);
        intent.setAction(ACTION_UPLOAD);
        intent.putExtra(KEY_UPLOAD_KEY, addition);
        context.startService(intent);
    }

    private static File getZipFile(Context context, String key) {
        File dir = logFileDir(context);
        String name = key.replaceAll("/", "");
        return new File(dir, name + ".zip");
    }

    public static int zipLogFile(Context context, LocalDate start, LocalDate end, String key) {
        File dir = logFileDir(context);
        if (!dir.exists()) {
            StockholmLogger.w(TAG, "not log file dir.");
            return LogService.STATUS_NO_LOG;
        }

        File[] files = dir.listFiles();
        if (files == null) {
            StockholmLogger.w(TAG, "log file is null.");
            return LogService.STATUS_NO_LOG;
        }

        List<String> logFiles = new ArrayList<>();
        for (File file : files) {
            String name = file.getName();
            if (!name.endsWith(".log")) continue;
            String tmp = name.substring(0, name .lastIndexOf(".log"));
            LocalDate nameDate = LocalDate.parse(tmp, DateTimeFormat.forPattern(FORMAT));
            if ((nameDate.isAfter(start) && nameDate.isBefore(end))
                    || nameDate.isEqual(start) || nameDate.isEqual(end)) {
                logFiles.add(file.getAbsolutePath());
            }
        }
        if (logFiles.isEmpty()) {
            StockholmLogger.w(TAG, "has no log match start & end time.");
            return LogService.STATUS_NO_LOG_DURATION;
        }
        String[] logs = new String[logFiles.size()];
        try {
            ZipUtil.zip(logFiles.toArray(logs), getZipFile(context, key).getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return LogService.STATUS_OK;
    }

    /**
     * @param context
     * @param key     query upload info key from push
     */
    static void upload(Context context, int key) {
        PreferenceFactory factory = new PreferenceFactory(context);
        EnvProvider envProvider = new EnvProvider(context);
        Env env;
        switch (BuildConfig.ENV_VALUE) {
            case 1:
                env = Env.DEV;
                break;
            case 2:
                env = Env.STG;
                break;
            case 3:
                env = Env.PROD;
                break;
            default:
                env = Env.PROD;
        }
        BaseUrl baseUrl = new BaseUrl(envProvider.get(env).getApiUrl());
        ServiceFactory serviceFactory = new ServiceFactory(baseUrl);
        LogService logService = serviceFactory.create(LogService.class);
        logService.getUploadToken(key)
                .map(response -> {
                    if (response.body() != null && response.body().isSuccess()) {
                        LogResp logResp = response.body().getData();
                        LocalDate start = LocalDate.parse(logResp.getStartDate(), DateTimeFormat.forPattern("yyyy-MM-dd"));
                        LocalDate end = LocalDate.parse(logResp.getEndDate(), DateTimeFormat.forPattern("yyyy-MM-dd"));
                        String key1 = logResp.getKey();
                        int ret = zipLogFile(context, start, end, key1);
                        StockholmLogger.d(TAG, "get log file ret: " + ret);
                        return new LogBean(ret, logResp.getKey(), logResp.getUptoken());
                    }
                    return null;
                })
                .flatMap(new Func1<LogBean, Observable<Response<BaseResponse>>>() {
                    @Override
                    public Observable<Response<BaseResponse>> call(LogBean logBean) {
                        if (logBean != null) {
                            int ret = logBean.getLogFileResult();
                            LogStatusReq req = new LogStatusReq();
                            LogStatusReq.LogBean bean = new LogStatusReq.LogBean();
                            if (ret == LogService.STATUS_OK) {
                                File logZip = getZipFile(context, logBean.getFileUploadKey());
                                UploadManager uploadManager = new UploadManager();
                                UploadOptions options = new UploadOptions(null, null, false, null, null);
                                ResponseInfo info = uploadManager.syncPut(logZip.getAbsolutePath(), logBean.getFileUploadKey(), logBean.getFileUploadToken(), options);
                                StockholmLogger.d(TAG, "upload qiniu result:\n" + info.toString());
                                boolean delete = logZip.delete();
                                StockholmLogger.d(TAG, "delete zip file, " + delete);
                                if (info.isOK()) {
                                    bean.setStatus(LogService.STATUS_OK);
                                    req.setLog(bean);
                                    return logService.updateUploadStatus(key, req);
                                } else {
                                    bean.setStatus(LogService.STATUS_UPLOAD_FAIL);
                                    req.setLog(bean);
                                    return logService.updateUploadStatus(key, req);
                                }
                            } else {
                                bean.setStatus(ret);
                                req.setLog(bean);
                                return logService.updateUploadStatus(key, req);
                            }
                        }
                        return null; // not well
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(resp -> {
                    if (resp != null) {
                        StockholmLogger.d(TAG, "upload success.");
                    }
                }, throwable -> {
                    StockholmLogger.e(TAG, "upload error.", throwable);
                    throwable.printStackTrace();
                });
    }

}
