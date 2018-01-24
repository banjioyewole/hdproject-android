package com.enzonium.efarrariwalls;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

class NetworkAbstractionLayer {
    //Logs in "Enzonium.Network-STD" are all Log.d, not Log.e.
    //Updated to non-deprecated networking methods in 2016

    static final String BASE_URL = "http://www.banj.io";
    static final String SOURCE_ROUTE = BASE_URL+"/efarrari/source";


    //Uses new networking protocol && returns a JSONArray Object.
    static JSONArray getJSONArrayfromURL(String requestURL) {
        URL url;
        String response = "";
        try {

            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            Log.d("NAL.Network-STD", "Server Response: " + responseCode);

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {

                response = responseCode + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            return new JSONArray(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Bitmap getBitmapFromURL(String requestURL ) {
        URL url;
        String response = "";
        InputStream in=null;

        try {
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                in = conn.getInputStream();

                final BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = true;
                return BitmapFactory.decodeStream(in , null, options);

            } else {
//                response = responseCode + "";
                return  null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally
        {
            if(in!=null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }


}
