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
    private static final int MAX_PNEU_TIME = 70;
    private static final int MIN_PNEU_TIME = 20;
    private int MIN_ENEMY_DEAD;
    private int NEXT_PNEU_TIME;
    private final Random random;
    private final Resources res;
    private final Context context;

    private ICanvasView canvasView;
    private static int width;
    private static int height;

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

    // Pneu
    private Pneu pneu;
    private int limitPneuX;
    private int limitPneuY;

    // Sensor
    private long lastUpdate;
    private float lastX;
    private static float xAcceleration;
    private int activePneus;

    public GameManager(CanvasView canvasView, int width, int height) {
        this.canvasView = canvasView;
        this.context = canvasView.getContext();
        mSprites = new ArrayList<>();
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
        initMainLoop(canvasView);
        initSensor();
    }

    private void initSensor() {
        senSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(onSensorListener, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public static float getxAcceleration() {
        return xAcceleration;
    }

    private SensorEventListener onSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Sensor mySensor = sensorEvent.sensor;

            if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                /*final float alpha = 0.8f;
                float gravity = alpha * SensorManager.GRAVITY_EARTH + (1 - alpha) * x;*/

                // get the change of the x,y,z values of the accelerometer
                //xAcceleration = Math.abs(dif);
//                xAcceleration = dif;
                xAcceleration = x * -1;
//                xAcceleration = x - gravity;

                lastX = x;

                long curTime = System.currentTimeMillis();

                if ((curTime - lastUpdate) > 2000) {
                    long diffTime = (curTime - lastUpdate);
                    lastUpdate = curTime;
                    //Log.d("Sensor", String.format("X : %s, Y : %s, Z : %s  | AcelX : %s | Dif : %s", x, y, z, xAcceleration, dif));
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

    private void initMainLoop(CanvasView canvasView) {
        gameLoopThread = new GameLoop(this, canvasView);
        gameLoopThread.setRunning(true);
        gameLoopThread.start();
    }

    private void newStage() {
        // Adiciona todos os inimigos na lista de mortos
        deadEnemies.addAll(enimies);
        // Limpa a lista de inimigos na lista
        enimies.clear();

        level++; // Avanca um nivel
        nextLevelSprite.visible = false; //Deixa a mensagem e imagem de proximo level invisiveis
        startTime = System.currentTimeMillis(); //Define a quantidade de Pinks de acordo com o level
        aliveEnemy = TOTAL_ENEMIES+level; //Define a quantidade de mosquitos de acordo com o level
        timeLevel = (int) Math.max(TIME_LEVEL * 3 - (System.currentTimeMillis() - startTime)/500, 1); // Sempre 19 segundos
        int newEnimies = (deadEnemy > 0)? aliveEnemy - deadEnemy : 0;
        Log.d("Enemy", deadEnemy + " mortos ");
        Log.d("Enemy", aliveEnemy + " vivos ");
        Log.d("Enemy", newEnimies + " novos");

        calcPneu();

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

    /**
     * Max => 70%
     * Min => 20%
     *
     * Tempo
     *  15 -> 70% = 10s
     *      1 - 10 = 1 x 10 = 10 { 70 - 10 = 60% }
     *      11 - 20 = 2 x 10 = 20 { 70 - 20 = 50% }
     *      21 - 30 = 3 x 10 = 30 { 70 - 30 = 40% }
     *
     *  Inimigo
     *      1 - 10 = 60% = 1, 6 ( SORTEAR )
     *      11 - 20 = 50% = 1, 12 ( SORTEAR )
     */
    private void calcPneu() {
        activePneus = 0;
        int range = (int) Math.ceil(level / 10f);
        Log.d("Pneu", "Range " + range);
        NEXT_PNEU_TIME = MAX_PNEU_TIME - (range * 10);
        NEXT_PNEU_TIME = (NEXT_PNEU_TIME < MIN_PNEU_TIME)? MIN_PNEU_TIME : NEXT_PNEU_TIME;
        int p = NEXT_PNEU_TIME;
        NEXT_PNEU_TIME = (int) Math.ceil((p / 100f) * timeLevel);
        MIN_ENEMY_DEAD = (int) Math.ceil((p / 100f) * aliveEnemy + 1);
        MIN_ENEMY_DEAD = random.nextInt(MIN_ENEMY_DEAD) + 1;
        Log.d("Pneu", "NEXT_PNEU_TIME : " + NEXT_PNEU_TIME);
        Log.d("Pneu", "MIN_ENEMY_DEAD : " + MIN_ENEMY_DEAD);
    }

    /**
     * Se chegou no tempo limite para jogar o pneu na tela e o jogador nao matou o numero minimo de mosquitos
     * @return true ou false
     */
    public boolean isPneuTime(){
        return  NEXT_PNEU_TIME == clock.getSecond() && MIN_ENEMY_DEAD > deadEnemy;
    }

    private void initNextLevel() {
        mSprites.add(nextLevelSprite = new Sprite(BitmapFactory.decodeResource(res, R.drawable.next_level)));
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
        Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.pneu);
        int rows = 1;
        int cols = 3;
        limitPneuX = width / 2 - bmp.getWidth() / 2;
        limitPneuY = 0;
        pneu = new Pneu(limitPneuX, limitPneuY , height - mActionBarSize - 15, bmp, rows, cols);
    }

    private void initRacket() {
        Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.raquete_sprite);
        int rows = 1;
        int cols = 4;
        racket = new Racket(GameManager.getWidth()/2 - bmp.getWidth() /2, height - bmp.getHeight() * 2, bmp, rows , cols);
        mSprites.add(racket);
    }

    private void initEnemies() {
        bitmapEnemy = BitmapFactory.decodeResource(res, R.drawable.sprite);
        limitEnemyX = getWidth()-bitmapEnemy.getWidth() / colsEnemy;
        limitEnemyY = getHeight()- bitmapEnemy.getHeight() / rowsEnemy;

        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
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
        checkPneu();
        updateSprites();
        canvasView.redraw();
        removeAll();
    }

    private void removeAll() {
        if(activePneus <= 0){
            int sec = pneu.getTime() - pneu.timeDone;
            if(sec > 1)
                mSprites.remove(pneu);
        }
    }

    public void onDraw() {
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
        if(!isEndStage()){
           /* Log.d("Enemy", aliveEnemy + " vivo");
            Log.d("Enemy", deadEnemies.size() + " mortos");*/
            racket.move(x, y);
        }else{
            //Log.d("Gameover", "Nao existe mais inimigos");
            int sec = getTimeAfterStageOver();
            // Se passar um 1 segundo depois do gameover fecha
            if(sec > 1)
                newStage(); // Inicia o novo nivel
        }
        //moveEnemies(); // Acelera todos
    }

    private int getTimeAfterStageOver() {
        Calendar dif = Calendar.getInstance();
        dif.setTimeInMillis(System.currentTimeMillis() - timeOver);
        return dif.get(Calendar.SECOND);
    }

    private void checkPneu() {

        // Detecta se estar na hora de mostrar o pneu
        if( pneu.isIdle() && isPneuTime() ){
            Log.d("Pneu", "Adiciona o pneu na tela" );
            // Adiciona um pneu na lista
            activePneus++;
            pneu.create(random.nextInt(limitPneuX), limitPneuY);
            mSprites.add(pneu);
            return;
        }

        if( activePneus <=0 ) return;

        if(pneu.isDone()){
            Log.d("Pneu", "+1 Pneu removido" );
            activePneus--;
            int add;
            score += add = (int) Math.max(100 - level*3 - (System.currentTimeMillis() - startTime)/500, 1);
            pneu.score = add;
            Log.d("Score", "Valeu : " + add);

            if(isEndStage()){
                stageFinished();
            }
        }
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

        int sec = getTimeAfterStageOver();

        // Atrasa a morte do ultimo inimigo em 1 segundo, para da tempo de aparecer a animacao da morte
        if(aliveEnemy <= 0){
            if(sec <= 1)
                return;
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
            e.score = add;
            //Log.d("Collision", "Um mosquito foi atingido pela raquete");
            //Log.d("Score", "Valeu : " + add);

            aliveEnemy--; // Remove um mosquito da lista
            deadEnemy++; // Matou um inimigo

            if(isEndStage()){
                stageFinished();
            }
        }
    }

    private void stageFinished() {
        Log.d("Gameover", "Fim do nivel");
        clock.setPause(true); // Pausa o relogio
        nextLevelSprite.visible = true; // Exibe imagem de proximo nível
        timeOver = System.currentTimeMillis(); // Tempo que o level terminou
    }

    public boolean isNextLevel(){
        return nextLevelSprite.visible;
    }

    private void gameOver(String text) {
        gameLoopThread.setRunning(false);
        Log.d("Game", "Mesagem : " + text);
        Context ctx = context;
        Intent intent = new Intent(ctx, GameOverActivity.class);
        intent.putExtra("score", score);
        ctx.startActivity(intent);
    }

    public boolean isEndStage(){
        return aliveEnemy <= 0 && activePneus <= 0;
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
