package com.logandhillon.logangamelib.math;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A mutable 2D floating-point vector.
 *
 * @author Logan Dhillon
 */
@ToString(includeFieldNames = false)
@EqualsAndHashCode
public class Vec2f {
    public float x;
    public float y;

    public Vec2f(float x, float y) {
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
    public Vec2f add(float dx, float dy) {
        this.x += dx;
        this.y += dy;
        return this;
    }

    public Vec2f set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }
}
