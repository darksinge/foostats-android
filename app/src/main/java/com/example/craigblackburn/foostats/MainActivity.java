package com.example.craigblackburn.foostats;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class MainActivity extends AppCompatActivity {

    private FacebookLoginFragment facebookLoginFragment;

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
