package com.logandhillon.logangamelib.api;

/**
 * Represents an operation that does not return a result and is intended to be run after something finishes.
 * <p>
 * Use deferable operations in try-with-resources blocks.
 *
 * @author Logan Dhillon
 */
@FunctionalInterface
public interface Deferrable extends AutoCloseable {
    /**
     * Deferred method, indended to be called after intent finishes.
     */
    @Override
    void close();
}
