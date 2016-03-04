package com.antizikagame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.antizikagame.object.Enemy;
import com.antizikagame.object.EnemyCircle;
import com.antizikagame.object.Racket;
import com.antizikagame.object.Sprite;
import com.antizikagame.view.CanvasView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by Pavel on 04.01.2016.
 */
public class GameManager implements IGameLoop {

    public static final int MAX_ENEMY_CIRCLE = 10;
    private static final int TOTAL_ENEMIES = 10;

    private static final int TIME_LEVEL = 5;
    private static final int MAX_SPEED_ENEMY = 7;
    private final Random random;
    private final Resources res;

    private CanvasView canvasView;
    private static int width;
    private static int height;

    private ArrayList<EnemyCircle> enemyCircles;
    private GameLoop gameLoopThread;
    private List<Sprite> mSprites;
    private List<Enemy> enimies;
    private List<Enemy> deadEnemies;
    private Racket racket;
    private ClockManager clock;
    private int level = 0;
    private int score = 0;
    private long startTime; // O tempo que o level foi iniciado
    private Sprite nextLevelSprite; // NextLevel
    private int aliveEnemy;
    private long timeOver;
    private int timeLevel; // Tempo estimado para o fim do nivel

    // Inimigos
    int rowsEnemy = 3;
    int colsEnemy = 4;
    int limitEnemyX, limitEnemyY;
    Bitmap bitmapEnemy;

    public GameManager(CanvasView canvasView, int width, int height) {
        this.canvasView = canvasView;
        mSprites = new ArrayList<>();
        enemyCircles = new ArrayList<>();
        enimies = new ArrayList<>();
        deadEnemies = new ArrayList<>();

        random = new Random();
        res = canvasView.getResources();

        this.width  = width;
        this.height = height;
        //initMainCircle();
        initNextLevel();
        newStage();
        initRacket();
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
        level++; // Avanca um nivel
        nextLevelSprite.visible = false; //Deixa a mensagem e imagem de proximo level invisiveis
        startTime = System.currentTimeMillis(); //Define a quantidade de Pinks de acordo com o level
        aliveEnemy = TOTAL_ENEMIES+level; //Define a quantidade de mosquitos de acordo com o level
        timeLevel = (int) Math.max(TIME_LEVEL * 3 - (System.currentTimeMillis() - startTime)/500, 1);
        int dead = deadEnemies.size()-1;
        int newEnimies = (dead > 0)? aliveEnemy - dead : 0;
        Log.d("Enemy", aliveEnemy + " vivos ");
        Log.d("Enemy", newEnimies + " novos");
        if(deadEnemies.size() > 0){
            for(Enemy e : deadEnemies){
                e.create(random.nextInt(limitEnemyX), random.nextInt(limitEnemyY), MAX_SPEED_ENEMY + level, random);
                mSprites.add(e);
                enimies.add(e);
            }

            deadEnemies.clear();
        }
        // Adiciona os novos inimigos
        for(int i=0; i<newEnimies; i++){
            Enemy e = new Enemy(random.nextInt(limitEnemyX), random.nextInt(limitEnemyY), MAX_SPEED_ENEMY + level, random, bitmapEnemy, rowsEnemy, colsEnemy);
            enimies.add(e);
            mSprites.add(e);
        }

        initClock();

        Log.d("Level", "" + level);
        Log.d("Time Level", "" + timeLevel);
    }

    private void initNextLevel() {
        mSprites.add(nextLevelSprite = new Sprite(BitmapFactory.decodeResource(res, R.mipmap.next_level)));
        nextLevelSprite.x = getWidth()/2 - nextLevelSprite.width/2;
        nextLevelSprite.y = (int) (getHeight()*0.2f);
        nextLevelSprite.visible = false;
    }

    private void initClock() {
        // Panel do Tempo
        Calendar now = Calendar.getInstance();
        // Cria o relogio
        if(clock == null){
            clock = new ClockManager();
        }

        clock.timeInitial(now.getTimeInMillis()).maxTime(Calendar.SECOND, timeLevel);

        Log.d("Clock", getClock());
    }

    public String getClock(){
        return clock.getTime();
    }

    private void initRacket() {
        Bitmap bmp = BitmapFactory.decodeResource(res, R.mipmap.raquete_sprite);
        int rows = 1;
        int cols = 4;
        racket = new Racket(GameManager.getWidth()/2 - bmp.getWidth() /2, height - bmp.getHeight() * 2, bmp, rows , cols);
        mSprites.add(racket);
    }

    private void initEnemies() {
        bitmapEnemy = BitmapFactory.decodeResource(res, R.mipmap.sprite);
        limitEnemyX = getWidth()-bitmapEnemy.getWidth()/colsEnemy;
        limitEnemyY = getHeight()- bitmapEnemy.getHeight() / rowsEnemy;

        for(int i=0; i<aliveEnemy; i++)
            enimies.add(new Enemy(random.nextInt(limitEnemyX), random.nextInt(limitEnemyY), MAX_SPEED_ENEMY + level, random, bitmapEnemy, rowsEnemy, colsEnemy));

        mSprites.addAll(enimies);
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
        if(aliveEnemy > 0){
           /* Log.d("Enemy", aliveEnemy + " vivo");
            Log.d("Enemy", deadEnemies.size() + " mortos");*/
            racket.move(x, y);
        }else{
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
        }
    }

    private void checkIfDead(Enemy e) {
        if(e.isAfterDead()){
            Log.d("Enemy", "+1 Matou um inimigo");
            deadEnemies.add(e);
        }
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
                Log.d("Gameover", "Fim do nivel");
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
