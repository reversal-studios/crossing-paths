package com.logandhillon.logangamelib.api.input;

import java.util.function.Consumer;

/**
 * @author Logan Dhillon
 */
public interface InputHandler {
    void onKeyDown(Consumer<KeyboardEvent> e);

    void onKeyUp(Consumer<KeyboardEvent> e);

    void onKeyTyped(Consumer<KeyboardEvent> e);

    void onMouseDown(Consumer<MouseClickEvent> handler);

    void onMouseUp(Consumer<MouseClickEvent> handler);

    void onMouseClicked(Consumer<MouseClickEvent> handler);

    void onMouseMoved(Consumer<MouseClickEvent> handler);

    void onMouseDragged(Consumer<MouseClickEvent> handler);
}
