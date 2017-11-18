package com.stockholm.fota.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.stockholm.common.Constant;
import com.stockholm.common.utils.StockholmLogger;

public class PushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        StockholmLogger.i("PushReceiver", intent.getAction());
        if (Constant.ACTION_PUSH_BROADCAST.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, PushMessageService.class);
            serviceIntent.putExtras(intent.getExtras());
            context.startService(serviceIntent);
        }
    }

}