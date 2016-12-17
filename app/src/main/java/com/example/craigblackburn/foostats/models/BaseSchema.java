package com.example.craigblackburn.foostats.models;

import android.support.annotation.NonNull;

import com.example.craigblackburn.foostats.FPlayer;

/**
 * Created by craigblackburn on 12/16/16.
 */

public class BaseSchema {

    private BaseSchema() {}

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";
    public static final String END = ";";
    public static final String COMMA = ",";
    public static final String OPEN = "(";
    public static final String CLOSE = ")";
    public static final String TEXT = " TEXT ";
    public static final String INTEGER = " INTEGER ";
    public static final String REQUIRED = " NOT NULL ";
    public static final String UNIQUE = " UNIQUE ";
    public static final String PRIMARY_KEY = " PRIMARY KEY ";
    public static final String FOREIGN_KEY = " FOREIGN KEY ";
    public static final String REFERENCES = " REFERENCES ";
    public static final String CASCADE_ON_UPDATE = " ON DELETE CASCADE ";
    public static final String CASCASE_ON_DELETE = " ON UPDATE CASCADE ";
    public static final String QUOTE = "'";
    public static final String DOT = ".";
    public static final String WHERE = " WHERE ";
    public static final String SELECT = " SELECT ";
    public static final String ALL = " * ";
    public static final String FROM = " FROM ";
    public static final String ON = " ON ";
    public static final String EQUALS = "=";
    public static final String LEFT_JOIN = " LEFT JOIN ";
    public static final String SELECT_ALL_FROM = SELECT + ALL + FROM;

    public static String surroundWithQuotes(String statement) {
        return QUOTE + statement + QUOTE;
    }

    public static final String foreignKeyBuilder(String columnName, String referenceTable, String referenceTableId, boolean onCascadeUpdate, boolean onCascadeDelete) {
        String optionCascadeUpdate = "";
        String optionCascadeDelete = "";
        if (onCascadeUpdate) {
            optionCascadeUpdate = CASCADE_ON_UPDATE;
        }

        if (onCascadeDelete) {
            optionCascadeDelete = CASCASE_ON_DELETE;
        }

        return FOREIGN_KEY + OPEN
                + columnName + CLOSE
                + REFERENCES + referenceTable + "(" + referenceTableId + ")" + optionCascadeUpdate + optionCascadeDelete + ";";
    }

    public static String sqlManyToManyQueryBuilder(String table, String joinTable, String table_column_id, String joinTable_column_1, String joinTable_column_2, String searchId) {
        return SELECT_ALL_FROM + table
                + LEFT_JOIN + joinTable
                + ON + table + DOT + table_column_id + EQUALS + joinTable + DOT + joinTable_column_1
                + WHERE + joinTable + DOT + joinTable_column_2 + EQUALS + surroundWithQuotes(searchId) + END;
    }


    public final static class PlayerSchema extends BaseSchema {

        private PlayerSchema() {}

        public final static String TABLE_NAME = "players";
        public final static String COLUMN_ID = "uuid";
        public final static String COLUMN_FACEBOOK_ID = "facebook_id";
        public final static String COLUMN_EMAIL = "email";
        public final static String COLUMN_FIRST_NAME = "firstName";
        public final static String COLUMN_LAST_NAME = "lastName";
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_ROLE = "role";
        public final static String COLUMN_USERNAME = "username";
        public final static String COLUMN_TEAMS = "teams";
        public final static String COLUMN_ACHIEVEMENTS = "achievements";
        public final static String COLUMN_WINS = "numWins";

        public final static String SQL_CREATE_TABLE_STATEMENT = CREATE_TABLE + TABLE_NAME + OPEN
                + COLUMN_ID + TEXT + PRIMARY_KEY + REQUIRED + UNIQUE + COMMA
                + COLUMN_FACEBOOK_ID + TEXT + COMMA
                + COLUMN_EMAIL + TEXT + UNIQUE + COMMA
                + COLUMN_FIRST_NAME + TEXT + COMMA
                + COLUMN_LAST_NAME + TEXT + COMMA
                + COLUMN_NAME + TEXT + COMMA
                + COLUMN_ROLE + TEXT + COMMA
                + COLUMN_USERNAME + TEXT + COMMA
                + foreignKeyBuilder(COLUMN_TEAMS, PlayerTeamSchema.TABLE_NAME, PlayerTeamSchema.COLUMN_PLAYER_ID, true, false) + COMMA
                + COLUMN_ACHIEVEMENTS + INTEGER + COMMA
                + COLUMN_WINS + TEXT
                + CLOSE + END;

