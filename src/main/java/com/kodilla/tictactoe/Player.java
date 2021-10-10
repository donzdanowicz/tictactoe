package com.kodilla.tictactoe;

public class Player {
    private String playerName;
    private int buttonClicked;

    public Player(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getButtonClicked() {
        return buttonClicked;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setButtonClicked(int buttonClicked) {
        this.buttonClicked = buttonClicked;
    }
}
