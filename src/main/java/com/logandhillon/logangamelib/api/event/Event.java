package com.logandhillon.logangamelib.api.event;

/**
 * @author Logan Dhillon
 */
public interface Event {
    boolean isConsumed();

    void consume();
}
