package com.bartsides.battletetris;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControllerFinder implements Runnable{
    private ArrayList<Controller> controllers = new ArrayList<>();
    private Handler handler;
    private ExecutorService executorService = Executors.newFixedThreadPool(8);

    public ControllerFinder(Handler handler){
        this.handler = handler;
    }

    @Override
    public void run() {
        while (true) {
            Controller[] allControllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
            for (Controller controller : allControllers) {
                for (Controller existingController : controllers)
                {
                    int portNumer = controller.getPortNumber();

                    System.out.println(existingController.getName());
                }

                String name = controller.getName();
                if (name.equals("Controller (XBOX 360 For Windows)")) {
                    executorService.execute(new ControllerHandler(handler, controller));
                }
            }
        }
    }
}
