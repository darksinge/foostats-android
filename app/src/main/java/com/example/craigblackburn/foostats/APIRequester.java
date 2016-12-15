package com.example.craigblackburn.foostats;


import android.annotation.SuppressLint;
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

    @SuppressWarnings("unused")
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
                JSONObject json;
                try {
                    json = new JSONObject(result);
                } catch (JSONException e1) {
                    json = new JSONObject();

                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        json.put("value", jsonArray);
                    } catch (JSONException e2) {
                        e1.printStackTrace();
                        e2.printStackTrace();
                    }

                }
                mListener.onAsyncTaskComplete(json);
            }

        };

        task.execute(urlString);

    }

    private void foostatRequestMethodPOST(String path, Bundle parameters) throws IOException, JSONException {

    }

    private void foostatsRequestMethodDELETE(String urlString, Bundle parameters) throws IOException, JSONException {
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... strings) {

                if (AccessToken.getCurrentAccessToken() == null) return null;

                String param = strings[0];

                URL url;
                HttpsURLConnection conn = null;

                String response = "";
                try {
                    url = new URL(param);

                    conn = (HttpsURLConnection) url.openConnection();
                    conn.setRequestMethod("DELETE");
                    conn.setRequestProperty("access_token", AccessToken.getCurrentAccessToken().getToken());

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
                JSONObject json;
                try {
                    json = new JSONObject(result);
                } catch (JSONException e1) {
                    json = new JSONObject();

                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        json.put("value", jsonArray);
                    } catch (JSONException e2) {
                        e1.printStackTrace();
                        e2.printStackTrace();
                    }

                }
                mListener.onAsyncTaskComplete(json);
            }

        };

        task.execute(urlString);
    }

    public static void getPlayers(final OnAPITaskCompleteListener delegate) {

        new APIRequester(new APITaskHelper() {
            @Override
            public void onAsyncTaskComplete(JSONObject json) {
                List<FPlayer> list = new ArrayList<>();
                try {
                    JSONArray playersJson = json.getJSONArray("players");
                    for (int i = 0; i < playersJson.length(); i++) {
                        JSONObject obj = playersJson.getJSONObject(i);
                        String id = obj.getString("uuid");
                        String facebookId = obj.getString("facebookId");
                        String email = obj.getString("email");
                        String firstName = obj.getString("firstName");
                        String lastName = obj.getString("lastName");
                        String role = obj.getString("role");
                        String username = obj.getString("username");

                        List<FTeam> teams = new ArrayList<>();
                        JSONArray teamsJson = obj.getJSONArray("teams");
                        for (int k = 0; k < teamsJson.length(); k++) {
                            JSONObject teamObj = teamsJson.getJSONObject(k);
                            String teamId = teamObj.getString("uuid");
                            String teamName = teamObj.getString("name");
                            teams.add(new FTeam(teamId, teamName, null, null));
                        }

                        FPlayer player = new FPlayer(id, facebookId, email, firstName, lastName, role, username, teams);
                        for (FTeam team : teams) {
                            try {
                                FTeam _existingTeam = FTeam.find(team.getTeamName());
                                if (_existingTeam != null) {
                                    _existingTeam.addPlayer(player);
                                }
                            } catch (Exception e) {
                                // ignore
                            }

                            team.addPlayer(player);
                        }

                        list.add(player);
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
                    JSONArray teamsJson = json.getJSONArray("value");
                    for (int i = 0; i < teamsJson.length(); i++) {
                        JSONObject obj = teamsJson.getJSONObject(i);
                        JSONArray players = obj.getJSONArray("players");
//                        JSONArray games = obj.getJSONArray("games");
                        String id = obj.getString("uuid");
                        String name = obj.getString("name");

                        JSONObject player1Json = players.getJSONObject(0);
                        JSONObject player2Json = players.getJSONObject(1);

                        FPlayer player1 = parsePlayerFromJSONObject(player1Json);
                        FPlayer player2 = parsePlayerFromJSONObject(player2Json);

                        FPlayer _player1 = FPlayer.find(player1.getId());
                        FPlayer _player2 = FPlayer.find(player2.getId());

                        if (_player1 != null) {
                            player1 = _player1;
                        }

                        if (_player2 != null) {
                            player2 = _player2;
                        }

                        FTeam team = new FTeam(id, name, player1, player2);

                        team.getPlayerOne().addTeam(team);
                        team.getPlayerTwo().addTeam(team);

                        list.add(new FTeam(id, name, player1, player2));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                delegate.onTeamResponse(list);
            }
        }).requestTeams();
    }

    private static FPlayer parsePlayerFromJSONObject(JSONObject obj) {
        try {
            String id = obj.getString("uuid");
            String facebookId = obj.getString("facebookId");
            String email = obj.getString("email");
            String firstName = obj.getString("firstName");
            String lastName = obj.getString("lastName");
            String role = obj.getString("role");
            String username = obj.getString("username");
            return new FPlayer(id, facebookId, email, firstName, lastName, role, username);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteTeam(String id, final OnAPITaskCompleteListener delegate) {
        new APIRequester(new APITaskHelper() {
            @Override
            public void onAsyncTaskComplete(JSONObject json) {
                boolean success;
                try {
                    success = json.getBoolean("success");
                } catch (JSONException e) {
                    e.printStackTrace();
                    success = false;
                }
                if (delegate != null) {
                    delegate.onTeamDeleteResponse(success);
                }
            }
        })._deleteTeam(id);
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

    private void _deleteTeam(String id) {
        try {
            foostatsRequestMethodDELETE(BASE_URL + TEAMS + "/" + id, null);
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
        void onTeamDeleteResponse(boolean success);
        void onGameResponse(List<FGame> games);
        void onAchievementsResponse(List<Achievements> achievements);
    }

}
