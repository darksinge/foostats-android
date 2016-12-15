package com.example.craigblackburn.foostats;

import android.database.SQLException;

import java.util.ArrayList;
import java.util.List;

public class FTeam extends FModel {

    interface TeamDelegate {
        void onTaskComplete(ArrayList<FTeam> list);
    }

    private FPlayer player1, player2;

    private String uuid, teamName;

    public FTeam(){}

    public FTeam(String id, String name,FPlayer p1, FPlayer p2) {
        uuid = id;
        teamName = name;
        player1 = p1;
        player2 = p2;
    }

    public String getId() {
        return this.uuid;
    }

    public String getTeamName() {
        return this.teamName;
    }

    public void setTeamName(String name) {
        this.teamName = name;
    }

    public FPlayer[] getPlayers() {
        return new FPlayer[]{this.player1, this.player2};
    }

    public FPlayer getPlayerOne() {
        return this.player1;
    }

    public FPlayer getPlayerTwo() {
        return this.player2;
    }

    public void setPlayerOne(FPlayer player) {
        player1 = player;
    }

    public void setPlayerTwo(FPlayer player) {
        player2 = player;
    }

    private boolean canSave() {
        return (this.uuid != null && this.teamName != null && this.player1 != null && this.player2 != null);
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


}
