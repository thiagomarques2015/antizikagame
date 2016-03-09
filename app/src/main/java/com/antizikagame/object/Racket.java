package com.antizikagame.object;

import android.graphics.Bitmap;

/**
 * Created by Thiago on 01/03/2016.
 */
public class Racket extends Sprite {
    private static final int MAIN_SPEED = 50;

    public Racket(int x, int y, Bitmap bmp, int bmp_rows, int bmp_columns) {
        super(bmp, bmp_rows, bmp_columns);
        setAnimation(ANIM_GOBACK); // Seta a animação apenas como "ida" (ciclíca).
        this.height =
        this.x = x;
        this.y = y;
    }

    public void move(int x1, int y1) {
        int dx = (x1 - x) - width / 2;
        int dy = (y1 - y) - height / 2;
        x += dx;
        y += dy;
    }
}
