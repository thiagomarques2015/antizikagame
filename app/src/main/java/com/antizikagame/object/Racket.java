package com.antizikagame.object;

import android.graphics.Bitmap;

import com.antizikagame.control.GameManager;

/**
 * Created by Thiago on 01/03/2016.
 */
public class Racket extends Sprite {
    private static final int MAIN_SPEED = 50;

    public Racket(int x, int y, Bitmap bmp, int bmp_rows, int bmp_columns) {
        super(bmp, bmp_rows, bmp_columns);
        setAnimation(ANIM_GOBACK); // Seta a animação apenas como "ida" (ciclíca).
        this.x = x;
        this.y = y;
    }

    public void move(int x1, int y1) {
        int dx = (x1 - x) * MAIN_SPEED / GameManager.getWidth();
        int dy = (y1 - y) * MAIN_SPEED / GameManager.getHeight();
        x += dx;
        y += dy;
    }
}
