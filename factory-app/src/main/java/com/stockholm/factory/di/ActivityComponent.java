package com.stockholm.factory.di;


import com.stockholm.factory.HomeActivity;
import com.stockholm.factory.OverActivity;
import com.stockholm.factory.bluetooth.BluetoothActivity;
import com.stockholm.factory.line.LineActivity;
import com.stockholm.factory.mic.MicActivity;
import com.stockholm.factory.screen.ScreenActivity;
import com.stockholm.factory.sensor.SensorActivity;
import com.stockholm.factory.speaker.SpeakerActivity;
import com.stockholm.factory.uuid.UuidActivity;
import com.stockholm.factory.wifi.WifiActivity;

import dagger.Component;

@Scopes.Activity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(HomeActivity activity);

    void inject(OverActivity activity);

    void inject(LineActivity activity);

    void inject(ScreenActivity activity);

    void inject(SensorActivity activity);

    void inject(WifiActivity activity);

    void inject(BluetoothActivity activity);

    void inject(SpeakerActivity activity);

    void inject(MicActivity activity);

    void inject(UuidActivity activity);
}