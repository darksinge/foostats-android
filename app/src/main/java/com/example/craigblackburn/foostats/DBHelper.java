package com.example.craigblackburn.foostats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;


public class DBHelper extends SQLiteOpenHelper {

    public static class DBHelperException extends Exception {
        public DBHelperException(String errorMessage) {
            super(errorMessage);
        }
    }

    private static DBHelper instance;
    private static Context mContext;

    private final static String DATABASE_NAME = "foostats";
    private final static int DB_VERSION = 1;

    private final static String USER_TABLE_NAME = "users";
    private final static String USER_COLUMN_ID = "facebook_id";
    private final static String USER_COLUMN_EMAIL = "email";
    private final static String USER_COLUMN_ACCESS_TOKEN = "access_token";

    private final static String PLAYER_TABLE_NAME = "fplayers";
    private final static String PLAYER_COLUMN_ID = "uuid";
    private final static String PLAYER_COLUMN_EMAIL = "email";
    private final static String PLAYER_COLUMN_FIRSTNAME = "firstName";
    private final static String PLAYER_COLUMN_LASTNAME = "lastName";
    private final static String PLAYER_COLUMN_ROLE = "role";
    private final static String PLAYER_COLUMN_USERNAME = "username";
    private final static String PLAYER_COLUMN_NAME = "name";
    private final static String PLAYER_COLUMN_TEAMS = "teams";
    private final static String PLAYER_COLUMN_ACHIEVEMENTS = "achievements";

    private final static String TEAM_TABLE_NAME = "fteams";
    private final static String TEAM_COLUMN_ID = "uuid";
    private final static String TEAM_COLUMN_PLAYER1 = "player1";
    private final static String TEAM_COLUMN_PLAYER2 = "player2";
    private final static String TEAM_COLUMN_HAS_GAMES = "games";

    private final static String TEAMGAMES_TABLE_NAME = "team_has_games";
    private final static String TEAMGAMES_TEAM_ID = "team_uuid";
    private final static String TEAMGAMES_GAME_ID = "game_uuid";

