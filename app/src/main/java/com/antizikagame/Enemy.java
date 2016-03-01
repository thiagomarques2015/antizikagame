package com.antizikagame;

import android.graphics.Bitmap;

import java.util.Random;

/**
 * Inimigo
 * Created by Thiago on 26/02/2016.
 */
public class Enemy extends Sprite {

    private static final int MOVING_LEFT = 1;
    private static final int MOVING_RIGHT = 2;
    private static final int DYING = 3;


    private int frame;
    private int firstFrame;
    private int speedX;
    private int speedY;
    private final int CONT = 4;


    public Enemy(int x, int y, Random random, Bitmap bmp, int bmp_rows, int bmp_columns) {
        super(bmp, bmp_rows, bmp_columns);
        speedX = random.nextInt(7) - 3; //Velocidade horizontal de -3 a 3.
        speedY = random.nextInt(7) - 3; //Velocidade vertical de -3 a 3.
        this.x = x;
        this.y = y;

        checkState();
        setAnimation(frame, firstFrame, firstFrame + CONT, ANIM_GO); // Seta a animação apenas como "ida" (ciclíca).
    }

    @Override
    public void update() {
        super.update();
        this.x += speedX;
        this.y += speedY;

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

        if(isLeft())
            state(MOVING_LEFT);
        else
            state(MOVING_RIGHT);
    }

    private boolean isLeft(){
        return speedX < 0;
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
}
