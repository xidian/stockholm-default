package com.stockholm.speech;

import com.facebook.stetho.Stetho;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.leakcanary.LeakCanary;
import com.stockholm.common.BaseApplication;
import com.stockholm.common.utils.FileUtil;
import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.speech.di.component.ApplicationComponent;
import com.stockholm.speech.di.component.DaggerApplicationComponent;
import com.stockholm.speech.di.module.ApplicationModule;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;


public class SpeechApplication extends BaseApplication {

    private ApplicationComponent applicationComponent;
    private String mSampleDirPath;

    @Override
    public void initializeThirdService() {
        StockholmLogger.init(this);
        FlowManager.init(new FlowConfig.Builder(this).build());
        Stetho.initializeWithDefaults(this);
        LeakCanary.install(this);
        Bugly.init(this, "4a11ae4c63", false);
        CrashReport.setIsDevelopmentDevice(this, BuildConfig.DEBUG);
        initialEnv();
    }

    @Override
    public void initializeInjector() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();
        applicationComponent.inject(this);
    }

    @Override
    public String initUMengAppKey() {
        return "59a8c84b1c5dd04872000300";
    }

    public ApplicationComponent getApplicationComponent() {
        return this.applicationComponent;
    }

    private void initialEnv() {
        if (mSampleDirPath == null) {
            mSampleDirPath = getExternalFilesDir(null).getPath();
        }
        FileUtil.copyFileFromAsset(this, FileConstant.SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/" + FileConstant.SPEECH_FEMALE_MODEL_NAME);
        FileUtil.copyFileFromAsset(this, FileConstant.TEXT_MODEL_NAME, mSampleDirPath + "/" + FileConstant.TEXT_MODEL_NAME);
        FileUtil.copyFileFromAsset(this, "english/" + FileConstant.ENGLISH_SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/"
                + FileConstant.ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        FileUtil.copyFileFromAsset(this, "english/" + FileConstant.ENGLISH_TEXT_MODEL_NAME, mSampleDirPath + "/"
                + FileConstant.ENGLISH_TEXT_MODEL_NAME);
    }

}