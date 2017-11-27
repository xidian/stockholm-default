package com.stockholm.fota;


import net.orange_box.storebox.annotations.method.KeyByString;

public interface FotaPreference {

    @KeyByString("fota_registered")
    void setFotaRegistered(boolean fotaRegistered);

    @KeyByString("fota_registered")
    boolean getFotaRegistered();

}