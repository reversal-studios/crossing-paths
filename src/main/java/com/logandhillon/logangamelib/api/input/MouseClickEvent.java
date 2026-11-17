package com.logandhillon.logangamelib.api.input;

import com.logandhillon.logangamelib.api.event.Event;
import com.logandhillon.logangamelib.math.Vec2f;

/**
 * A type of {@link Event} made for mouse/pointer input, i.e. the mouse was clicked or moved.
 *
 * @author Logan Dhillon
 */
public interface MouseClickEvent extends Event {
    /**
     * Relative position of the mouse when this event was fired.
     *
     * @return relative position vector
     */
    Vec2f pos();

    /**
     * @return mouse button involved in this event
     */
    Button button();

    /**
     * @return number of mouse clicks associated with this event
     */
    int clickCount();

    /**
     * @return record of keyboard modifier keys (e.g. ctrl, shift) pressed when this event was fired
     */
    KeyCode.Modifiers modifiers();

    /**
     * A mouse button.
     */
    enum Button {
        PRIMARY, SECONDARY, MIDDLE, OTHER
    }
}
