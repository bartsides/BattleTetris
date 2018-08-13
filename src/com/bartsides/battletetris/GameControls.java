package com.bartsides.battletetris;

import java.awt.event.KeyEvent;

public class GameControls {
    {
        player1left = KeyEvent.VK_A;
        player1right = KeyEvent.VK_D;
        player1down = KeyEvent.VK_S;
        player2left = KeyEvent.VK_LEFT;
        player2right = KeyEvent.VK_RIGHT;
        player2down = KeyEvent.VK_DOWN;
    }

    private int player1left;
    private int player1right;
    private int player1down;
    private int player2left;
    private int player2right;
    private int player2down;

    public int getPlayer1left() {
        return player1left;
    }

    public void setPlayer1left(int player1left) {
        this.player1left = player1left;
    }

    public int getPlayer1right() {
        return player1right;
    }

    public void setPlayer1right(int player1right) {
        this.player1right = player1right;
    }

    public int getPlayer1down() {
        return player1down;
    }

    public void setPlayer1down(int player1down) {
        this.player1down = player1down;
    }

    public int getPlayer2left() {
        return player2left;
    }

    public void setPlayer2left(int player2left) {
        this.player2left = player2left;
    }

    public int getPlayer2right() {
        return player2right;
    }

    public void setPlayer2right(int player2right) {
        this.player2right = player2right;
    }

    public int getPlayer2down() {
        return player2down;
    }

    public void setPlayer2down(int player2down) {
        this.player2down = player2down;
    }
}
