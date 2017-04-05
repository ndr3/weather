package com.example.weatherapp;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.weatherapp.model.ForecastDTO;
import com.example.weatherapp.model.WeatherDTO;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class MainActivity extends AppCompatActivity {
    static final int NUM_ITEMS = 3;

    private ViewPager mViewPager;
    private FragmentStatePagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PlaceAutocompleteFragment autoCompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autoCompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                switch (mViewPager.getCurrentItem()) {
                    case 0:
                        CurrentWeatherFragment currentWeatherFragment = (CurrentWeatherFragment) mAdapter.instantiateItem(mViewPager, 0);
                        currentWeatherFragment.setPlace(place);
                        break;
                    case 1:
                    case 2:
                        ForecastWeatherFragment forecastWeatherFragment = (ForecastWeatherFragment) mAdapter.instantiateItem(mViewPager, 1);
                        forecastWeatherFragment.setPlace(place);
                        break;
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
        mAdapter = new WeatherAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
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