    private final static String GAME_TABLE_NAME = "fgames";
    private final static String GAME_COLUMN_ID = "uuid";
    private final static String GAME_COLUMN_HAS_TEAMS = "teams";
    private final static String GAME_COLUMN_BLUE_P1 = "blue_player_1";
    private final static String GAME_COLUMN_BLUE_P2 = "blue_player_2";
    private final static String GAME_COLUMN_RED_P1 = "red_player_1";
    private final static String GAME_COLUMN_RED_P2 = "red_player_2";
    private final static String GAME_COLUMN_BLUE_P1_SCORE = "blue_player1_score";
    private final static String GAME_COLUMN_BLUE_P2_SCORE = "blue_player2_score";
    private final static String GAME_COLUMN_RED_P1_SCORE = "red_player1_score";
    private final static String GAME_COLUMN_RED_P2_SCORE = "red_player2_score";
    private final static String GAME_COLUMN_WINNER = "null";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
        instance = this;
        mContext = context;
    }

    public static synchronized DBHelper getInstance(@NonNull Context context) {

        if (instance != null) {
            return instance;
        }

        if (instance == null && context != null) {
            instance = new DBHelper(mContext);
        }

        return instance;
    }

    public static synchronized DBHelper getInstance() throws DBHelperException {
        if (instance != null) {
            return instance;
        } else {
            throw new DBHelperException("Instance of DBHelper has not been instantiated and context was not provided.");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTableStatement = "CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME + "("
                + USER_COLUMN_ID + " INTEGER PRIMARY KEY,"
                + USER_COLUMN_EMAIL + " TEXT,"
                + USER_COLUMN_ACCESS_TOKEN + " TEXT" + ");";
        db.execSQL(createUserTableStatement);

        String createPlayerTableStatement = "CREATE TABLE IF NOT EXISTS " + PLAYER_TABLE_NAME + "("
                + PLAYER_COLUMN_ID + " TEXT PRIMARY KEY, "
                + PLAYER_COLUMN_EMAIL + " TEXT, "
                + PLAYER_COLUMN_FIRSTNAME + " TEXT, "
                + PLAYER_COLUMN_LASTNAME + " TEXT, "
                + PLAYER_COLUMN_ROLE + " TEXT, "
                + PLAYER_COLUMN_USERNAME + " TEXT, "
                + PLAYER_COLUMN_NAME + " TEXT, "
                + PLAYER_COLUMN_TEAMS + " TEXT, "
                + PLAYER_COLUMN_ACHIEVEMENTS + " TEXT" + ");";
        db.execSQL(createPlayerTableStatement);

        String createTeamTableStatement = "CREATE TABLE IF NOT EXISTS " + TEAM_TABLE_NAME + "("
                + TEAM_COLUMN_ID + " TEXT PRIMARY KEY, "
                + "FOREIGN KEY(" + TEAM_COLUMN_PLAYER1 + ") REFERENCES " + PLAYER_TABLE_NAME + "(" + PLAYER_COLUMN_ID + ")"
                + "FOREIGN KEY(" + TEAM_COLUMN_PLAYER2 + ") REFERENCES " + PLAYER_TABLE_NAME + "(" + PLAYER_COLUMN_ID + ")" + ");";
        db.execSQL(createTeamTableStatement);

        String createGameTableStatement = "CREATE TABLE IF NOT EXISTS " + GAME_TABLE_NAME + "("
                + GAME_COLUMN_ID + " TEXT PRIMARY KEY, "
                + GAME_COLUMN_BLUE_P1 + " TEXT, "
                + GAME_COLUMN_BLUE_P2 + " TEXT, "
                + GAME_COLUMN_RED_P1 + " TEXT, "
                + GAME_COLUMN_RED_P2 + " TEXT, "
                + GAME_COLUMN_BLUE_P1_SCORE + " INTEGER, "
                + GAME_COLUMN_BLUE_P2_SCORE + " INTEGER, "
                + GAME_COLUMN_RED_P1_SCORE + " INTEGER, "
                + GAME_COLUMN_RED_P2_SCORE + " INTEGER, "
                + GAME_COLUMN_WINNER + " TEXT" + ")";
        db.execSQL(createGameTableStatement);

        String createGameTeamTableStatement = "CREATE TABLE IF NOT EXISTS " + TEAMGAMES_TABLE_NAME + "("
                + TEAMGAMES_TEAM_ID + " TEXT, "
                + TEAMGAMES_GAME_ID + " TEXT, "
                + "PRIMARY KEY(" + TEAMGAMES_TEAM_ID + "," + TEAMGAMES_GAME_ID + ")" + ");";
        db.execSQL(createGameTeamTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PLAYER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TEAM_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GAME_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TEAMGAMES_TABLE_NAME);
        onCreate(db);
    }

    public void forceUpgrade() {
        SQLiteDatabase db = getWritableDatabase();
        onUpgrade(db, DB_VERSION, DB_VERSION);
    }

    public FBUser findOne(String id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(USER_TABLE_NAME, null, USER_COLUMN_ID + "=?", new String[]{id}, null, null, null, null);
        cursor.moveToFirst();
        String facebookId = cursor.getString(cursor.getColumnIndex(USER_COLUMN_ID));
        String token = cursor.getString(cursor.getColumnIndex(USER_COLUMN_ACCESS_TOKEN));
        String email = cursor.getString(cursor.getColumnIndex(USER_COLUMN_EMAIL));
        cursor.close();
        return new FBUser(facebookId, token, email);
    }

    public boolean insert(FBUser user) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE_NAME + " WHERE " + USER_COLUMN_ID + "=" + user.getFacebookId(), null);
        cursor.moveToFirst();

        FBUser _user = parseUserSQLResponse(cursor);

        if (_user != null && _user.equals(user)) {
            return false;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COLUMN_ID, user.getFacebookId());
        contentValues.put(USER_COLUMN_ACCESS_TOKEN, user.getAccessToken());
        contentValues.put(USER_COLUMN_EMAIL, user.getEmail());
        db.insert(USER_TABLE_NAME, null, contentValues);
        cursor.close();
        return true;
    }

    public boolean insert(FPlayer player) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE_NAME + " WHERE " + USER_COLUMN_ID + "=" + player.getId(), null);

        if (cursor.getCount() > 0) {
            cursor.close();
            return update(player);
        }

        ContentValues values = new ContentValues();
        values.put(PLAYER_COLUMN_EMAIL, player.getEmail());
        values.put(PLAYER_COLUMN_FIRSTNAME, player.getFirstName());
        values.put(PLAYER_COLUMN_LASTNAME, player.getLastName());
        values.put(PLAYER_COLUMN_ROLE, player.getRole());
        values.put(PLAYER_COLUMN_USERNAME, player.getUsername());
        values.put(PLAYER_COLUMN_TEAMS, FPlayer.serializeTeams(player));
        db.insert(USER_TABLE_NAME, null, values);
        cursor.close();
        return true;
    }


    /**
     * -------------------------------
     * @description - Parser functions
     * -------------------------------
     * */

    private FBUser parseUserSQLResponse(Cursor cursor) {
        FBUser user = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndex(USER_COLUMN_ID));
            String token = cursor.getString(cursor.getColumnIndex(USER_COLUMN_ACCESS_TOKEN));
            String email = cursor.getString(cursor.getColumnIndex(USER_COLUMN_EMAIL));
            cursor.close();
            user = new FBUser(id, token, email);
        }
        cursor.close();
        return user;
    }

    private FTeam parseTeamResponse(Cursor cursor) {
        FTeam team = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndex(TEAM_COLUMN_ID));
            String player1 = cursor.getString(cursor.getColumnIndex(TEAM_COLUMN_PLAYER1));
            String player2 = cursor.getString(cursor.getColumnIndex(TEAM_COLUMN_PLAYER2));
            team = new FTeam(id, player1, player2);
        }
        cursor.close();
        return team;
    }

    private FPlayer parsePlayerResponse(Cursor cursor) {
        FPlayer player = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndex(PLAYER_COLUMN_ID));
            player = new FTeam(id, player1, player2);
        }
        cursor.close();
        return player;
    }

    /**
     * -------------------------------
     * @description - CRUD Operations
     * -------------------------------
     * */
    public boolean update(FBUser user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_EMAIL, user.getEmail());
        values.put(USER_COLUMN_ACCESS_TOKEN, user.getAccessToken());
        db.update(USER_TABLE_NAME, values, USER_COLUMN_ID + "=?", new String[]{user.getFacebookId()});
        return true;
    }

    public boolean update(FPlayer player) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PLAYER_COLUMN_EMAIL, player.getEmail());
        values.put(PLAYER_COLUMN_FIRSTNAME, player.getFirstName());
        values.put(PLAYER_COLUMN_LASTNAME, player.getLastName());
        values.put(PLAYER_COLUMN_ROLE, player.getRole());
        values.put(PLAYER_COLUMN_USERNAME, player.getUsername());
        values.put(PLAYER_COLUMN_TEAMS, FPlayer.serializeTeams(player));
