package com.example.craigblackburn.foostats.models;

import android.support.annotation.NonNull;
import com.example.craigblackburn.foostats.FPlayer;

/**
 *
 * @description :: Team Table Schema
 *
 * */
public class TeamSchema extends BaseSchema {

    private TeamSchema() {}

    public final static String TABLE_NAME = "Team";
    public final static String COLUMN_ID = "uuid";
    public final static String COLUMN_TEAM_NAME = "team_name";
    public final static String PLAYER_1_COLUMN = "player_1";
    public final static String PLAYER_2_COLUMN = "player_2";

    public static final String SQL_CREATE_TABLE_STATEMENT = CREATE_TABLE + TABLE_NAME + OPEN
            + COLUMN_ID + TEXT + REQUIRED + UNIQUE + COMMA
            + COLUMN_TEAM_NAME + TEXT + UNIQUE + REQUIRED + COMMA
            + PLAYER_1_COLUMN + TEXT + REFERENCES + PlayerSchema.TABLE_NAME + surroundWithParenthesis(PlayerSchema.COLUMN_ID) + COMMA
            + PLAYER_2_COLUMN + TEXT + REFERENCES + PlayerSchema.TABLE_NAME + surroundWithParenthesis(PlayerSchema.COLUMN_ID)
            + CLOSE + END;
}
