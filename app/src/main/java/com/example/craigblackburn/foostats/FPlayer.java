package com.example.craigblackburn.foostats;


import android.content.Context;

import java.util.ArrayList;

public class FPlayer extends FModels {

    private String uuid, email, firstName, lastName, role, username, name;
    private FTeam[] teams;
    private Achievements[] achievements;

    public FPlayer() {}

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
        ArrayList<FTeam> list = new ArrayList<>();
        for (int i = 0; i < this.teams.length; i++) {
            list.add(this.teams[i]);
        }
        return list;
    }
    public ArrayList<Achievements> getAchievments() {
        ArrayList<Achievements> list = new ArrayList<>();
        for (int i = 0; i < this.achievements.length; i++) {
            list.add(this.achievements[i]);
        }
        return list;
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
        for (int i = 0; i < array.length; i++) {
            if (array[i] != ',') {
                tempId += String.valueOf(array[i]);
            } else {
                teamIds.add(tempId);
                tempId = "";
            }
        }

        for (String id : teamIds) {
            FTeam team = DBHelper.findTeamById(id);
            if (team != null) {
                teams.add(team);
            }
        }

        return teams;
    }


    /**
     * Example response
    {
        "teams":[
        {
            "uuid":"a0bb332d-6040-41dd-af76-0dcab3862a66",
                "name":"TeamAwesome",
                "createdAt":"2016-12-13T22:55:45.000Z",
                "updatedAt":"2016-12-13T22:55:45.000Z"
        }
        ],
        "achievements":[

        ],
        "uuid":"db71d47a-27cc-45a9-9085-92777436ad52",
            "email":"cr.blackburn89@gmail.com",
            "firstName":"Craig",
            "lastName":"Blackburn",
            "role":"admin",
            "username":"Craig Blackburn",
            "createdAt":"2016-12-08T21:50:57.000Z",
            "updatedAt":"2016-12-13T21:24:21.000Z",
            "name":"Craig Blackburn"
    }
    */
}
