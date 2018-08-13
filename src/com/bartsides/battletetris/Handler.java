package com.bartsides.battletetris;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.bartsides.battletetris.Direction.Left;
import static com.bartsides.battletetris.Direction.Right;
import static com.bartsides.battletetris.Piece.blockWidth;

public class Handler implements ControllerEventListener, KeyboardEventListener {
    public Player player1 = null, player2 = null;
    private Color[][] well;
    private Random random = new Random();
    private Timer timer = null, playersAddedTimer = null;
    private boolean running = true;
    public Date gameoverTime = null;
    private Thread thread, controllerFinderThread;
    private ControllerHandler controllerHandler;
    private ControllerFinder controllerFinder = null;
    private boolean addingPlayer1 = false;
    private boolean addingPlayer2 = false;
    private ExecutorService executorService;
    public ArrayList<Message> messages = new ArrayList<>();
    private ArrayList<ControllerHandler> controllerHandlers;

    public Handler(){
        executorService = Executors.newFixedThreadPool(8);
        initializeWell();
        addPlayers();
    }

    public boolean addingMode(){
        return addingPlayer1 || addingPlayer2;
    }

    public void addPlayers(){
        player1 = new Player(Direction.Down);
        player2 = new Player(Direction.Up);
        addingPlayer1 = true;

        controllerFinder = new ControllerFinder(this);
        controllerFinderThread = new Thread(controllerFinder);
        controllerFinderThread.start();

        if (playersAddedTimer != null)
            playersAddedTimer.cancel();
        playersAddedTimer = new Timer();
        playersAddedTimer.scheduleAtFixedRate(new TimerTask(){
            public void run(){
                if (!addingMode())
                    playersAdded();
            }
        }, 0, 10);
    }

    public void playersAdded(){
        playersAddedTimer.cancel();
        executorService.shutdown();
        executorService = Executors.newFixedThreadPool(8);
        controllerHandlers = new ArrayList<>();

        if (player1.controller != null || player2.controller != null) {
            if (player1.controller != null){
                ControllerHandler controllerHandler = new ControllerHandler(this, player1);
                controllerHandlers.add(controllerHandler);
                executorService.execute(new ControllerHandler(this, player1));
            }

            if (player2.controller != null) {
                ControllerHandler controllerHandler = new ControllerHandler(this, player2);
                controllerHandlers.add(controllerHandler);
                executorService.execute(new ControllerHandler(this, player2));
            }
        }

        start();
    }

    public void start(){
        initializeWell();
        newPlayerPiece(player1);
        newPlayerPiece(player2);

        scheduleTimer();
        running = true;
    }

