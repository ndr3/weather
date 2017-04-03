package com.example.weatherapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class WeatherFragment extends Fragment {

    private TextView mTempTextView;
    private ImageView mCondIcon;

    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.weather_fragment, container, false);
        mTempTextView = (TextView) view.findViewById(R.id.textview_temp);
        mCondIcon = (ImageView) view.findViewById(R.id.cond_icon);

        return view;
    }

    public void setTemperature(String temp) {
        mTempTextView.setText(temp);
    }

    public void setConditionIcon(Bitmap condIcon) {
        mCondIcon.setImageBitmap(Bitmap.createScaledBitmap(condIcon, 100, 100, false));
    }
}
