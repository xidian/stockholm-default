package com.stockholm.fota.ota;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.adups.iot_libs.OtaAgentPolicy;
import com.adups.iot_libs.info.VersionInfo;
import com.adups.iot_libs.inter.ICheckVersionCallback;
import com.adups.iot_libs.inter.IDownloadListener;
import com.adups.iot_libs.inter.IRebootUpgradeCallBack;
import com.google.gson.Gson;
import com.stockholm.api.rom.RomService;
import com.stockholm.common.utils.PreferenceFactory;
import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.fota.FotaApplication;
import com.stockholm.fota.FotaPreference;
import com.stockholm.fota.di.component.ApplicationComponent;
import com.stockholm.fota.di.component.DaggerServiceComponent;

import javax.inject.Inject;


public class UpdateService extends IntentService {

    private static final String TAG = "UpdateService";

    @Inject
    PreferenceFactory preferenceFactory;

    @Inject
    RomService romService;

    @Inject
    Context context;

    public UpdateService() {
        super("UpdateService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationComponent component = ((FotaApplication) getApplication()).getApplicationComponent();
        DaggerServiceComponent.builder().applicationComponent(component).build().inject(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        checkUpdateAndDownload();
    }

    public void checkUpdateAndDownload() {
        FotaPreference fotaPreference = preferenceFactory.create(FotaPreference.class);
        OtaAgentPolicy.checkVersion(new ICheckVersionCallback() {
            @Override
            public void onCheckSuccess(VersionInfo versionInfo) {
                StockholmLogger.d(TAG, "检测版本成功！ " + new Gson().toJson(versionInfo));
                fotaPreference.setNewVersionName(versionInfo.versionName);
                download();
            }

            @Override
            public void onCheckFail(int status) {
                StockholmLogger.d(TAG, "check version error" + status);
            }
        });
    }

    public void download() {
        IDownloadListener iDownloadListener = new IDownloadListener() {
            @Override
            public void onPrepare() {
                StockholmLogger.d(TAG, "prepare for download");
            }

            @Override
            public void onDownloadProgress(long downSize, long totalSize) {
                StockholmLogger.d(TAG, "downloading :" + downSize + " / " + totalSize);
            }

            @Override
            public void onFailed(int error) {
                StockholmLogger.d(TAG, "download update error,code:" + error);
            }

            @Override
            public void onCompleted() {
                StockholmLogger.d(TAG, "download complete");
                rebootUpgrade();
            }

            @Override
            public void onCancel() {
                StockholmLogger.d(TAG, "download canceled");
            }
        };
        OtaAgentPolicy.download(iDownloadListener);
    }

    public void rebootUpgrade() {
        IRebootUpgradeCallBack iRebootUpgradeCallBack
                = (error, message) -> StockholmLogger.e(TAG, "enter recovery error,error code:" + error);
        OtaAgentPolicy.rebootUpgrade(iRebootUpgradeCallBack);
    }

}
