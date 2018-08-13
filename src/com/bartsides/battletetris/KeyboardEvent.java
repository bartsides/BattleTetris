package com.bartsides.battletetris;

public class KeyboardEvent {
    public int key;
    public boolean pressed;

    public KeyboardEvent(int key, boolean pressed){
        this.key = key;
        this.pressed = pressed;
    }
}
