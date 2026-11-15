package com.logandhillon.logangamelib.api.input;

import com.logandhillon.logangamelib.api.event.Event;

/**
 * @author Logan Dhillon
 */
public interface KeyboardEvent extends Event {
    KeyCode key();

    KeyCode.Modifiers modifiers();
}
