package com.example.craigblackburn.foostats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class FacebookLoginManager extends Activity {

    private static final String TAG = FacebookLoginManager.class.getSimpleName();
    private FacebookListener mListener;
    private CallbackManager callbackManager;

    private FacebookLoginManager(FacebookListener listener) {
        callbackManager = CallbackManager.Factory.create();
        mListener = listener;

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        makeGraphRequest();
                    }

                    @Override
                    public void onCancel() {
                        mListener.facebookCallback(null, false, "User cancelled login");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("E_FACEBOOK_LOGIN", error.toString());
                        mListener.facebookCallback(null, false, error.toString());
                    }

                });
    }

    public static FacebookLoginManager newInstance(FacebookListener listener) {
        return new FacebookLoginManager(listener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    public void makeGraphRequest() {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email");

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String email = object.getString("email");
                            String id = object.getString("id");
                            String token = AccessToken.getCurrentAccessToken().getToken();
                            User user = new User(id, token, email);

                            boolean success = user.save();

                            if (!success) {
                                Log.d(TAG, "Failed to insert new user into database. :(");
                            }

                            mListener.facebookCallback(user, success, "User inserted into database");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void login(Activity activity) {
        LoginManager instance = LoginManager.getInstance();
        if (instance != null) {
            instance.logInWithReadPermissions(activity, Arrays.asList("email", "public_profile"));
        }
    }

    public void logout() {
        LoginManager.getInstance().logOut();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    interface FacebookListener {
        void facebookCallback(User user, boolean success, String info);
    }


}