//        values.put(PLAYER_COLUMN_ACHIEVEMENTS, FPlayer.serializeAchievements(player));

        db.update(PLAYER_TABLE_NAME, values, PLAYER_COLUMN_ID + "=?", new String[]{player.getId()});
        return true;
    }

    public int delete(FBUser user) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(USER_TABLE_NAME, USER_COLUMN_ID + "=?", new String[] {user.getFacebookId()});
    }

    public ArrayList<FBUser> findUsers() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE_NAME, null);

        ArrayList<FBUser> list = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(parseUserSQLResponse(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        return list;
    }

    public ArrayList<FTeam> findTeams() throws Exception {
        ArrayList<FTeam> teams = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        if (teams.isEmpty())
            throw new Exception("This method has not been implemented.");

        return teams;
    }

    public FTeam findTeamById(String id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TEAM_TABLE_NAME + " WHERE " + TEAM_COLUMN_ID + " = " + id);
        return parseTeamResponse(cursor);
    }

    public FPlayer findPlayerById(String id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + PLAYER_TABLE_NAME + " WHERE " + PLAYER_COLUMN_ID + "=" + id);
        return parsePlayerResponse(cursor);
    }

    public boolean insertPlayers(FPlayer[] players) {

        for (int i = 0; i < players.length; i++) {
            insert()
        }

        return true;
    }

}
