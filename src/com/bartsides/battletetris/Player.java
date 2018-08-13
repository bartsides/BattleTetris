package com.bartsides.battletetris;

import java.awt.*;
import net.java.games.input.Controller;

public class Player {
    public Direction direction;
    public Controller controller = null;
    private Piece piece = null;
    public Color color;

    public Player(Direction direction, Controller controller){
        this.direction = direction;
        this.controller = controller;
        color = this.direction == Direction.Up ? Game.player1Color : Game.player2Color;
    }

    public Player(Direction direction){
        this(direction, null);
    }

    public void setPiece(Piece piece){
        this.piece = piece;
        this.piece.direction = direction;
        this.piece.color = color;
    }

    public Piece getPiece(){
        return piece;
    }
}
