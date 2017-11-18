package com.stockholm.speech;

import android.content.Intent;

import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.common.view.ReleaseBaseActivity;

public class HomeActivity extends ReleaseBaseActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();

    @Override
    protected void initInject() {

    }

    @Override
    protected int getLayoutResource() {
        return 0;
    }

    @Override
    protected void init() {
        StockholmLogger.d(TAG, "init: speech has been opened");
        Intent ttsService = new Intent(this, TTSService.class);
        startService(ttsService);
        finish();
    }

}