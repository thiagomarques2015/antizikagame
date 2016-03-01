package com.antizikagame.object;

/**
 * Created by Pavel on 05.01.2016.
 */
public class SimpleCircle{

    public int x;
    public int y;
    protected int radius;
    private   int color;

    public SimpleCircle(int x, int y, int radius) {
        this.x      = x;
        this.y      = y;
        this.radius = radius;
    }

    public boolean isIntercept(SimpleCircle mainArea) {
        return radius + mainArea.radius >= Math.sqrt(Math.pow(x - mainArea.x, 2) + Math.pow(y - mainArea.y, 2));
    }

    public SimpleCircle getCircleArea() {
        return new SimpleCircle(x, y, radius * 3);
    }

    public int getX()      { return x; }
    public int getY()      { return y; }
    public int getRadius() { return radius; }
    public int getColor()  { return color; }

    public void setColor(int color) { this.color = color; }
}
