package com.example.craigblackburn.foostats.models;

/**
 *
 * @description :: Team<--->Game Join Table Schema
 *
 * */
public class GamesSchema extends BaseSchema {
    public final static String TABLE_NAME = "Games";
    public final static String GAME_COLUMN = "game";
    public final static String TEAM_COLUMN = "team";

    public final static String SQL_CREATE_TABLE_STATEMENT =
            CREATE_TABLE + TABLE_NAME + OPEN
                    + GAME_COLUMN + TEXT + REQUIRED + REFERENCES + GameSchema.TABLE_NAME + surroundWithParenthesis(GameSchema.ID_COLUMN) + COMMA
                    + TEAM_COLUMN + TEXT + REQUIRED + REFERENCES + TeamSchema.TABLE_NAME + surroundWithParenthesis(TeamSchema.COLUMN_ID) + COMMA
                    + PRIMARY_KEY + OPEN + GAME_COLUMN + COMMA + TEAM_COLUMN + CLOSE
                    + CLOSE + END;

}