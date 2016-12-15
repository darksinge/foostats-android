package com.example.craigblackburn.foostats;

import android.database.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public boolean hasPlayerWithId(String id) {
        if (Objects.equals(player1.getId(), id)) return true;
        if (Objects.equals(player2.getId(), id)) return true;
        return false;
    }

    public int removePlayer(FPlayer player) {
        if (Objects.equals(player1.getId(), player.getId())){
            player1 = null;
            return 0;
        }
        if (Objects.equals(player2.getId(), player.getId())) {
            player2 = null;
            return 1;
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
        return team.getId() + ";" + team.getTeamName() + ";" + team.getPlayerOne().getId() + ";" + team.getPlayerTwo().getId();
    }

    public static FTeam deserialize(String string) {
        FTeam team = null;
        String[] components = string.split(";");
        if (components.length == 4) {
            try {
                team = new FTeam(components[0], components[1], FPlayer.find(components[2]), FPlayer.find(components[3]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return team;
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
        return -1;
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


}
