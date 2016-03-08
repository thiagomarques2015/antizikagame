package com.antizikagame.object;

import android.graphics.Bitmap;

import com.antizikagame.control.GameManager;

import java.util.Calendar;

/**
 * Created by Thiago on 08/03/2016.
 */
public class Pneu extends Sprite {
    private static final int GRAVITY = 0;
    private final int finalY;
    private int speedY;
    private int timeStarted;

    public Pneu(int x, int y, int finalY, Bitmap bmp, int bmp_rows, int bmp_columns) {
        super(bmp, bmp_rows, bmp_columns);

        this.x = x;
        this.y = y;
        this.finalY = finalY;
        setAnimation(0, 0, 1, ANIM_STOP);
        timeStarted = getTime();
    }

    private int getTime(){
        return Calendar.getInstance().get(Calendar.SECOND);
    }

    @Override
    public void update() {
        super.update();

        this.x += GameManager.getDeltaX();

        if(y > finalY){
            return;
        }

        int time = getTime() - timeStarted;

        speedY = speedY + GRAVITY  + time;

        this.y += speedY;
    }
}
