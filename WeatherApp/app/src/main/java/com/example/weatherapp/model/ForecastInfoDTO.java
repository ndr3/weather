package com.example.weatherapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by matyas on 04-Apr-17.
 */

public class ForecastInfoDTO {
    @SerializedName("temp")
    public TemperatureDTO temperature;

    public WeatherInfoDTO[] weather;

    public float pressure;

    public float humidity;
}
