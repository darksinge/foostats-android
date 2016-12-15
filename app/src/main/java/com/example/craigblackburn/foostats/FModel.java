package com.example.craigblackburn.foostats;

import android.database.SQLException;

import org.json.JSONObject;

import java.util.List;
import java.util.UUID;


public class FModel implements APIRequester.OnAPITaskCompleteListener {

    protected static DBHelper helper;
    private ModelListener mListener;

    public FModel(){}

    public FModel(ModelListener listener) {
        this.mListener = listener;
    }

    public static void initialize(DBHelper dbHelper) {
        helper = dbHelper;
    }

    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }

    public static void pushToServer() {

    }

    public void updateFromServer() {
        if (helper != null) {
            helper.forceUpgradeGameData();
        }
        APIRequester.getPlayers(this);
        APIRequester.getTeams(this);
    }



    @Override
    public void onPlayerResponse(List<FPlayer> players) {
        boolean isSuccess = false;
        int numRecordsInserted = 0;
        for (FPlayer p : players) {
            try {
                p.save();
                numRecordsInserted++;
                isSuccess = true;
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mListener != null) {
            mListener.onTaskComplete(isSuccess, numRecordsInserted, MainActivity.PLAYER_REQUEST_FLAG);
        }
    }

    @Override
    public void onTeamResponse(List<FTeam> teams) {
        boolean isSuccess = false;
        int numRecordsInserted = 0;
        for (FTeam team : teams) {
            try {
                team.save();
                numRecordsInserted++;
                isSuccess = true;
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mListener != null) {
            mListener.onTaskComplete(isSuccess, numRecordsInserted, MainActivity.TEAM_REQUEST_FLAG);
        }
    }

    @Override
    public void onTeamDeleteResponse(boolean success) {
        if (success)
            updateFromServer();
    }

    @Override
    public void onGameResponse(List<FGame> games) {
        boolean isSuccess = false;
        int numRecordsInserted = 0;
        for (FGame game : games) {
            try {
                game.save();
                numRecordsInserted++;
                isSuccess = true;
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mListener != null) {
            mListener.onTaskComplete(isSuccess, numRecordsInserted, MainActivity.GAME_REQUEST_FLAG);
        }
    }

    @Override
    public void onAchievementsResponse(List<Achievements> achievements) {
        boolean isSuccess = false;
        int numRecordsInserted = 0;
        for (Achievements achievement : achievements) {
            try {
                achievement.save();
                numRecordsInserted++;
                isSuccess = true;
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mListener != null) {
            mListener.onTaskComplete(isSuccess, numRecordsInserted, MainActivity.ACHIEVEMENT_REQUEST_FLAG);
        }
    }


    interface ModelListener {
        void onTaskComplete(boolean isSuccess, int numRecordsInserted, String requestFlag);
    }

}
