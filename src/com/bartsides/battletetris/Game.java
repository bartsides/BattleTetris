package com.bartsides.battletetris;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class Game extends Canvas implements Runnable{
    //public static final int width = 640, height = width / 12 * 9;
    public static final int columns = 12, rows = 30;
    public static final int width = Piece.blockWidth*columns + 16, height = Piece.blockWidth*rows + 39;
    public static final Color player1Color = Color.ORANGE, player2Color = Color.BLUE, wellColor = Color.GRAY;


    private Thread thread;
    private boolean running = false;
    private Handler handler = new Handler();
    private GameControls gameControls;

    public Game(){
        try {
            this.addKeyListener(new KeyboardHandler(handler, gameControls));
            new Window(width, height, "Battle Tetris", this);
            initialize();
        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void initialize(){
        gameControls = new GameControls();
    }

    public synchronized void start(){
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    public synchronized void stop(){
        try{
            thread.join();
            running = false;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            while (delta >= 1) {
                //tick();
                delta--;
            }

            if (running)
                render();

            frames++;
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                frames = 0;
            }
        }

        stop();
    }

    private void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null){
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        g.setColor(Color.black);
        g.fillRect(0,0,width,height);

        handler.render(g);

        g.dispose();
        bs.show();
    }

    public static void main (String[] args){
        new Game();
    }
}
