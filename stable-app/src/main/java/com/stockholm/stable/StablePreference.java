package com.stockholm.stable;


import net.orange_box.storebox.annotations.method.KeyByString;

public interface StablePreference {

    @KeyByString("location_weather_forecast")
    void setLocationForecast(String json);

    @KeyByString("location_weather_forecast")
    String getLocationForecast();

}