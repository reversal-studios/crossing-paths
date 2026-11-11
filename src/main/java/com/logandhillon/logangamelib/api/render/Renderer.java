package com.logandhillon.logangamelib.api.render;

import java.time.format.TextStyle;

/**
 * @author Logan Dhillon
 */
public interface Renderer {
    void clear(Color color);
    void draw(Sprite sprite, float x, float y, float w, float h, Color tint);
    void drawText(Font font, String text, float x, float y, TextStyle style);
}
