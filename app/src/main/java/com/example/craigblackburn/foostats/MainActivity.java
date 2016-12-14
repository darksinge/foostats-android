package com.example.craigblackburn.foostats;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements FacebookLoginManager.FacebookListener, APIRequester.APIListener {

    private final static String TAG = "MAIN_ACTIVITY";

    private FacebookLoginManager facebookManager;
    private DBHelper dbHelper;
    private Menu mMenu;
    private TextView tv;
    private ProgressDialog progressDialog;
    private FBUser mUser;
    private Button testButton;
    private APIRequester apiDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
        AppEventsLogger.activateApp(getApplication());

        FModels.initialize(getApplicationContext());

        apiDelegate = new APIRequester(this);
        dbHelper = new DBHelper(getApplicationContext());
        tv = (TextView) findViewById(R.id.text_view);
        facebookManager = FacebookLoginManager.newInstance(this);
        testButton = (Button) findViewById(R.id.test_button);

        dbHelper.forceUpgrade();

        mUser = FBUser.findOne();

        if (mUser != null) {
            tv.setText(mUser.getDisplayMessage());
        } else {
            tv.setText("User not found!");
        }

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apiDelegate.getPlayers();
            }
        });

    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage("Logging you in...");
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private boolean userIsLoggedIn() {
        return FBUser.findOne() != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu, menu);
        mMenu = menu;
        return (super.onCreateOptionsMenu(menu));
    }

    public void updateMenu() {
        onPrepareOptionsMenu(mMenu);
    }

    public void login(MenuItem item) {

        FBUser user = FBUser.findOne();
        if (user == null && AccessToken.getCurrentAccessToken() != null) {
            showProgressDialog();
            facebookManager.makeGraphRequest();
        }

        facebookManager.login(this);
        showProgressDialog();
        updateMenu();
    }

    public void logout(MenuItem item) {
        facebookManager.logout();
        dbHelper.delete(mUser);
        mUser = null;
        updateMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {

        boolean isLogged = userIsLoggedIn();

        menu.findItem(R.id.logout).setVisible(isLogged);
        menu.findItem(R.id.login).setVisible(!isLogged);

        if (mUser != null) {
            tv.setText(mUser.getDisplayMessage());
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.main_menu:

                return true;
            case R.id.help:

                return true;
            case R.id.login:
                login(item);
                return true;
            case R.id.logout:
                logout(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebookManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void facebookCallback(FBUser user, boolean success, String info) {
        dismissProgressDialog();
        if (success) {
            mUser = user;
            tv.setText(user.getDisplayMessage());
        } else {
            tv.setText(info);
        }

        updateMenu();
    }

    @Override
    public void onTaskComplete(JSONObject json) {
        Gson gson = new Gson();

//            {
//                "teams":[
//                {
//                    "uuid":"a0bb332d-6040-41dd-af76-0dcab3862a66",
//                        "name":"TeamAwesome",
//                        "createdAt":"2016-12-13T22:55:45.000Z",
//                        "updatedAt":"2016-12-13T22:55:45.000Z"
//                }
//                ],
//                "achievements":[
//
//                ],
//                "uuid":"db71d47a-27cc-45a9-9085-92777436ad52",
//                    "email":"cr.blackburn89@gmail.com",
//                    "firstName":"Craig",
//                    "lastName":"Blackburn",
//                    "role":"admin",
//                    "username":"Craig Blackburn",
//                    "createdAt":"2016-12-08T21:50:57.000Z",
//                    "updatedAt":"2016-12-13T21:24:21.000Z",
//                    "name":"Craig Blackburn"
//            }
        try {
            JSONArray playersJson = json.getJSONArray("players");

            FPlayer[] players = new FPlayer[playersJson.length()];
            for (int i = 0; i < playersJson.length(); i++) {
                JSONObject obj = playersJson.getJSONObject(i);
                players[i] = gson.fromJson(obj.toString(), FPlayer.class);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
