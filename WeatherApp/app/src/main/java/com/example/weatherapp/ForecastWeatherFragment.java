package com.example.weatherapp;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.weatherapp.model.ForecastDTO;
import com.google.android.gms.location.places.Place;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import okhttp3.Request;

public class ForecastWeatherFragment extends Fragment {

    private static String FORECAST_WEATHER_URL ="http://api.openweathermap.org/data/2.5/forecast/daily?q=%s,RO&cnt=%s&APPID=%s&units=metric";
    private static String IMG_URL = "http://api.openweathermap.org/img/w/";
    private static String OPENWEATHERMAP_API_KEY = "599f795795dc6a51ffe33c0a3fca858c";

    private ForecastDTO mForecastData;
    private Typeface mWeatherFont;
    private TextView mCityTextView;
    private TextView mTempTextView;
    private TextView mCondIcon;
    private Place mPlace;

    public ForecastWeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWeatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_forecast_weather, container, false);
        mCityTextView = (TextView) view.findViewById(R.id.textview_city);
        mTempTextView = (TextView) view.findViewById(R.id.textview_temp);
        mCondIcon = (TextView) view.findViewById(R.id.cond_icon);
        mCondIcon.setTypeface(mWeatherFont);

        return view;
    }

    public void setPlace(Place place) {
        mPlace = place;

        try {
            new RetrieveForecastWeatherDataTask().execute(place).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCityName(String name) {
        mCityTextView.setText(name);
    }

    public void setTemperature(float temp) {
        DecimalFormat twoDForm = new DecimalFormat("#.#");
        mTempTextView.setText(twoDForm.format(temp) + " ℃");
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

    private class RetrieveForecastWeatherDataTask extends AsyncTask<Place, Void, Void> {
        @Override
        protected Void doInBackground(Place... params) {
            String forecastUrl = String.format(FORECAST_WEATHER_URL, params[0].getName(), "11", OPENWEATHERMAP_API_KEY);
            Request forecastRequest = new Request.Builder()
                    .url(forecastUrl)
                    .build();

            try {
                String forecastData = HttpClientUtil.getCallResponse(forecastRequest).string();

                Gson gson = new Gson();
                mForecastData = gson.fromJson(forecastData, ForecastDTO.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setTemperature(mForecastData.list[0].temp.max);
            setCityName(mPlace.getName().toString());
            setConditionIcon(mForecastData.list[0].weather[0].id);
        }
    }
}
