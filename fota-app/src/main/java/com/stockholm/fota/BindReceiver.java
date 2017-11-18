package com.stockholm.fota;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.adups.iot_libs.info.DeviceInfo;
import com.stockholm.api.rom.RomInfoReq;
import com.stockholm.api.rom.RomService;
import com.stockholm.common.Constant;
import com.stockholm.common.utils.OsUtils;
import com.stockholm.common.utils.PreferenceFactory;
import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.fota.di.component.ApplicationComponent;
import com.stockholm.fota.di.component.DaggerReceiverComponent;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BindReceiver extends BroadcastReceiver {

    @Inject
    RomService romService;

    @Inject
    PreferenceFactory preferenceFactory;

    private FotaPreference fotaPreference;

    @Override
    public void onReceive(Context context, Intent intent) {
        ApplicationComponent component = ((FotaApplication) context.getApplicationContext()).getApplicationComponent();
        DaggerReceiverComponent.builder().applicationComponent(component).build().inject(this);
        fotaPreference = preferenceFactory.create(FotaPreference.class);
        updateRomVersion(context);
    }

    private void updateRomVersion(Context context) {
        StockholmLogger.d("BindReceiver", "version info: " + DeviceInfo.getInstance().toString() + DeviceInfo.getInstance().version);
        String launcherVersion = OsUtils.getAppVersionName(context, Constant.APP_PACKAGE_NAME_LAUNCHER);
        RomInfoReq.RomBean romBean = new RomInfoReq.RomBean();
        romBean.setDeviceType(DeviceInfo.getInstance().deviceType);
        romBean.setLauncherVersion(launcherVersion);
        romBean.setMid(DeviceInfo.getInstance().mid);
        romBean.setModels(DeviceInfo.getInstance().models);
        romBean.setOem(DeviceInfo.getInstance().oem);
        romBean.setPlatform(DeviceInfo.getInstance().platform);
        romBean.setVersion(DeviceInfo.getInstance().version);
        if (null != fotaPreference.getNewVersionName()) {
            romBean.setVersionName(fotaPreference.getNewVersionName());
        }
        RomInfoReq romInfoReq = new RomInfoReq();
        romInfoReq.setRom(romBean);
        romService.updateVersion(romInfoReq)
                .retry(5)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    if (resp.isSuccessful()) {
                        StockholmLogger.d("BindReceiver", "update version info success");
                    }
                }, e -> StockholmLogger.e("BindReceiver", e.toString()));
    }
}
