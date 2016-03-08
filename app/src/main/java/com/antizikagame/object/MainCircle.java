package com.antizikagame.object;

import android.graphics.Color;

import com.antizikagame.control.GameManager;

/**
 * Created by Pavel on 04.01.2016.
 */
public class MainCircle extends SimpleCircle {

    public static final int RADIUS     = 50;
    public static final int MAIN_SPEED = 30;
    public static final int COLOR      = Color.BLUE;

    public MainCircle(int x, int y) {
        super(x, y, RADIUS);
        setColor(COLOR);
    }

    public void moveMainCircleWhenTouchAt(int x1, int y1) {
        int dx = (x1 - x) * MAIN_SPEED / GameManager.getWidth();
        int dy = (y1 - y) * MAIN_SPEED / GameManager.getHeight();
        x += dx;
        y += dy;
    }

    public void initRadius() {
        radius = RADIUS;
    }

    public void growRadius(SimpleCircle simpleCircle) {
        radius = (int) Math.sqrt(Math.pow(radius, 2) + Math.pow(simpleCircle.radius, 2));
    }
}
