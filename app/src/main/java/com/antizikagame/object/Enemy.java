package com.antizikagame.object;

import android.graphics.Bitmap;
import android.graphics.Paint;

import com.antizikagame.GameManager;

import java.util.Calendar;
import java.util.Random;

/**
 * Inimigo
 * Created by Thiago on 26/02/2016.
 */
public class Enemy extends Sprite {

    private static final int MOVING_LEFT = 1;
    private static final int MOVING_RIGHT = 2;
    private static final int DYING = 3;

    private static final int MAX_SPEED = 7;
    private static final int RANGE_SPEED = MAX_SPEED / 2 - 1; // Range da velocidade
    private static final int GRAVITY = 10;

    private int frame;
    private int firstFrame;
    private int speedX;
    private int speedY;
    private final int CONT = 4;
    private boolean dying;
    private boolean dead;
    private int timeStarted;


    public Enemy(int x, int y, Random random, Bitmap bmp, int bmp_rows, int bmp_columns) {
        super(bmp, bmp_rows, bmp_columns);

        this.x = x;
        this.y = y;

        speedX = (random.nextInt(MAX_SPEED) - RANGE_SPEED) + 1; //Velocidade horizontal de -3 a 3.
        speedY = (random.nextInt(MAX_SPEED) - RANGE_SPEED) + 1; //Velocidade vertical de -3 a 3.

        checkState();
        setAnimation(frame, firstFrame, firstFrame + CONT, ANIM_GO); // Seta a animação apenas como "ida" (ciclíca).
    }

    @Override
    public void update() {
        super.update();

        if(isDead()){
            incremmentSpeedFreeFall();
            return;
        }

        this.y += speedY;
        this.x += speedX;

        if(x < 0 || x > GameManager.getWidth()-width){
            speedX *= -1;
            changeDirection();
        }else if(y < 0 || y > GameManager.getHeight()-height){
            speedY *= -1;
        }
    }

    private void changeDirection(){
        checkState();
        setAnimation(frame, firstFrame, firstFrame + CONT, ANIM_GOBACK); // Seta a animação apenas como "ida" (ciclíca).
    }

    private void checkState(){

        if(dying){
            state(DYING);
            return;
        }

        if(isLeft())
            state(MOVING_LEFT);
        else
            state(MOVING_RIGHT);
    }

    private boolean isLeft(){
        return speedX < 0;
    }

    public void kill(){
        if(isDead()) return;
        // Mata o inimigo
        dying = true;
        timeStarted = getTime();
        // Inicia em 0 a velocidade em queda livre
        speedY = 0;
        // Muda a direcao dele para baixo
        changeDirection();
        mPaint = new Paint();
    }

    /**
     * Esquerda
     *  firstFrame = 0
     *  frame = 2
     *
     *  Direita
     *    firstFrame = 5
     *    frame = 8
     *
     *  Morreu
     *      firstFrame = 9
     *      frame = 12
     */
    private void state(int status){

        switch (status){
            case MOVING_LEFT :
                firstFrame = 0;
                frame = 2;
                break;
            case MOVING_RIGHT :
                firstFrame = 4;
                frame = 7;
                break;
            case DYING :
                firstFrame = 9;
                frame = 12;
                break;
        }


    }

    private int getTime(){
        return Calendar.getInstance().get(Calendar.SECOND);
    }

    /**
     * Incrementa a velocidade
     */
    private void incremmentSpeedFreeFall(){
        int time = getTime() - timeStarted;
        if(!dead && time < 1){
            return;
        }else{
            dead = true;
            dying = false;
            timeStarted = getTime();
            time = getTime() - timeStarted;
        }
        speedY = speedY + GRAVITY  + time;

        //Log.d("Gravity", String.format("Y = %s, T = %s , V = %s" , y, time, speedY));

        this.y += speedY;
    }

    public boolean isDead() {
        return dying || dead;
    }
}
