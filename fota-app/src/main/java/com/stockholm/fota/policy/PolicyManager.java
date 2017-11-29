package com.stockholm.fota.policy;

import com.adups.iot_libs.info.PolicyMapInfo;
import com.adups.iot_libs.info.VersionInfo;
import com.stockholm.common.utils.StockholmLogger;

public class PolicyManager {

    private static final String TAG = "PolicyManager";
    private static final String KEY_CHECK_CYCLE = "checkCycle";

    public PolicyManager() {
    }

    public int getCheckCycle() {
        PolicyMapInfo cycleInfo = null;
        try {
            cycleInfo = VersionInfo.getInstance().policyHashMap.get(KEY_CHECK_CYCLE);
            StockholmLogger.e(TAG, "cycleInfo: " + cycleInfo.toString());
        } catch (Exception e) {
            StockholmLogger.e(TAG, "get cycle info error: " + e.getMessage());
            return -1;
        }
        if (cycleInfo != null) {
            try {
                int cycle = Integer.parseInt(cycleInfo.key_value);
                StockholmLogger.e(TAG, "cycle: " + cycle);
                return cycle > 60 ? cycle : 60;
            } catch (NumberFormatException e) {
                StockholmLogger.e(TAG, "parse cycle error: " + e.getMessage());
                e.printStackTrace();
                return -2;
            }
        } else {
            return -1;
        }
    }

}
