package com.example.weatherapp;

import com.example.weatherapp.model.WeatherDTO;

/**
 * Created by matyas on 07-Apr-17.
 */

public interface IWeatherFragment {
    public void setWeatherData(WeatherDTO weatherData, int index);
}
