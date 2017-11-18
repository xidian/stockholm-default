package com.stockholm.fota;


import net.orange_box.storebox.annotations.method.KeyByString;

public interface FotaPreference {

    @KeyByString("new_version_name")
    void setNewVersionName(String newVersionName);

    @KeyByString("new_version_name")
    String getNewVersionName();

    @KeyByString("fota_registered")
    void setFotaRegistered(boolean fotaRegistered);

    @KeyByString("fota_registered")
    boolean getFotaRegistered();

}