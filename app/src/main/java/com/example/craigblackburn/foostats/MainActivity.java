package com.example.craigblackburn.foostats;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
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
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements FacebookLoginManager.FacebookListener {

    private final static String TAG = "MAIN_ACTIVITY";

    private FacebookLoginManager facebookManager;
    private DBHelper dbHelper;
    private Menu mMenu;
    private TextView tv;
    private ProgressDialog progressDialog;
    private User mUser;
    private Button testButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
        AppEventsLogger.activateApp(getApplication());

        FModels.initialize(getApplicationContext());

        dbHelper = new DBHelper(getApplicationContext());
        tv = (TextView) findViewById(R.id.text_view);
        facebookManager = FacebookLoginManager.newInstance(this);
        testButton = (Button) findViewById(R.id.test_button);

        dbHelper.forceUpgrade();

        mUser = User.findOne();

        if (mUser != null) {
            tv.setText(mUser.getDisplayMessage());
        } else {
            tv.setText("User not found!");
        }

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIRequester.getPlayers(new PlayerDelegate() {
                    @Override
                    public void onTaskComplete(ArrayList<FPlayer> list) {
                        String message = "";
                        for (FPlayer player : list) {
                            message += player.toString() + "\n";
                        }
                        tv.setText(message);
                    }
                });
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
        return User.findOne() != null;
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

        User user = User.findOne();
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
    public void facebookCallback(User user, boolean success, String info) {
        dismissProgressDialog();
        if (success) {
            mUser = user;
            tv.setText(user.getDisplayMessage());
        } else {
            tv.setText(info);
        }

        updateMenu();
    }

}
