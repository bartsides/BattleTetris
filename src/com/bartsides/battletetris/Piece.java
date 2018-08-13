package com.bartsides.battletetris;

import java.awt.*;

public class Piece {
    public static final int blockWidth = 36;

    private boolean movingDown = false;
    public PieceType pieceType;
    private Point[][] points;
    public Color color;
    public Point location;
    private int rotation = 0;
    public Direction direction;

    public Piece(int x, int y, PieceType pieceType){
        this.pieceType = pieceType;
        init();
        location = new Point(x, y);
    }

    public void beginMovingDown() {
        movingDown = true;
    }

    public boolean isMovingDown(){
        return movingDown;
    }

    public void stopMovingDown() {
        movingDown = false;
    }

    public void addX(int amount){
        this.location.x += amount;
    }

    public void addY(int amount){
        this.location.y += amount;
    }

    public void render(Graphics g) {
        g.setColor(color);
        for (Point p : getPoints()){
            g.fillRect((location.x + p.x) * blockWidth, (location.y + p.y) * blockWidth,
                    blockWidth-1, blockWidth-1);
        }
    }

    public void setRotation(int i){
        rotation = i;
    }

    public int getRotation(){
        return rotation;
    }

    public Point[] getPoints(){
        return points[rotation];
    }

    public Point[] getPoints(int i){
        return points[i];
    }

    public int getFarthestY(){
        Point[] points = getPoints();
        int y = points[0].y;
        for (Point point : points){
            if (direction == Direction.Down && point.y > y)
                y = point.y;

            if (direction == Direction.Up && point.y < y)
                y = point.y;
        }

        return y + location.y;
    }

    private void init(){
        switch (pieceType){
            case I:
                //color = Color.cyan;
                points = new Point[][]{
                        { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
                        { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) },
                        { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
                        { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) }
                };
                break;
            case O:
                //color = Color.yellow;
                points = new Point[][]{
                        { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
                        { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
                        { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
                        { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) }
                };
                break;
            case T:
                //color = Color.pink;
                points = new Point[][]{
                        { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1) },
                        { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
                        { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2) },
                        { new Point(1, 0), new Point(1, 1), new Point(2, 1), new Point(1, 2) }
                };
                break;
            case S:
                //color = Color.green;
                points = new Point[][]{
                        { new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
                        { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
                        { new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
                        { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) }
                };
                break;
            case Z:
                //color = Color.red;
                points = new Point[][]{
                        { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
                        { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) },
                        { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
                        { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) }
                };
                break;
            case J:
                //color = Color.blue;
                points = new Point[][]{
                        { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0) },
                        { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2) },
                        { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2) },
                        { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0) }
                };
                break;
            case L:
                //color = Color.orange;
                points = new Point[][]{
                        { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2) },
                        { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2) },
                        { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0) },
                        { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 0) }
                };
                break;
            default:
                points = new Point[4][];
        }
    }
}
