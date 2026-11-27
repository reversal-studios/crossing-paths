package com.logandhillon.logangamelib.scene;

import com.logandhillon.logangamelib.api.input.MouseClickEvent;
import com.logandhillon.logangamelib.entity.Clickable;
import com.logandhillon.logangamelib.entity.Entity;
import com.logandhillon.logangamelib.math.Vec2f;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.util.HashMap;

/**
 * A UI scene is a type of {@link GameScene} that listens to the cursor and registers events handlers for
 * {@link MouseClickEvent mouse click events}.
 * <p>
 * UI scenes are used with {@link Clickable} entities. Clickables can only be used in UI scenes.
 *
 * @param <H> game-specific handler type associated with this scene.
 *
 * @author Logan Dhillon
 * @see Clickable
 */
public abstract class UIScene<H> extends GameScene<H> {
    private static final Logger LOG = LoggerContext.getContext().getLogger(UIScene.class);

    private final HashMap<Clickable, ClickableFlags> clickables       = new HashMap<>();
    private       Clickable[]                        cachedClickables = new Clickable[0];

    private static final class ClickableFlags {
        private boolean isHovering = false;
        private boolean isActive   = false;
    }

    /**
     * Creates a new UI scene without registering mouse events.
     *
     * @see #onStart()
     */
    public UIScene() {
    }

    /**
     * Attaches the UI scene's mouse events to the game scene's
     * {@link com.logandhillon.logangamelib.api.input.InputHandler InputHandler}
     */
    @Override
    protected void onStart() {
        super.onStart();
        input().onMouseClicked(this::onMouseClicked);
        input().onMouseDown(this::onMouseDown);
        input().onMouseMoved(this::onMouseMoved);
    }

    @Override
    protected void onEntityAdded(Entity entity) {
        super.onEntityAdded(entity);

        if (entity instanceof Clickable clickable) {
            clickables.put(clickable, new ClickableFlags());
            cachedClickables = clickables.keySet().toArray(new Clickable[0]);
        }
    }

    @Override
    protected void onEntityRemoved(Entity entity) {
        super.onEntityRemoved(entity);

        if (entity instanceof Clickable clickable && clickables.remove(clickable) != null) {
            cachedClickables = clickables.keySet().toArray(new Clickable[0]);
        }
    }

    /**
     * Runs when a pointer click is delivered to the scene.
     * <p>
     * This method goes through all attached clickables and, if it is within the clickable's hitbox, runs the
     * {@link Clickable#onClick} event handler.
     *
     * @param e details about the mouse click event. this can be used to get the mouse button pressed, x/y position,
     *          etc.
     *
     * @see Clickable#onClick
     */
    protected void onMouseClicked(MouseClickEvent e) {
        LOG.debug("{}: Handling mouse click event at {}", this.getClass().getSimpleName(), e.pos());

        for (Clickable c: cachedClickables) {
            ClickableFlags flags = clickables.get(c);

            // if there are no flags, this Clickable was unregistered (so skip it)
            if (flags == null) continue;

            // if the mouse is within the hitbox of the clickable, trigger it's onClick event.
            if (checkHitbox(e, c)) {
                LOG.debug("Click event sent to {} (Clickable)", c.toString());
                c.onClick(e);
                flags.isActive = true;
            }
            // if outside the hitbox and the clickable is "active"
            else if (flags.isActive) {
                c.onBlur();
                flags.isActive = false;
            }
        }
    }

    /**
     * Handles mouse pressed events and passes them to the {@link Clickable}
     *
     * @param e details about the mouse click event. this can be used to get the mouse button pressed, x/y position,
     *          etc.
     */
    protected void onMouseDown(MouseClickEvent e) {
        for (Clickable c: cachedClickables) {
            if (checkHitbox(e, c))
                c.onMouseDown(e);
        }
    }

    /**
     * Runs when the pointer is moved within the scene.
     * <p>
     * This method goes through all attached clickables and, if it is within the clickable's hitbox, runs the
     * {@link Clickable#onMouseEnter} or {@link Clickable#onMouseLeave} event handler, depending on what just happened
     * relative to said Clickable.
     *
     * @param e details about the mouse click event. this can be used to get the mouse button pressed, x/y position,
     *          etc.
     *
     * @see Clickable#onMouseEnter
     * @see Clickable#onMouseLeave
     */
    protected void onMouseMoved(MouseClickEvent e) {
        for (Clickable c: cachedClickables) {
            ClickableFlags flags = clickables.get(c);

            // if the mouse is within the hitbox of the clickable
            if (checkHitbox(e, c) && !flags.isHovering) {
                c.onMouseEnter(e);
                flags.isHovering = true; // mark as active
            }

            // if mouse is outside clickable hitbox
            else if (!checkHitbox(e, c) && flags.isHovering) {
                c.onMouseLeave(e);
                flags.isHovering = false; // mark as not active
            }
        }
    }

    /**
     * Checks the hitbox of the given clickable and sees if the mouse (from event) is in the hitbox of it.
     *
     * @param e the mouseevent
     * @param c the clickable to check against
     *
     * @return true if the cursor is inside the clickable.
     */
    private boolean checkHitbox(MouseClickEvent e, Clickable c) {
        Vec2f p = e.pos();
        return p.x >= c.getX() && p.x <= c.getX() + c.getWidth() &&
               p.y >= c.getY() && p.y <= c.getY() + c.getHeight();
    }
}