        public final static String sqlGetTeamsStatement(@NonNull FPlayer player) {
            String searchTable = TeamSchema.TABLE_NAME;
            String joinTable = PlayerTeamSchema.TABLE_NAME;
            String searchTableColumnIDName = TeamSchema.COLUMN_ID;
            String joinTableColumnID_1 = PlayerTeamSchema.COLUMN_TEAM_ID;
            String joinTableColumnID_2 = PlayerTeamSchema.COLUMN_PLAYER_ID;
            String searchId = player.getId();
            return sqlManyToManyQueryBuilder(searchTable, joinTable, searchTableColumnIDName, joinTableColumnID_1, joinTableColumnID_2, searchId);
        }
    }

    /**
     *
     * @description :: Player Team Join Table declaration
     *
     * */
    public final static class TeamSchema extends BaseSchema {

        private TeamSchema() {}

        public final static String TABLE_NAME = "teams";
        public final static String COLUMN_ID = "uuid";
        public final static String COLUMN_NAME = "team_name";
        public final static String COLUMN_PLAYERS = "players";
        public final static String COLUMN_GAMES = "games";

        public static final String SQL_CREATE_TABLE_STATEMENT = CREATE_TABLE + TABLE_NAME + OPEN
                + COLUMN_ID + TEXT + REQUIRED + UNIQUE + COMMA
                + COLUMN_NAME + TEXT + UNIQUE + REQUIRED + COMMA + END;

        public final static String sqlGetTeamsStatement(@NonNull FPlayer player) {
            String searchTable = TeamSchema.TABLE_NAME;
            String joinTable = PlayerTeamSchema.TABLE_NAME;
            String searchTableColumnIDName = TeamSchema.COLUMN_ID;
            String joinTableColumnID_1 = PlayerTeamSchema.COLUMN_TEAM_ID;
            String joinTableColumnID_2 = PlayerTeamSchema.COLUMN_PLAYER_ID;
            String searchId = player.getId();
            return sqlManyToManyQueryBuilder(searchTable, joinTable, searchTableColumnIDName, joinTableColumnID_1, joinTableColumnID_2, searchId);
        }

    }


    /**
     *
     * @description :: Player Team Join Table declaration
     *
     * */
    public final class PlayerTeamSchema extends BaseSchema {
        public final static String TABLE_NAME = "player_has_teams__team_has_players";
        public final static String COLUMN_TEAM_ID = "team_uuid";
        public final static String COLUMN_PLAYER_ID = "player_uuid";

        public final static String SQL_CREATE_TABLE_STATEMENT = CREATE_TABLE + TABLE_NAME + OPEN
                + COLUMN_TEAM_ID + TEXT + REQUIRED + COMMA
                + COLUMN_PLAYER_ID + TEXT + REQUIRED + COMMA
                + PRIMARY_KEY + OPEN + TeamSchema.COLUMN_ID + COMMA + PlayerSchema.COLUMN_ID + CLOSE
                + CLOSE + END;

    }

    public static final class TeamGameSchema extends BaseSchema {
        public final static String TABLE_NAME = "team_has_games__game_has_teams";
        public final static String GAME_COLUMN = "game";
        public final static String TEAM_COLUMN = "team";
        public final static String IS_BLUE_COLUMN = "is_blue";
        public final static String PLAYER_ONE_SCORE_COLUMN = "player_one_score";
        public final static String PLAYER_TWO_SCORE_COLUMN = "player_two_score";

        public final static String SQL_CREATE_TABLE_STATEMENT = CREATE_TABLE + TABLE_NAME + OPEN
                + GAME_COLUMN +

    }

    public static final class GameSchema extends BaseSchema {

        private GameSchema() {}

        public final static String TABLE_NAME = "games";
        public final static String ID_COLUMN_COLUMN = "uuid";
        public final static String BLUE_TEAM_COLUMN = "blue_team";
        public final static String RED_TEAM_COLUMN = "red_team";
        public final static String PLAYER1_SCORE_COLUMN = "blue_player1_score";
        public final static String PLAYER2_SCORE_COLUMN = "blue_player2_score";
        public final static String PLAYER3_SCORE_COLUMN = "red_player1_score";
        public final static String PLAYER4_SCORE_COLUMN = "red_player2_score";
        public final static String GAME_LENGTH_IN_SECONDS_COLUMN = "game_length_in_seconds";
        public final static String GAME_DATE_COLUMN = "createdAt";
        public final static String GAME_MATCH_TOKEN_COLUMN = "match_token";
        public final static String GAME_MATCH_ORDER_COLUMN = "match_order";

        public final static String SQL_CREATE_TABLE_STATEMENT = CREATE_TABLE + TABLE_NAME + OPEN
                + ID_COLUMN_COLUMN + PRIMARY_KEY + UNIQUE + REQUIRED
                + BLUE_TEAM_COLUMN +

    }



}
