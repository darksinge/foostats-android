package com.example.craigblackburn.foostats;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


public class FooRequester extends AsyncTask<String, Void, String> {

    public static final String TAG = "FOO_REQUESTER";
    public static final String BASE_URL = "https://foostats.herokuapp.com";

    private OnFooPostExecute mListener;

    private FooRequester(OnFooPostExecute listener) {
        super();
        this.mListener = listener;
    }

    public static void makePlayerGETRequest(final OnRequesterTaskComplete listener, String playerId) {
        String urlString = BASE_URL + "/player";
        if (playerId != null)
            urlString += "/" + playerId;
        new FooRequester(new OnFooPostExecute() {
            @Override
            public boolean onTaskComplete(String response, Exception e) {
                if (e != null) {
                    listener.onPlayerResponse(null, e);
                    return false;
                }

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

                    listener.onPlayerResponse(list, null);
                    return true;
                } catch (JSONException error) {
                    listener.onPlayerResponse(null, error);
                    return false;
                }
            }
        }).execute(urlString);
    }

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
            mListener.onTaskComplete(null, e);
        } finally {
            conn.disconnect();
        }

        return response;

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mListener.onTaskComplete(s, null);
    }

    @Override
    protected void onCancelled(String s) {
        super.onCancelled(s);
        mListener.onTaskComplete(null, new Exception(s));
    }


    private interface OnFooPostExecute {
        boolean onTaskComplete(@Nullable String response, @Nullable Exception e);
    }

    public interface OnRequesterTaskComplete {
        void onPlayerResponse(List<FPlayer> players, Exception e);
        void onPlayerDeleteResponse(boolean success, Exception e);
        void onTeamResponse(List<FTeam> teams, Exception e);
        void onTeamDeleteResponse(boolean success, Exception e);
        void onGameResponse(List<FGame> games, Exception e);
        void onAchievementsResponse(List<Achievements> achievements, Exception e);

        void onPlayerPushResponse(List<FPlayer> players, Exception e);
        void onTeamPushResponse(List<FTeam> teams, Exception e);
        void onGamePushResponse(List<FGame> games, Exception e);
    }

}
