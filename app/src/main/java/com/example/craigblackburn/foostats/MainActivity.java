package com.example.craigblackburn.foostats;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MAIN_ACTIVITY";

    private DBHelper dbHelper;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = DBHelper.newInstance(getApplicationContext());
        dbHelper.openDatabase();
        FModels.initialize(dbHelper);

        Intent intent = getIntent();
        String serializedUser = intent.getStringExtra("user");

        mUser = User.deserialize(serializedUser);

        if (mUser != null) {
            mUser.save();
        }

//        dbHelper.forceUpgrade();

        setupView();

    }

    public void onTestButtonClick(View view) {
        // do stuff...
    }

    private void setupView() {
        // do stuff...
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.logout).setVisible(AccessToken.getCurrentAccessToken() != null);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:

                return true;
            case R.id.help:

                return true;
            case R.id.logout:
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.stats:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
