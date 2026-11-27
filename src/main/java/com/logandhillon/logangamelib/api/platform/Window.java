package com.logandhillon.logangamelib.api.platform;

/**
 * Dimensions and scaling behaviour for a scene window.
 *
 * @param width            logical canvas width
 * @param height           logical canvas height
 * @param scalingTolerance aspect-ratio tolerance used when scaling
 *
 * @author Logan Dhillon
 */
public record Window(int width, int height, float scalingTolerance) {
    public static final Window DEFAULT = new Window(1280, 720, 0.05f);

    public Window {
        if (width <= 0) throw new IllegalArgumentException("Scene width must be positive");
        if (height <= 0) throw new IllegalArgumentException("Scene height must be positive");
        if (scalingTolerance < 0 || scalingTolerance >= 1 || Float.isNaN(scalingTolerance))
            throw new IllegalArgumentException("Scaling tolerance must be in [0, 1)");
    }

    public float aspectRatio() {
        return (float)width / height;
    }
}
