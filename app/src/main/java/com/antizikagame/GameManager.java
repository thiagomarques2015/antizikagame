package com.antizikagame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.antizikagame.object.Enemy;
import com.antizikagame.object.EnemyCircle;
import com.antizikagame.object.MainCircle;
import com.antizikagame.object.Racket;
import com.antizikagame.object.SimpleCircle;
import com.antizikagame.object.Sprite;
import com.antizikagame.view.CanvasView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Pavel on 04.01.2016.
 */
public class GameManager implements IGameLoop {

    public static final int MAX_ENEMY_CIRCLE = 10;
    private static final int TOTAL_ENEMIES = 10;


    private static final int TIME_LEVEL_1 = 15;

    private CanvasView canvasView;
    private static int width;
    private static int height;

    private MainCircle mainCircle;
    private ArrayList<EnemyCircle> enemyCircles;
    private GameLoop gameLoopThread;
    private List<Sprite> mSprites;
    private List<Enemy> enemyMosquitos;
    private Racket racket;
    private ClockManager clock;
    private SimpleDateFormat clockFormat;

    public GameManager(CanvasView canvasView, int width, int height) {
        this.canvasView = canvasView;
        mSprites = new ArrayList<>();
        enemyCircles = new ArrayList<>();
        enemyMosquitos = new ArrayList<>();
        this.width  = width;
        this.height = height;
        //initMainCircle();
        initRacket();
        initTexts();
        //initEnemyCircles();
        initEnemies();
        initMainLoop();
    }

    private void initTexts() {
        // Panel do Tempo
        Calendar now = Calendar.getInstance();
        // Formato do relogio
        clockFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
        // Cria o relogio
        clock = new ClockManager().timeInitial(now.getTimeInMillis()).maxTime(Calendar.SECOND, TIME_LEVEL_1);
        Log.d("Clock", getClock());
    }

    private void initMainLoop() {
        gameLoopThread = new GameLoop(this, canvasView);
        gameLoopThread.setRunning(true);
        gameLoopThread.start();
    }

    public String getClock(){
        return clockFormat.format(clock.getDif().getTime());
    }

    private void initRacket() {
        Resources res = canvasView.getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, R.mipmap.raquete_sprite);
        int rows = 1;
        int cols = 4;
        racket = new Racket(GameManager.getWidth()/2 - bmp.getWidth()/2, height - bmp.getHeight() * 2, bmp, rows , cols);
        mSprites.add(racket);
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
            enemyMosquitos.add(new Enemy(random.nextInt(limitEnemyX), random.nextInt(limitEnemyY), random, bitmapEnemy, rows, cols));

        mSprites.addAll(enemyMosquitos);
    }

    private void initEnemyCircles() {
        SimpleCircle mainArea = mainCircle.getCircleArea();
        for (int i = 0; i< MAX_ENEMY_CIRCLE; i++) {
            EnemyCircle ec;
            do {
                ec = EnemyCircle.getRandomCircle();
            } while (ec.isIntercept(mainArea));
            ec = EnemyCircle.getRandomCircle();
            enemyCircles.add(ec);
        }
        calculateAndSetColor();
    }

    private void calculateAndSetColor() {
        for (EnemyCircle ec : enemyCircles) {
            ec.setEnemyOrFood(mainCircle);
        }
    }

    @Override
    public void update() {
        checkCollision();
        updateSprites();
        moveCircles();
        canvasView.redraw();
    }

    public void onDraw() {
        //canvasView.drawCircle(mainCircle);
        for (EnemyCircle ec : enemyCircles) {
            canvasView.drawCircle(ec);
        }
        for(Sprite s : mSprites){
            canvasView.drawSprite(s);
        }
    }

    private void updateSprites() {
        for(Sprite s : mSprites)
            s.update();
    }

    public void onTouchEvent(int x, int y) {
        //mainCircle.moveMainCircleWhenTouchAt(x, y);
        racket.move(x, y);
        //moveEnemies(); // Acelera todos
    }

    private void checkCollision(){

        for (Enemy e : enemyMosquitos) {
            if(racket.checkForCollision(e)){
                e.kill();
                //Log.d("Collision", "Um mosquito foi atingido pela raquete");
            }
        }
    }

    private void checkCollisionCircles() {
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
