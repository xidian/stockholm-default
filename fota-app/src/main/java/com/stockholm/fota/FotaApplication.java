package com.stockholm.fota;

import com.adups.iot_libs.MqttAgentPolicy;
import com.adups.iot_libs.OtaAgentPolicy;
import com.adups.iot_libs.inter.IRegisterListener;
import com.adups.iot_libs.security.FotaException;
import com.facebook.stetho.Stetho;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.leakcanary.LeakCanary;
import com.stockholm.common.BaseApplication;
import com.stockholm.common.utils.DeviceUUIDFactory;
import com.stockholm.common.utils.OsUtils;
import com.stockholm.common.utils.PreferenceFactory;
import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.fota.di.component.ApplicationComponent;
import com.stockholm.fota.di.component.DaggerApplicationComponent;
import com.stockholm.fota.di.module.ApplicationModule;
import com.stockholm.fota.log.MLog;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

import javax.inject.Inject;


public class FotaApplication extends BaseApplication {

    private static final String FOTA_UPDATE_PATH = "/cache/update.zip";

    @Inject
    PreferenceFactory preferenceFactory;

    private ApplicationComponent applicationComponent;

    @Override
    public void initializeThirdService() {
        StockholmLogger.init(this);
        FlowManager.init(new FlowConfig.Builder(this).build());
        Stetho.initializeWithDefaults(this);
        LeakCanary.install(this);
        Bugly.init(this, "4a11ae4c63", false);
        CrashReport.setIsDevelopmentDevice(this, BuildConfig.DEBUG);
        initFota();
        MLog.init(this);
    }

    @Override
    public void initializeInjector() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();
        applicationComponent.inject(this);
    }

    public ApplicationComponent getApplicationComponent() {
        return this.applicationComponent;
    }

    private void initFota() {
        OtaAgentPolicy.showTrace(true);
        OtaAgentPolicy.setUpdatePath(FOTA_UPDATE_PATH);
        DeviceUUIDFactory deviceUUIDFactory = new DeviceUUIDFactory();
        try {
            OtaAgentPolicy.initFota(this.getApplicationContext());
            OtaAgentPolicy.setDeviceInfo(deviceUUIDFactory.getDeviceId());
            fotaRegister();
        } catch (FotaException e) {
            e.printStackTrace();
            StockholmLogger.e("FotaApplication", "init fota error" + e.getMessage());
        }
    }

    private void fotaRegister() {
        if (!OsUtils.isNetworkConnected(this)) {
            StockholmLogger.d("FotaApplication", "start ota service but no network");
            return;
        }
        FotaPreference fotaPreference = preferenceFactory.create(FotaPreference.class);
        OtaAgentPolicy.register(new IRegisterListener() {
            @Override
            public void onSuccess() {
                StockholmLogger.d("FotaApplication", "注册设备成功");
                fotaPreference.setFotaRegistered(true);
            }

            @Override
            public void onFailed(int i) {
                StockholmLogger.d("FotaApplication", "注册设备失败");
                fotaPreference.setFotaRegistered(false);
            }
        });
        if (!MqttAgentPolicy.isConnected()) {
            MqttAgentPolicy.connect();
        }
    }
}