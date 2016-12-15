package com.example.craigblackburn.foostats;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GameSetupActivity extends AppCompatActivity implements PlayerListFragment.OnFragmentInteractionListener {

    private static final String TAG = "GAME_SETUP_ACTIVITY";
    private List<FPlayer> mPlayers;

    private Button selectBlueTeamButton;
    private Button selectRedTeamButton;
    private Button startGameButton;
    private Switch switch1;
    private TextView bluePlayerOneLabel;
    private TextView bluePlayerTwoLabel;
    private TextView redPlayerOneLabel;
    private TextView redPlayerTwoLabel;

    private FTeam blueTeam;
    private FTeam redTeam;
    private List<FPlayer> randomPlayers;
    private List<FPlayer> removedQueue;

    private PlayerListFragment playerListFragment;
    private boolean isRandomTeams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setup);

//        Intent intent = getIntent();

        selectBlueTeamButton = (Button) findViewById(R.id.select_blue_players);
        selectRedTeamButton = (Button) findViewById(R.id.select_red_players);
        startGameButton = (Button) findViewById(R.id.start_game_button);
        switch1 = (Switch) findViewById(R.id.switch1);
        bluePlayerOneLabel = (TextView) findViewById(R.id.blue_player_one);
        bluePlayerTwoLabel = (TextView) findViewById(R.id.blue_player_two);
        redPlayerOneLabel = (TextView) findViewById(R.id.red_player_one);
        redPlayerTwoLabel = (TextView) findViewById(R.id.red_player_two);

        blueTeam = new FTeam();
        redTeam = new FTeam();
        randomPlayers = new ArrayList<>();
        removedQueue = new ArrayList<>();

        startGameButton.setEnabled(false);

        isRandomTeams = switch1.isChecked();
        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view instanceof Switch) {
                    isRandomTeams = ((Switch) view).isChecked();
                    resetTeams();
                    startGameButton.setEnabled(false);
                    blueTeam.setPlayerOne(null);
                    blueTeam.setPlayerTwo(null);
                    redTeam.setPlayerOne(null);
                    redTeam.setPlayerTwo(null);

                    bluePlayerOneLabel.setText("not selected");
                    bluePlayerTwoLabel.setText("not selected");
                    redPlayerOneLabel.setText("not selected");
                    redPlayerTwoLabel.setText("not selected");

                    selectBlueTeamButton.setEnabled(!isRandomTeams);
                    selectRedTeamButton.setEnabled(!isRandomTeams);
                    if (isRandomTeams) {
                        playerListFragment = PlayerListFragment.newInstance(GameSetupActivity.this, mPlayers, PlayerListFragment.TeamSelector.RANDOM_TEAMS);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.list_fragment_container, playerListFragment)
                                .commit();
                    } else if (playerListFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .remove(playerListFragment)
                                .commit();
                    }
                }
            }
        });

        try {
            mPlayers = FPlayer.find();
        } catch (Exception e) {
            e.printStackTrace();
            mPlayers = new ArrayList<>();
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

    public void resetTeams() {
        blueTeam.setPlayerOne(null);
        blueTeam.setPlayerTwo(null);
        redTeam.setPlayerOne(null);
        redTeam.setPlayerTwo(null);
        try {
            mPlayers = FPlayer.find();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setButtonOnClickListeners() {
        selectBlueTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerListFragment.TeamSelector selector = isRandomTeams ? PlayerListFragment.TeamSelector.RANDOM_TEAMS : PlayerListFragment.TeamSelector.BLUE_TEAM;
                playerListFragment = PlayerListFragment.newInstance(GameSetupActivity.this, mPlayers, selector);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.list_fragment_container, playerListFragment)
                        .commit();
            }
        });

        selectRedTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerListFragment.TeamSelector selector = isRandomTeams ? PlayerListFragment.TeamSelector.RANDOM_TEAMS : PlayerListFragment.TeamSelector.RED_TEAM;
                playerListFragment = PlayerListFragment.newInstance(GameSetupActivity.this, mPlayers, selector);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.list_fragment_container, playerListFragment)
                        .commit();
            }
        });

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRandomTeams) {
                    if (randomPlayers.size() != 4) {
                        new AlertDialog.Builder(GameSetupActivity.this)
                                .setTitle("Hold up there cowboy.")
                                .setMessage("You need to select at least 4 players")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    } else {

                        List<FPlayer> players = new ArrayList<>();
                        while (randomPlayers.size() > 0) {
                            int randomNum = new Random().nextInt(randomPlayers.size());
                            players.add(randomPlayers.remove(randomNum));
                        }

                        Log.d(TAG, String.valueOf(players.size()));

                        blueTeam.addPlayer(players.get(0));
                        blueTeam.addPlayer(players.get(1));
                        redTeam.addPlayer(players.get(2));
                        redTeam.addPlayer(players.get(3));

                        performIntentWithGameActivity(blueTeam, redTeam);

                    }
                } else {
                    if (blueTeam.playerCount() == 2 && redTeam.playerCount() == 2) {
                        performIntentWithGameActivity(blueTeam, redTeam);
                    } else {
                        new AlertDialog.Builder(GameSetupActivity.this)
                                .setTitle("You wot mate?")
                                .setMessage("Each team needs to have 2 players")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }
            }
        });
    }

    private void performIntentWithGameActivity(FTeam blue, FTeam red) {
        blue.generateName();
        red.generateName();
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("blueTeam", FTeam.serialize(blue));
        intent.putExtra("redTeam", FTeam.serialize(red));
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(FPlayer player, PlayerListFragment.TeamSelector selector, boolean didSelect) {
        switch (selector) {
            case BLUE_TEAM:
                if (didSelect) {
                    int position = blueTeam.addPlayer(player);
                    if (position == 0) {
                        bluePlayerOneLabel.setText(player.getUsername());
                    } else if (position == 1) {
                        bluePlayerTwoLabel.setText(player.getUsername());
                    }
                } else {
                    int position = blueTeam.removePlayer(player);
                    if (position == 0) {
                        bluePlayerOneLabel.setText("not selected");
                    } else if (position == 1) {
                        bluePlayerTwoLabel.setText("not selected");
                    }
                }
                if (blueTeam.playerCount() == 2 && playerListFragment != null) {
                    for (FPlayer p : removedQueue) {
                        mPlayers.remove(p);
                    }
                    Snackbar.make(findViewById(android.R.id.content), "Blue Team Selected", Snackbar.LENGTH_LONG)
                            .show();
                    playerListFragment.setListFragmentCheckBoxEnabled(false);
                    getSupportFragmentManager().beginTransaction()
                            .remove(playerListFragment)
                            .commit();
                }
                break;
            case RED_TEAM:
                if (didSelect) {
                    int position = redTeam.addPlayer(player);
                    if (position == 0) {
                        redPlayerOneLabel.setText(player.getUsername());
                    } else if (position == 1) {
                        redPlayerTwoLabel.setText(player.getUsername());
                    }
                } else {
                    int position = redTeam.removePlayer(player);
                    if (position == 0) {
                        redPlayerOneLabel.setText("not selected");
                    } else if (position == 1) {
                        redPlayerTwoLabel.setText("not selected");
                    }
                }
                if (redTeam.playerCount() == 2 && playerListFragment != null) {
                    for (FPlayer p : removedQueue) {
                        mPlayers.remove(p);
                    }
                    Snackbar.make(findViewById(android.R.id.content), "Red Team Selected", Snackbar.LENGTH_LONG)
                            .show();
                    playerListFragment.setListFragmentCheckBoxEnabled(false);
                    getSupportFragmentManager().beginTransaction()
                            .remove(playerListFragment)
                            .commit();
                }
                break;
            case RANDOM_TEAMS:
                if (didSelect) {
                    randomPlayers.add(player);
                } else {
                    randomPlayers.remove(player);
                }
                if (randomPlayers.size() == 4) {
                    for (FPlayer p : removedQueue) {
                        mPlayers.remove(p);
                    }
                    Snackbar.make(findViewById(android.R.id.content), "Selected Players", Snackbar.LENGTH_LONG)
                            .show();
                    playerListFragment.setListFragmentCheckBoxEnabled(false);
                    getSupportFragmentManager().beginTransaction()
                            .remove(playerListFragment)
                            .commit();
                }
                break;
        }

        if (didSelect && !mPlayers.contains(player)) {
            mPlayers.add(player);
            removedQueue.remove(player);
        } else {
            removedQueue.add(player);
        }

        if (blueTeam.playerCount() == 2 && redTeam.playerCount() == 2) {
            startGameButton.setEnabled(true);
        } else if (randomPlayers.size() == 4) {
            startGameButton.setEnabled(true);
        }

        Log.d(TAG, "Blue Team Count: " + String.valueOf(blueTeam.playerCount()));
        Log.d(TAG, "Red Team Count: " + String.valueOf(redTeam.playerCount()));
        Log.d(TAG, "Random Team Count: " + String.valueOf(randomPlayers.size()));
    }
}
