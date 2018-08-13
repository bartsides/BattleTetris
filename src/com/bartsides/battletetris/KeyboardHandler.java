package com.bartsides.battletetris;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class KeyboardHandler extends KeyAdapter {
    private Handler handler;
    private GameControls gameControls;
    ArrayList<KeyboardEventListener> keyboardListeners = new ArrayList<>();

    public KeyboardHandler(Handler handler, GameControls gameControls){
        this.handler = handler;
        keyboardListeners.add(handler);
        this.gameControls = gameControls;
    }

    @Override
    public void keyPressed(KeyEvent e){
        if (handler.player1.controller != null && handler.player2.controller != null)
            return;

        int key = e.getKeyCode();
        Player player = handler.player1.controller == null ? handler.player1 : handler.player2;

        if (handler.addingMode()){
            for (KeyboardEventListener listener : keyboardListeners)
                listener.handleEvent(new KeyboardEvent(key, true));
            return;
        }

        if (!handler.isRunning()){
            if (TimeUnit.MILLISECONDS.toSeconds(new Date().getTime() - handler.gameoverTime.getTime()) > 5)
                handler.start();
            return;
        }

        switch (key){
            case KeyEvent.VK_A:
                // A - LEFT
                handler.movePlayer(player, Direction.Left);
                break;
            case KeyEvent.VK_D:
                // D - RIGHT
                handler.movePlayer(player, Direction.Right);
                break;
            case KeyEvent.VK_W:
                // W - UP
                handler.rotatePlayer(player, -1);
                break;
            case KeyEvent.VK_S:
                // S - DOWN
                handler.rotatePlayer(player, 1);
                break;
            case KeyEvent.VK_SPACE:
                handler.dropDownPlayer(player);
                break;
            default:
                System.out.println("Key Pressed: " + key);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (handler.player1.controller == null && handler.player2.controller == null)
            return;

        int key = e.getKeyCode();
        ArrayList<Player> players = new ArrayList<>();
        if (handler.player1.controller == null)
            players.add(handler.player1);
        if (handler.player2.controller == null)
            players.add(handler.player2);

        for (Player player : players) {
            switch (key) {
                case KeyEvent.VK_A:
                    // A - LEFT
                    handler.stopPlayer(player, Direction.Left);
                    break;
                case KeyEvent.VK_S:
                    // S - DOWN
                    handler.stopPlayer(player, Direction.Down);
                    break;
                case KeyEvent.VK_D:
                    // D - RIGHT
                    handler.stopPlayer(player, Direction.Right);
                    break;
                case KeyEvent.VK_W:
                    // W - UP
                    handler.stopPlayer(player, Direction.Up);
                    break;
            }
        }
    }
}
