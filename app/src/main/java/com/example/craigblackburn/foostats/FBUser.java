package com.example.craigblackburn.foostats;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

public class FBUser {

    private String facebookId;
    private String accessToken;
    private String email;

    public FBUser() {}

    public FBUser(String id, String token, String email) {
        this.facebookId = id;
        this.accessToken = token;
        this.email = email;
    }

    public String getId() {
        return this.facebookId;
    }

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

    public String toString() {
        return "Email: " + this.email
                + "\nAccess Token: " + this.accessToken
                + "\nFacebook ID: " + this.facebookId;
    }

    public String getDisplayMessage() {
        return this.email + " is logged in";
    }

    public boolean equals(FBUser user) {
        return Objects.equals(this, user);
    }

    public static FBUser findOne(){
        try {
            DBHelper helper = DBHelper.getInstance(null);
            ArrayList<FBUser> users = helper.findUsers();

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

}
