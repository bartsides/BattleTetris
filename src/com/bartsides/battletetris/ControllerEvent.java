package com.bartsides.battletetris;

import java.util.EventObject;

import net.java.games.input.Component;
import net.java.games.input.Event;

public class ControllerEvent extends EventObject {
    private Event event;
    private Component component;
    public Player player;
    public float value, deadzone;
    public String name;
    public boolean isAnalog;

    ControllerEvent(Object source, Player player) throws Exception{
        super(source);

        if (!(source instanceof Event))
            throw new Exception("Unidentified object.");

        event = (Event) source;
        value = event.getValue();
        component = event.getComponent();
        name = component.getName();
        deadzone = component.getDeadZone();
        isAnalog = component.isAnalog();
        this.player = player;
    }
}