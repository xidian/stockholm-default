package com.stockholm.fota.policy;

public class PolicyConfig {
    public boolean checkCycle = true;

    private static PolicyConfig instance;

    private PolicyConfig() {
    }

    public static PolicyConfig getInstance() {
        if (instance == null) {
            synchronized (PolicyConfig.class) {
                if (instance == null) {
                    instance = new PolicyConfig();
                }
            }
        }
        return instance;
    }

    /**
     * 配置循环检测版本周期
     *
     * @param value
     * @return PolicyConfig
     */
    public void requestCheckCycle(boolean value) {
        checkCycle = value;
    }
}
