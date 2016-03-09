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
    private float frameTime = 3.666f;

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

        int time = getTime() - timeStarted;
        speedY = speedY + GRAVITY  + time;
        this.y += speedY;

        if(y > finalY){
            this.y = finalY;
        }

        float xAcceleration = GameManager.getxAcceleration();

        //Calculate new speed
        float xVelocity = (xAcceleration * frameTime);
//        yVelocity += (yAcceleration * frameTime);

        //Calc distance travelled in that time
        float xS = (xVelocity/2)*frameTime;
//        float yS = (yVelocity/2)*frameTime;
        this.x += xS;

        if(x < 0){
            x = 0;
        }

        if(x > GameManager.getWidth()-width){
            x = GameManager.getWidth()-width;
        }
    }
}
