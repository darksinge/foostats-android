package com.example.craigblackburn.foostats;

import android.content.Context;

public class FTeam {

    private static DBHelper helper;
    private static FPlayer player1, player2;

    private String uuid;

    public static void initialize(Context context) {
        helper = DBHelper.getInstance(context);
    }

    public FTeam(){}

    public FTeam(String id, FPlayer p1, FPlayer p2) {
        uuid = id;
        player1 = p1;
        player2 = p2;
    }

    public String getId() {
        return this.uuid;
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

    public void setPlayers(FPlayer p1, FPlayer p2) {
        setPlayerOne(p1);
        setPlayerTwo(p2);
    }

    public boolean save() {
        return helper.insert(this) > 0;
    }


}
