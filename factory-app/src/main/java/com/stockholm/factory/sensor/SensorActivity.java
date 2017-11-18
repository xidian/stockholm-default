package com.stockholm.factory.sensor;


import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.stockholm.common.view.ReleaseBaseActivity;
import com.stockholm.factory.FactoryApplication;
import com.stockholm.factory.PathUtils;
import com.stockholm.factory.R;
import com.stockholm.factory.Tips;
import com.stockholm.factory.di.ApplicationComponent;
import com.stockholm.factory.di.DaggerActivityComponent;

import javax.inject.Inject;

import butterknife.BindView;

public class SensorActivity extends ReleaseBaseActivity implements SensorView {

    @BindView(R.id.tv_sensor_light)
    TextView tvLight;
    @BindView(R.id.tv_sensor_temperature)
    TextView tvTemperature;
    @BindView(R.id.tv_sensor_humiture)
    TextView tvHumiture;
    @BindView(R.id.tv_countdown)
    TextView tvCountdown;

    @Inject
    SensorPresenter presenter;
    @Inject
    PathUtils pathUtils;

    @Override
    protected void initInject() {
        ApplicationComponent component = ((FactoryApplication) getApplication()).getApplicationComponent();
        DaggerActivityComponent.builder().applicationComponent(component).build().inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_sensor;
    }

    @Override
    protected void init() {
        presenter.attachView(this);
        presenter.registerSensor();

        tvLight.setText(getString(R.string.sensor_light_value, 0f));
        tvTemperature.setText(getString(R.string.sensor_temperature_value, 0f));
        tvHumiture.setText(getString(R.string.sensor_humiture_value, 0f));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.release();
    }

    @Override
    public void onReadLightSensorValue(float light) {
        tvLight.setText(getString(R.string.sensor_light_value, light));
    }

    @Override
    public void onReadTemperatureSensorValue(float temperature) {
        tvTemperature.setText(getString(R.string.sensor_temperature_value, temperature));
    }

    @Override
    public void onReadHumitureSensorValue(float humiture) {
        tvHumiture.setText(getString(R.string.sensor_humiture_value, humiture));
    }

    @Override
    public void onLineShortDrag() {
        //prevent line drag
    }

    @Override
    public void onCountdown(long millisUntilFinished) {
        int display = (int) (millisUntilFinished / 1000);
        tvCountdown.setText(String.valueOf(display));
    }

    @Override
    public void onTestFinish(boolean pass) {
        Tips tips = new Tips(this);
        if (pass) {
            tips.showSuccess();
            pathUtils.goNext(this);
        } else {
            tvCountdown.setTextColor(ContextCompat.getColor(this, R.color.red));
            tvCountdown.setText(R.string.fail);
        }
    }
}
