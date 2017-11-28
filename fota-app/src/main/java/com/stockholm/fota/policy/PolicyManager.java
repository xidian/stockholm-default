package com.stockholm.fota.policy;

import com.adups.iot_libs.info.PolicyMapInfo;
import com.adups.iot_libs.info.VersionInfo;

public class PolicyManager {

    private static final String KEY_CHECK_CYCLE = "checkCycle";

    public PolicyManager() {
    }

    public int getCheckCycle() {
        PolicyMapInfo cycleInfo = null;
        try {
            cycleInfo = VersionInfo.getInstance().policyHashMap.get(KEY_CHECK_CYCLE);
        } catch (Exception e) {
            return -1;
        }
        if (cycleInfo != null) {
            try {
                int cycle = Integer.parseInt(cycleInfo.key_value);
                return cycle > 60 ? cycle : 60;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return -2;
            }
        } else {
            return -1;
        }
    }

}
