package com.example.craigblackburn.foostats;

import android.database.SQLException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FTeam extends FModel {

    private static final String TAG = "FTEAM";

    interface TeamDelegate {
        void onTaskComplete(ArrayList<FTeam> list);
    }

    private FPlayer player1, player2;
    private List<FPlayer> playerOverflow;

    private String uuid, teamName;

    public FTeam(){}

    public FTeam(String id, String name,FPlayer p1, FPlayer p2) {
        uuid = id;
        teamName = name;
        player1 = p1;
        player2 = p2;
        playerOverflow = new ArrayList<>();
    }

    public String getId() {
        if (this.uuid == null)
            this.uuid = generateUuid();
        return this.uuid;
    }

    public String getDescription() {
        String desc = "\tName: " + getTeamName();
        if (uuid != null) desc += "\n\tId: " + getId();
        if (player1 != null) {
            desc += "\n\tPlayer 1: " + player1.getName();
        }
        if (player2 != null) {
            desc += "\n\tPlayer 2: " + player2.getName();
        }
        desc += "\n\tPlayer Count: " + playerCount();
        desc += "\n\tPlayer Overflow Count: " + playerOverflow.size();
        return desc;
    }

    public String getTeamName() {
        return this.teamName;
    }

    public void setTeamName(String name) {
        this.teamName = name;
    }

    public List<FPlayer> getPlayers() {
        List<FPlayer> list = new ArrayList<>();
        if (player1 != null)
            list.add(player1);
        if (player2 != null)
            list.add(player2);
        if (playerOverflow != null) {
            for (FPlayer p:playerOverflow) {
                list.add(p);
            }
        }
        return list;
    }

    public FPlayer getPlayerOne() {
        return this.player1;
    }

    public FPlayer getPlayerTwo() {
        return this.player2;
    }

    public boolean hasPlayerWithId(String id) {
        if (Objects.equals(player1.getId(), id)) return true;
        if (Objects.equals(player2.getId(), id)) return true;
        return false;
    }

    public int removePlayer(@NonNull FPlayer player) {
        if (player == null)
            return -1;
        if (player1 != null) {
            if (Objects.equals(player1.getId(), player.getId())){
                player1 = null;
                return 0;
            }
        }

        if (player2 != null) {
            if (Objects.equals(player2.getId(), player.getId())) {
                player2 = null;
                return 1;
            }
        }

        return -1;
    }

    public void setPlayerOne(FPlayer player) { player1 = player; }

    public String generateName() {
        FTeam team = findTeamByPlayers(this.player1, this.player2);
        if (team != null) {
            return team.getTeamName();
        }
        return this.teamName = "Team " + player1.getFirstName() + " " + player2.getFirstName();
    }

    public static String serialize(FTeam team) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("uuid", team.getId());
            obj.put("teamName", team.getTeamName());
            if (team.getPlayerOne() != null)
                obj.put("player1", FPlayer.serializePlayer(team.getPlayerOne()));
            if (team.getPlayerTwo() != null)
                obj.put("player2", FPlayer.serializePlayer(team.getPlayerTwo()));
            return obj.toString();
        } catch (JSONException e) {
            // this should never get here!
            e.printStackTrace();
            return "";
        }
    }

    public static JSONArray serialize(List<FTeam> teams) {
        JSONArray jsonArray = new JSONArray();

        try {
            for (FTeam team : teams) {
                JSONObject obj = new JSONObject();
                obj.put("uuid", team.getId());
                obj.put("teamName", team.getTeamName());
                if (team.getPlayerOne() != null)
                    obj.put("player1", FPlayer.serializePlayer(team.getPlayerOne()));
                if (team.getPlayerTwo() != null)
                    obj.put("player2", FPlayer.serializePlayer(team.getPlayerTwo()));
                jsonArray.put(obj);
            }
        } catch (JSONException e) {
            // this should never get here!
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }

        return jsonArray;

    }

    public static FTeam deserialize(String jsonString) {
        FTeam team = null;
        JSONObject obj;

        try {
            obj = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        try {
            String id = obj.getString("uuid");
            String name = obj.getString("teamName");

            team = new FTeam(id, name, null, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            String player1Serialized = obj.getString("player1");
            team.setPlayerOne(FPlayer.deserializePlayer(player1Serialized));
        } catch (JSONException e) {
            Log.d(TAG, "No value for player 1, FTeam deserializer.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String player2Serialized = obj.getString("player2");
            team.setPlayerTwo(FPlayer.deserializePlayer(player2Serialized));
        } catch (JSONException e) {
            Log.d(TAG, "No value for player 1, FTeam deserializer.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return team;
    }

    public static List<FTeam> deserialize(JSONArray jsonArray) {
        List<FTeam> teams = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                FTeam team = FTeam.deserialize(jsonArray.get(i).toString());
                if (team != null)
                    teams.add(team);
            } catch (JSONException e) {
                // this should also never get here!
                e.printStackTrace();
            }
        }
        return teams;
    }

    public static FTeam findTeamByPlayers(FPlayer p1, FPlayer p2) {
        try {
            List<FTeam> teams = FTeam.find();
            for (FTeam team : teams) {
                if (team.hasPlayerWithId(p1.getId()) && team.hasPlayerWithId(p2.getId())) {
                    return team;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int playerCount() {
        int count = 0;
        if (this.player1 != null) count++;
        if (this.player2 != null) count++;
        return count;
    }

    public int addPlayer(FPlayer player) {
        if (player1 == null) {
            setPlayerOne(player);
            return 0;
        } else if (player2 == null) {
            setPlayerTwo(player);
            return 1;
        }

        // Add players to team in a queue-like FILO manner.
        List<FPlayer> players = getPlayers();
        players.add(0, player);
        player1 = players.remove(0);
        player2 = players.remove(0);
        for (FPlayer p : players) {
            playerOverflow.add(p);
        }
        return playerOverflow.size() + 1;
    }

    public void setPlayerTwo(FPlayer player) {
        player2 = player;
    }

    private boolean canSave() {
        return (this.uuid != null && this.teamName != null);
    }

    public boolean save() {
        if (canSave())
            return helper.insert(this) > 0;
        else
            return false;
    }

    public static List<FTeam> find() throws Exception {
        if (helper != null) {
            return helper.findTeams();
        }
        throw new Exception("DBHelper instance has not been initialized.");
    }

    public static FTeam find(String id) throws Exception {
        if (helper != null) {
            return helper.findTeamById(id);
        }
        throw new Exception("DBHelper instance has not been initialized.");
    }


}
