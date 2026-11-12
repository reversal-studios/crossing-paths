package com.logandhillon.logangamelib.api.render;

/**
 * @author Logan Dhillon
 */
public interface Texture {
    int width();
    int height();

    record Region(float x, float y, float width, float height) {}
}
