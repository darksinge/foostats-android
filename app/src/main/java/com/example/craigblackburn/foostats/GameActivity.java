package com.example.craigblackburn.foostats;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    private FTeam blueTeam;
    private FTeam redTeam;

    private Button blueAdd1Button;
    private Button blueAdd2Button;
    private Button blueSub1Button;
    private Button blueSub2Button;
    private TextView blue1NameLabel;
    private TextView blue2NameLabel;

    private Button redAdd1Button;
    private Button redAdd2Button;
    private Button redSub1Button;
    private Button redSub2Button;
    private TextView red1NameLabel;
    private TextView red2NameLabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        blueTeam = FTeam.deserialize(intent.getStringExtra("blueTeam"));
        redTeam = FTeam.deserialize(intent.getStringExtra("redTeam"));

        if (blueTeam == null || redTeam == null) {
            throw new RuntimeException("Failed to deserialize team(s)!");
        }

        blueAdd1Button = (Button) findViewById(R.id.blue1AddButton);
        blueAdd2Button = (Button) findViewById(R.id.blue2AddButton);
        blueSub1Button = (Button) findViewById(R.id.blue1SubButton);
        blueSub2Button = (Button) findViewById(R.id.blue2SubButton);
        blue1NameLabel = (TextView) findViewById(R.id.blueP1NameLabel);
        blue2NameLabel = (TextView) findViewById(R.id.blueP2NameLabel);

        redAdd1Button = (Button) findViewById(R.id.red1AddButton);
        redAdd2Button = (Button) findViewById(R.id.red2AddButton);
        redSub1Button = (Button) findViewById(R.id.red1SubButton);
        redSub2Button = (Button) findViewById(R.id.red2SubButton);
        red1NameLabel = (TextView) findViewById(R.id.redP1NameLabel);
        red2NameLabel = (TextView) findViewById(R.id.redP2NameLabel);

    }
}
