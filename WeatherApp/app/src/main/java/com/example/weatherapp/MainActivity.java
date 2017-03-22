package com.example.weatherapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Double convertFromKelvinToCelsius(Double degrees) {
        return degrees - 273.15;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autocomplete_location);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(WeatherHttpClient.LOCATIONS.keySet()));
        textView.setAdapter(adapter);

        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Object item = parent.getItemAtPosition(position);
                Integer i = WeatherHttpClient.LOCATIONS.get((String)item);

                WeatherHttpClient httpClient = new WeatherHttpClient();
                Double temp = httpClient.getTemperature(i);
                double degreesCelsius = convertFromKelvinToCelsius(temp);
                DecimalFormat twoDForm = new DecimalFormat("#.#");

                TextView tempTextView = (TextView) findViewById(R.id.textview_temp);
                tempTextView.setText(temp == null ? null : twoDForm.format(degreesCelsius));

                byte[] byteArray = httpClient.getImage();
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                ImageView image = (ImageView) findViewById(R.id.cond_icon);
                image.setImageBitmap(Bitmap.createScaledBitmap(bmp, 100, 100, false));
            }
        });
    }
}
