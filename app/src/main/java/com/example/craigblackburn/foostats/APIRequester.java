package com.example.craigblackburn.foostats;


import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

interface PlayerDelegate {
    void onTaskComplete(ArrayList<FPlayer> list);
}

interface TeamDelegate {
    void onTaskComplete(ArrayList<FTeam> list);
}

interface GameDelegate {
    void onTaskComplete(ArrayList<FGame> list);
}

public class APIRequester extends Activity {

    interface APITaskHelper {
        void onAsyncTaskComplete(JSONObject json);
    }

    private static String TAG = "API_REQUESTER";
    private APITaskHelper mListener;

    private static String PLAYERS = "/player";
    private static String TEAMS = "/team";
    private static String STATISTICS = "/statistics";
    private static String ACHIEVEMENTS = "/achievements";

    private static String BASE_URL = "https://foostats.herokuapp.com";

    private APIRequester() {}

    public APIRequester(APITaskHelper listener) {
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
                    mListener.onAsyncTaskComplete(json);
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

    public static APIRequester getPlayers(final PlayerDelegate delegate) {

        APIRequester requester = new APIRequester(new APITaskHelper() {
            @Override
            public void onAsyncTaskComplete(JSONObject json) {
                Gson gson = new Gson();

//            {
//                "teams":[
//                {
//                    "uuid":"a0bb332d-6040-41dd-af76-0dcab3862a66",
//                        "name":"TeamAwesome",
//                        "createdAt":"2016-12-13T22:55:45.000Z",
//                        "updatedAt":"2016-12-13T22:55:45.000Z"
//                }
//                ],
//                "achievements":[
//
//                ],
//                "uuid":"db71d47a-27cc-45a9-9085-92777436ad52",
//                    "email":"cr.blackburn89@gmail.com",
//                    "firstName":"Craig",
//                    "lastName":"Blackburn",
//                    "role":"admin",
//                    "username":"Craig Blackburn",
//                    "createdAt":"2016-12-08T21:50:57.000Z",
//                    "updatedAt":"2016-12-13T21:24:21.000Z",
//                    "name":"Craig Blackburn"
//            }
                ArrayList<FPlayer> list = new ArrayList<>();

                try {
                    JSONArray playersJson = json.getJSONArray("players");

                    for (int i = 0; i < playersJson.length(); i++) {
                        JSONObject obj = playersJson.getJSONObject(i);
                        list.add(gson.fromJson(obj.toString(), FPlayer.class));
                    }

                    delegate.onTaskComplete(list);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        requester.requestPlayers();
        return requester;
    }

    public void requestPlayers() {
        try {
            getFoostatRequest(BASE_URL + PLAYERS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
