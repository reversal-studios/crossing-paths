package com.logandhillon.logangamelib.api.input;

import com.logandhillon.logangamelib.api.event.Event;

/**
 * A type of {@link Event} for keyboard input, i.e. a key being pressed or released.
 *
 * @author Logan Dhillon
 */
public interface KeyboardEvent extends Event {
    /**
     * @return keycode information, e.g. string representation, scancode, type
     */
    KeyCode key();

    String character();

    /**
     * @return keycode modifiers, e.g. was shift or ctrl pressed
     */
    KeyCode.Modifiers modifiers();
}
