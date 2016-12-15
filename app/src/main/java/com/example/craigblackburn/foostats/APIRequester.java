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
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class APIRequester extends Activity {

    private static String TAG = "API_REQUESTER";
    private APITaskHelper mListener;
    private OnAPITaskCompleteListener onAPITaskCompleteListener;

    private static String PLAYERS = "/player";
    private static String TEAMS = "/team";
    private static String STATISTICS = "/statistics";
    private static String ACHIEVEMENTS = "/achievements";

    private static String BASE_URL = "https://foostats.herokuapp.com";

    public APIRequester() {}

    public APIRequester(OnAPITaskCompleteListener listener) {
        onAPITaskCompleteListener = listener;
    }

    private APIRequester(APITaskHelper listener) {
        mListener = listener;
    }

    private void foostatRequestMethodGET(String urlString) throws IOException, JSONException {

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

    public static void getPlayers(final OnAPITaskCompleteListener delegate) {

        new APIRequester(new APITaskHelper() {
            @Override
            public void onAsyncTaskComplete(JSONObject json) {
                Gson gson = new Gson();
                List<FPlayer> list = new ArrayList<>();
                try {
                    JSONArray playersJson = json.getJSONArray("players");

                    for (int i = 0; i < playersJson.length(); i++) {
                        JSONObject obj = playersJson.getJSONObject(i);
                        list.add(gson.fromJson(obj.toString(), FPlayer.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                delegate.onPlayerResponse(list);
            }
        }).requestPlayers();
    }

    public static void getTeams(final OnAPITaskCompleteListener delegate) {
        new APIRequester(new APITaskHelper() {
            @Override
            public void onAsyncTaskComplete(JSONObject json) {
                Gson gson = new Gson();
                ArrayList<FTeam> list = new ArrayList<>();
                try {
                    JSONArray teamsJson = json.getJSONArray("teams");
                    for (int i = 0; i < teamsJson.length(); i++) {
                        JSONObject obj = teamsJson.getJSONObject(i);
                        list.add(gson.fromJson(obj.toString(), FTeam.class));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                delegate.onTeamResponse(list);
            }
        }).requestTeams();
    }

    public void requestPlayers() {
        try {
            foostatRequestMethodGET(BASE_URL + PLAYERS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestTeams() {
        try {
            foostatRequestMethodGET(BASE_URL + TEAMS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private interface APITaskHelper {
        void onAsyncTaskComplete(JSONObject json);
    }

    public interface OnAPITaskCompleteListener {
        void onPlayerResponse(List<FPlayer> players);
        void onTeamResponse(List<FTeam> teams);
        void onGameResponse(List<FGame> games);
        void onAchievementsResponse(List<Achievements> achievements);
    }

}
