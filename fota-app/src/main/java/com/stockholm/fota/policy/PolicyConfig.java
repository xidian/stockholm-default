package com.stockholm.fota.policy;

public final class PolicyConfig {
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

}
