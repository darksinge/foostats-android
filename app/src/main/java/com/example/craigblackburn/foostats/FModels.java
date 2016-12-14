package com.example.craigblackburn.foostats;

import android.content.Context;

import java.util.UUID;

/**
 * Created by craigblackburn on 12/13/16.
 */

public class FModels {

    public FModels(){}

    protected static DBHelper helper;

    public static void initialize(Context context) {
        helper = DBHelper.getInstance(context);
    }

    public static void pushToServer() {

    }

    public static void pullFromServer() {

    }

    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }

    interface ModelListener {
        void onTaskComplete();
    }

}
