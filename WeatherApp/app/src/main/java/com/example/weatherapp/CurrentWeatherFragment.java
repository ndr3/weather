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

import com.example.weatherapp.model.WeatherDTO;
import com.google.android.gms.location.places.Place;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

import okhttp3.Request;

public class CurrentWeatherFragment extends Fragment {

    private static String CURRENT_WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&APPID=%s&units=metric";
    private static String IMG_URL = "http://api.openweathermap.org/img/w/";
    private static String OPENWEATHERMAP_API_KEY = "599f795795dc6a51ffe33c0a3fca858c";

    private WeatherDTO mCurrentWeatherData;
    private Typeface mWeatherFont;
    private TextView mCityTextView;
    private TextView mTempTextView;
    private TextView mCondIcon;
    private Place mPlace;

    public CurrentWeatherFragment() {
        // Required empty public constructor
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
        mCondIcon = (TextView) view.findViewById(R.id.cond_icon);
        mCondIcon.setTypeface(mWeatherFont);

        return view;
    }

    public void setPlace(Place place) {
        mPlace = place;

        try {
            new RetrieveCurrentWeatherDataTask().execute(place).get();
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

    private class RetrieveCurrentWeatherDataTask extends AsyncTask<Place, Void, Void> {
        @Override
        protected Void doInBackground(Place... params) {
            String weatherUrl = String.format(CURRENT_WEATHER_URL, params[0].getName(), OPENWEATHERMAP_API_KEY);

            Request weatherRequest = new Request.Builder()
                    .url(weatherUrl)
                    .build();

            try {
                String weatherData = HttpClientUtil.getCallResponse(weatherRequest).string();

                Gson gson = new Gson();
                mCurrentWeatherData = gson.fromJson(weatherData, WeatherDTO.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setTemperature(mCurrentWeatherData.main.temp);
            setCityName(mCurrentWeatherData.name);
            setConditionIcon(mCurrentWeatherData.weather[0].id, mCurrentWeatherData.sys.sunrise * 1000,
                    mCurrentWeatherData.sys.sunset * 1000);
        }
    }
}
