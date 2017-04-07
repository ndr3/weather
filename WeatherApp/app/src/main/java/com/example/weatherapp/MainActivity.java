package com.example.weatherapp;

import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.weatherapp.model.WeatherDTO;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Request;

public class MainActivity extends AppCompatActivity {
    private static String WEATHER_URL ="http://api.openweathermap.org/data/2.5/forecast/daily?q=%s&cnt=%s&APPID=%s&units=metric";
    private static String OPENWEATHERMAP_API_KEY = "599f795795dc6a51ffe33c0a3fca858c";

    static final int NUM_ITEMS = 3;
    static final int PAGE_LIMIT = 2;

    private ViewPager mViewPager;
    private FragmentStatePagerAdapter mAdapter;
    private Place mPlace;

    private WeatherDTO mWeatherData;

    CurrentWeatherFragment mCurrentWeatherFragment;
    ForecastWeatherFragment mTomorrowWeatherFragment;
    ForecastWeatherFragment mTenDaysWeatherFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PlaceAutocompleteFragment autoCompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autoCompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                try {
                    new MainActivity.RetrieveWeatherDataTask().execute(place).get();
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

        mAdapter = new WeatherAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(PAGE_LIMIT);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        mCurrentWeatherFragment = (CurrentWeatherFragment) mAdapter.instantiateItem(mViewPager, 0);
        mTomorrowWeatherFragment = (ForecastWeatherFragment) mAdapter.instantiateItem(mViewPager, 1);
        mTenDaysWeatherFragment = (ForecastWeatherFragment) mAdapter.instantiateItem(mViewPager, 2);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());

                if (mWeatherData != null) {
                    IWeatherFragment fragment = (IWeatherFragment) mAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
                    fragment.setWeatherData(mWeatherData);
                }
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

    private class RetrieveWeatherDataTask extends AsyncTask<Place, Void, Void> {
        @Override
        protected Void doInBackground(Place... params) {
            String forecastUrl = String.format(WEATHER_URL, params[0].getName(), "11", OPENWEATHERMAP_API_KEY);
            Request forecastRequest = new Request.Builder()
                    .url(forecastUrl)
                    .build();

            try {
                String forecastData = HttpClientUtil.getCallResponse(forecastRequest).string();
                Gson gson = new Gson();
                mWeatherData = gson.fromJson(forecastData, WeatherDTO.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            IWeatherFragment fragment = (IWeatherFragment) mAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
            fragment.setWeatherData(mWeatherData);
        }
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
            switch (position) {
                case 0:
                    return new CurrentWeatherFragment();
                case 1:
                case 2:
                    return new ForecastWeatherFragment();
                default:
                    return new ForecastWeatherFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }
}
