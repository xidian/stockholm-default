package com.stockholm.factory.sensor;


import com.stockholm.common.view.MvpView;

public interface SensorView extends MvpView {
    void onReadLightSensorValue(float light);
    void onReadTemperatureSensorValue(float temperature);
    void onReadHumitureSensorValue(float humiture);
    void onTestFinish(boolean pass);
    void onCountdown(long millisUntilFinished);
}
