package com.example.keneri.gowildweather;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


//TODO: This class is to download the necessary information from OpenWeatherMap
public class DownloadTask extends AsyncTask<String, Void, String> {

    String result = "";
    URL url;
    HttpURLConnection urlConnection = null;

    @Override
    protected String doInBackground(String... urls) {

        //TODO: Creating a URL connection and an input stream from the URL connection
        try{
            url = new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            int data = reader.read();

            while (data != -1){
                char current = (char) data;
                result += current;
                data = reader.read();
            }
            return result;
        }

        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override

    //TODO:Trying to get information from the API
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        try {
            //TODO: Getting the location(Singapore) & description(cloudy, rainy, etc.) from the API
            JSONObject jsonObject = new JSONObject(result);
            String placeName = jsonObject.getString("name");
            String description = jsonObject.getString("description");

            //TODO: Return the result to MainActivity.
            MainActivity.location.setText(placeName);
            MainActivity.description.setText(description);
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}

