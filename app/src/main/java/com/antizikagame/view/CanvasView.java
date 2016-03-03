package com.antizikagame.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.antizikagame.GameManager;
import com.antizikagame.ICanvasView;
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
    private Paint paintText;
    private float timeX;
    private float timeY;

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initWidthAndHeight(context);
        initPaint();
        gameManager = new GameManager(this, width, height);
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

        // Clock
        paintText = new Paint();
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(35);
        timeX = width* 0.8f;
        timeY = height*0.05f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        // Relogio
        canvas.drawText(gameManager.getClock(), timeX, timeY, paintText);
        gameManager.onDraw();
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
    public void showMessage(String text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        Log.d("Game", "Mesagem : " + text);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            gameManager.onTouchEvent(x, y);
        }
        invalidate();
        return true;
    }
}
