package com.example.craigblackburn.foostats.models;

/**
 * Created by craigblackburn on 12/21/16.
 */

public class AchievementSchema extends BaseSchema {

    private AchievementSchema() {}

    public static final String TABLE_NAME = "Achievement";
    public static final String COLUMN_ID = "uuid";
    public static final String COLUMN_PLAYER = "player";
    public static final String COLUMN_PLAYED_1_GAME = "played_1_game";

    public static final String SQL_CREATE_TABLE_STATEMENT = CREATE_TABLE + TABLE_NAME + OPEN
            + COLUMN_ID + TEXT + PRIMARY_KEY + REQUIRED + COMMA
            + COLUMN_PLAYER + " text references " + PlayerSchema.TABLE_NAME + "(" + PlayerSchema.COLUMN_ID + ")" + COMMA
            + COLUMN_PLAYED_1_GAME + " integer default 0"
            + CLOSE + END;

}
