package com.logandhillon.logangamelib.math;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A mutable 3D floating-point vector.
 *
 * @author Logan Dhillon
 */
@ToString(includeFieldNames = false)
@EqualsAndHashCode
public class Vec3f {
    public float x;
    public float y;
    public float z;

    public Vec3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Mutates this vector in-place to avoid 'new' allocations in the game loop
     *
     * @return modified vector
     */
    public Vec3f add(float dx, float dy, float dz) {
        this.x += dx;
        this.y += dy;
        this.z += dz;
        return this;
    }

    public Vec3f set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }
}
