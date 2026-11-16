package com.logandhillon.logangamelib.entity;

import com.logandhillon.logangamelib.api.input.MouseClickEvent;
import com.logandhillon.logangamelib.engine.UIScene;
import javafx.scene.input.MouseEvent;

/**
 * A Clickable is a special type of {@link Entity} that can be clicked by the mouse. It can only be used within a
 * {@link UIScene} (it <i>can</i> be used in all GameScenes, but it will not be clickable).
 *
 * @author Logan Dhillon
 * @see MouseEvent
 * @see UIScene
 */
public abstract class Clickable extends BoundEntity<UIScene<?>> {
    protected float w;
    protected float h;

    /**
     * Creates an entity at the specified position.
     *
     * @param x x-position (from left)
     * @param y y-position (from top)
     * @param w width of this element's hitbox
     * @param h width of this element's hitbox
     */
    public Clickable(float x, float y, float w, float h) {
        super(x, y);
        this.w = w;
        this.h = h;
    }

    /**
     * Runs the click handler that was supplied when the Button was created.
     *
     * @param e the mouse event provided by JavaFX
     */
    public abstract void onClick(MouseClickEvent e);

    /**
     * Called when this element WAS active, but was clicked away from and thus is no longer active.
     */
    public void onBlur() {}

    /**
     * Called when the mouse is pressed (not clicked) on this Clickable.
     *
     * @param e  mouse event
     *
     * @see MouseEvent#MOUSE_PRESSED
     */
    public void onMouseDown(MouseClickEvent e) {}

    /**
     * Runs the mouse enter handler if it was set.
     *
     * @param e the mouse event provided by JavaFX
     */
    public void onMouseEnter(MouseClickEvent e) {}

    /**
     * Runs the mouse leave handler if it was set.
     *
     * @param e the mouse event provided by JavaFX
     */
    public void onMouseLeave(MouseClickEvent e) {}

    public float getWidth() {
        return w;
    }

    public float getHeight() {
        return h;
    }

    public void setSize(float w, float h) {
        this.w = w;
        this.h = h;
    }
}
