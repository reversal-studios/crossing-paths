package com.logandhillon.logangamelib.api.input;

import com.logandhillon.logangamelib.api.event.EventSubscription;

import java.util.function.Consumer;

/**
 * @author Logan Dhillon
 */
public interface InputHandler {
    EventSubscription onKeyDown(Consumer<KeyboardEvent> e);

    EventSubscription onKeyUp(Consumer<KeyboardEvent> e);

    EventSubscription onKeyTyped(Consumer<KeyboardEvent> e);

    EventSubscription onMouseDown(Consumer<MouseClickEvent> handler);

    EventSubscription onMouseUp(Consumer<MouseClickEvent> handler);

    EventSubscription onMouseClicked(Consumer<MouseClickEvent> handler);

    EventSubscription onMouseMoved(Consumer<MouseClickEvent> handler);

    EventSubscription onMouseDragged(Consumer<MouseClickEvent> handler);
}
