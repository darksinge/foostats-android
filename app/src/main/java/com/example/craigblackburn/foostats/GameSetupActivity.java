package com.example.craigblackburn.foostats;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class GameSetupActivity extends AppCompatActivity {

    private List<FPlayer> players;

    private Button selectBlueTeamButton;
    private Button selectRedTeamButton;
    private Button startGameButton;
    private SwitchCompat switchCompat;

    private PlayerListFragment playerListFragment;
    private boolean isRandomTeams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setup);

        Intent intent = getIntent();

        selectBlueTeamButton = (Button) findViewById(R.id.select_blue_players);
        selectRedTeamButton = (Button) findViewById(R.id.select_red_players);
        startGameButton = (Button) findViewById(R.id.start_game_button);

        try {
            players = FPlayer.find();
        } catch (Exception e) {
            e.printStackTrace();
            players = new ArrayList<>();
            new AlertDialog.Builder(GameSetupActivity.this)
                    .setTitle("Hmm, there might be a problem...")
                    .setMessage("There might be a problem with the server, no players could be found. Please try refreshing your data by tapping \"Refresh\" from the toolbar menu.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        setButtonOnClickListeners();

    }

    private void setButtonOnClickListeners() {
        selectBlueTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerListFragment = PlayerListFragment.newInstance(players.size(), false, PlayerListFragment.TeamSelector.BLUE_TEAM);
            }
        });

        selectRedTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

}
