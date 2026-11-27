package com.logandhillon.logangamelib.api.scene;

/**
 * A running scene created by a platform backend. Access backend-specific interface using {@link #surface()}.
 *
 * @param <T> platform surface type
 *
 * @author Logan Dhillon
 * @see SceneFactory
 */
public interface SceneHandle<T> extends AutoCloseable {
    /**
     * Gets the platform surface from this handle.
     *
     * @return surface as per platform type
     */
    T surface();

    /**
     * Closes the scene in this handle and all associations (e.g. lifecycles, listeners)
     */
    @Override
    void close();
}
