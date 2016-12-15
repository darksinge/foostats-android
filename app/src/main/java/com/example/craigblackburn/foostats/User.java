package com.example.craigblackburn.foostats;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;

import com.facebook.AccessToken;

import java.util.ArrayList;
import java.util.Objects;

public class User extends FModel {

    interface UserDelegate {
        void onTaskComplete(ArrayList<User> list);
    }

    private String facebookId;
    private String accessToken;
    private String name;
    private String email;

    public User() {}

    public User(String id, String token, String email, String name) {
        this.facebookId = id;
        this.accessToken = token;
        this.email = email;
        this.name = name;
    }

    public static User getLoggedInUser(Context context) {
        if (AccessToken.getCurrentAccessToken() == null)
            return null;

        User user = null;
        DBHelper dbHelper = helper;
        if (dbHelper == null) {
            dbHelper = DBHelper.newInstance(context);
        }

        if (!dbHelper.getDatabase().isOpen()) {
            dbHelper.openDatabase();
        }

        user = dbHelper.findUserByAccessToken(AccessToken.getCurrentAccessToken().getToken());
        if (user != null) {
            return user;
        }

        user = dbHelper.findUserById(AccessToken.getCurrentAccessToken().getUserId());
        if (user != null) {
            user.setAccessToken(AccessToken.getCurrentAccessToken().getToken());
            user.save();
            return user;
        }

        dbHelper.close();
        return user;
    }

    public String getId() {
        return this.facebookId;
    }

    public String getName() { return this.name; }

    public String getFacebookId() {
        return this.facebookId;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getEmail() {
        return this.email;
    }

    public void setFacebookId(String id) {
        this.facebookId = id;
    }

    public void setAccessToken(String token) {
        this.accessToken = token;
    }

    public void setEmail(String name) {
        this.email = name;
    }

    public void setName(String name) { this.name = name; }

    public String toString() {
        return "Email: " + this.email
                + "\nAccess Token: " + this.accessToken
                + "\nFacebook ID: " + this.facebookId;
    }

    public String getDisplayMessage() {
        return this.email + " is logged in";
    }

    public boolean equals(User user) {
        return Objects.equals(this, user);
    }

    public static String serialize(User user) {
        return user.getId() + ";" + user.getAccessToken() + ";" + user.getEmail() + ";" + user.getName();
    }

    public static User deserialize(@NonNull String string) {
        String[] components = string.split(";");
        User user = new User();
        if (components.length == 4) {
            user.setFacebookId(components[0]);
            user.setAccessToken(components[1]);
            user.setEmail(components[2]);
            user.setName(components[3]);
        }
        return user;
    }

    public static User findOne(){
        try {
            ArrayList<User> users = helper.findUsers();

            if (users.size() > 0) {
                return users.get(0);
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<User> findAll() {
        ArrayList<User> list = new ArrayList<>();
        try {
            list = helper.findUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private boolean canSave() {
        return this.getId() != null && this.getEmail() != null && this.getAccessToken() != null && this.getName() != null;
    }

    public void save() throws SQLiteException {
        if (helper != null) {
            if (canSave()) {
                if (helper.insert(this) == -1) throw new SQLiteException("Unknown error, failed to save user record.");
            } else {
                throw new SQLiteException("Cannot save record, missing required fields!");
            }
        }
    }

    public FPlayer mapToPlayer() throws Exception {
        FPlayer player = null;
        if (helper == null) {
            throw new Exception("Database helper has not been initialized!");
        }



        return player;
    }

    public void delete() {
        helper.delete(this);
    }

    public void deleteAll() {
        helper.deleteAllUsers();
    }

}
