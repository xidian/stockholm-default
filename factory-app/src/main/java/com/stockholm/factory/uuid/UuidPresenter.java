package com.stockholm.factory.uuid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.stockholm.common.view.BasePresenter;

import javax.inject.Inject;

public class UuidPresenter extends BasePresenter<UuidView> {

    private static final String ACTION_GET_SN = "com.stockholm.action.serialnumber.request";
    private static final String ACTION_RECEIVE_SN = "com.stockholm.action.serialnumber.response";
    private static final String KEY_SN = "com.stockholm.key.serialnumber";

    private SnReceiver snReceiver;

    @Inject
    public UuidPresenter() {

    }

    public void getSn(Context context) {
        Intent intent = new Intent(ACTION_GET_SN);
        context.sendBroadcast(intent);
    }

    public void registerSnReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_RECEIVE_SN);
        snReceiver = new SnReceiver();
        context.registerReceiver(snReceiver, intentFilter);
    }

    public void unRegisterReceiver(Context context) {
        if (null != snReceiver) {
            context.unregisterReceiver(snReceiver);
        }
    }

    class SnReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            getMvpView().onSnGot(intent.getStringExtra(KEY_SN));
        }
    }
}
