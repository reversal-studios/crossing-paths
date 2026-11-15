package com.logandhillon.logangamelib.math;

/**
 * A mutable 3D floating-point vector.
 *
 * @author Logan Dhillon
 */
public class Vector3f {
    public float x;
    public float y;
    public float z;

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Mutates this vector in-place to avoid 'new' allocations in the game loop
     *
     * @return modified vector
     */
    public Vector3f add(float dx, float dy, float dz) {
        this.x += dx;
        this.y += dy;
        this.z += dz;
        return this;
    }

    public Vector3f set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }
}
