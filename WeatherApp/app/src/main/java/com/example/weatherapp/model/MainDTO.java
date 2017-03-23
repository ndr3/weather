package com.example.weatherapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by matyas on 22-Mar-17.
 */

public class MainDTO {

    public float temp;

    public float pressure;

    public float humidity;

    @SerializedName("temp_min")
    public float tempMin;

    @SerializedName("temp_max")
    public float tempMax;
}