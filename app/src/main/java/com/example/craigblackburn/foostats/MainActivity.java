package com.example.craigblackburn.foostats;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MainActivity extends AppCompatActivity {

    private FacebookLoginFragment facebookLoginFragment;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());
        setContentView(R.layout.activity_main);



        if (savedInstanceState == null) {
            facebookLoginFragment = new FacebookLoginFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.com_facebook_fragment_container, facebookLoginFragment)
                    .commit();
        } else {
            facebookLoginFragment = (FacebookLoginFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.com_facebook_fragment_container);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebookLoginFragment.onActivityResult(requestCode, resultCode, data);
    }


}
