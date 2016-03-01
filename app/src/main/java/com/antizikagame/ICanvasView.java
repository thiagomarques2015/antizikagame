package com.antizikagame;

import com.antizikagame.object.SimpleCircle;
import com.antizikagame.object.Sprite;

/**
 * Created by Pavel on 04.01.2016.
 */
public interface ICanvasView {
    void drawCircle(SimpleCircle mainCircle);
    void drawSprite(Sprite sprite);
    void redraw();
    void showMessage(String text);
}
