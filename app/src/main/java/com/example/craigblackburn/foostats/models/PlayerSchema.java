package com.example.craigblackburn.foostats.models;

import android.support.annotation.NonNull;
import com.example.craigblackburn.foostats.FPlayer;

/**
 *
 * @description :: Player Table Schema
 *
 * */
public class PlayerSchema extends BaseSchema {

    private PlayerSchema() {}

    public final static String TABLE_NAME = "Player";
    public final static String COLUMN_ID = "uuid";
    public final static String COLUMN_FACEBOOK_ID = "facebook_id";
    public final static String COLUMN_EMAIL = "email";
    public final static String COLUMN_FIRST_NAME = "firstName";
    public final static String COLUMN_LAST_NAME = "lastName";
    public final static String COLUMN_ROLE = "role";
    public final static String COLUMN_USERNAME = "username";
    public final static String COLUMN_ACHIEVEMENTS = "achievements";

    public final static String SQL_CREATE_TABLE_STATEMENT = CREATE_TABLE + TABLE_NAME + OPEN
            + COLUMN_ID + TEXT + PRIMARY_KEY + REQUIRED + UNIQUE + COMMA
            + COLUMN_FACEBOOK_ID + TEXT + COMMA
            + COLUMN_EMAIL + TEXT + UNIQUE + COMMA
            + COLUMN_FIRST_NAME + TEXT + REQUIRED + COMMA
            + COLUMN_LAST_NAME + TEXT + REQUIRED + COMMA
            + COLUMN_ROLE + TEXT + COMMA
            + COLUMN_USERNAME + TEXT + REQUIRED + COMMA
            + COLUMN_ACHIEVEMENTS + " text references Achievements(uuid)"
            + CLOSE + END;

    public final static String sqlGetTeamsStatement(@NonNull FPlayer player) {
        String searchTable = TeamSchema.TABLE_NAME;
        String joinTable = TeamsSchema.TABLE_NAME;
        String searchTableColumnIDName = TeamSchema.COLUMN_ID;
        String joinTableColumnID_1 = TeamsSchema.COLUMN_TEAM;
        String joinTableColumnID_2 = TeamsSchema.COLUMN_PLAYER;
        String searchId = player.getId();
        return sqlManyToManyQueryBuilder(searchTable, joinTable, searchTableColumnIDName, joinTableColumnID_1, joinTableColumnID_2, searchId);
    }
}
