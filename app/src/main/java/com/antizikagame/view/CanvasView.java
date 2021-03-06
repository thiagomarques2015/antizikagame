package com.antizikagame.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.antizikagame.R;
import com.antizikagame.control.GameManager;
import com.antizikagame.control.ICanvasView;
import com.antizikagame.control.SoundManager;
import com.antizikagame.object.SimpleCircle;
import com.antizikagame.object.Sprite;

/**
 * Game principal
 * Created by Pavel on 04.01.2016.
 */
public class CanvasView extends View implements ICanvasView {

    private int width;
    private int height;

    private Paint  paint;
    private Canvas canvas;
    private GameManager gameManager;

    private Toast toast;
    private Paint paintClock, paintText;
    private float timeX;
    private float timeY;
    private float scoreX;
    private float scoreY;
    private Paint paintNext;
    public static Paint paintHit;
    private Paint paintClockStage;
    private Paint.FontMetrics fms;
    private Paint.FontMetrics fmsh;
    private Paint paintHighScoreText;
    private float highscoreX;
    private float highscoreY;

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initWidthAndHeight(context);
        initPaint();
        SoundManager soundManager = SoundManager.getInstance(context);
        gameManager = new GameManager(soundManager, this, width, height);
    }

    private void initWidthAndHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        width = point.x;
        height = point.y;
    }


    private void initPaint() {
        // Sprites
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        // Hit
        paintHit = new Paint();
        paintHit.setColor(Color.WHITE);
        paintHit.setTextSize(getTextSize(R.dimen.point_size));

        // Clock
        paintClock = new Paint();
        paintClock.setColor(Color.WHITE);
        paintClock.setTextSize(getTextSize(R.dimen.clock_size));
        timeX = width* 0.8f;
        timeY = height*0.05f;

        // Clock Stage
        paintClockStage = new Paint();
        paintClockStage.setColor(Color.WHITE);
        paintClockStage.setTextSize(getTextSize(R.dimen.clock_size));

        // Next Level
        paintNext = new Paint();
        paintNext.setColor(Color.parseColor("#1f1a17"));
        paintNext.setTextSize(getTextSize(R.dimen.next_level_size));

        // Score
        fms = new Paint.FontMetrics();
        paintText = new Paint();
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(getTextSize(R.dimen.score_size));
        scoreX = width* 0.05f;
        scoreY = height*0.05f;
        paintText.getFontMetrics(fms);

        // HighScore
        fmsh = new Paint.FontMetrics();
        paintHighScoreText = new Paint();
        paintHighScoreText.setColor(Color.WHITE);
        paintHighScoreText.setTextSize(getTextSize(R.dimen.score_size));
        highscoreX = width* 0.05f;
        highscoreY = height*0.11f;
        paintHighScoreText.getFontMetrics(fmsh);
    }

    private float getTextSize(int id){
        return getContext().getResources().getDimensionPixelOffset(id);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        // Relogio
        canvas.drawText(gameManager.getClock(), timeX, timeY, paintClock);
        // Relogio Stage
        if(gameManager.isNextLevel()){
            String textStage = "00";
            float ws = paintClock.measureText(textStage, 0, textStage.length());
            canvas.drawText(gameManager.getStageClock(), getWidth()/2 -ws/2, getHeight()*0.5f, paintClockStage);
        }
        // Score
        drawScore(R.string.score, R.color.colorPrimaryDark, gameManager.getScore().toString(), scoreX, scoreY, fms, paintText);
        // HighScore
        drawScore(R.string.highscore, R.color.colorPrimary, gameManager.getHighScore().toString(),
                highscoreX, highscoreY, fmsh, paintHighScoreText);

        String nextLevel = getContext().getString(R.string.next_level_msg);
        float w = paintNext.measureText(nextLevel, 0, nextLevel.length());

        if(gameManager.isNextLevel())
            canvas.drawText(nextLevel, getWidth()/2 - w/2, getHeight()*0.7f, paintNext);

        gameManager.onDraw();
    }

    private void drawScore(int txtId, int color, String txt, float x, float y, Paint.FontMetrics fm, Paint paint) {
        int margin = 15;
        String scoreTxt = String.format(getContext().getString(txtId), txt);
        paint.setColor(color);

        canvas.drawRect(x - margin, y + fm.top - margin,
                x + paint.measureText(scoreTxt) + margin, y + fm.bottom + margin, paint);

        paint.setColor(Color.WHITE);

        canvas.drawText(scoreTxt, x, y, paint);
    }

    @Override
    public void drawCircle(SimpleCircle circle) {
        paint.setColor(circle.getColor());
        canvas.drawCircle(circle.getX(), circle.getY(), circle.getRadius(), paint);
    }

    @Override
    public void drawSprite(Sprite sprite) {
        sprite.onDraw(canvas);
    }

    @Override
    public void redraw() {
        invalidate();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

        Log.d("Canvas", "Ganhou focus? " + hasWindowFocus);

        // Pausa o jogo menos o relogio
        gameManager.setPause(!hasWindowFocus);

        if(hasWindowFocus){
            gameManager.resume();
        }else{
            gameManager.pause();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE :
                gameManager.onTouchEvent(x, y);
                break;
            case MotionEvent.ACTION_UP:
                //Log.d("Click", "Evento + " + event.getAction());
                gameManager.status();
                break;
        }
        invalidate();
        return true;
    }

    public static Paint getPaintHit() {
        return paintHit;
    }
}
