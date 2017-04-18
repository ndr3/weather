package com.example.weatherapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.weatherapp.model.WeatherDTO;
import com.google.android.gms.location.places.Place;

import java.sql.Date;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ForecastWeatherFragment extends Fragment implements IWeatherFragment {

    private WeatherDTO mWeatherData;
    private ListView mListView;

    public ForecastWeatherFragment() {
        // Required empty public constructor
    }

    public void setWeatherData(WeatherDTO weatherData, int index){
        mWeatherData = weatherData;
        updateWeatherData();
    }

    private void updateWeatherData()
    {
        String[] forecastArray = new String[10];
        for (int i = 0; i < 10; i++) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, i);
            forecastArray[i]= c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
                    + " " + String.valueOf(mWeatherData.list[i+2].temp.day) + " ℃";
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.forecast_listview, forecastArray);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_forecast_weather, container, false);
        mListView = (ListView) view.findViewById(R.id.day_list);

        return view;
    }
}
