package com.stockholm.display;


import net.orange_box.storebox.annotations.method.KeyByString;

public interface DisplayPreference {

    @KeyByString("enableAutoDisplay")
    void enableAutoDisplay(boolean enableAutoDisplay);

    @KeyByString("enableAutoDisplay")
    boolean isEnableAutoDisplay();

    @KeyByString("openAutoDisplay")
    void openAutoDisplay(boolean openAutoDisplay);

    @KeyByString("openAutoDisplay")
    boolean isOpenAutoDisplay();

    @KeyByString("media_playing")
    void mediaPlaying(boolean playing);

    @KeyByString("media_playing")
    boolean hasMediaPlaying();

}