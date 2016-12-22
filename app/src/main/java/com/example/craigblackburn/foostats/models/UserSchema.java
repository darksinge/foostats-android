package com.example.craigblackburn.foostats.models;

/**
 * Created by craigblackburn on 12/21/16.
 */

public class UserSchema {

    public static final String TABLE_NAME = "User";
    public static final String COLUMN_ID = "uuid";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_ACCESS_TOKEN = "access_token";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PLAYER = "player";

    public static final String SQL_CREATE_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + COLUMN_ID + " TEXT PRIMARY KEY UNIQUE NOT NULL,"
            + COLUMN_EMAIL + " TEXT UNIQUE NOT NULL,"
            + COLUMN_ACCESS_TOKEN + " TEXT NOT NULL,"
            + COLUMN_NAME + " TEXT NOT NULL,"
            + COLUMN_PLAYER + " TEXT REFERENCES " + PlayerSchema.TABLE_NAME + "(" + PlayerSchema.COLUMN_ID + ")"
            + ");";

}
