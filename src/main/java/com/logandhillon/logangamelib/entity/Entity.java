package com.logandhillon.logangamelib.entity;

import com.logandhillon.logangamelib.engine.GameScene;
import javafx.scene.canvas.GraphicsContext;

/**
 * An entity is the most basic paradigm that can be handled by the game engine. It provides methods for
 * {@link Entity#onUpdate(float)} and {@link Entity#onRender(GraphicsContext, float, float)}, which must be implemented
 * by your subclass.
 *
 * @author Logan Dhillon
 * @see GameScene
 */
public abstract class Entity implements GameObject {
    protected GameScene<?> parent;
    protected float        x;
    protected float        y;

    /**
     * Creates an entity at the specified position.
     *
     * @param x x-position (from left)
     * @param y y-position (from top)
     */
    public Entity(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Called every tick to render the entity.
     *
     * @param g the graphical context to render to.
     *
     * @apiNote the X and Y parameters are ignored, and are only implemented because {@link GameObject} requires them.
     * the entity will always render at its own X and Y positions.
     * @implNote do not implement this method to change how the entity is rendered.
     * @see Entity#onRender
     */
    public void render(GraphicsContext g, float x, float y) {
        this.render(g);
    }

    /**
     * Called every tick to render the entity.
     *
     * @param g the graphical context to render to.
     *
     * @implNote do not implement this method to change how the entity is rendered.
     * @see Entity#onRender
     */
    public void render(GraphicsContext g) {
        this.onRender(g, this.x, this.y);
    }

    /**
     * Called every tick to render the entity; responsible for rendering this entity to the provided graphics context.
     *
     * @param g the graphical context to render to.
     * @param x the x position to render the entity at
     * @param y the y position to render the entity at
     *
     * @apiNote Do not call this to render the entity.
     * @implNote implement this method to change the render behaviour of your entity.
     * @see Entity#render(GraphicsContext)
     */
    protected abstract void onRender(GraphicsContext g, float x, float y);

    /**
     * Runs when this object is attached to a parent.
     *
     * @param parent the parent that this object is now attached to.
     *
     * @see GameScene
     */
    public void onAttach(GameScene<?> parent) {
        this.parent = parent;
    }

    /**
     * Helper function to remove this entity from it's parent scene.
     *
     * @see GameScene#killEntity(Entity)
     */
    public void kill() {
        parent.killEntity(this);
    }

    /**
     * Immediately sets the absolute position of this entity to the new position. To move an entity by a relative
     * amount, use {@link Entity#translate(float, float)}
     *
     * @param x new x-position
     * @param y new y-position
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Translates this entity relatively by a set amount of units. To move an entity to an absolute position, use
     * {@link Entity#setPosition(float, float)}
     *
     * @param x ∆x units to move by
     * @param y ∆y units to move by
     */
    public void translate(float x, float y) {
        this.x += x;
        this.y += y;
    }

    public float getY() {
        return y;
    }

    public float getX() {
        return x;
    }
}
