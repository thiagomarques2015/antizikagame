package com.antizikagame.object;

import android.graphics.Color;

import com.antizikagame.control.GameManager;

import java.util.Random;

/**
 * Created by Pavel on 05.01.2016.
 */
public class EnemyCircle extends SimpleCircle {

    public static final int FROM_RADIUS  = 50;
    public static final int TO_RADIUS    = 10;
    public static final int RANDOM_SPEED = 3;
    public static final int MAX_RANDOM_SPEED = RANDOM_SPEED * 2;
    public static final int ENEMY_COLOR  = Color.RED;
    public static final int FOOD_COLOR   = Color.GREEN;

    private int dx;
    private int dy;


    public EnemyCircle(int x, int y, int radius, int dx, int dy) {
        super(x, y, radius);
        this.dx = dx;
        this.dy = dy;
    }

    public static EnemyCircle getRandomCircle() {
        Random random = new Random();
        int x = random.nextInt(GameManager.getWidth());
        int y = random.nextInt(GameManager.getHeight());
        int radius = TO_RADIUS + random.nextInt(FROM_RADIUS - TO_RADIUS);
        int dx = random.nextInt(RANDOM_SPEED) -  MAX_RANDOM_SPEED;
        int dy = random.nextInt(RANDOM_SPEED) - MAX_RANDOM_SPEED;
        EnemyCircle enemyCircle = new EnemyCircle(x, y, radius, dx, dy);
        enemyCircle.setColor(ENEMY_COLOR);
        return enemyCircle;
    }

    public void moveOnStep() {
        x += dx;
        y += dy;
        checkBound();
    }

    private void checkBound() {
        if (x > GameManager.getWidth() || x < 0) {
            dx = -dx;
        }

        if (y > GameManager.getHeight() || y < 0) {
            dy = -dy;
        }
    }

    public void setEnemyOrFood(MainCircle mainCircle) {
        if (isSmallerThan(mainCircle)) {
            setColor(FOOD_COLOR);
        }
    }

    public boolean isSmallerThan(MainCircle mainCircle) {
        if (radius < mainCircle.radius) {
            return true;
        }
        return false;
    }

}
