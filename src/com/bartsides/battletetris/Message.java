package com.bartsides.battletetris;

import java.util.Calendar;

public class Message {
    public String message;
    public Calendar expiration;

    public Message(String message, int seconds){
        this.message = message;
        this.expiration = Calendar.getInstance();
        this.expiration.add(Calendar.SECOND, seconds);
    }
}