    public void scheduleTimer(){
        if (timer != null)
            timer.cancel();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            public void run(){
                dropDownTick();
            }
        },0, 1000);
    }

    public void pause(){
        if (timer != null)
            timer.cancel();
        running = false;
    }

    public void resume(){
        scheduleTimer();
        running = true;
    }

    private void gameover(boolean player1wins){
        pause();
        String message = "Player " + (player1wins ? "1":"2") + " wins!";
        System.out.println(message);
        messages.add(new Message(message, 5));
        running = false;
        gameoverTime = new Date();
    }

    public void render(Graphics g){
        // Paint the well
        g.setColor(Game.wellColor);
        g.fillRect(0, 0, blockWidth*Game.columns, blockWidth*Game.rows);

        for (int i = 0; i < Game.columns; i++) {
            for (int j = 0; j < Game.rows; j++) {
                g.setColor(well[i][j]);
                g.fillRect(blockWidth*i, blockWidth*j, blockWidth-1, blockWidth-1);
            }
        }

        if (player1 != null && player1.getPiece() != null)
            player1.getPiece().render(g);
        if (player2 != null && player2.getPiece() != null)
            player2.getPiece().render(g);

        if (addingMode()){
            displayMessage("player " + (addingPlayer1 ? "1" : "2") + " press a button", g);
        }
        else if (messages.size() > 0){
            ArrayList<Message> removalList = new ArrayList<>();
            for (Message message : messages) {
                if (Calendar.getInstance().getTime().compareTo(message.expiration.getTime()) > 0) {
                    removalList.add(message);
                    continue;
                }

                displayMessage(message.message, g);
                break;
            }

            for (Message message : removalList){
                messages.remove(message);
            }
        }
        else if (!isRunning()){
            displayMessage("Press a button to play", g);
        }
    }

    private void displayMessage(String message, Graphics g){
        g.setColor(Color.black);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 35));
        Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(message, g);
        int x = (int)((Game.width - stringBounds.getWidth())/2) - 6;
        int y = (int)((Game.height - stringBounds.getHeight())/2);
        g.drawString(message, x, y);
        g.setColor(Color.white);
        g.drawString(message, x-2, y-2);
    }

    private boolean collidesAt(int x, int y, Piece piece){
        return collidesAt(x, y, piece.getPoints(), piece.color);
    }

    private boolean collidesAt(int x, int y, Point[] points, Color color){
        for (Point p : points){
            Color cellColor = well[p.x + x][p.y + y];
            if (cellColor == color || cellColor == Game.wellColor){
                return true;
            }
        }
        return false;
    }

    public void dropDownTick(){
        dropDownPlayer(player1);
        dropDownPlayer(player2);
    }

    public void clearRows(boolean player1, Piece piece) {
        boolean gap;

        int min = Game.rows + 1, max = -1;
        for (Point p : piece.getPoints()){
            int y = p.y + piece.location.y;
            if (y < min)
                min = y;
            if (y > max)
                max = y;
        }

        for (int j = max; j >= min; j--) {
            gap = false;
            for (int i = 1; i < 11; i++) {
                if (well[i][j] == (player1 ? Game.player1Color : Game.player2Color)) {
                    gap = true;
                    break;
                }
            }
            if (!gap) {
                deleteRow(j, player1 ? Direction.Down : Direction.Up);
                j += 1;
            }
        }
    }

    public void deleteRow(int row, Direction direction) {
        if (direction == Direction.Down)
            for (int j = row - 1; j > 0; j--)
                for (int i = 1; i < Game.columns; i++)
                    well[i][j + 1] = well[i][j];
        else
            for (int j = 1; j < Game.rows; j++)
                for (int i = 1; i < Game.columns; i++)
                    well[i][j - 1] = well[i][j];
    }

    public boolean isRunning(){
        return running;
    }

    private void initializeWell(){
        well = new Color[Game.columns][Game.height];
        for (int i = 0; i < Game.columns; i++) {
            for (int j = 0; j < Game.rows; j++) {
                if (i == 0 || i == Game.columns - 1) {
                    well[i][j] = Game.wellColor;
                } else {
                    if (j <= Game.rows/2 - 1)
                        well[i][j] = Game.player1Color;
                    else
                        well[i][j] = Game.player2Color;
                }
            }
        }
    }

    public void newPlayerPiece(Player player){
        if (player == player1) {
            player.setPiece(new Piece(Game.columns / 2, 0,
                    PieceType.valueOf(random.nextInt(6))));
        }
        else {
            player.setPiece(new Piece(Game.columns / 2, Game.rows,
                    PieceType.valueOf(random.nextInt(6))));
            player.getPiece().setRotation(2);

            int max = 0;
            for (Point point : player.getPiece().getPoints())
                if (point.y > max)
                    max = point.y;

            max++;
            player.getPiece().addY(-max);
        }

        if (collidesAt(player.getPiece().location.x, player.getPiece().location.y, player.getPiece()))
            gameover(player != player1);
    }

    public void dropDownPlayer(Player player){
        if (player == null)
            return;

        int amount = player.direction == Direction.Down ? 1 : -1;
        int farthestY = player.getPiece().getFarthestY();
        if ((player.direction == Direction.Down && farthestY + 1 >= Game.rows) ||
                (player.direction == Direction.Up && farthestY - 1 < 0)){
            newPlayerPiece(player);
        }
        else if (!collidesAt(player.getPiece().location.x, player.getPiece().location.y + amount, player.getPiece())) {
            player.getPiece().addY(amount);
        }
        else
            fixToWellPlayer(player);
    }

    public void fixToWellPlayer(Player player){
        if (player == null)
            return;

        for (Point p : player.getPiece().getPoints()) {
            well[player.getPiece().location.x + p.x][player.getPiece().location.y + p.y] = player.getPiece().color;
        }
        clearRows(player == player1, player.getPiece());
        newPlayerPiece(player);
    }

    public void rotatePlayer(Player player, int i){
        if (player == null || player.getPiece() == null)
            return;

        int newRotation = (player.getPiece().getRotation() + i) % 4;
        if (newRotation < 0) {
            newRotation = 3;
        }
        if (!collidesAt(player.getPiece().location.x, player.getPiece().location.y,
                player.getPiece().getPoints(newRotation), player.getPiece().color)) {
            player.getPiece().setRotation(newRotation);
        }
    }

    public void movePlayer(Player player, Direction direction){
        if (player == null)
            return;

        if (direction == Left || direction == Right){
            int amount = direction == Left ? -1 : 1;

            if (!collidesAt(player.getPiece().location.x + amount, player.getPiece().location.y,
                    player.getPiece().getPoints(), player.getPiece().color)) {
                player.getPiece().addX(amount);
            }
        }
    }

    public void stopPlayer(Player player, Direction direction){
        if (player == null)
            return;

        switch (direction){
            case Left:
                break;
            case Right:
                break;
            case Down:
                player.getPiece().stopMovingDown();
                break;
        }
    }

    @Override
    public void handleEvent(KeyboardEvent event){
        if (!addingMode())
            return;

        if (addingPlayer1) {
            addingPlayer1 = false;
            addingPlayer2 = true;
            return;
        }
        if (addingPlayer2)
            addingPlayer2 = false;
    }

    @Override
    public void handleEvent(ControllerEvent event) {
        if (addingMode()){
            if (addingPlayer1) {
                player1.controller = event.player.controller;
                addingPlayer1 = false;
                addingPlayer2 = true;
                return;
            }

            if (addingPlayer2) {
                player2.controller = event.player.controller;
                addingPlayer2 = false;
            }
            return;
        }

        if (!isRunning()){
            if (TimeUnit.MILLISECONDS.toSeconds(new Date().getTime() - gameoverTime.getTime()) > 5 &&
                    !event.isAnalog)
                start();
            return;
        }

        switch (event.name){
            case "Y Axis":      // Left Joystick Up or Down
                if (event.player.direction == Direction.Down && event.value > 0)
                    dropDownPlayer(event.player);
                else if (event.player.direction == Direction.Up && event.value < 0)
                    dropDownPlayer(event.player);
                break;
            case "X Axis":      // Left Joystick Left or Right
                movePlayer(event.player, event.value > 0 ? Direction.Right : Direction.Left);
                break;
            case "Button 0":    // A
                rotatePlayer(event.player,1);
                break;
            case "Button 1":    // B
                rotatePlayer(event.player,-1);
                break;
            case "Button 2":    // X
                rotatePlayer(event.player,-1);
                break;
            case "Button 3":    // Y
                break;
            case "Z Axis":      // Triggers - Left 0 to 1 : Right 0 to -1
                dropDownPlayer(event.player);
                break;
            case "Hat Switch": // D-Pad
                if (event.value == 0.25f)      // Up
                    rotatePlayer(event.player, 1);
                else if (event.value == 0.75f) // Down
                    dropDownPlayer(event.player);
                else if (event.value == 0.50f) // Right
                    movePlayer(event.player, Direction.Right);
                else if (event.value == 1.00f) // Left
                    movePlayer(event.player, Direction.Left);
                break;
        }
    }
}