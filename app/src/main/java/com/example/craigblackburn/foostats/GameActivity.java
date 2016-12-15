package com.example.craigblackburn.foostats;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    private FTeam blueTeam;
    private FTeam redTeam;
    private FGame game;

    private TextView scoreLabel;

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
        String blueTeamId = intent.getStringExtra("blueTeamId");
        String redTeamId = intent.getStringExtra("redTeamId");

        try {
            blueTeam = FTeam.find(blueTeamId);
            redTeam = FTeam.find(redTeamId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (blueTeam == null || redTeam == null) {
            throw new RuntimeException("Failed to deserialize team(s)!");
        }

        game = new FGame(blueTeam, redTeam);

        scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        scoreLabel.setText("Blue Team : 0       Red Team : 0");

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

        blue1NameLabel.setText(blueTeam.getPlayerOne().getName());
        blue2NameLabel.setText(blueTeam.getPlayerTwo().getName());

        red1NameLabel.setText(redTeam.getPlayerOne().getName());
        red2NameLabel.setText(redTeam.getPlayerTwo().getName());

        addButtonListeners();

    }

    public void updateScore() {
        int blueTeamScore = game.getBlueTeamScore();
        int redTeamScore = game.getRedTeamScore();
        scoreLabel.setText("Blue Team : " + String.valueOf(blueTeamScore) + "     Red Team: " + String.valueOf(redTeamScore));
        if (blueTeamScore >= 10) {
            blueTeam.getPlayerOne().addWin();
            blueTeam.getPlayerTwo().addWin();
            redTeam.getPlayerOne().save();
            redTeam.getPlayerTwo().save();
            blueTeam.save();
            redTeam.save();
            new AlertDialog.Builder(GameActivity.this)
                    .setTitle("Blue Team Won!")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(GameActivity.this, MainActivity.class);
                            try {
                                game.save();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            startActivity(intent);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else if (redTeamScore >= 10) {
            redTeam.getPlayerOne().addWin();
            redTeam.getPlayerTwo().addWin();
            redTeam.getPlayerOne().save();
            redTeam.getPlayerTwo().save();
            new AlertDialog.Builder(GameActivity.this)
                    .setTitle("Red Team Won!")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(GameActivity.this, MainActivity.class);
                            try {
                                game.save();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            startActivity(intent);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }

    public void addButtonListeners() {
        blueAdd1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.addPoint(blueTeam.getPlayerOne());
                updateScore();
            }
        });

        blueAdd2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.addPoint(blueTeam.getPlayerTwo());
                updateScore();
            }
        });

        blueSub1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.subtractPoint(0);
                updateScore();
            }
        });

        blueSub2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.subtractPoint(1);
                updateScore();
            }
        });

        redAdd1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.addPoint(redTeam.getPlayerTwo());
                updateScore();
            }
        });

        redAdd2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.addPoint(redTeam.getPlayerTwo());
                updateScore();
            }
        });

        redSub1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.subtractPoint(2);
                updateScore();
            }
        });

        redSub2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.subtractPoint(3);
                updateScore();
            }
        });





    }

}
