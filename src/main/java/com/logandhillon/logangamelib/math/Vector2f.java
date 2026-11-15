package com.logandhillon.logangamelib.math;

/**
 * A mutable 2D floating-point vector.
 *
 * @author Logan Dhillon
 */
public class Vector2f {
    public float x;
    public float y;

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Mutates this vector in-place to avoid 'new' allocations in the game loop
     *
     * @param dx change in x
     * @param dy change in y
     *
     * @return modified vector
     */
    public Vector2f add(float dx, float dy) {
        this.x += dx;
        this.y += dy;
        return this;
    }

    public Vector2f set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }
}
