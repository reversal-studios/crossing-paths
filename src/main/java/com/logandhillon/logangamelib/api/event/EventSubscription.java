package com.logandhillon.logangamelib.api.event;

/**
 * @author Logan Dhillon
 */
@FunctionalInterface
public interface EventSubscription extends AutoCloseable {
    @Override
    void close();
}
