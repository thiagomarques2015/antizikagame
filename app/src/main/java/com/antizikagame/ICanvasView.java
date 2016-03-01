package com.antizikagame;

/**
 * Created by Pavel on 04.01.2016.
 */
public interface ICanvasView {
    void drawCircle(SimpleCircle mainCircle);
    void drawSprite(Sprite sprite);
    void redraw();
    void showMessage(String text);
}
