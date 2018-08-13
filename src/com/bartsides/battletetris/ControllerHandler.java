package com.bartsides.battletetris;

import net.java.games.input.*;

import java.util.ArrayList;

public class ControllerHandler implements Runnable {
    Handler handler;
    Player player;
    ArrayList<ControllerEventListener> controllerListeners = new ArrayList<>();
    Controller controller = null;

    private ControllerHandler(Handler handler){
        this.handler = handler;
        controllerListeners.add(handler);
    }

    public ControllerHandler(Handler handler, Player player){
        this(handler);
        this.player = player;
    }

    public ControllerHandler(Handler handler, Controller controller){
        this(handler);
        this.controller = controller;
    }

    @Override
    public void run() {
        if (controller != null) {
            EventQueue eventQueue = controller.getEventQueue();
            Event event = new Event();
            while (true) {
                if (!handler.addingMode())
                    return;

                if (!controller.poll())
                    return;

                while (eventQueue.getNextEvent(event)) {
                    if (!handler.addingMode())
                        return;

                    if (event.getComponent() != null && !event.getComponent().isAnalog() && event.getValue() == 1) {
                        try {
                            Player player = new Player(Direction.Down, controller);
                            ControllerEvent controllerEvent = new ControllerEvent(event, player);

                            for (ControllerEventListener listener : controllerListeners)
                                listener.handleEvent(controllerEvent);
                        } catch (Exception e) {}
                    }
                }
            }
        }

        while (true) {
            Controller controller = player.controller;

            if (!controller.poll()) {
                handler.addPlayers();
                continue;
            }

            EventQueue eventQueue = controller.getEventQueue();
            Event event = new Event();
            while (eventQueue.getNextEvent(event)) {
                try {
                    ControllerEvent controllerEvent = new ControllerEvent(event, player);

                    boolean reportable = false;
                    float value = controllerEvent.value;

                    switch (controllerEvent.name) {
                        case "Y Axis":  // Left Joystick Up or Down
                            if (Math.abs(value) >= .7)
                                reportable = true;
                            break;
                        case "X Axis":  // Left Joystick Left or Right
                            if (Math.abs(value) >= .7)
                                reportable = true;
                            break;
                        case "Button 0": // A
                            if (value == 1)
                                reportable = true;
                            break;
                        case "Button 1": // B
                            if (value == 1)
                                reportable = true;
                            break;
                        case "Button 2": // X
                            if (value == 1)
                                reportable = true;
                            break;
                        case "Button 3": // Y
                            if (value == 1)
                                reportable = true;
                            break;
                        case "Z Axis": // Triggers - Left 0 to 1 : Right 0 to -1
                            if (Math.abs(value) >= .7)
                                reportable = true;
                            break;
                        case "Hat Switch":
                            reportable = true;
                            break;
                    }

                    if (!reportable)
                        continue;

                    for (ControllerEventListener listener : controllerListeners) {
                        listener.handleEvent(controllerEvent);
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
    }
}
