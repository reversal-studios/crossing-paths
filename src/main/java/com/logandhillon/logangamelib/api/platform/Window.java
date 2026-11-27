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
public record Window(int width, int height, float scalingTolerance, int minWidth, int minHeight) {
    public static final Window DEFAULT = new Window(1280, 720, 0.05f);

    public Window(int width, int height, float scalingTolerance) {
        this(width, height, scalingTolerance, 0, 0);
    }

    public Window {
        if (width <= 0) throw new IllegalArgumentException("Scene width must be positive");
        if (height <= 0) throw new IllegalArgumentException("Scene height must be positive");
        if (scalingTolerance < 0 || scalingTolerance >= 1 || Float.isNaN(scalingTolerance))
            throw new IllegalArgumentException("Scaling tolerance must be in [0, 1)");
        if (minWidth < 0 || minWidth > width)
            throw new IllegalArgumentException("Minimum window width must be in [0, width]");
        if (minHeight < 0 || minHeight > height)
            throw new IllegalArgumentException("Minimum window height must be in [0, height]");
    }

    public float aspectRatio() {
        return (float)width / height;
    }
}
