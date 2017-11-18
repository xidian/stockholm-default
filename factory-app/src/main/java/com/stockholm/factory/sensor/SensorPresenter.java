package com.stockholm.factory.sensor;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;

import com.stockholm.common.utils.WeakHandler;
import com.stockholm.common.view.BasePresenter;
import com.stockholm.factory.LogUtils;

import javax.inject.Inject;

public class SensorPresenter extends BasePresenter<SensorView> {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Sensor temperatureSensor;
    private Sensor humitureSensor;

    private LogUtils logUtils;
    private WeakHandler handler;
    private CountDownTimer timer;

    private float lightValue = 0;
    private float temperatureValue = 0;
    private float humitureValue = 0;

    private SensorEventListener lightListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            if (values != null && values.length > 0) {
                lightValue += values[0];
                getMvpView().onReadLightSensorValue(values[0]);
            } else {
                getMvpView().onReadLightSensorValue(0);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private SensorEventListener temperatureListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            if (values != null && values.length > 0) {
                temperatureValue += values[0];
                getMvpView().onReadTemperatureSensorValue(values[0]);
            } else {
                getMvpView().onReadTemperatureSensorValue(0);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private SensorEventListener humitureListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            if (values != null && values.length > 0) {
                humitureValue += values[0];
                getMvpView().onReadHumitureSensorValue(values[0]);
            } else {
                getMvpView().onReadHumitureSensorValue(0);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Inject
    public SensorPresenter(Context context,
                           LogUtils logUtils,
                           WeakHandler weakHandler) {
        this.logUtils = logUtils;
        this.handler = weakHandler;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
        humitureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
    }

    public void registerSensor() {
        countdown();
        sensorManager.registerListener(lightListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(temperatureListener, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(humitureListener, humitureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        handler.postDelayed(() -> {
            if ((int) lightValue <= 0) {
                logUtils.write(LogUtils.SENSOR_LIGHT, LogUtils.FAIL);
            } else {
                logUtils.write(LogUtils.SENSOR_LIGHT, LogUtils.PASS);
            }
            //零下温度会有问题
            if ((int) temperatureValue <= 0) {
                logUtils.write(LogUtils.SENSOR_TEMPERATURE, LogUtils.FAIL);
            } else {
                logUtils.write(LogUtils.SENSOR_TEMPERATURE, LogUtils.PASS);
            }
            if ((int) humitureValue <= 0) {
                logUtils.write(LogUtils.SENSOR_HUMITURE, LogUtils.FAIL);
            } else {
                logUtils.write(LogUtils.SENSOR_HUMITURE, LogUtils.PASS);
            }
            boolean pass = lightValue > 0 && temperatureValue > 0 && humitureValue > 0;
            getMvpView().onTestFinish(pass);
        }, 3000);
    }

    private void unregisterSensor() {
        sensorManager.unregisterListener(lightListener);
        sensorManager.unregisterListener(temperatureListener);
        sensorManager.unregisterListener(humitureListener);
    }

    private void countdown() {
        timer = new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                getMvpView().onCountdown(millisUntilFinished);
            }

            @Override
            public void onFinish() {

            }
        };
        timer.start();
    }

    void release() {
        unregisterSensor();
        if (timer != null) {
            timer.cancel();
        }
    }

}
