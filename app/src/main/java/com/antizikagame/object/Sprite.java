package com.antizikagame.object;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by Thiago on 26/02/2016.
 */
public class Sprite {
    public static final int ANIM_STOP = 0;
    public static final int ANIM_GO = 1;
    public static final int ANIM_GOBACK = 2;
    protected int animation;
    public int x;
    public int y;
    private Bitmap mBitmap;
    public boolean visible;
    private final int BMP_ROWS;
    private final int BMP_COLUMNS;
    private int currentFrame;
    public int width;
    public int height;
    private int firstFrame;
    private int lastFrame;
    private boolean animationControl;
    public Paint mPaint;
    private Point lastCollision;

    public Sprite(Bitmap bmp) {
        this(bmp, 1, 1);
    }

    public Sprite(Bitmap bmp, int bmp_rows, int bmp_columns) {
        this.animation = 2;
        this.x = 0;
        this.y = 0;
        this.visible = true;
        this.currentFrame = 0;
        this.firstFrame = 0;
        this.lastFrame = 1;
        this.animationControl = false;
        this.mBitmap = bmp;
        this.BMP_ROWS = bmp_rows;
        this.BMP_COLUMNS = bmp_columns;
        this.width = bmp.getWidth() / this.BMP_COLUMNS;
        this.height = bmp.getHeight() / this.BMP_ROWS;
        this.lastFrame = this.BMP_COLUMNS * this.BMP_ROWS;
        if(this.getFrameCount() == 1) {
            this.animation = 0;
        }

    }

    public void update() {
        switch(this.animation) {
            case 1:
                this.currentFrame = (this.currentFrame + 1 - this.firstFrame) % (this.lastFrame - this.firstFrame) + this.firstFrame;
                break;
            case 2:
                if(this.currentFrame + 1 == this.lastFrame) {
                    this.animationControl = true;
                } else if(this.currentFrame == this.firstFrame) {
                    this.animationControl = false;
                }

                this.currentFrame += this.animationControl?-1:1;
        }

    }

    public void onDraw(Canvas canvas) {
        if(this.visible) {
            int srcX = this.currentFrame % this.BMP_COLUMNS * this.width;
            int srcY = this.currentFrame / this.BMP_COLUMNS * this.height;
            canvas.drawBitmap(this.mBitmap, new Rect(srcX, srcY, srcX + this.width, srcY + this.height), new Rect(this.x, this.y, this.x + this.width, this.y + this.height), this.mPaint);
        }
    }

    public void setFirstFrame(int frame) {
        this.firstFrame = frame;
        if(this.firstFrame >= this.lastFrame) {
            this.lastFrame = this.firstFrame + 1;
            this.currentFrame = this.firstFrame;
            this.animationControl = false;
        } else if(this.currentFrame < this.firstFrame) {
            this.currentFrame = this.firstFrame;
            this.animationControl = false;
        }

    }

    public void setLastFrame(int frame) {
        this.lastFrame = frame;
        if(this.lastFrame <= this.firstFrame) {
            this.firstFrame = this.lastFrame - 1;
            this.currentFrame = this.firstFrame;
            this.animationControl = false;
        } else if(this.currentFrame > frame) {
            this.currentFrame = this.firstFrame;
            this.animationControl = false;
        }

    }

    public int getFrameCount() {
        return this.BMP_COLUMNS * this.BMP_ROWS;
    }

    public void setCurrentFrame(int frame) {
        this.currentFrame = frame;
        this.firstFrame = frame;
        this.lastFrame = frame + 1;
    }

    public boolean setAnimation(int frame, int iframe, int lframe, int type) {
        if(frame >= iframe && frame < lframe && iframe < lframe && type >= 0 && type <= 2) {
            this.currentFrame = frame;
            this.firstFrame = iframe;
            this.lastFrame = lframe;
            if(this.getFrameCount() > 1) {
                this.animation = type;
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean setAnimation(int type) {
        if(this.getFrameCount() > 1) {
            this.animation = type;
            return true;
        } else {
            return false;
        }
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public double getDist(SimpleCircle circle){
        return Math.sqrt( (x-circle.x)*(x-circle.x) + (y-circle.y)*(y-circle.y) );
    }

    public double getDist(Sprite sprite){
        return Math.sqrt( (x-sprite.x)*(x-sprite.x) + (y-sprite.y)*(y-sprite.y) );
    }

    public boolean checkForCollision(Sprite sprite) {
        try {
            if (x<0 && sprite.x<0 && y<0 && sprite.y<0) return false;
            Rect r1 = new Rect(x, y, x
                    + width,  y + height);
            Rect r2 = new Rect(sprite.x, sprite.y, sprite.x +
                    sprite.width, sprite.y + sprite.height);
            Rect r3 = new Rect(r1);
            if(r1.intersect(r2)) {
                for (int i = r1.left; i<r1.right; i++) {
                    for (int j = r1.top; j<r1.bottom; j++) {
                        if (mBitmap.getPixel(i-r3.left, j-r3.top)!=
                                Color.TRANSPARENT) {
                            if (sprite.getBitmap().getPixel(i - r2.left, j - r2.top) !=
                                    Color.TRANSPARENT) {
                                //lastCollision = new Point(sprite.x +i-r2.left, sprite.y + j-r2.top);
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //lastCollision = new Point(-1,-1);
        return false;
    }
}
