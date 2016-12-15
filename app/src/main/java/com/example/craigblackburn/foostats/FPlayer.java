package com.example.craigblackburn.foostats;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//     Example response
//    {
//        "teams":[
//        {
//            "uuid":"a0bb332d-6040-41dd-af76-0dcab3862a66",
//                "name":"TeamAwesome",
//                "createdAt":"2016-12-13T22:55:45.000Z",
//                "updatedAt":"2016-12-13T22:55:45.000Z"
//        }
//        ],
//        "achievements":[
//
//        ],
//        "uuid":"db71d47a-27cc-45a9-9085-92777436ad52",
//            "email":"cr.blackburn89@gmail.com",
//            "firstName":"Craig",
//            "lastName":"Blackburn",
//            "role":"admin",
//            "username":"Craig Blackburn",
//            "createdAt":"2016-12-08T21:50:57.000Z",
//            "updatedAt":"2016-12-13T21:24:21.000Z",
//            "name":"Craig Blackburn"
//    }

public class FPlayer extends FModel {

    interface PlayerDelegate {
        void onTaskComplete(ArrayList<FPlayer> list);
    }

    private String uuid, facebookId, email, firstName, lastName, role, username;
    private List<FTeam> teams;
    private List<Achievements> achievements;

    public FPlayer() {}

    public FPlayer(String id, String facebookId, String email, String firstname, String lastname, String role, String username, List<FTeam> teams) {
        this.uuid = id;
        this.facebookId = facebookId;
        this.email = email;
        this.firstName = firstname;
        this.lastName = lastname;
        this.role = role;
        this.username = username;
        this.teams = teams;
    }

    public FPlayer(String id, String facebookId, String email, String firstname, String lastname, String role, String username) {
        this.uuid = id;
        this.facebookId = facebookId;
        this.email = email;
        this.firstName = firstname;
        this.lastName = lastname;
        this.role = role;
        this.username = username;
        this.teams = new ArrayList<>();
    }

    public void setId(String id) { this.uuid = id; }
    public String getId() {
        return this.uuid;
    }

    public String getFacebookId() { return this.facebookId; }

    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.firstName + " " + this.lastName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getRole() {
        return this.role;
    }

    public String getUsername() { return this.username; }

    public List<FTeam> getTeams() {
        return this.teams;
    }

    public List<Achievements> getAchievments() {
        return this.achievements;
    }

    public static String serializePlayer(FPlayer player) {
        Gson gson = new Gson();
        return gson.toJson(player);
    }

    public static FPlayer deserializePlayer(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new FPlayer();
    }

    private boolean canSave() {
        return this.uuid != null;
    }

    public boolean save() {
        if (canSave())
            return helper.insert(this) > 0;
        else
            return false;
    }

    public static List<FPlayer> find() throws Exception {
        if (helper != null)
            return helper.findPlayers();
        throw new Exception("DBHelper instance has not been initialized!");
    }

    public static FPlayer find(String id) throws Exception {
        if (helper != null)
            return helper.findPlayerById(id);
        throw new Exception("DBHelper instance has not been initialized!");
    }

    public String toString() {
        return "Name: " + getName()
                + "\nID: " + getId()
                + "\nEmail: " + getEmail()
                + "\nRole: " + getRole();
    }

    public void addTeam(FTeam team) {
        List<FTeam> list = getTeams();
        if (!list.contains(team)) {
            teams.add(team);
        }
    }

}
