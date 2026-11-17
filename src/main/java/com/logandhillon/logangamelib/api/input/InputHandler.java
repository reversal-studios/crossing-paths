package com.logandhillon.logangamelib.api.input;

import com.logandhillon.logangamelib.api.event.EventHandler;

/**
 * An input handler is an interface with all input events that are passed from the engine to the game scene and
 * entities.
 *
 * @author Logan Dhillon
 * @see com.logandhillon.logangamelib.engine.GameScene#input()
 */
public interface InputHandler {
    /**
     * Called when a key is pressed.
     *
     * @param e details about the called event
     */
    void onKeyDown(EventHandler<KeyboardEvent> e);

    /**
     * Called when a key is released.
     *
     * @param e details about the called event
     */
    void onKeyUp(EventHandler<KeyboardEvent> e);

    /**
     * Called when a key is "typed," i.e. when a key combination results in a valid printable character.
     *
     * @param e details about the called event
     */
    void onKeyTyped(EventHandler<KeyboardEvent> e);

    /**
     * Called when a mouse button is pressed anywhere on the screen.
     *
     * @param e details about the called event
     */
    void onMouseDown(EventHandler<MouseClickEvent> e);

    /**
     * Called when a mouse button is released anywhere on the screen.
     *
     * @param e details about the called event
     */
    void onMouseUp(EventHandler<MouseClickEvent> e);

    /**
     * Called when a mouse button is clicked (pressed and released) on the same entity.
     *
     * @param e details about the called event
     */
    void onMouseClicked(EventHandler<MouseClickEvent> e);

    /**
     * Called when the mouse is moved.
     *
     * @param e details about the called event
     */
    void onMouseMoved(EventHandler<MouseClickEvent> e);

    /**
     * Called when the mouse is moved while active, causing a "drag."
     *
     * @param e details about the called event
     */
    void onMouseDragged(EventHandler<MouseClickEvent> e);
}
