package com.example.craigblackburn.foostats;

import java.util.Objects;
import java.util.UUID;


public class FGame extends FModels {

    public static int BLUE_PLAYER_ONE = 0;
    public static int BLUE_PLAYER_TWO = 1;
    public static int RED_PLAYER_ONE = 2;
    public static int RED_PLAYER_TWO = 3;
    public static String TIE_GAME_FLAG = "tie_game";

    private String uuid;
    private FTeam blueTeam, redTeam;
    private FPlayer bluePlayer1, bluePlayer2, redPlayer1, redPlayer2;
    private int blueTeamPlayer1Score, blueTeamPlayer2Score, redTeamPlayer1Score, redTeamPlayer2Score;

    public FGame(){}

    public FGame(String id, FTeam blueTeam, FTeam redTeam) {
        this.uuid = id;
        this.blueTeam = blueTeam;
        this.redTeam = redTeam;
        this.blueTeamPlayer1Score = 0;
        this.blueTeamPlayer2Score = 0;
        this.redTeamPlayer1Score = 0;
        this.redTeamPlayer2Score = 0;
        this.bluePlayer1 = blueTeam.getPlayerOne();
        this.bluePlayer2 = blueTeam.getPlayerTwo();
        this.redPlayer1 = redTeam.getPlayerOne();
        this.redPlayer2 = redTeam.getPlayerTwo();
    }

    public FGame newGame(FTeam blueTeam, FTeam redTeam) {
        return new FGame(generateUuid(), blueTeam, redTeam);
    }

    public String getId() {
        return this.uuid;
    }

    public FTeam getBlueTeam() {
        return this.blueTeam;
    }

    public FTeam getRedTeam() {
        return this.redTeam;
    }

    public Integer getBlueTeamScore() {
        return getBluePlayerOneScore() + getBluePlayerTwoScore();
    }

    public Integer getRedTeamScore() {
        return getRedPlayerOneScore() + getRedPlayerTwoScore();
    }

    public Integer getBluePlayerOneScore() {
        return this.blueTeamPlayer1Score;
    }

    public Integer getBluePlayerTwoScore() {
        return this.blueTeamPlayer2Score;
    }

    public Integer getRedPlayerOneScore() {
        return this.redTeamPlayer1Score;
    }

    public Integer getRedPlayerTwoScore() {
        return this.redTeamPlayer2Score;
    }

    public FTeam getWinningTeam() {
        if (getBlueTeamScore() > getRedTeamScore()) {
            return blueTeam;
        } else if (getBlueTeamScore() < getRedTeamScore()) {
            return redTeam;
        } else {
            return null;
        }
    }

    public String getWinningTeamId() {
        if (getBlueTeamScore() > getRedTeamScore()) {
            return blueTeam.getId();
        } else if (getBlueTeamScore() < getRedTeamScore()) {
            return redTeam.getId();
        } else {
            return TIE_GAME_FLAG;
        }
    }

    public FPlayer getPlayer(int i) {
        switch (i) {
            case 0:
                return this.blueTeam.getPlayerOne();
            case 1:
                return this.blueTeam.getPlayerTwo();
            case 2:
                return this.redTeam.getPlayerOne();
            case 3:
                return this.redTeam.getPlayerTwo();
            default:
                return null;
        }
    }

    public Integer addPoint(FPlayer player) {
        if (Objects.equals(player.getId(), bluePlayer1.getId())) {
            return ++blueTeamPlayer1Score;
        }

        if (Objects.equals(player.getId(), bluePlayer2.getId())) {
            return ++blueTeamPlayer2Score;
        }

        if (Objects.equals(player.getId(), redPlayer1.getId())) {
            return ++redTeamPlayer1Score;
        }

        if (Objects.equals(player.getId(), redPlayer2.getId())) {
            return ++redTeamPlayer2Score;
        }

        return null;
    }

    public void setScore(int playerPosition, int score) {
        switch (playerPosition) {
            case 0:
                blueTeamPlayer1Score = score;
                break;
            case 1:
                blueTeamPlayer2Score = score;
                break;
            case 2:
                redTeamPlayer1Score = score;
                break;
            case 3:
                redTeamPlayer2Score = score;
                break;
            default:
                break;
        }
    }

    private boolean canSave() {
        return (this.uuid != null && this.blueTeam != null && this.redTeam != null);
    }

    public boolean save() {
        if (canSave()) {
            return helper.insert(this) > 0;
        } else {
            return false;
        }
    }

}
