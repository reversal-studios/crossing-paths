package com.logandhillon.logangamelib.api.event;

/**
 * Interface for functional event handlers, providing a {@link handle} method that is called appropriately.
 *
 * @author Logan Dhillon
 */
@FunctionalInterface
public interface EventHandler<T extends Event> {
    /**
     * Called when the event this handler is handling is called.
     *
     * @param event called event information
     */
    void handle(T event);
}
