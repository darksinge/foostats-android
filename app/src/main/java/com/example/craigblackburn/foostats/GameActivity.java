package com.example.craigblackburn.foostats;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GameActivity extends AppCompatActivity {

    private FTeam blueTeam;
    private FTeam redTeam;

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

    }
}
