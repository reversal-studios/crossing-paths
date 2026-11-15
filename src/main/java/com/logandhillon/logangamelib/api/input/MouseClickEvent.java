package com.logandhillon.logangamelib.api.input;

import com.logandhillon.logangamelib.api.event.Event;
import com.logandhillon.logangamelib.math.Vector2f;

/**
 * @author Logan Dhillon
 */
public interface MouseClickEvent extends Event {
    Vector2f pos();

    Button button();

    int clickCount();

    KeyCode.Modifiers modifiers();

    enum Button {
        PRIMARY, SECONDARY, MIDDLE, OTHER
    }
}
