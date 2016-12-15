package com.example.craigblackburn.foostats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;


public class DBHelper extends SQLiteOpenHelper {

    private static String TAG = "DB_HELPER";

    public static class DBHelperException extends Exception {
        public DBHelperException(String errorMessage) {
            super(errorMessage);
        }
    }

    private static DBHelper instance;
    private static Context context;
    private SQLiteDatabase mDatabase;

    private final static String DATABASE_NAME = "foostats";
    private final static int DB_VERSION = 1;

    private final static String USER_TABLE_NAME = "users";
    private final static String USER_COLUMN_ID = "facebook_id";
    private final static String USER_COLUMN_ACCESS_TOKEN = "access_token";
    private final static String USER_COLUMN_EMAIL = "email";
    private final static String USER_COLUMN_NAME = "name";

    private final static String PLAYER_TABLE_NAME = "fplayers";
    private final static String PLAYER_COLUMN_ID = "uuid";
    private final static String PLAYER_COLUMN_FB_ID = "facebook_id";
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
    private final static String TEAM_COLUMN_NAME = "team_name";
    private final static String TEAM_COLUMN_PLAYER1 = "player1";
    private final static String TEAM_COLUMN_PLAYER2 = "player2";
    private final static String TEAM_COLUMN_HAS_GAMES = "games";

    private final static String TEAMGAMES_TABLE_NAME = "team_has_games";
    private final static String TEAMGAMES_TEAM_ID = "team_uuid";
    private final static String TEAMGAMES_GAME_ID = "game_uuid";

    private final static String GAME_TABLE_NAME = "fgames";
    private final static String GAME_COLUMN_ID = "uuid";
    private final static String GAME_COLUMN_HAS_TEAMS = "teams";
    private final static String GAME_COLUMN_BLUE_TEAM_ID = "blue_team_id";
    private final static String GAME_COLUMN_RED_TEAM_ID = "red_team_id";
    private final static String GAME_COLUMN_BLUE_P1 = "blue_player_1";
    private final static String GAME_COLUMN_BLUE_P2 = "blue_player_2";
    private final static String GAME_COLUMN_RED_P1 = "red_player_1";
    private final static String GAME_COLUMN_RED_P2 = "red_player_2";
    private final static String GAME_COLUMN_BLUE_P1_SCORE = "blue_player1_score";
    private final static String GAME_COLUMN_BLUE_P2_SCORE = "blue_player2_score";
    private final static String GAME_COLUMN_RED_P1_SCORE = "red_player1_score";
    private final static String GAME_COLUMN_RED_P2_SCORE = "red_player2_score";
    private final static String GAME_COLUMN_WINNING_TEAM_ID = "winning_team_id";


