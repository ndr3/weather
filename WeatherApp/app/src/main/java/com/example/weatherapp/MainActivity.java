package com.example.weatherapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weatherapp.model.WeatherDTO;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?id=";
    private static String IMG_URL = "http://api.openweathermap.org/img/w/";
    private static String OPENWEATHERMAP_API_KEY = "599f795795dc6a51ffe33c0a3fca858c";

    private WeatherDTO m_weatherData;

    public static final HashMap<String, Integer> LOCATIONS;

    static {
        LOCATIONS = new HashMap<String, Integer>();
        LOCATIONS.put("Cluj-Napoca", 681290);
        LOCATIONS.put("Jibou", 675261);
        LOCATIONS.put("Borsec", 684143);
        LOCATIONS.put("Zalau", 662334);
        LOCATIONS.put("Sibiu", 667268);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autocomplete_location);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(LOCATIONS.keySet()));
        textView.setAdapter(adapter);

        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                Integer i = LOCATIONS.get((String)item);

                try{
                    new RetrieveWeatherDataTask().execute(i).get();
                    String iconCode = m_weatherData.weather[0].icon;
                    new RetrieveImageTask().execute(iconCode).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class RetrieveWeatherDataTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            String myUrl = BASE_URL + params[0] + "&APPID=" + OPENWEATHERMAP_API_KEY + "&units=metric";

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
            TextView tempTextView = (TextView) findViewById(R.id.textview_temp);
            tempTextView.setText(result);
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
                byte[] result = getCallResponse(request).bytes();
                bmp = BitmapFactory.decodeByteArray(result, 0, result.length);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            ImageView image = (ImageView) findViewById(R.id.cond_icon);
            image.setImageBitmap(Bitmap.createScaledBitmap(bmp, 100, 100, false));
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
}
