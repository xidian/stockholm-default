package com.stockholm.fota.push;


import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.stockholm.common.Constant;
import com.stockholm.common.JPushOrder;
import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.fota.log.MLog;

public class PushMessageService extends IntentService {

    private static final String TAG = "PushMessageService";

    public PushMessageService() {
        super("FotaPushMessageService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) return;

        int order = intent.getExtras().getInt(Constant.JPUSH_ORDER);
        String addition = intent.getStringExtra(Constant.JPUSH_ADDITION);
        StockholmLogger.d(TAG, "push order: " + order + "push addition: " + addition);
        if (order == JPushOrder.UPLOAD_LOG) {
            MLog.uploadLog(this, addition);
        }
    }
}
