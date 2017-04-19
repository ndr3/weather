package com.example.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

import com.example.weatherapp.model.WeatherDTO;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Request;

public class MainActivity extends AppCompatActivity {
    private static String WEATHER_URL ="http://api.openweathermap.org/data/2.5/forecast/daily?q=%s&cnt=%s&APPID=%s&units=%s";
    private static String OPENWEATHERMAP_API_KEY = "599f795795dc6a51ffe33c0a3fca858c";

    static final int FORECAST_DAYS = 12;
    static final String DEFAULT_UNITS = "metric";
    static final String UNITS_PREFERENCE_KEY = "com.example.weatherapp.units";

    static final int NUM_ITEMS = 3;
    static final int PAGE_LIMIT = 2;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PHOTOS = "photos";
    private static final String TAG_MOVIES = "movies";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;
    public static int NAV_ITEM_INDEX = 0;

    private NavigationView mNavigationView;
    private ViewPager mViewPager;
    private FragmentStatePagerAdapter mAdapter;
    private Place mPlace;

    private WeatherDTO mWeatherData;

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

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

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

        setUpNavigationView();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());

                if (mWeatherData != null) {
                    int index =  mViewPager.getCurrentItem();
                    IWeatherFragment fragment = (IWeatherFragment) mAdapter.instantiateItem(mViewPager, index);
                    fragment.setWeatherData(mWeatherData, index);
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

    private void setUpNavigationView() {
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        NAV_ITEM_INDEX = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_photos:
                        NAV_ITEM_INDEX = 1;
                        CURRENT_TAG = TAG_PHOTOS;
                        break;
                    case R.id.nav_movies:
                        NAV_ITEM_INDEX = 2;
                        CURRENT_TAG = TAG_MOVIES;
                        break;
                    case R.id.nav_notifications:
                        NAV_ITEM_INDEX = 3;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;
                    case R.id.nav_settings:
                        NAV_ITEM_INDEX = 4;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    default:
                        NAV_ITEM_INDEX = 0;
                        break;
                }


                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }

                loadHomeFragment();

                return true;
            }
        });
    }


    private Fragment getHomeFragment() {
        switch (NAV_ITEM_INDEX) {
            case 4:
                // settings fragment
                ForecastWeatherFragment.SettingsFragment settingsFragment = new ForecastWeatherFragment.SettingsFragment();
                return settingsFragment;
            default:
                return null;
        }
    }

    private void loadHomeFragment() {
        Fragment fragment = getHomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private class RetrieveWeatherDataTask extends AsyncTask<Place, Void, Void> {
        @Override
        protected Void doInBackground(Place... params) {
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("com.example.weatherapp", Context.MODE_PRIVATE);
            String units = prefs.getString(UNITS_PREFERENCE_KEY, DEFAULT_UNITS);

            String forecastUrl = String.format(WEATHER_URL, params[0].getName(), String.valueOf(FORECAST_DAYS),
                    OPENWEATHERMAP_API_KEY, units);
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
            int index =  mViewPager.getCurrentItem();
            IWeatherFragment fragment = (IWeatherFragment) mAdapter.instantiateItem(mViewPager, index);
            fragment.setWeatherData(mWeatherData, index);
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
                case 1:
                    return new DailyWeatherFragment();
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
