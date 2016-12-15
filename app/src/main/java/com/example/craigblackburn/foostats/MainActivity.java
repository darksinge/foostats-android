package com.example.craigblackburn.foostats;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends AppCompatActivity implements FModel.ModelListener {

    private final static String TAG = "MAIN_ACTIVITY";

    private DBHelper dbHelper;
    private User mUser;
    private TextView userLabel;
    private FModel model;
    private Button mButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = DBHelper.newInstance(getApplicationContext());
        dbHelper.openDatabase();
        FModel.initialize(dbHelper);
        model = new FModel(this);
        mButton = (Button) findViewById(R.id.test_button);

        Intent intent = getIntent();
        String serializedUser = intent.getStringExtra("user");
        mUser = User.deserialize(serializedUser);

        userLabel = (TextView) findViewById(R.id.user_label);

        try {
            mUser.save();
            userLabel.setText(mUser.getName() + "'s Stats Quick Look");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void startNewGame(View view) {
        Intent intent = new Intent(this, GameSetupActivity.class);
        startActivity(intent);
    }

    public void onTestButtonClick(View view) {
        try {
            List<FPlayer> playerList = FPlayer.find();
            List<FTeam> teamList = FTeam.find();

            Log.d(TAG, "Player Count: " + playerList.size());
            Log.d(TAG, "Team Count: " + teamList.size());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showProgressDialog(@Nullable String message) {

        if (message == null || message.isEmpty()) {
            message = "Loading...";
        }

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(MainActivity.this);
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

        boolean isLogged = User.getLoggedInUser(this) != null;

        menu.findItem(R.id.logout).setVisible(isLogged);
        menu.findItem(R.id.dashboard).setVisible(isLogged);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Oops")
                        .setMessage("This hasn't been implemented yet, sorry!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
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
            case R.id.refresh:
                if (model != null) {
                    showProgressDialog("Fetching all the data...");
                    model.pullFromServer();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static final String PLAYER_REQUEST_FLAG = "player_request";
    public static final String TEAM_REQUEST_FLAG = "team_request";
    public static final String GAME_REQUEST_FLAG = "game_request";
    public static final String ACHIEVEMENT_REQUEST_FLAG = "achievement_request";
    private boolean didRequestPlayers = false;
    private boolean didRequestTeams = false;
    private boolean didRequestGames = false;
    private boolean didRequestAchievements = false;

    @Override
    public void onTaskComplete(boolean isSuccess, int numRecordsInserted, String requestFlag) {
        switch (requestFlag) {
            case PLAYER_REQUEST_FLAG:
                didRequestPlayers = true;
                break;
            case TEAM_REQUEST_FLAG:
                didRequestTeams = true;
                break;
            case GAME_REQUEST_FLAG:
                didRequestGames = true;
                break;
            case ACHIEVEMENT_REQUEST_FLAG:
                didRequestAchievements = true;
                break;
            default:
                break;
        }
        if (didRequestPlayers && didRequestTeams) {
            dismissProgressDialog();
            didRequestPlayers = false;
            didRequestTeams = false;
            didRequestGames = false;
            didRequestAchievements = false;
        }
    }
}
