package com.antizikagame.object;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.antizikagame.control.GameManager;
import com.antizikagame.view.CanvasView;

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
    private boolean done;
    private int wonX;
    private int wonY;
    private boolean hit;
    public int score;
    private boolean doing;

    public Pneu(int x, int y, int finalY, Bitmap bmp, int bmp_rows, int bmp_columns) {
        super(bmp, bmp_rows, bmp_columns);
        this.finalY = finalY;
        wonY = finalY;
        create(x, y);
        setAnimation(0, 0, 1, ANIM_STOP);
    }

    public Pneu create(int x, int y){
        this.x = x;
        this.y = y;
        timeStarted = getTime();
        doing = true;
        done = false;
        return this;
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

        if(x < -width){
            done = true;
            hit = true;
            wonX = width;
            x = -width;
        }

        if(x > GameManager.getWidth()+width){
            done = true;
            hit = true;
            wonX = GameManager.getWidth() - width;
            x = GameManager.getWidth()+width;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(hit){
            hit = false;
            canvas.drawText("+" + score, wonX, wonY, CanvasView.getPaintHit());
        }
    }

    public boolean isDoing() {
        return doing;
    }

    public boolean isHit(){
        return hit;
    }

    public boolean isDone() {
        return done;
    }
}
