package com.example.weatherapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.weatherapp.model.WeatherDTO;
import com.google.android.gms.location.places.Place;

import java.text.DecimalFormat;
import java.util.Locale;

public class DailyWeatherFragment extends Fragment implements IWeatherFragment {

    private WeatherDTO mWeatherData;
    private Typeface mWeatherFont;
    private TextView mCityTextView;
    private TextView mTempTextView;
    private TextView mDetailsTextView;
    private TextView mCondIcon;
    private Place mPlace;

    public DailyWeatherFragment() {
        // Required empty public constructor
    }

    public void setWeatherData(WeatherDTO weatherData, int index){
        mWeatherData = weatherData;
        updateWeatherData(index);
    }

    private void updateWeatherData(int index)
    {
        setTemperature(mWeatherData.list[index].temp.max);
        setCityName(mWeatherData.city.name);
        setConditionIcon(mWeatherData.list[index].weather[0].id);
        mDetailsTextView.setText(mWeatherData.list[index].weather[0].description.toUpperCase(Locale.US)
                + "\nHumidity: " + mWeatherData.list[index].humidity + "%"
                + "\nPressure: " + mWeatherData.list[index].pressure + " hPa");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWeatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_current_weather, container, false);
        mCityTextView = (TextView) view.findViewById(R.id.textview_city);
        mTempTextView = (TextView) view.findViewById(R.id.textview_temp);
        mDetailsTextView = (TextView) view.findViewById(R.id.details_field);
        mCondIcon = (TextView) view.findViewById(R.id.cond_icon);
        mCondIcon.setTypeface(mWeatherFont);

        return view;
    }

    private void setCityName(String name) {
        mCityTextView.setText(name);
    }

    private void setTemperature(float temp) {
        DecimalFormat twoDForm = new DecimalFormat("#.#");
        mTempTextView.setText(twoDForm.format(temp) + " ℃");
    }

    private void setConditionIcon(int actualId) {
        int id = actualId / 100;
        String icon = "";
        switch(id) {
            case 2 : icon = getActivity().getString(R.string.weather_thunder);
                break;
            case 3 : icon = getActivity().getString(R.string.weather_drizzle);
                break;
            case 7 : icon = getActivity().getString(R.string.weather_foggy);
                break;
            case 8 : icon = getActivity().getString(R.string.weather_cloudy);
                break;
            case 6 : icon = getActivity().getString(R.string.weather_snowy);
                break;
            case 5 : icon = getActivity().getString(R.string.weather_rainy);
                break;
        }
        mCondIcon.setText(icon);
    }
}
