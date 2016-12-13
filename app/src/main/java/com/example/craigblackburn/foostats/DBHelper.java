package com.example.craigblackburn.foostats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by craigblackburn on 12/12/16.
 */

public class DBHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "foostats";
    private final static int DB_VERSION = 1;
    private final static String USER_TABLE_NAME = "users";
    private final static String USER_COLUMN_ID = "facebook_id";
    private final static String USER_COLUMN_EMAIL = "email";
    private final static String USER_COLUMN_ACCESS_TOKEN = "access_token";

    public DBHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DB_VERSION;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createStatement = "CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME + "("
                + USER_COLUMN_ID + " INTEGER PRIMARY KEY,"
                + USER_COLUMN_EMAIL + " TEXT,"
                + USER_COLUMN_ACCESS_TOKEN + " TEXT" + ");";
        db.execSQL(createStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + USER_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertUser(FBUser user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COLUMN_ID, user.getFacebookId());
        contentValues.put(USER_COLUMN_ACCESS_TOKEN, user.getAccessToken());
        contentValues.put(USER_COLUMN_EMAIL, user.getEmail());
        db.insert(USER_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateUser(String email, String accessToken) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_EMAIL, email);
        values.put(USER_COLUMN_ACCESS_TOKEN, accessToken);
        db.insert(USER_TABLE_NAME, null, values);
        return true;
    }

    public int deleteUser(FBUser user) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(USER_TABLE_NAME, USER_COLUMN_ID + "=?", new String[] {user.getFacebookId()});
    }

    public ArrayList<FBUser> getAllUsers() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE_NAME, null);
        cursor.moveToFirst();

        ArrayList<FBUser> list = new ArrayList<>();

        while (!cursor.isAfterLast()) {
            String id = cursor.getString(cursor.getColumnIndex(USER_COLUMN_ID));
            String token = cursor.getString(cursor.getColumnIndex(USER_COLUMN_ACCESS_TOKEN));
            String email = cursor.getString(cursor.getColumnIndex(USER_COLUMN_EMAIL));
            list.add(new FBUser(id, token, email));
        }

        cursor.close();
        return list;
    }

}
