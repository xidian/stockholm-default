package com.stockholm.fota;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.adups.iot_libs.info.DeviceInfo;
import com.stockholm.api.rom.RomInfoReq;
import com.stockholm.api.rom.RomService;
import com.stockholm.common.Constant;
import com.stockholm.common.bus.RxEventBus;
import com.stockholm.common.utils.OsUtils;
import com.stockholm.common.utils.PreferenceFactory;
import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.fota.di.component.ApplicationComponent;
import com.stockholm.fota.di.component.DaggerReceiverComponent;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NetStateReceiver extends BroadcastReceiver {

    private static final String TAG = "FotaService-NetReceiver";

    @Inject
    RomService romService;

    @Inject
    PreferenceFactory preferenceFactory;

    @Inject
    RxEventBus eventBus;

    @Override
    public void onReceive(Context context, Intent intent) {
        ApplicationComponent component = ((FotaApplication) context.getApplicationContext()).getApplicationComponent();
        DaggerReceiverComponent.builder().applicationComponent(component).build().inject(this);
        updateRomVersion(context);
        remindOta();
    }

    private void updateRomVersion(Context context) {
        StockholmLogger.d(TAG, "version info: " + DeviceInfo.getInstance().toString() + DeviceInfo.getInstance().version);
        FotaPreference fotaPreference = preferenceFactory.create(FotaPreference.class);
        String launcherVersion = OsUtils.getAppVersionName(context, Constant.APP_PACKAGE_NAME_LAUNCHER);
        RomInfoReq.RomBean romBean = new RomInfoReq.RomBean();
        romBean.setDeviceType(DeviceInfo.getInstance().deviceType);
        romBean.setLauncherVersion(launcherVersion);
        romBean.setMid(DeviceInfo.getInstance().mid);
        romBean.setModels(DeviceInfo.getInstance().models);
        romBean.setOem(DeviceInfo.getInstance().oem);
        romBean.setPlatform(DeviceInfo.getInstance().platform);
        romBean.setVersion(DeviceInfo.getInstance().version);
        romBean.setVersionName(fotaPreference.getNewVersionName());
        RomInfoReq romInfoReq = new RomInfoReq();
        romInfoReq.setRom(romBean);
        romService.updateVersion(romInfoReq)
                .retry(5)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    if (resp.isSuccessful()) {
                        StockholmLogger.d(TAG, "update version info success");
                    }
                }, e -> StockholmLogger.e(TAG, e.toString()));
    }

    private void remindOta() {
        eventBus.post(new RemindOtaEvent());
    }
}