    public static DBHelper newInstance(Context c) {
        DBHelper helper = new DBHelper(c);
        instance = helper;
        context = c;
        helper.openDatabase();
        return helper;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    public void openDatabase() {
        if (mDatabase == null) {
            mDatabase  = DBHelper.getInstance(context).getWritableDatabase();
        } else if (!mDatabase.isOpen()) {
            mDatabase = getWritableDatabase();
        }
    }

    public SQLiteDatabase getDatabase() {
        if (mDatabase != null) {
            if (mDatabase.isOpen())
                mDatabase.close();
            mDatabase = getWritableDatabase();
        } else {
            mDatabase = DBHelper.getInstance(context).getWritableDatabase();
        }
        return mDatabase;
    }

    public static synchronized DBHelper getInstance(@NonNull Context context) {

        if (instance != null) {
            return instance;
        }

        if (instance == null && context != null) {
            instance = new DBHelper(context);
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
                + USER_COLUMN_ID + " INTEGER PRIMARY KEY UNIQUE,"
                + USER_COLUMN_EMAIL + " TEXT UNIQUE NOT NULL,"
                + USER_COLUMN_ACCESS_TOKEN + " TEXT NOT NULL,"
                + USER_COLUMN_NAME + " TEXT NOT NULL" + ");";
        db.execSQL(createUserTableStatement);

        String createPlayerTableStatement = "CREATE TABLE IF NOT EXISTS " + PLAYER_TABLE_NAME + "("
                + PLAYER_COLUMN_ID + " TEXT PRIMARY KEY UNIQUE, "
                + PLAYER_COLUMN_FB_ID + " TEXT UNIQUE, "
                + PLAYER_COLUMN_EMAIL + " TEXT UNIQUE, "
                + PLAYER_COLUMN_FIRSTNAME + " TEXT, "
                + PLAYER_COLUMN_LASTNAME + " TEXT, "
                + PLAYER_COLUMN_ROLE + " TEXT, "
                + PLAYER_COLUMN_USERNAME + " TEXT UNIQUE, "
                + PLAYER_COLUMN_NAME + " TEXT, "
                + PLAYER_COLUMN_TEAMS + " TEXT, "
                + PLAYER_COLUMN_ACHIEVEMENTS + " TEXT" + ");";
        db.execSQL(createPlayerTableStatement);

        String createTeamTableStatement = "CREATE TABLE IF NOT EXISTS " + TEAM_TABLE_NAME + "("
                + TEAM_COLUMN_ID + " TEXT PRIMARY KEY UNIQUE, "
                + TEAM_COLUMN_NAME + " TEXT UNIQUE, "
                + TEAM_COLUMN_PLAYER1 + " TEXT, "
                + TEAM_COLUMN_PLAYER2 + " TEXT, "
                + "FOREIGN KEY(" + TEAM_COLUMN_PLAYER1 + ") REFERENCES " + PLAYER_TABLE_NAME + "(" + PLAYER_COLUMN_ID + "), "
                + "FOREIGN KEY(" + TEAM_COLUMN_PLAYER2 + ") REFERENCES " + PLAYER_TABLE_NAME + "(" + PLAYER_COLUMN_ID + ")" + ");";
        db.execSQL(createTeamTableStatement);

        String createGameTableStatement = "CREATE TABLE IF NOT EXISTS " + GAME_TABLE_NAME + "("
                + GAME_COLUMN_ID + " TEXT PRIMARY KEY UNIQUE, "
                + GAME_COLUMN_BLUE_TEAM_ID + " TEXT, "
                + GAME_COLUMN_RED_TEAM_ID + " TEXT, "
                + GAME_COLUMN_BLUE_P1 + " TEXT, "
                + GAME_COLUMN_BLUE_P2 + " TEXT, "
                + GAME_COLUMN_RED_P1 + " TEXT, "
                + GAME_COLUMN_RED_P2 + " TEXT, "
                + GAME_COLUMN_BLUE_P1_SCORE + " INTEGER, "
                + GAME_COLUMN_BLUE_P2_SCORE + " INTEGER, "
                + GAME_COLUMN_RED_P1_SCORE + " INTEGER, "
                + GAME_COLUMN_RED_P2_SCORE + " INTEGER, "
                + GAME_COLUMN_WINNING_TEAM_ID + " TEXT" + ")";
        db.execSQL(createGameTableStatement);

        String createGameTeamTableStatement = "CREATE TABLE IF NOT EXISTS " + TEAMGAMES_TABLE_NAME + "("
                + TEAMGAMES_TEAM_ID + " TEXT, "
                + TEAMGAMES_GAME_ID + " TEXT, "
                + "PRIMARY KEY(" + TEAMGAMES_TEAM_ID + "," + TEAMGAMES_GAME_ID + ")" + ");";
        db.execSQL(createGameTeamTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (!db.isOpen()) {
            db = getWritableDatabase();
        }

        // set oldVersion to -1 to prevent user table from being overwritten on server data pull.
        if (oldVersion > 0) {
            db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        }

        db.execSQL("DROP TABLE IF EXISTS " + PLAYER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TEAM_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GAME_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TEAMGAMES_TABLE_NAME);
        onCreate(db);
    }

    public void forceUpgrade() {
        onUpgrade(getDatabase(), DB_VERSION, DB_VERSION);
    }

    public void forceUpgradeGameData() {
        onUpgrade(getDatabase(), -1, DB_VERSION);
    }


    /**
     * -----------------------------+
     * @description - User QUERIES |
     * -----------------------------+
     * */

    private ArrayList<User> parseUserResponse(Cursor cursor) {
        ArrayList<User> list = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String id = cursor.getString(cursor.getColumnIndex(USER_COLUMN_ID));
            String token = cursor.getString(cursor.getColumnIndex(USER_COLUMN_ACCESS_TOKEN));
            String email = cursor.getString(cursor.getColumnIndex(USER_COLUMN_EMAIL));
            String name = cursor.getString(cursor.getColumnIndex(USER_COLUMN_NAME));
            list.add(new User(id, token, email, name));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public int insert(User user) {
        if (findUserById(user.getId()) != null) {
            return update(user);
        }

        SQLiteDatabase db = getDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COLUMN_ID, user.getFacebookId());
        contentValues.put(USER_COLUMN_ACCESS_TOKEN, user.getAccessToken());
        contentValues.put(USER_COLUMN_EMAIL, user.getEmail());
        contentValues.put(USER_COLUMN_NAME, user.getName());
        return (int) db.insert(USER_TABLE_NAME, null, contentValues);
    }

    public User findUserById(String id) {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.query(USER_TABLE_NAME, null, USER_COLUMN_ID + "=?", new String[]{id}, null, null, null, null);
        ArrayList<User> list = parseUserResponse(cursor);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public User findUserByAccessToken(String accessToken) {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.query(USER_TABLE_NAME, null, USER_COLUMN_ID + "=?", new String[]{accessToken}, null, null, null, null);
        ArrayList<User> list = parseUserResponse(cursor);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public ArrayList<User> findUsers() {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE_NAME, null);
        return parseUserResponse(cursor);
    }

    public int update(User user) {
        SQLiteDatabase db = getDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_EMAIL, user.getEmail());
        values.put(USER_COLUMN_ACCESS_TOKEN, user.getAccessToken());
        values.put(USER_COLUMN_NAME, user.getName());
        return db.update(USER_TABLE_NAME, values, USER_COLUMN_ID + "=?", new String[]{user.getFacebookId()});
    }

    public int delete(User user) {
        SQLiteDatabase db = getDatabase();
        return db.delete(USER_TABLE_NAME, USER_COLUMN_ID + "=?", new String[] {user.getFacebookId()});
    }

    public int deleteAllUsers() {
        SQLiteDatabase db = getDatabase();
        ArrayList<User> list = findUsers();
        String[] userIds = new String[list.size()];
        for (User user : list) {
            userIds[list.indexOf(user)] = user.getId();
        }
        return db.delete(USER_TABLE_NAME, USER_COLUMN_ID + "=?", userIds);
    }

    /**
     * -----------------------------+
     * @description - FTeam QUERIES |
     * -----------------------------+
     * */

    private ArrayList<FTeam> parseTeamResponse(Cursor cursor) {
        ArrayList<FTeam> list = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String id = cursor.getString(cursor.getColumnIndex(TEAM_COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(TEAM_COLUMN_NAME));
            String player1Id = cursor.getString(cursor.getColumnIndex(TEAM_COLUMN_PLAYER1));
            String player2Id = cursor.getString(cursor.getColumnIndex(TEAM_COLUMN_PLAYER2));

            list.add(new FTeam(id, name, findPlayerById(player1Id), findPlayerById(player2Id)));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public ArrayList<FTeam> findTeams() throws Exception {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TEAM_TABLE_NAME, null);
        return parseTeamResponse(cursor);
    }

    public FTeam findTeamById(String id) {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TEAM_TABLE_NAME + " WHERE " + TEAM_COLUMN_ID + "=\"" + id +"\"", null);
        ArrayList<FTeam> list = parseTeamResponse(cursor);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public int insert(FTeam team) {
        if (findTeamById(team.getId()) != null) {
            return update(team);
        }

        SQLiteDatabase db = getDatabase();

        ContentValues values = new ContentValues();
        values.put(TEAM_COLUMN_ID, team.getId());
        values.put(TEAM_COLUMN_NAME, team.getTeamName());
        values.put(TEAM_COLUMN_PLAYER1, team.getPlayerOne().getId());
        values.put(TEAM_COLUMN_PLAYER2, team.getPlayerTwo().getId());
        return (int) db.insert(TEAM_TABLE_NAME, null, values);
    }

    public int update(FTeam team) {
        SQLiteDatabase db = getDatabase();
        ContentValues values = new ContentValues();
        values.put(TEAM_COLUMN_ID, team.getId());
        values.put(TEAM_COLUMN_NAME, team.getTeamName());
        values.put(TEAM_COLUMN_PLAYER1, team.getPlayerOne().getId());
        values.put(TEAM_COLUMN_PLAYER2, team.getPlayerTwo().getId());
        return db.update(TEAM_TABLE_NAME, values, TEAM_COLUMN_ID + "=?", new String[]{team.getId()});
    }

    /**
     * -----------------------------+
     * @description - FPlayer QUERIES |
     * -----------------------------+
     * */

    private ArrayList<FPlayer> parsePlayerResponse(Cursor cursor) {
        ArrayList<FPlayer> list = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String id = cursor.getString(cursor.getColumnIndex(PLAYER_COLUMN_ID));
            String facebookId = cursor.getString(cursor.getColumnIndex(PLAYER_COLUMN_FB_ID));
            String email = cursor.getString(cursor.getColumnIndex(PLAYER_COLUMN_EMAIL));
            String firstname = cursor.getString(cursor.getColumnIndex(PLAYER_COLUMN_FIRSTNAME));
            String lastname = cursor.getString(cursor.getColumnIndex(PLAYER_COLUMN_LASTNAME));
            String role = cursor.getString(cursor.getColumnIndex(PLAYER_COLUMN_ROLE));
            String username = cursor.getString(cursor.getColumnIndex(PLAYER_COLUMN_USERNAME));
            String flatTeams = cursor.getString(cursor.getColumnIndex(PLAYER_COLUMN_TEAMS));

            ArrayList<FTeam> teams = FPlayer.deserializeTeams(flatTeams);

            list.add(new FPlayer(id, facebookId, email, firstname, lastname, role, username, teams));

            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public int insert(FPlayer player) {

        if (findPlayerById(player.getId()) != null) {
            return update(player);
        }

        SQLiteDatabase db = getDatabase();

        ContentValues values = new ContentValues();
        values.put(PLAYER_COLUMN_EMAIL, player.getEmail());
        values.put(PLAYER_COLUMN_FB_ID, player.getFacebookId());
        values.put(PLAYER_COLUMN_FIRSTNAME, player.getFirstName());
        values.put(PLAYER_COLUMN_LASTNAME, player.getLastName());
        values.put(PLAYER_COLUMN_ROLE, player.getRole());
        values.put(PLAYER_COLUMN_USERNAME, player.getUsername());
        values.put(PLAYER_COLUMN_TEAMS, FPlayer.serializeTeams(player));

        int dbCode;
        try {
            dbCode = (int) db.insertOrThrow(PLAYER_TABLE_NAME, null, values);
        }catch (SQLiteConstraintException e) {
            Log.d(TAG, e.getLocalizedMessage());
            dbCode = -1;
        } catch (RuntimeException e) {
            Log.d(TAG, e.getLocalizedMessage());
            dbCode = -1;
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
            dbCode = -1;
        }
        return dbCode;
    }

    public FPlayer findPlayerById(String id) {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + PLAYER_TABLE_NAME + " WHERE " + PLAYER_COLUMN_ID + "=\"" + id + "\"", null);
        ArrayList<FPlayer> list = parsePlayerResponse(cursor);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public FPlayer findPlayerByEmail(String email) {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + PLAYER_TABLE_NAME + " WHERE " + PLAYER_COLUMN_EMAIL + "=\"" + email + "\"", null);
        ArrayList<FPlayer> list = parsePlayerResponse(cursor);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public FPlayer findPlayerByFacebookId(String id) {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + PLAYER_TABLE_NAME + " WHERE " + PLAYER_COLUMN_FB_ID + "=\"" + id + "\"", null);
        ArrayList<FPlayer> list = parsePlayerResponse(cursor);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public ArrayList<FPlayer> findPlayers() {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + PLAYER_TABLE_NAME, null);
        return parsePlayerResponse(cursor);
    }

    public int update(FPlayer player) {
        SQLiteDatabase db = getDatabase();
        ContentValues values = new ContentValues();
        values.put(PLAYER_COLUMN_EMAIL, player.getEmail());
        values.put(PLAYER_COLUMN_FB_ID, player.getFacebookId());
        values.put(PLAYER_COLUMN_FIRSTNAME, player.getFirstName());
        values.put(PLAYER_COLUMN_LASTNAME, player.getLastName());
        values.put(PLAYER_COLUMN_ROLE, player.getRole());
        values.put(PLAYER_COLUMN_USERNAME, player.getUsername());
        values.put(PLAYER_COLUMN_TEAMS, FPlayer.serializeTeams(player));
//        values.put(PLAYER_COLUMN_ACHIEVEMENTS, FPlayer.serializeAchievements(player));

        return db.update(PLAYER_TABLE_NAME, values, PLAYER_COLUMN_ID + "=?", new String[]{player.getId()});
    }

    /**
     * -----------------------------+
     * @description - FGame QUERIES |
     * -----------------------------+
     * */

    private ArrayList<FGame> parseGameResponse(Cursor cursor) {
        ArrayList<FGame> list = new ArrayList<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {

            String id = cursor.getString(cursor.getColumnIndex(GAME_COLUMN_ID));
            String blueTeamId = cursor.getString(cursor.getColumnIndex(GAME_COLUMN_BLUE_TEAM_ID));
            String redTeamId = cursor.getString(cursor.getColumnIndex(GAME_COLUMN_RED_TEAM_ID));
            String bluep1Id = cursor.getString(cursor.getColumnIndex(GAME_COLUMN_BLUE_P1));
            String bluep2Id = cursor.getString(cursor.getColumnIndex(GAME_COLUMN_BLUE_P2));
            String redp1Id = cursor.getString(cursor.getColumnIndex(GAME_COLUMN_RED_P1));
            String redp2Id = cursor.getString(cursor.getColumnIndex(GAME_COLUMN_RED_P2));
            int blue1Score = cursor.getInt(cursor.getColumnIndex(GAME_COLUMN_BLUE_P1_SCORE));
            int blue2Score = cursor.getInt(cursor.getColumnIndex(GAME_COLUMN_BLUE_P2_SCORE));
            int red1Score = cursor.getInt(cursor.getColumnIndex(GAME_COLUMN_RED_P1_SCORE));
            int red2Score = cursor.getInt(cursor.getColumnIndex(GAME_COLUMN_RED_P2_SCORE));
            String winningTeamId = cursor.getString(cursor.getColumnIndex(GAME_COLUMN_WINNING_TEAM_ID));

            FTeam blueTeam = findTeamById(blueTeamId);
            FTeam redTeam = findTeamById(redTeamId);

            if (blueTeam == null || redTeam == null) {
                continue;
            }

            FGame game = new FGame(id, blueTeam, redTeam);
            game.setScore(FGame.BLUE_PLAYER_ONE, blue1Score);
            game.setScore(FGame.BLUE_PLAYER_TWO, blue2Score);
            game.setScore(FGame.RED_PLAYER_ONE, red1Score);
            game.setScore(FGame.RED_PLAYER_TWO, red2Score);

            list.add(game);

            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public FGame findGameById(String id) {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + GAME_TABLE_NAME + " WHERE " + GAME_COLUMN_ID + "=\"" + id + "\"", null);
        ArrayList<FGame> list = parseGameResponse(cursor);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public ArrayList<FGame> findGames() {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + GAME_TABLE_NAME, null);
        return parseGameResponse(cursor);
    }

    public int insert(FGame game) {
        if (findGameById(game.getId()) != null) {
            update(game);
        }

        SQLiteDatabase db = getDatabase();

        ContentValues values = new ContentValues();
        values.put(GAME_COLUMN_ID, game.getId());
        values.put(GAME_COLUMN_HAS_TEAMS, game.getId());
        values.put(GAME_COLUMN_BLUE_P1, game.getPlayer(FGame.BLUE_PLAYER_ONE).getId());
        values.put(GAME_COLUMN_BLUE_P2, game.getPlayer(FGame.BLUE_PLAYER_TWO).getId());
        values.put(GAME_COLUMN_RED_P1, game.getPlayer(FGame.RED_PLAYER_ONE).getId());
        values.put(GAME_COLUMN_RED_P2, game.getPlayer(FGame.RED_PLAYER_TWO).getId());
        values.put(GAME_COLUMN_BLUE_P1_SCORE, game.getBluePlayerOneScore());
        values.put(GAME_COLUMN_BLUE_P2_SCORE, game.getBluePlayerTwoScore());
        values.put(GAME_COLUMN_RED_P1_SCORE, game.getRedPlayerOneScore());
        values.put(GAME_COLUMN_RED_P2_SCORE, game.getRedPlayerTwoScore());
        values.put(GAME_COLUMN_WINNING_TEAM_ID, game.getWinningTeamId());
        return (int) db.insert(GAME_TABLE_NAME, null, values);
    }

    public int update(FGame game) {
        SQLiteDatabase db = getDatabase();
        ContentValues values = new ContentValues();
        values.put(GAME_COLUMN_HAS_TEAMS, game.getId());
        values.put(GAME_COLUMN_BLUE_P1, game.getPlayer(FGame.BLUE_PLAYER_ONE).getId());
        values.put(GAME_COLUMN_BLUE_P2, game.getPlayer(FGame.BLUE_PLAYER_TWO).getId());
        values.put(GAME_COLUMN_RED_P1, game.getPlayer(FGame.RED_PLAYER_ONE).getId());
        values.put(GAME_COLUMN_RED_P2, game.getPlayer(FGame.RED_PLAYER_TWO).getId());
        values.put(GAME_COLUMN_BLUE_P1_SCORE, game.getBluePlayerOneScore());
        values.put(GAME_COLUMN_BLUE_P2_SCORE, game.getBluePlayerTwoScore());
        values.put(GAME_COLUMN_RED_P1_SCORE, game.getRedPlayerOneScore());
        values.put(GAME_COLUMN_RED_P2_SCORE, game.getRedPlayerTwoScore());
        values.put(GAME_COLUMN_WINNING_TEAM_ID, game.getWinningTeamId());
        return db.update(GAME_TABLE_NAME, values, GAME_COLUMN_ID + "=?", new String[]{game.getId()});
    }

}
