package com.example.craigblackburn.foostats;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

public class GameSetupActivity extends AppCompatActivity {

    private List<FPlayer> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setup);

        Intent intent = getIntent();

        try {
            players = FPlayer.find();
        } catch (Exception e) {
            e.printStackTrace();
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



    }

}
