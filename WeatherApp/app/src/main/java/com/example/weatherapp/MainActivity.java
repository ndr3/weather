package com.example.weatherapp;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.example.weatherapp.model.ForecastDTO;
import com.example.weatherapp.model.WeatherDTO;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&APPID=%s&units=metric";
    private static String FORECAST_URL ="http://api.openweathermap.org/data/2.5/forecast/daily?q=%s,RO&cnt=%s&APPID=%s";
    private static String IMG_URL = "http://api.openweathermap.org/img/w/";
    private static String OPENWEATHERMAP_API_KEY = "599f795795dc6a51ffe33c0a3fca858c";

    static final int NUM_ITEMS = 3;

    private WeatherDTO mWeatherData;
    private ForecastDTO mForecastData;
    private ViewPager mViewPager;
    private FragmentStatePagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PlaceAutocompleteFragment autoCompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        //((TextView) autoCompleteFragment).setTextColor(Color.WHITE);
        autoCompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                try {
                    new MainActivity.RetrieveWeatherDataTask().execute(place).get();
                    String iconCode = mWeatherData.weather[0].icon;
                    new MainActivity.RetrieveImageTask().execute(iconCode).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Status status) {
                Log.i("PLACE", "An error occurred: " + status);
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("TODAY"));
        tabLayout.addTab(tabLayout.newTab().setText("TOMORROW"));
        tabLayout.addTab(tabLayout.newTab().setText("10 DAYS"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new WeatherAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }
        });
    }

    public static class WeatherAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public WeatherAdapter(FragmentManager fm, int numOfTabs) {
            super(fm);
            mNumOfTabs = numOfTabs;
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            WeatherFragment fragment = new WeatherFragment();
//            switch (position) {
//                case 0:
//                    fragment.setTemperature("0");
//                    break;
//                case 1:
//                    fragment.setTemperature("1");
//                    break;
//                case 2:
//                    fragment.setTemperature("2");
//                    break;
//            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }

    private ResponseBody getCallResponse(Request request) {
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        Response response = null;
        try {
            response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new IOException(response.message() + " " + response.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.body();
    }

    private class RetrieveWeatherDataTask extends AsyncTask<Place, Void, String> {
        @Override
        protected String doInBackground(Place... params) {
            String weatherUrl = String.format(BASE_URL, params[0].getName(), OPENWEATHERMAP_API_KEY);
            String forecastUrl = String.format(FORECAST_URL, params[0].getName(), "11", OPENWEATHERMAP_API_KEY);

            Request weatherRequest = new Request.Builder()
                    .url(weatherUrl)
                    .build();

            Request forecastRequest = new Request.Builder()
                    .url(forecastUrl)
                    .build();

            try {
                String weatherData = getCallResponse(weatherRequest).string();
                String forecastData = getCallResponse(forecastRequest).string();

                Gson gson = new Gson();
                mWeatherData = gson.fromJson(weatherData, WeatherDTO.class);
                mForecastData = gson.fromJson(forecastData, ForecastDTO.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            float temp = mWeatherData.main.temp;
            DecimalFormat twoDForm = new DecimalFormat("#.#");

            return twoDForm.format(temp);
        }

        @Override
        protected void onPostExecute(String result) {
            WeatherFragment fragment = (WeatherFragment) mAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
            if (fragment != null) {
                fragment.setTemperature(result);
            }

            WeatherFragment fragment2 = (WeatherFragment) mAdapter.instantiateItem(mViewPager, 2);
            if (fragment != null) {
                fragment.setTemperature("33");
            }
        }
    }

    public class RetrieveImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            String myUrl = IMG_URL + params[0];
            Request request = new Request.Builder()
                    .url(myUrl)
                    .build();

            Bitmap bmp = null;
            try {
                ResponseBody body = getCallResponse(request);
                byte[] result = body.bytes();

                bmp = BitmapFactory.decodeByteArray(result, 0, result.length);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            WeatherFragment fragment = (WeatherFragment) mAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
            if (fragment != null) {
                fragment.setConditionIcon(bmp);
            }
        }
    }
}
