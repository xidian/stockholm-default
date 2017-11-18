package com.stockholm.fota.log;


import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.stockholm.common.utils.StockholmLogger;

import org.joda.time.LocalDateTime;
import org.json.JSONException;
import org.json.JSONObject;

public class MLogService extends IntentService {

    private static final String TAG = "MLogService";

    public MLogService() {
        super("MLogService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        StockholmLogger.d("MLogService", "onHandleIntent:" + LocalDateTime.now());
        if (intent != null) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action) && action.equals(MLog.ACTION_UPLOAD)) {
                StockholmLogger.d(TAG, "server action upload log.");
                String addition = intent.getStringExtra(MLog.KEY_UPLOAD_KEY);
                try {
                    JSONObject object = new JSONObject(addition);
                    int key = object.getInt("id");
                    MLog.writeLogCat(this);
                    MLog.upload(this, key);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                StockholmLogger.d(TAG, "timing action upload log.");
                MLog.writeLogCat(this);
                MLog.processBackup(this);
            }
        }
    }
}
