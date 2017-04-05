package com.example.weatherapp;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by matyas on 05-Apr-17.
 */

public class HttpClientUtil {


    public static ResponseBody getCallResponse(Request request) {
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
