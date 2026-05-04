package com.logandhillon.logangamelib.entity.ui;

import com.logandhillon.logangamelib.engine.GameScene;
import com.logandhillon.logangamelib.entity.Entity;
import com.logandhillon.logangamelib.resource.base.Colors;
import javafx.scene.canvas.GraphicsContext;

/**
 * A modal is a group of other entities that is rendered inside it. This modal is made for generic menus, without any
 * headers and just space for content.
 *
 * @author Logan Dhillon
 * @apiNote Do not attach entities inside this modal, just the modal itself.
 */
public class ModalEntity extends Entity {
    protected final float w, h;
    private final Entity[] entities;
    private final int      cornerDiameter;

    private GameScene<?> parent;

    /**
     * Creates an entity at the specified position. All entities passed to this modal will be translated such that (0,
     * 0) is the top-left corner of this modal.
     *
     * @param x x-position (from left)
     * @param y y-position (from top)
     * @param w width of modal
     * @param h height of modal
     */
    public ModalEntity(float x, float y, float w, float h, int cornerRadius, Entity... entities) {
        super(x, y);
        this.w = w;
        this.h = h;
        this.cornerDiameter = cornerRadius * 2;
        this.entities = entities;
    }

    /**
     * Creates an entity at the specified position. All entities passed to this modal will be translated such that (0,
     * 0) is the top-left corner of this modal.
     * <p>
     * Defines the corner radius as 25px (default)
     *
     * @param x x-position (from left)
     * @param y y-position (from top)
     * @param w width of modal
     * @param h height of modal
     */
    public ModalEntity(float x, float y, float w, float h, Entity... entities) {
        this(x, y, w, h, 25, entities);
    }

    @Override
    protected void onRender(GraphicsContext g, float x, float y) {
        g.setFill(Colors.FOREGROUND_TRANS_40);
        g.fillRoundRect(x, y, w, h, cornerDiameter, cornerDiameter);
    }

    @Override
    public void onUpdate(float dt) {

    }

    @Override
    public void onDestroy() {

    }

    /**
     * Attaches this entity and all entities controlled by it to the parent.
     *
     * @param parent the parent that this object is now attached to.
     */
    @Override
    public void onAttach(GameScene<?> parent) {
        super.onAttach(parent);
        this.parent = parent;

        // add all controlled entities to the parent
        for (Entity e: entities) addEntity(e);
    }

    /**
     * Adds an entity to be controlled by this modal.
     *
     * @param e the entity to attach.
     *
     * @apiNote this modal must be attached to a GameScene before calling this.
     */
    public void addEntity(Entity e) {
        if (parent == null)
            throw new NullPointerException(
                    "This ModalEntity has not been attached to a GameScene yet, and thus you cannot add entities to " +
                    "it yet.");
        e.translate(x, y); // translate to relative 0,0
        parent.addEntity(e);
    }
}
