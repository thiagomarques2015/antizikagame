package com.antizikagame;

/**
 * Created by Thiago on 25/02/2016.
 */
public class GameLoop extends Thread {

    static final long FPS = 30;
    private CanvasView view;
    private boolean running = false;
    private final IGameLoop callback;

    public GameLoop(IGameLoop gameManager, CanvasView view) {
        this.view = view;
        this.callback = gameManager;
    }

    public void setRunning(boolean run) {
        running = run;
    }

    private Runnable onDraw = new Runnable() {
        @Override
        public void run() {
            if(callback != null)
                callback.update();
        }
    };

    @Override
    public void run() {
        long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;
        while (running) {
            startTime = System.currentTimeMillis();
            try {
                view.post(onDraw);
            } finally {
                sleepTime = ticksPS-(System.currentTimeMillis() - startTime);
            }
            try {
                if (sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(10);
            } catch (Exception e) {}
        }
    }
}
