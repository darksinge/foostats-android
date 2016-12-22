package com.example.craigblackburn.foostats;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static String TAG = "LOGIN_ACTIVITY";

    private CallbackManager callbackManager;
    private ProgressDialog progressDialog;
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
//        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
        AppEventsLogger.activateApp(getApplication());
        setContentView(R.layout.activity_login);

        /**************************** DB Upgrade ******************************/
        /************* (Uncomment methods for development only) ***************/
//        dropTables();
//        dropGameDataOnly();
        /**********************************************************************/
        /**********************************************************************/

        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "ON SUCCESS CALLBACK");
                attemptLogin();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Login Error")
                        .setMessage("There was an error logging you in.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        FGame game = new FGame();
//        game.startTimer();

        attemptLogin();
    }

    private void dropTables() {
        DBHelper dbHelper = DBHelper.newInstance(getApplicationContext());
        dbHelper.openDatabase();
        FModel.initialize(dbHelper);
        dbHelper.forceUpgrade();
    }

    private void dropGameDataOnly() {
        DBHelper dbHelper = DBHelper.newInstance(getApplicationContext());
        dbHelper.openDatabase();
        FModel.initialize(dbHelper);
        dbHelper.forceUpgradeGameData();
    }

    public synchronized void attemptLogin() {
        Log.d(TAG, "ATTEMPTING LOGIN");
        User user = User.getLoggedInUser(this);
        if (user != null) {
            Log.d(TAG, "THE LOGGED IN USER EXISTS");
            performSegueToMainActivity(user);
        } else if (AccessToken.getCurrentAccessToken() != null) {
            Log.d(TAG, "ABOUT TO MAKE GRAPH REQUEST - 1 ");
            showProgressDialog("Grabbing some fancy info...");
            makeGraphRequest(new LoginListener() {
                @Override
                public void onTaskComplete(User user) {
                    dismissProgressDialog();
                    Log.d(TAG, "ON TASK COMPLETE - 1 ");
                    try {
                        user.save();
                        performSegueToMainActivity(user);
                    } catch (SQLiteException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Log.d(TAG, "LOGIN ATTEMPT FAILED     ");
        }
    }

    public void performSegueToMainActivity(User user) {
        Log.d(TAG, "PERFORMING INTENT");
        Intent intent = new Intent(this, MainActivity.class);
        if (user != null) {
            intent.putExtra("user", User.serialize(user));
        }
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.d("LOGIN_ACTIVITY", data.toString());
    }

    public void makeGraphRequest(final LoginListener listener) {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,name");

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        User user = null;
                        try {
                            String email = object.getString("email");
                            String id = object.getString("id");
                            String name = object.getString("name");
                            String token = AccessToken.getCurrentAccessToken().getToken();

                            user = new User(id, token, email, name);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (SQLiteException sError) {
                            sError.printStackTrace();
                        }

                        listener.onTaskComplete(user);

                    }
                });
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void showProgressDialog(@Nullable String message) {

        if (message == null || message.isEmpty()) {
            message = "Loading...";
        }

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(message);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }



    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        super.onPrepareOptionsMenu(menu);

        boolean isLogged = User.getLoggedInUser(this) != null;

        menu.findItem(R.id.logout).setVisible(isLogged);
        menu.findItem(R.id.dashboard).setVisible(isLogged);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        AccessToken token = AccessToken.getCurrentAccessToken();
        Log.d(TAG, "Token NOT NULL: " + String.valueOf(token != null));
        attemptLogin();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, LoginActivity.class);
        switch (item.getItemId()) {
            case R.id.settings:
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Info")
                        .setMessage("This has not been implemented yet.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            case R.id.dashboard:
                User user = User.getLoggedInUser(this);
                if (user != null) {
                    performSegueToMainActivity(user);
                    return true;
                } else {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Error")
                            .setMessage("Please login first.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return false;
                }
            case R.id.help:
                // not implemented
                return true;
            case R.id.logout:
                LoginManager.getInstance().logOut();
                startActivity(intent);
                return true;
            case R.id.stats:
                // not implemented
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    interface LoginListener {
        void onTaskComplete(User user);
    }


}

