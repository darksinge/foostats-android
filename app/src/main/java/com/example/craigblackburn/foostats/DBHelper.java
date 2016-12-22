package com.example.craigblackburn.foostats;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.craigblackburn.foostats.models.AchievementSchema;
import com.example.craigblackburn.foostats.models.GameSchema;
import com.example.craigblackburn.foostats.models.GamesSchema;
import com.example.craigblackburn.foostats.models.PlayerSchema;
import com.example.craigblackburn.foostats.models.TeamSchema;
import com.example.craigblackburn.foostats.models.TeamsSchema;
import com.example.craigblackburn.foostats.models.UserSchema;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


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

    public static DBHelper newInstance(Context c) {
        DBHelper helper = new DBHelper(c);
        instance = helper;
        context = c;
        helper.openDatabase();
        return helper;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, context.getResources().getInteger(R.integer.DB_VERSION));
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
        db.execSQL(UserSchema.SQL_CREATE_TABLE_STATEMENT);
        db.execSQL(PlayerSchema.SQL_CREATE_TABLE_STATEMENT);
        db.execSQL(TeamSchema.SQL_CREATE_TABLE_STATEMENT);
        db.execSQL(TeamsSchema.SQL_CREATE_TABLE_STATEMENT);
        db.execSQL(GameSchema.SQL_CREATE_TABLE_STATEMENT);
        db.execSQL(GamesSchema.SQL_CREATE_TABLE_STATEMENT);
        db.execSQL(AchievementSchema.SQL_CREATE_TABLE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // To prevent the user table from being overwritten during a server pull, set [oldVersion] equal to -1
        if (oldVersion > 0) {
            db.execSQL("DROP TABLE IF EXISTS " + UserSchema.TABLE_NAME);
        }

        db.execSQL("DROP TABLE IF EXISTS " + PlayerSchema.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TeamSchema.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TeamsSchema.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GamesSchema.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GameSchema.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AchievementSchema.TABLE_NAME);
        onCreate(db);
    }

    public void forceUpgrade() {
        int version = context.getResources().getInteger(R.integer.DB_VERSION);
        onUpgrade(getDatabase(), version, version);
    }

    public void forceUpgradeGameData() {
        int version = context.getResources().getInteger(R.integer.DB_VERSION);
        onUpgrade(getDatabase(), -1, version);
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
            String id = cursor.getString(cursor.getColumnIndex(UserSchema.COLUMN_ID));
            String token = cursor.getString(cursor.getColumnIndex(UserSchema.COLUMN_ACCESS_TOKEN));
            String email = cursor.getString(cursor.getColumnIndex(UserSchema.COLUMN_EMAIL));
            String name = cursor.getString(cursor.getColumnIndex(UserSchema.COLUMN_NAME));
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
        contentValues.put(UserSchema.COLUMN_ID, user.getFacebookId());
        contentValues.put(UserSchema.COLUMN_ACCESS_TOKEN, user.getAccessToken());
        contentValues.put(UserSchema.COLUMN_EMAIL, user.getEmail());
        contentValues.put(UserSchema.COLUMN_NAME, user.getName());
        return (int) db.insert(UserSchema.TABLE_NAME, null, contentValues);
    }

    public User findUserById(String id) {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.query(UserSchema.TABLE_NAME, null, UserSchema.COLUMN_ID + "=?", new String[]{id}, null, null, null, null);
        ArrayList<User> list = parseUserResponse(cursor);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public User findUserByAccessToken(String accessToken) {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.query(UserSchema.TABLE_NAME, null, UserSchema.COLUMN_ID + "=?", new String[]{accessToken}, null, null, null, null);
        ArrayList<User> list = parseUserResponse(cursor);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public ArrayList<User> findUsers() {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + UserSchema.TABLE_NAME, null);
        return parseUserResponse(cursor);
    }

    public int update(User user) {
        SQLiteDatabase db = getDatabase();
        ContentValues values = new ContentValues();
        values.put(UserSchema.COLUMN_EMAIL, user.getEmail());
        values.put(UserSchema.COLUMN_ACCESS_TOKEN, user.getAccessToken());
        values.put(UserSchema.COLUMN_NAME, user.getName());
        return db.update(UserSchema.TABLE_NAME, values, UserSchema.COLUMN_ID + "=?", new String[]{user.getFacebookId()});
    }

    public int delete(User user) {
        SQLiteDatabase db = getDatabase();
        return db.delete(UserSchema.TABLE_NAME, UserSchema.COLUMN_ID + "=?", new String[] {user.getFacebookId()});
    }

    public int deleteAllUsers() {
        SQLiteDatabase db = getDatabase();
        ArrayList<User> list = findUsers();
        String[] userIds = new String[list.size()];
        for (User user : list) {
            userIds[list.indexOf(user)] = user.getId();
        }
        return db.delete(UserSchema.TABLE_NAME, UserSchema.COLUMN_ID + "=?", userIds);
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
            String id = cursor.getString(cursor.getColumnIndex(TeamSchema.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(TeamSchema.COLUMN_TEAM_NAME));
            String player1Id = cursor.getString(cursor.getColumnIndex(TeamSchema.PLAYER_1_COLUMN));
            String player2Id = cursor.getString(cursor.getColumnIndex(TeamSchema.PLAYER_2_COLUMN));

            list.add(new FTeam(id, name, findPlayerById(player1Id), findPlayerById(player2Id)));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public ArrayList<FTeam> findTeams() throws Exception {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TeamSchema.TABLE_NAME, null);
        return parseTeamResponse(cursor);
    }

    public FTeam findTeamById(String id) {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TeamSchema.TABLE_NAME + " WHERE " + TeamSchema.COLUMN_ID + "=\"" + id +"\"", null);
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
        values.put(TeamSchema.COLUMN_ID, team.getId());
        values.put(TeamSchema.COLUMN_TEAM_NAME, team.getTeamName());
        values.put(TeamSchema.PLAYER_1_COLUMN, team.getPlayerOne().getId());
        values.put(TeamSchema.PLAYER_2_COLUMN, team.getPlayerTwo().getId());
        return (int) db.insert(TeamSchema.TABLE_NAME, null, values);
    }

    public int update(FTeam team) {
        SQLiteDatabase db = getDatabase();
        ContentValues values = new ContentValues();
        values.put(TeamSchema.COLUMN_ID, team.getId());
        values.put(TeamSchema.COLUMN_TEAM_NAME, team.getTeamName());
        values.put(TeamSchema.PLAYER_1_COLUMN, team.getPlayerOne().getId());
        values.put(TeamSchema.PLAYER_2_COLUMN, team.getPlayerTwo().getId());
        return db.update(TeamSchema.TABLE_NAME, values, TeamSchema.COLUMN_ID + "=?", new String[]{team.getId()});
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
            String id = cursor.getString(cursor.getColumnIndex(PlayerSchema.COLUMN_ID));
            String facebookId = cursor.getString(cursor.getColumnIndex(PlayerSchema.COLUMN_FACEBOOK_ID));
            String email = cursor.getString(cursor.getColumnIndex(PlayerSchema.COLUMN_EMAIL));
            String firstname = cursor.getString(cursor.getColumnIndex(PlayerSchema.COLUMN_FIRST_NAME));
            String lastname = cursor.getString(cursor.getColumnIndex(PlayerSchema.COLUMN_LAST_NAME));
            String role = cursor.getString(cursor.getColumnIndex(PlayerSchema.COLUMN_ROLE));
            String username = cursor.getString(cursor.getColumnIndex(PlayerSchema.COLUMN_USERNAME));

            FPlayer player = new FPlayer(id, facebookId, email, firstname, lastname, role, username);
            list.add(player);
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
        values.put(PlayerSchema.COLUMN_ID, player.getId());
        values.put(PlayerSchema.COLUMN_FACEBOOK_ID, player.getFacebookId());
        values.put(PlayerSchema.COLUMN_EMAIL, player.getEmail());
        values.put(PlayerSchema.COLUMN_FIRST_NAME, player.getFirstName());
        values.put(PlayerSchema.COLUMN_LAST_NAME, player.getLastName());
        values.put(PlayerSchema.COLUMN_ROLE, player.getRole());
        values.put(PlayerSchema.COLUMN_USERNAME, player.getUsername());

        int dbCode;
        try {
            dbCode = (int) db.insertOrThrow(PlayerSchema.TABLE_NAME, null, values);
        }catch (SQLiteConstraintException e) {
            Log.d(TAG, e.getLocalizedMessage());
            dbCode = -1;
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
            dbCode = -1;
        }
        Log.d(TAG, "DB INSERT CODE: " + String.valueOf(dbCode));
        return dbCode;
    }

    public FPlayer findPlayerById(String id) {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + PlayerSchema.TABLE_NAME + " WHERE " + PlayerSchema.COLUMN_ID + "=\"" + id + "\"", null);
        ArrayList<FPlayer> list = parsePlayerResponse(cursor);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public FPlayer findPlayerByEmail(String email) {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + PlayerSchema.TABLE_NAME + " WHERE " + PlayerSchema.COLUMN_EMAIL + "=\"" + email + "\"", null);
        ArrayList<FPlayer> list = parsePlayerResponse(cursor);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public FPlayer findPlayerByFacebookId(String id) {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + PlayerSchema.TABLE_NAME + " WHERE " + PlayerSchema.COLUMN_FACEBOOK_ID + "=\"" + id + "\"", null);
        ArrayList<FPlayer> list = parsePlayerResponse(cursor);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public ArrayList<FPlayer> findPlayers() {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.query(PlayerSchema.TABLE_NAME, null, null, null, null, null, null);
        return parsePlayerResponse(cursor);
    }

    public int update(FPlayer player) {
        SQLiteDatabase db = getDatabase();
        ContentValues values = new ContentValues();
        values.put(PlayerSchema.COLUMN_ID, player.getId());
        values.put(PlayerSchema.COLUMN_EMAIL, player.getEmail());
        values.put(PlayerSchema.COLUMN_FACEBOOK_ID, player.getFacebookId());
        values.put(PlayerSchema.COLUMN_FIRST_NAME, player.getFirstName());
        values.put(PlayerSchema.COLUMN_LAST_NAME, player.getLastName());
        values.put(PlayerSchema.COLUMN_ROLE, player.getRole());
        values.put(PlayerSchema.COLUMN_USERNAME, player.getUsername());

        return db.update(PlayerSchema.TABLE_NAME, values, PlayerSchema.COLUMN_ID + "=?", new String[]{player.getId()});
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

            String id = cursor.getString(cursor.getColumnIndex(GameSchema.ID_COLUMN));
            String blueTeamId = cursor.getString(cursor.getColumnIndex(GameSchema.TEAM_A_COLUMN));
            String redTeamId = cursor.getString(cursor.getColumnIndex(GameSchema.TEAM_B_COLUMN));
            int blue1Score = cursor.getInt(cursor.getColumnIndex(GameSchema.PLAYER_1_SCORE_COLUMN));
            int blue2Score = cursor.getInt(cursor.getColumnIndex(GameSchema.PLAYER_2_SCORE_COLUMN));
            int red1Score = cursor.getInt(cursor.getColumnIndex(GameSchema.PLAYER_3_SCORE_COLUMN));
            int red2Score = cursor.getInt(cursor.getColumnIndex(GameSchema.PLAYER_4_SCORE_COLUMN));

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
        Cursor cursor = db.rawQuery("SELECT * FROM " + GameSchema.TABLE_NAME + " WHERE " + GameSchema.ID_COLUMN + "=\"" + id + "\"", null);
        ArrayList<FGame> list = parseGameResponse(cursor);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public ArrayList<FGame> findGames() {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + GameSchema.TABLE_NAME, null);
        return parseGameResponse(cursor);
    }

    public int insert(FGame game) {
        if (findGameById(game.getId()) != null) {
            update(game);
        }

        SQLiteDatabase db = getDatabase();
        Date date = new Date();
        ContentValues values = new ContentValues();
        values.put(GameSchema.ID_COLUMN, game.getId());
        values.put(GameSchema.TEAM_A_COLUMN, game.getBlueTeam().getId());
        values.put(GameSchema.TEAM_B_COLUMN, game.getRedTeam().getId());
        values.put(GameSchema.PLAYER_1_SCORE_COLUMN, game.getBluePlayerOneScore());
        values.put(GameSchema.PLAYER_2_SCORE_COLUMN, game.getBluePlayerTwoScore());
        values.put(GameSchema.PLAYER_3_SCORE_COLUMN, game.getRedPlayerOneScore());
        values.put(GameSchema.PLAYER_4_SCORE_COLUMN, game.getRedPlayerTwoScore());
        values.put(GameSchema.GAME_LENGTH_IN_SECONDS_COLUMN, game.getMatchLength());
        values.put(GameSchema.GAME_MATCH_TOKEN_COLUMN, game.getMatchToken());
        values.put(GameSchema.GAME_MATCH_ORDER_COLUMN, game.getMatchOrder());
        values.put(GameSchema.GAME_DATE_COLUMN, new Date().toString());
        return (int) db.insert(GameSchema.TABLE_NAME, null, values);
    }

    public int update(FGame game) {
        SQLiteDatabase db = getDatabase();
        ContentValues values = new ContentValues();

        values.put(GameSchema.ID_COLUMN, game.getId());
        values.put(GameSchema.TEAM_A_COLUMN, game.getBlueTeam().getId());
        values.put(GameSchema.TEAM_B_COLUMN, game.getRedTeam().getId());
        values.put(GameSchema.PLAYER_1_SCORE_COLUMN, game.getBluePlayerOneScore());
        values.put(GameSchema.PLAYER_2_SCORE_COLUMN, game.getBluePlayerTwoScore());
        values.put(GameSchema.PLAYER_3_SCORE_COLUMN, game.getRedPlayerOneScore());
        values.put(GameSchema.PLAYER_4_SCORE_COLUMN, game.getRedPlayerTwoScore());
        values.put(GameSchema.GAME_LENGTH_IN_SECONDS_COLUMN, game.getMatchLength());
        values.put(GameSchema.GAME_MATCH_TOKEN_COLUMN, game.getMatchToken());
        values.put(GameSchema.GAME_MATCH_ORDER_COLUMN, game.getMatchOrder());
        return db.update(GameSchema.TABLE_NAME, values, GameSchema.ID_COLUMN + "=?", new String[]{game.getId()});
    }

}
