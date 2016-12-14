package com.example.craigblackburn.foostats;

import java.util.ArrayList;

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

public class FPlayer extends FModels {

    interface PlayerDelegate {
        void onTaskComplete(ArrayList<FPlayer> list);
    }

    private String uuid, email, firstName, lastName, role, username;
    private ArrayList<FTeam> teams;
    private ArrayList<Achievements> achievements;

    public FPlayer() {}

    public FPlayer(String id, String email, String firstname, String lastname, String role, String username, ArrayList<FTeam> teams) {
        this.uuid = id;
        this.email = email;
        this.firstName = firstname;
        this.lastName = lastname;
        this.role = role;
        this.username = username;
        this.teams = teams;
    }

    public String getId() {
        return this.uuid;
    }

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

    public String getUsername() {
        return this.username;
    }

    public ArrayList<FTeam> getTeams() {
        return this.teams;
    }

    public ArrayList<Achievements> getAchievments() {
        return this.achievements;
    }

    public static String serializeTeams(FPlayer player) {
        String flatten = "";
        ArrayList<FTeam> teams = player.getTeams();
        for(FTeam team : teams) {
            flatten += team.getId() + ",";
        }

        if (flatten.endsWith(",")) {
            return flatten.substring(0, flatten.length() - 1);
        } else {
            return flatten;
        }
    }

    public static ArrayList<FTeam> deserializeTeams(String rawValue) {
        ArrayList<FTeam> teams = new ArrayList<>();
        ArrayList<String> teamIds = new ArrayList<>();
        String tempId = "";
        char[] array = rawValue.toCharArray();
        for (char character : array) {
            if (character != ',') {
                tempId += character;
            } else {
                teamIds.add(tempId);
                tempId = "";
            }
        }

        for (String id : teamIds) {
            FTeam team = helper.findTeamById(id);
            if (team != null) {
                teams.add(team);
            }
        }

        return teams;
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

    public static ArrayList<FPlayer> find() {
        if (helper != null)
            return helper.findPlayers();
        else
            return null;
    }

    public String toString() {
        return "Name: " + getName()
                + "\nID: " + getId()
                + "\nEmail: " + getEmail()
                + "\nRole: " + getRole();
    }

}
