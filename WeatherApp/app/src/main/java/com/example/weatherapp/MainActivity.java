package com.example.weatherapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    static final int NUM_ITEMS = 3;

    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&APPID=%s&units=metric";
    private static String IMG_URL = "http://api.openweathermap.org/img/w/";
    private static String OPENWEATHERMAP_API_KEY = "599f795795dc6a51ffe33c0a3fca858c";

    private WeatherDTO m_weatherData;
    static private TextView mTempTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mTempTextView = (TextView) findViewById(R.id.textview_temp);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Tab 1"));
        tabLayout.addTab(tabLayout.newTab().setText("Tab 2"));
        tabLayout.addTab(tabLayout.newTab().setText("Tab 3"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new WeatherAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        PlaceAutocompleteFragment autoCompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autoCompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                try {
                    new RetrieveWeatherDataTask().execute(place).get();
                    String iconCode = m_weatherData.weather[0].icon;
                    new RetrieveImageTask().execute(iconCode).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Status status) {
                Log.i("PLACE", "An error occurred: " + status);
            }
        });
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
            //String myUrl = BASE_URL + params[0].getName() + "&APPID=" + OPENWEATHERMAP_API_KEY + "&units=metric";
            String myUrl = String.format(BASE_URL, params[0].getName(), OPENWEATHERMAP_API_KEY);

            Request request = new Request.Builder()
                    .url(myUrl)
                    .build();

            try {
                String weatherData = getCallResponse(request).string();
                Gson gson = new Gson();
                m_weatherData = gson.fromJson(weatherData, WeatherDTO.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            float temp = m_weatherData.main.temp;
            DecimalFormat twoDForm = new DecimalFormat("#.#");

            return twoDForm.format(temp);
        }

        @Override
        protected void onPostExecute(String result) {
            mTempTextView.setText(result);
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
            //ImageView image = (ImageView) findViewById(R.id.cond_icon);
            //image.setImageBitmap(Bitmap.createScaledBitmap(bmp, 100, 100, false));
        }
    }

    public static class WeatherAdapter extends FragmentPagerAdapter {
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
            return new WeatherFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }

    public static class WeatherFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.weather_fragment, container, false);
        }
    }
}
