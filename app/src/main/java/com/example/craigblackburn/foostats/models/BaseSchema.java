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
    public static final String DEFAULT_0 = " DEFAULT 0";
    public static final String SELECT_ALL_FROM = SELECT + ALL + FROM;

    public static final String surroundWithQuotes(String statement) { return QUOTE + statement + QUOTE; }
    public static final String surroundWithParenthesis(String string) { return OPEN + string + CLOSE; }

    public static final String foreignKeyBuilder(String columnName, String referenceTable, String referenceTableId, boolean onCascadeUpdate, boolean onCascadeDelete) {
        String optionCascadeUpdate = "";
        String optionCascadeDelete = "";
        if (onCascadeUpdate) {
            optionCascadeUpdate = CASCADE_ON_UPDATE;
        }

        if (onCascadeDelete) {
            optionCascadeDelete = CASCASE_ON_DELETE;
        }

        return FOREIGN_KEY + OPEN + columnName + CLOSE
                + REFERENCES + referenceTable + "(" + referenceTableId + ")"
                + optionCascadeUpdate + optionCascadeDelete + ";";
    }

    public static String sqlManyToManyQueryBuilder(String table, String joinTable, String table_column_id, String joinTable_column_1, String joinTable_column_2, String searchId) {
        return SELECT_ALL_FROM + table
                + LEFT_JOIN + joinTable
                + ON + table + DOT + table_column_id + EQUALS + joinTable + DOT + joinTable_column_1
                + WHERE + joinTable + DOT + joinTable_column_2 + EQUALS + surroundWithQuotes(searchId) + END;
    }



    /**********************************************************************************************/
    /***************************************   TABLES   *******************************************/
    /**********************************************************************************************/

    /**
     *
     * @description :: Player Table Schema
     *
     * */
    public final static class PlayerSchema extends BaseSchema {

        private PlayerSchema() {}

        public final static String TABLE_NAME = "Players";
        public final static String COLUMN_ID = "uuid";
        public final static String COLUMN_FACEBOOK_ID = "facebook_id";
        public final static String COLUMN_EMAIL = "email";
        public final static String COLUMN_FIRST_NAME = "firstName";
        public final static String COLUMN_LAST_NAME = "lastName";
        public final static String COLUMN_ROLE = "role";
        public final static String COLUMN_USERNAME = "username";
//        public final static String COLUMN_ACHIEVEMENTS = "achievements";

        public final static String SQL_CREATE_TABLE_STATEMENT = CREATE_TABLE + TABLE_NAME + OPEN
                + COLUMN_ID + TEXT + PRIMARY_KEY + REQUIRED + UNIQUE + COMMA
                + COLUMN_FACEBOOK_ID + TEXT + COMMA
                + COLUMN_EMAIL + TEXT + UNIQUE + COMMA
                + COLUMN_FIRST_NAME + TEXT + COMMA
                + COLUMN_LAST_NAME + TEXT + COMMA
                + COLUMN_ROLE + TEXT + COMMA
                + COLUMN_USERNAME + TEXT + COMMA
//                + COLUMN_ACHIEVEMENTS + INTEGER + COMMA
                + CLOSE + END;

        public final static String sqlGetTeamsStatement(@NonNull FPlayer player) {
            String searchTable = TeamSchema.TABLE_NAME;
            String joinTable = JoinTeamsPlayersSchema.TABLE_NAME;
            String searchTableColumnIDName = TeamSchema.COLUMN_ID;
            String joinTableColumnID_1 = JoinTeamsPlayersSchema.COLUMN_TEAM;
            String joinTableColumnID_2 = JoinTeamsPlayersSchema.COLUMN_PLAYER;
            String searchId = player.getId();
            return sqlManyToManyQueryBuilder(searchTable, joinTable, searchTableColumnIDName, joinTableColumnID_1, joinTableColumnID_2, searchId);
        }
    }

    /**
     *
     * @description :: Player<--->Team Join Table Schema
     *
     * */
    public final static class JoinTeamsPlayersSchema extends BaseSchema {
        public final static String TABLE_NAME = "Join_Teams_Players";
        public final static String COLUMN_TEAM = "team";
        public final static String COLUMN_PLAYER = "player";

        public final static String SQL_CREATE_TABLE_STATEMENT = CREATE_TABLE + TABLE_NAME + OPEN
                + COLUMN_PLAYER + REFERENCES + PlayerSchema.TABLE_NAME + surroundWithParenthesis(PlayerSchema.COLUMN_ID) + REQUIRED + COMMA
                + COLUMN_TEAM + REFERENCES + TeamSchema.TABLE_NAME + surroundWithParenthesis(TeamSchema.COLUMN_ID) + REQUIRED + COMMA
                + PRIMARY_KEY + OPEN + COLUMN_PLAYER + COMMA + COLUMN_TEAM + CLOSE
                + CLOSE + END;
    }

    /**
     *
     * @description :: Team Table Schema
     *
     * */
    public final static class TeamSchema extends BaseSchema {

        private TeamSchema() {}

        public final static String TABLE_NAME = "Teams";
        public final static String COLUMN_ID = "uuid";
        public final static String COLUMN_TEAM_NAME = "team_name";
        public final static String PLAYER_1_COLUMN = "player_1";
        public final static String PLAYER_2_COLUMN = "player_2";

        public static final String SQL_CREATE_TABLE_STATEMENT = CREATE_TABLE + TABLE_NAME + OPEN
                + COLUMN_ID + TEXT + REQUIRED + UNIQUE + COMMA
                + COLUMN_TEAM_NAME + TEXT + UNIQUE + REQUIRED + COMMA
                + PLAYER_1_COLUMN + TEXT + REFERENCES + JoinTeamsPlayersSchema.TABLE_NAME + surroundWithParenthesis(JoinTeamsPlayersSchema.COLUMN_PLAYER) + COMMA
                + PLAYER_2_COLUMN + TEXT + REFERENCES + JoinTeamsPlayersSchema.TABLE_NAME + surroundWithParenthesis(JoinTeamsPlayersSchema.COLUMN_PLAYER) + COMMA
                + CLOSE + END;

        public final static String sqlGetTeamsStatement(@NonNull FPlayer player) {
            String searchTable = TeamSchema.TABLE_NAME;
            String joinTable = JoinTeamsPlayersSchema.TABLE_NAME;
            String searchTableColumnIDName = TeamSchema.COLUMN_ID;
            String joinTableColumnID_1 = JoinTeamsPlayersSchema.COLUMN_TEAM;
            String joinTableColumnID_2 = JoinTeamsPlayersSchema.COLUMN_PLAYER;
            String searchId = player.getId();
            return sqlManyToManyQueryBuilder(searchTable, joinTable, searchTableColumnIDName, joinTableColumnID_1, joinTableColumnID_2, searchId);
        }

    }

    /**
     *
     * @description :: Team<--->Game Join Table Schema
     *
     * */
    public static final class JoinTeamGameSchema extends BaseSchema {
        public final static String TABLE_NAME = "Join_Teams_Games";
        public final static String GAME_COLUMN = "game";
        public final static String TEAM_COLUMN = "team";

        public final static String SQL_CREATE_TABLE_STATEMENT =
                CREATE_TABLE + TABLE_NAME + OPEN
                        + GAME_COLUMN + TEXT + REQUIRED + REFERENCES + GameSchema.TABLE_NAME + surroundWithParenthesis(GameSchema.ID_COLUMN) + COMMA
                        + TEAM_COLUMN + TEXT + REQUIRED + REFERENCES + TeamSchema.TABLE_NAME + surroundWithParenthesis(TeamSchema.COLUMN_ID) + COMMA
                        + PRIMARY_KEY + OPEN + GAME_COLUMN + COMMA + TEAM_COLUMN + CLOSE
                + CLOSE + END;

    }

    /**
     *
     * @description :: Game Table Schema
     *
     * */
    public static final class GameSchema extends BaseSchema {

        private GameSchema() {}

        public final static String TABLE_NAME = "Games";
        public final static String ID_COLUMN = "uuid";
        public final static String TEAM_A_COLUMN = "team_a";
        public final static String TEAM_B_COLUMN = "team_b";
        public final static String TEAM_A_PLAYER_1_SCORE_COLUMN = "player_one_score";
        public final static String TEAM_A_PLAYER_2_SCORE_COLUMN = "player_two_score";
        public final static String TEAM__B_PLAYER_1_SCORE_COLUMN = "player_one_score";
        public final static String TEAM_B_PLAYER_2_SCORE_COLUMN = "player_two_score";
        public final static String GAME_LENGTH_IN_SECONDS_COLUMN = "game_length_in_seconds";
        public final static String GAME_DATE_COLUMN = "created_date";
        public final static String GAME_MATCH_TOKEN_COLUMN = "match_token";
        public final static String GAME_MATCH_ORDER_COLUMN = "match_order";

        public final static String SQL_CREATE_TABLE_STATEMENT = CREATE_TABLE + TABLE_NAME + OPEN
                + ID_COLUMN + PRIMARY_KEY + TEXT + REQUIRED + UNIQUE + COMMA
                + TEAM_A_COLUMN + TEXT + REQUIRED + REFERENCES + JoinTeamGameSchema.TABLE_NAME + surroundWithParenthesis(JoinTeamGameSchema.TEAM_COLUMN) + COMMA
                + TEAM_B_COLUMN + TEXT + REQUIRED + REFERENCES + JoinTeamGameSchema.TABLE_NAME + surroundWithParenthesis(JoinTeamGameSchema.TEAM_COLUMN) + COMMA
                + TEAM_A_PLAYER_1_SCORE_COLUMN + INTEGER + DEFAULT_0 + COMMA
                + TEAM_A_PLAYER_2_SCORE_COLUMN + INTEGER + DEFAULT_0 + COMMA
                + TEAM__B_PLAYER_1_SCORE_COLUMN + INTEGER + DEFAULT_0 + COMMA
                + TEAM_B_PLAYER_2_SCORE_COLUMN + INTEGER + DEFAULT_0 + COMMA
                + GAME_LENGTH_IN_SECONDS_COLUMN + INTEGER + DEFAULT_0 + COMMA
                + GAME_DATE_COLUMN + TEXT + REQUIRED + COMMA
                + GAME_MATCH_TOKEN_COLUMN + TEXT + REQUIRED + COMMA
                + GAME_MATCH_ORDER_COLUMN + INTEGER + DEFAULT_0 + COMMA
                + CLOSE + END;

    }












}
