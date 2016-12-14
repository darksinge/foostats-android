package com.example.craigblackburn.foostats;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class APIRequester extends Activity {

    private static String TAG = "API_REQUESTER";
    private APIListener mListener;

    private static String PLAYERS = "/player";
    private static String TEAMS = "/team";
    private static String STATISTICS = "/statistics";
    private static String ACHIEVEMENTS = "/achievements";

    private static String BASE_URL = "https://foostats.herokuapp.com";

    private APIRequester() {}

    public APIRequester(APIListener listener) {
        mListener = listener;
    }

    private void getFoostatRequest(String urlString) throws IOException, JSONException {

        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... strings) {

                String param = strings[0];

                URL url;
                HttpsURLConnection conn = null;

                String response = "";
                try {
                    url = new URL(param);

                    conn = (HttpsURLConnection) url.openConnection();

                    if (AccessToken.getCurrentAccessToken() != null) {
                        conn.setRequestProperty("access_token", AccessToken.getCurrentAccessToken().getToken());
                    } else {
                        return null;
                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String input;

                    while ((input = br.readLine()) != null) {
                        response += input;
                        Log.d(TAG, input);
                    }

                    br.close();

                } catch (Exception e){
                    e.printStackTrace();
                    return null;
                } finally {
                    conn.disconnect();
                }

                return response;
            }

            @Override
            protected void onPostExecute(String result) {
                System.out.println("END CALL");
                try {
                    JSONObject json = new JSONObject(result);
                    mListener.onTaskComplete(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        };

        task.execute(urlString);

    }

    private JSONObject postFoostatRequest(String path, Bundle parameters) throws IOException, JSONException {
        return null;
    }

    public boolean getPlayers() {
        try {
            getFoostatRequest(BASE_URL + PLAYERS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    interface APIListener {
        void onTaskComplete(JSONObject json);
    }

}
