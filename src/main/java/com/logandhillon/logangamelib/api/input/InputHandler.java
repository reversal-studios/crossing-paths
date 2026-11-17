package com.logandhillon.logangamelib.api.input;

import com.logandhillon.logangamelib.api.event.EventHandler;

/**
 * @author Logan Dhillon
 */
public interface InputHandler {
    void onKeyDown(EventHandler<KeyboardEvent> e);

    void onKeyUp(EventHandler<KeyboardEvent> e);

    void onKeyTyped(EventHandler<KeyboardEvent> e);

    void onMouseDown(EventHandler<MouseClickEvent> handler);

    void onMouseUp(EventHandler<MouseClickEvent> handler);

    void onMouseClicked(EventHandler<MouseClickEvent> handler);

    void onMouseMoved(EventHandler<MouseClickEvent> handler);

    void onMouseDragged(EventHandler<MouseClickEvent> handler);
}
