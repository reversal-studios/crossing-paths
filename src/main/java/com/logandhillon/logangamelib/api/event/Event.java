package com.logandhillon.logangamelib.api.event;

/**
 * An event is an interface for any propagated system that may be consumed as is it passed to event handlers.
 *
 * @author Logan Dhillon
 * @see EventHandler
 */
public interface Event {
    /**
     * @return if this event has already been consumed.
     */
    boolean isConsumed();

    /**
     * Executes this event's purpose. It should also mark this event as consumed and stop further propagation.
     */
    void consume();
}
