package com.example.weatherapp.model;

import android.location.Location;

import com.google.gson.annotations.SerializedName;

/**
 * Created by matyas on 22-Mar-17.
 */

public class WeatherDTO {

    @SerializedName("coord")
    public Location location;

    public WeatherInfoDTO[] weather;

    public MainDTO main;

    public int visibility;

    public WindDTO wind;

    public int dt;

    public SysDTO sys;

    public int id;

    public String name;

    public int cod;
}