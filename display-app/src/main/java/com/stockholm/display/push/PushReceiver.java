package com.stockholm.display.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.stockholm.common.Constant;


public class PushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Constant.ACTION_PUSH_BROADCAST.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, PushMessageService.class);
            serviceIntent.putExtras(intent.getExtras());
            context.startService(serviceIntent);
        }
    }
}
