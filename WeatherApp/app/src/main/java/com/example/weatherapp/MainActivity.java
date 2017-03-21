package com.example.weatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Struct;
import java.text.DecimalFormat;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static String API_KEY = "599f795795dc6a51ffe33c0a3fca858c";

    private static final String[] LOCATIONS = new String[] {
            "Cluj-Napoca", "Jibou", "Toplita"
    };

    private class RetrieveWeatherDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String myUrl = BASE_URL + "Toplita,ro&APPID=" + API_KEY;

            FormBody.Builder formBuilder = new FormBody.Builder().add("location", "Toplita, ro");

            RequestBody formBody = formBuilder.build();
            Request request = new Request.Builder()
                    .url(myUrl)
                    .build();

            String result = null;
            try {
                okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException(response.message() + " " + response.toString());
                }
                result = response.body().string();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return result;
        }
    }

    private Double getTemperature(String weatherData)
    {
        try {

            JSONObject jObject = new JSONObject(weatherData);
            JSONObject mainObject = jObject.getJSONObject("main");
            return mainObject.getDouble("temp");
        }
        catch (Exception e) {
            return null;
        }
    }

    private double convertFromKelvinToCelcius(double degrees)
    {
        return degrees - 273.15;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autocomplete_location);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, LOCATIONS);
        textView.setAdapter(adapter);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = null;
                try {
                    result = new RetrieveWeatherDataTask().execute("").get();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                TextView tempTextView = (TextView) findViewById(R.id.textview_temp);
                Double temp = getTemperature(result);
                double degreesCelsius = convertFromKelvinToCelcius((double)temp);
                DecimalFormat twoDForm = new DecimalFormat("#.#");
                tempTextView.setText(temp == null ? null : twoDForm.format(degreesCelsius));
            }
        });
    }
}
