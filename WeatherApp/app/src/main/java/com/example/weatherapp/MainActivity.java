package com.example.weatherapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private Double convertFromKelvinToCelsius(Double degrees) {
        return degrees - 273.15;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autocomplete_location);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, WeatherHttpClient.LOCATIONS);
        textView.setAdapter(adapter);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeatherHttpClient httpClient = new WeatherHttpClient();

                TextView tempTextView = (TextView) findViewById(R.id.textview_temp);
                Double temp = httpClient.getTemperature();
                double degreesCelsius = convertFromKelvinToCelsius(temp);
                DecimalFormat twoDForm = new DecimalFormat("#.#");
                tempTextView.setText(temp == null ? null : twoDForm.format(degreesCelsius));

                byte[] byteArray = httpClient.getImage();
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                ImageView image = (ImageView) findViewById(R.id.cond_icon);
                image.setImageBitmap(Bitmap.createScaledBitmap(bmp, 100, 100, false));
            }
        });
    }
}
