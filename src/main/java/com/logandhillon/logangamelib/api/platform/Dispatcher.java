package com.logandhillon.logangamelib.api.platform;

/**
 * Represents a task that will be dispatched to be executed elsewhere.
 *
 * @author Logan Dhillon
 * @see Runnable
 */
@FunctionalInterface
public interface Dispatcher {
    void execute(Runnable task);
}
