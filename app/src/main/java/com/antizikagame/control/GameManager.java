package com.antizikagame.control;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.antizikagame.R;
import com.antizikagame.object.Enemy;
import com.antizikagame.object.EnemyCircle;
import com.antizikagame.object.Pneu;
import com.antizikagame.object.Racket;
import com.antizikagame.object.Sprite;
import com.antizikagame.view.CanvasView;
import com.antizikagame.view.GameOverActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by Pavel on 04.01.2016.
 */
public class GameManager implements IGameLoop {

    private static final int TOTAL_ENEMIES = 10;

    private static final int TIME_LEVEL = 5;
    private static final int MAX_SPEED_ENEMY = 7;
    private final Random random;
    private final Resources res;
    private final Context context;

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
    private int aliveEnemy; // Total de inimigos vivos
    private int deadEnemy; // Total de inimigos mortos
    private long timeOver; // Quando o tempo do level esgotou
    private int timeLevel; // Tempo estimado para o fim do nivel

    // Inimigos
    int rowsEnemy = 3;
    int colsEnemy = 4;
    int limitEnemyX, limitEnemyY;
    Bitmap bitmapEnemy;
    private int mActionBarSize;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;


    // Sensor
    private long lastUpdate;
    private float lastX;
    private static float deltaX;

    public GameManager(CanvasView canvasView, int width, int height) {
        this.canvasView = canvasView;
        this.context = canvasView.getContext();
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
        initPneu();
        initMainLoop();
        initSensor();
    }

    private void initSensor() {
        senSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(onSensorListener, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public static float getDeltaX() {
        return deltaX;
    }

    private SensorEventListener onSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Sensor mySensor = sensorEvent.sensor;

            if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                // get the change of the x,y,z values of the accelerometer
                deltaX = Math.abs(lastX - x);

                lastX = x;

                long curTime = System.currentTimeMillis();

                if ((curTime - lastUpdate) > 2000) {
                    long diffTime = (curTime - lastUpdate);
                    lastUpdate = curTime;
                    //Log.d("Sensor", String.format("X : %s, Y : %s, Z : %s  | AcelX : %s", x, y, z, deltaX));
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    public void startSensor(){
        senSensorManager.registerListener(onSensorListener, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void pauseSensor(){
        senSensorManager.unregisterListener(onSensorListener);
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
        int newEnimies = (deadEnemy > 0)? aliveEnemy - deadEnemy : 0;
        Log.d("Enemy", deadEnemy + " mortos ");
        Log.d("Enemy", aliveEnemy + " vivos ");
        Log.d("Enemy", newEnimies + " novos");

        deadEnemy = 0; // Renicia os inimigos que foram mortos

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
            Enemy e = new Enemy(random.nextInt(limitEnemyX), random.nextInt(limitEnemyY), MAX_SPEED_ENEMY + level, mActionBarSize, random, bitmapEnemy, rowsEnemy, colsEnemy);
            enimies.add(e);
            mSprites.add(e);
        }

        initClock();

        Log.d("Level", "" + level);
        Log.d("Time Level", "" + timeLevel);
        Log.d("Enemy", enimies.size() + " na lista");
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

        clock.timeInitial(now.getTimeInMillis()).maxTime(Calendar.SECOND, timeLevel).setPause(false);

        Log.d("Clock", getClock());
    }

    public String getClock(){
        return clock.getTime();
    }

    private void initPneu() {
        Bitmap bmp = BitmapFactory.decodeResource(res, R.mipmap.pneu);
        int rows = 1;
        int cols = 3;
        Pneu pneu = new Pneu(GameManager.getWidth() / 2 - bmp.getWidth() / 2, 0, height - mActionBarSize - 15, bmp, rows, cols);
        mSprites.add(pneu);
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
        limitEnemyX = getWidth()-bitmapEnemy.getWidth() / colsEnemy;
        limitEnemyY = getHeight()- bitmapEnemy.getHeight() / rowsEnemy;

        final TypedArray styledAttributes = canvasView.getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        // Altura da action bar
        mActionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        Log.d("Action", mActionBarSize + "px");

        mActionBarSize += 15;
        limitEnemyY -= mActionBarSize;

        for(int i=0; i<aliveEnemy; i++)
            enimies.add(new Enemy(random.nextInt(limitEnemyX), random.nextInt(limitEnemyY), MAX_SPEED_ENEMY + level, mActionBarSize, random, bitmapEnemy, rowsEnemy, colsEnemy));

        mSprites.addAll(enimies);
    }

    @Override
    public void update() {
        if(clock.isOut()){
            gameOver("Game Over, você foi muito bem! Mas não basta apenas matar os mosquitos, você precisa eliminar os focos e evita qualquer criadouro com água parada.");
            return;
        }
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
                newStage(); // Inicia o novo nivel
        }
        //moveEnemies(); // Acelera todos
    }

    private synchronized void checkEnimies(){

        //Log.d("Gameover", "Nao existe mais mosquitos vivos");
        if(aliveEnemy <= 0){
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

            if(aliveEnemy <= 0)
                enimies.clear();
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
            deadEnemy++; // Matou um inimigo

            if(aliveEnemy <= 0){
                Log.d("Gameover", "Fim do nivel");
                clock.setPause(true); // Pausa o relogio
                nextLevelSprite.visible = true; // Exibe imagem de proximo nível
                timeOver = System.currentTimeMillis(); // Tempo que o level terminou
                // Adiciona todos os inimigos na lista de mortos
                deadEnemies.addAll(enimies);
            }
        }
    }

    public boolean isNextLevel(){
        return nextLevelSprite.visible;
    }

    private void gameOver(String text) {
        gameLoopThread.setRunning(false);
        Log.d("Game", "Mesagem : " + text);
        Context ctx = canvasView.getContext();
        Intent intent = new Intent(ctx, GameOverActivity.class);
        intent.putExtra("score", score);
        ctx.startActivity(intent);
    }

    public boolean isGameOver(){
        return gameLoopThread != null && gameLoopThread.isRunning();
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

    // Implementar o status
    public void status() {
        Log.d("Enemy", "***** STATUS *******");
        Log.d("Enemy", aliveEnemy + " vivo");
        Log.d("Enemy", deadEnemy + " mortos");
        Log.d("Enemy", mSprites.size() + " sprites");
        Log.d("Enemy", enimies.size() + " na lista de ativos");
        Log.d("Enemy", deadEnemies.size() + " na lista de mortos");
    }
}
