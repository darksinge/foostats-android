package com.example.craigblackburn.foostats;

import android.content.Context;

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

}
