package com.stockholm.bind.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.stockholm.common.Constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CommonUtils {

    private CommonUtils() {

    }

    public static List<String> getInstalledPackageNames(Context context) {
        List<String> packageNames = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);

        for (PackageInfo packageInfo : packageInfoList) {
            if (packageInfo.requestedPermissions != null && packageInfo.applicationInfo != null) {
                List<String> permissionList = Arrays.asList(packageInfo.requestedPermissions);
                if (permissionList.contains(Constant.OPEN_IN_LAUNCHER_PERMISSION)) {
                    packageNames.add(packageInfo.packageName);
                }
            }
        }

        return packageNames;
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiNetworkInfo.isConnected();
    }
}
