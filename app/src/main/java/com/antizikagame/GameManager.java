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

    private static final int TIME_LEVEL = 5;

    private CanvasView canvasView;
    private static int width;
    private static int height;

    private MainCircle mainCircle;
    private ArrayList<EnemyCircle> enemyCircles;
    private GameLoop gameLoopThread;
    private List<Sprite> mSprites;
    private List<Enemy> enimies;
    private List<Enemy> deadEnemies;
    private Racket racket;
    private ClockManager clock;
    private SimpleDateFormat clockFormat;
    private int level;
    private int score;
    private long startTime; // O tempo que o level foi iniciado
    private Sprite nextLevelSprite; // NextLevel
    private int aliveEnemy;
    private long timeOver;

    public GameManager(CanvasView canvasView, int width, int height) {
        this.canvasView = canvasView;
        mSprites = new ArrayList<>();
        enemyCircles = new ArrayList<>();
        enimies = new ArrayList<>();
        deadEnemies = new ArrayList<>();
        this.width  = width;
        this.height = height;
        //initMainCircle();
        initNextLevel();
        newStage();
        initRacket();
        initTexts();
        //initEnemyCircles();
        initEnemies();
        initMainLoop();
    }

    private void initMainLoop() {
        gameLoopThread = new GameLoop(this, canvasView);
        gameLoopThread.setRunning(true);
        gameLoopThread.start();
    }

    private void newStage() {
        nextLevelSprite.visible = false; //Deixa a mensagem e imagem de proximo level invisiveis
        startTime = System.currentTimeMillis(); //Define a quantidade de Pinks de acordo com o level
        aliveEnemy = TOTAL_ENEMIES+level; //Define a quantidade de mosquitos de acordo com o level
        level = (int) Math.max(TIME_LEVEL * 3 - (System.currentTimeMillis() - startTime)/500, 1);
        Log.d("Level", "" + level);
    }

    private void initNextLevel() {
        Resources res = canvasView.getResources();
        mSprites.add(nextLevelSprite = new Sprite(BitmapFactory.decodeResource(res, R.mipmap.next_level)));
        nextLevelSprite.x = getWidth()/2 - nextLevelSprite.width/2;
        nextLevelSprite.y = (int) (getHeight()*0.2f);
        nextLevelSprite.visible = false;
    }

    private void initTexts() {
        // Panel do Tempo
        Calendar now = Calendar.getInstance();
        // Formato do relogio
        clockFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
        // Cria o relogio
        clock = new ClockManager().timeInitial(now.getTimeInMillis()).maxTime(Calendar.SECOND, level);
        Log.d("Clock", getClock());
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

    private void initEnemies() {
        Random random = new Random();
        Resources res = canvasView.getResources();
        Bitmap bitmapEnemy = BitmapFactory.decodeResource(res, R.mipmap.sprite);
        int rows = 3;
        int cols = 4;
        int limitEnemyX = getWidth()-bitmapEnemy.getWidth()/cols,
            limitEnemyY = getHeight()-bitmapEnemy.getHeight()/rows;

        for(int i=0; i<aliveEnemy; i++)
            enimies.add(new Enemy(random.nextInt(limitEnemyX), random.nextInt(limitEnemyY), random, bitmapEnemy, rows, cols));

        mSprites.addAll(enimies);
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
        checkEnimies();
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

    public synchronized void onTouchEvent(int x, int y) {
        //mainCircle.moveMainCircleWhenTouchAt(x, y);
        if(aliveEnemy > 0)
            racket.move(x, y);
        else{
            //Log.d("Gameover", "Nao existe mais inimigos");
            Calendar dif = Calendar.getInstance();
            dif.setTimeInMillis(System.currentTimeMillis() - timeOver);
            int sec = dif.get(Calendar.SECOND);
            // Se passar um 1 segundo depois do gameover fecha
            if(sec > 1)
                // Inicia o novo nivel
                newStage();
        }
        //moveEnemies(); // Acelera todos
    }

    private synchronized void checkEnimies(){

        if(aliveEnemy <= 0){
            //Log.d("Gameover", "Nao existe mais mosquitos vivos");
            return;
        }

        for (Enemy e : enimies) {
            checkCollision(e);
            checkIfDead(e);
        }

        if(deadEnemies.size() > 0){
            // Remove os inimigos da lista
            enimies.removeAll(deadEnemies);
            // Remove os inimigos da lista de sprites
            mSprites.removeAll(deadEnemies);
            // Limpa a lista de inimigos mortos
            deadEnemies.clear();
        }
    }

    private void checkIfDead(Enemy e) {
        if(e.isAfterDead())
            deadEnemies.add(e);
    }

    private void checkCollision(Enemy e){
        if(e.isDead()) return;
        if(racket.checkForCollision(e)){
            e.kill();
            int add;
            score += add = (int) Math.max(100 - level*3 - (System.currentTimeMillis() - startTime)/500, 1);
            //Log.d("Collision", "Um mosquito foi atingido pela raquete");
            //Log.d("Score", "Valeu : " + add);

            aliveEnemy--; // Remove um mosquito da lista

            if(aliveEnemy <= 0){
                nextLevelSprite.visible = true; // Exibe imagem de proximo nível
                timeOver = System.currentTimeMillis(); // Tempo que o level terminou
            }
        }
    }

    public boolean isNextLevel(){
        return nextLevelSprite.visible;
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

    public Integer getScore() {
        return score;
    }

    public static int getWidth()  { return width; }

    public static int getHeight() { return height; }
}
