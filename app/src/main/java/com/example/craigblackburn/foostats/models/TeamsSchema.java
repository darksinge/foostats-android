package com.example.craigblackburn.foostats.models;

/**
 *
 * @description :: Player<--->Team Join Table Schema
 *
 * */
public class TeamsSchema extends BaseSchema {
    public final static String TABLE_NAME = "Teams";
    public final static String COLUMN_TEAM = "team";
    public final static String COLUMN_PLAYER = "player";

    public final static String SQL_CREATE_TABLE_STATEMENT = CREATE_TABLE + TABLE_NAME + OPEN
            + COLUMN_PLAYER + REFERENCES + PlayerSchema.TABLE_NAME + "(" + PlayerSchema.COLUMN_ID + ")" + COMMA
            + COLUMN_TEAM + REFERENCES + TeamSchema.TABLE_NAME + "(" + TeamSchema.COLUMN_ID + ")"
            + CLOSE + END;
}