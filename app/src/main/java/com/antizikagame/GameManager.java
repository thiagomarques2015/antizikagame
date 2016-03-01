package com.antizikagame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Pavel on 04.01.2016.
 */
public class GameManager {

    public static final int MAX_ENEMY_CIRCLE = 10;
    private static final int TOTAL_ENEMIES = 10;

    private CanvasView canvasView;
    private static int width;
    private static int height;

    private MainCircle mainCircle;
    private ArrayList<EnemyCircle> enemyCircles;
    private GameLoop gameLoopThread;
    private List<Sprite> mSprites;

    public GameManager(CanvasView canvasView, int width, int height) {
        this.canvasView = canvasView;
        mSprites = new ArrayList<>();
        this.width  = width;
        this.height = height;
        initMainCircle();
        initEnemyCircles();
        initEnemies();
        initMainLoop();
    }

    private void initMainLoop() {
        gameLoopThread = new GameLoop(this, canvasView);
        gameLoopThread.setRunning(true);
        gameLoopThread.start();
    }

    private void initMainCircle() {
        mainCircle = new MainCircle(width/2, height/2);
    }

    private void initEnemies() {
        Random random = new Random();
        Resources res = canvasView.getResources();
        Bitmap bitmapEnemy = BitmapFactory.decodeResource(res, R.mipmap.sprite);
        int rows = 3;
        int cols = 4;
        int limitEnemyX = getWidth()-bitmapEnemy.getWidth()/cols,
            limitEnemyY = getHeight()-bitmapEnemy.getHeight()/rows;

        for(int i=0; i<TOTAL_ENEMIES; i++)
            mSprites.add(new Enemy(random.nextInt(limitEnemyX), random.nextInt(limitEnemyY), random, bitmapEnemy, rows, cols));
    }

    private void initEnemyCircles() {
        SimpleCircle mainArea = mainCircle.getCircleArea();
        enemyCircles = new ArrayList<>();
        for (int i = 0; i< MAX_ENEMY_CIRCLE; i++) {
            EnemyCircle ec;
            do {
                ec = EnemyCircle.getRandomCircle();
            } while (ec.isIntercept(mainArea));
            enemyCircles.add(ec);
        }
        calculateAndSetColor();
    }

    private void calculateAndSetColor() {
        for (EnemyCircle ec : enemyCircles) {
            ec.setEnemyOrFood(mainCircle);
        }
    }

    public void onDraw() {
        canvasView.drawCircle(mainCircle);
        for (EnemyCircle ec : enemyCircles) {
            canvasView.drawCircle(ec);
        }
        for(Sprite s : mSprites){
            canvasView.drawSprite(s);
        }
    }

    public void moveEnemies(){
        //checkCollision();
        updateSprites();
        moveCircles();
        canvasView.redraw();
    }

    private void updateSprites() {
        for(Sprite s : mSprites)
            s.update();
    }

    public void onTouchEvent(int x, int y) {
        mainCircle.moveMainCircleWhenTouchAt(x, y);
        moveEnemies();
    }

    private void checkCollision() {
        SimpleCircle circleForDel = null;
        for (EnemyCircle ec : enemyCircles) {
            if (mainCircle.isIntercept(ec)) {
                if (ec.isSmallerThan(mainCircle)) {
                    mainCircle.growRadius(ec);
                    circleForDel = ec;
                    calculateAndSetColor();
                    break;
                } else {
                    gameOver("Your Lose");
                    return;
                }
            }
        }
        if (circleForDel != null) {
            enemyCircles.remove(circleForDel);
        }
        if (enemyCircles.isEmpty()) {
            gameOver("Your Win");
        }
    }

    private void gameOver(String text) {
        gameLoopThread.setRunning(false);
        canvasView.showMessage(text);
        mainCircle.initRadius();
        initEnemyCircles();
        canvasView.redraw();

    }

    private void moveCircles() {
        for (EnemyCircle ec : enemyCircles) {
            ec.moveOnStep();
        }
    }

    public static int getWidth()  { return width; }
    public static int getHeight() { return height; }
}
