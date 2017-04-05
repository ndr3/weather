package com.example.weatherapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Date;

public class WeatherFragment extends Fragment {
    private Typeface mWeatherFont;
    private TextView mCityTextView;
    private TextView mTempTextView;
    private TextView mCondIcon;

    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWeatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.weather_fragment, container, false);
        mCityTextView = (TextView) view.findViewById(R.id.textview_city);
        mTempTextView = (TextView) view.findViewById(R.id.textview_temp);
        mCondIcon = (TextView) view.findViewById(R.id.cond_icon);
        mCondIcon.setTypeface(mWeatherFont);

        return view;
    }

    public void setCityName(String name) {
        mCityTextView.setText(name);
    }

    public void setTemperature(float temp) {
        DecimalFormat twoDForm = new DecimalFormat("#.#");
        mTempTextView.setText(twoDForm.format(temp) + " ℃");
    }

    public void setConditionIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
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
        }
        mCondIcon.setText(icon);
    }

    public void setConditionIcon(int actualId) {
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
