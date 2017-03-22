package com.example.weatherapp;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by matyas on 22-Mar-17.
 */

public class WeatherHttpClient {
    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static String IMG_URL = "http://api.openweathermap.org/img/w/";
    private static String OPENWEATHERAPP_API_KEY = "599f795795dc6a51ffe33c0a3fca858c";

    private String m_weatherData;

    public static final String[] LOCATIONS = new String[]{
            "Cluj-Napoca", "Jibou", "Toplita"
    };

    public class RetrieveWeatherDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String myUrl = BASE_URL + "Toplita,ro&APPID=" + OPENWEATHERAPP_API_KEY;

            FormBody.Builder formBuilder = new FormBody.Builder().add("location", "Toplita, ro");

            RequestBody formBody = formBuilder.build();
            Request request = new Request.Builder()
                    .url(myUrl)
                    .build();

            try {
                okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException(response.message() + " " + response.toString());
                }
                m_weatherData = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return m_weatherData;
        }
    }

    public Double getTemperature() {
        try {
            String weatherData = new RetrieveWeatherDataTask().execute("").get();

            JSONObject jObject = new JSONObject(weatherData);
            JSONObject mainObject = jObject.getJSONObject("main");
            return mainObject.getDouble("temp");
        } catch (Exception e) {
            return null;
        }
    }

    public class RetrieveImageTask extends AsyncTask<String, Void, byte[]> {
        @Override
        protected byte[] doInBackground(String... params) {
            String myUrl = IMG_URL + params[0];
            Request request = new Request.Builder()
                    .url(myUrl)
                    .build();

            byte[] result = null;
            try {
                okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException(response.message() + " " + response.toString());
                }
                result = response.body().bytes();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }
    }

    public byte[] getImage() {
        byte[] byteArray = null;
        try {
            JSONObject jObject = new JSONObject(m_weatherData);
            JSONArray array = jObject.getJSONArray("weather");
            JSONObject weatherObject = array.getJSONObject(0);
            String iconCode =  weatherObject.getString("icon");
            byteArray = new RetrieveImageTask().execute(iconCode).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteArray;
    }
}
