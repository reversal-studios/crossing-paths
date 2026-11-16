package com.logandhillon.logangamelib.engine;

import com.logandhillon.logangamelib.api.input.MouseClickEvent;
import com.logandhillon.logangamelib.entity.Clickable;
import com.logandhillon.logangamelib.entity.Entity;
import com.logandhillon.logangamelib.math.Vec2f;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * A UI scene is a type of {@link GameScene} that listens to the cursor and registers events handlers for
 * {@link MouseClickEvent mouse click events}.
 * <p>
 * UI scenes are used with {@link Clickable} entities. Clickables can only be used in UI scenes.
 *
 * @param <H> expected type of {@link LGLGameHandler} that this game scene shall work with.
 *
 * @author Logan Dhillon
 * @see Clickable
 */
public abstract class UIScene<H extends LGLGameHandler<H>> extends GameScene<H> {
    private static final Logger LOG = LoggerContext.getContext().getLogger(UIScene.class);

    private final HashMap<Clickable, ClickableFlags> clickables       = new HashMap<>();
    private       Clickable[]                        cachedClickables = new Clickable[0];

    private static final class ClickableFlags {
        private boolean isHovering = false;
        private boolean isActive   = false;
    }

    /**
     * Creates a new UI scene and registers the mouse events.
     */
    public UIScene() {
    }

    /**
     * Attaches the UI scene's mouse events to the game scene's
     * {@link com.logandhillon.logangamelib.api.input.InputHandler InputHandler}
     */
    public void addMouseEvents() {
        input().onMouseDown(this::onMouseDown);
        input().onMouseClicked(this::onMouseClicked);
        input().onMouseMoved(this::onMouseMoved);
    }

    @Override
    public void onBuild(Scene scene) {
        this.input().onMouseDown(this::onMouseDown);
    }

    @Override
    public void discard(Scene scene) {
        super.discard(scene);

        // clear stored clickables to avoid memory leaks / stale state
        for (Clickable c: clickables.keySet()) c.onDestroy();
        clickables.clear();
        cachedClickables = new Clickable[0];
    }

    @Override
    public void clearEntities(boolean discard, Predicate<Entity> predicate) {
        super.clearEntities(discard, predicate);

        // safe removal
        for (Iterator<Clickable> it = clickables.keySet().iterator(); it.hasNext(); ) {
            Entity e = it.next();
            if (predicate.test(e)) {
                it.remove(); // safe removal
                if (discard) e.onDestroy();
            }
        }
        LOG.debug("Safely removed all matching Clickables from this UI scene");

        cachedClickables = clickables.keySet().toArray(new Clickable[0]);
        LOG.debug("Reloaded clickable cache");
    }

    /**
     * Works the same as the regular {@link GameScene}, but also appends {@link Clickable} entities to a separate list,
     * that will get special treatment in the scene lifecycle to trigger mouse events.
     *
     * @param e the entity or clickable to append.
     */
    @Override
    public void addEntity(Entity e) {
        super.addEntity(e);
        if (e instanceof Clickable) clickables.put((Clickable)e, new ClickableFlags());
        if (e instanceof Clickable) {
            cachedClickables = clickables.keySet().toArray(new Clickable[0]);
        }
    }

    /**
     * Runs when the mouse is clicked on the JavaFX {@link Scene}.
     * <p>
     * This method goes through all attached clickables and, if it is within the clickable's hitbox, runs the
     * {@link Clickable#onClick} event handler.
     *
     * @param e details about the mouse click event. this can be used to get the mouse button pressed, x/y position,
     *          etc.
     *
     * @see MouseEvent
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
     * Runs when the mouse is moved within the JavaFX {@link Scene}.
     * <p>
     * This method goes through all attached clickables and, if it is within the clickable's hitbox, runs the
     * {@link Clickable#onMouseEnter} or {@link Clickable#onMouseLeave} event handler, depending on what just happened
     * relative to said Clickable.
     *
     * @param e details about the mouse click event. this can be used to get the mouse button pressed, x/y position,
     *          etc.
     *
     * @see MouseEvent
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
