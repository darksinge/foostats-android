package com.example.craigblackburn.foostats.models;

/**
 *
 * @description :: Game Table Schema
 *
 * */
public class GameSchema extends BaseSchema {

    private GameSchema() {}

    public final static String TABLE_NAME = "Game";
    public final static String ID_COLUMN = "uuid";
    public final static String TEAM_A_COLUMN = "team_a";
    public final static String TEAM_B_COLUMN = "team_b";
    public final static String PLAYER_1_SCORE_COLUMN = "player_one_score";
    public final static String PLAYER_2_SCORE_COLUMN = "player_two_score";
    public final static String PLAYER_3_SCORE_COLUMN = "player_three_score";
    public final static String PLAYER_4_SCORE_COLUMN = "player_four_score";
    public final static String GAME_LENGTH_IN_SECONDS_COLUMN = "game_length_in_seconds";
    public final static String GAME_DATE_COLUMN = "created_date";
    public final static String GAME_MATCH_TOKEN_COLUMN = "match_token";
    public final static String GAME_MATCH_ORDER_COLUMN = "match_order";

    public final static String SQL_CREATE_TABLE_STATEMENT = CREATE_TABLE + TABLE_NAME + OPEN
            + ID_COLUMN + TEXT + PRIMARY_KEY + UNIQUE + COMMA
            + TEAM_A_COLUMN + TEXT + REQUIRED + REFERENCES + TeamSchema.TABLE_NAME + "(" + TeamSchema.COLUMN_ID + ")" + COMMA
            + TEAM_B_COLUMN + TEXT + REQUIRED + REFERENCES + TeamSchema.TABLE_NAME + "(" + TeamSchema.COLUMN_ID + ")" + COMMA
            + PLAYER_1_SCORE_COLUMN + INTEGER + DEFAULT_0 + COMMA
            + PLAYER_2_SCORE_COLUMN + INTEGER + DEFAULT_0 + COMMA
            + PLAYER_3_SCORE_COLUMN + INTEGER + DEFAULT_0 + COMMA
            + PLAYER_4_SCORE_COLUMN + INTEGER + DEFAULT_0 + COMMA
            + GAME_LENGTH_IN_SECONDS_COLUMN + INTEGER + DEFAULT_0 + COMMA
            + GAME_DATE_COLUMN + TEXT + REQUIRED + COMMA
            + GAME_MATCH_TOKEN_COLUMN + TEXT + REQUIRED + COMMA
            + GAME_MATCH_ORDER_COLUMN + INTEGER + DEFAULT_0
            + CLOSE + END;

}
