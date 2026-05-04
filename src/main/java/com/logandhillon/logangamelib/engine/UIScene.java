package com.logandhillon.logangamelib.engine;

import com.logandhillon.logangamelib.entity.Clickable;
import com.logandhillon.logangamelib.entity.Entity;
import com.logandhillon.logangamelib.entity.ui.Draggable;
import javafx.geometry.Point3D;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * A UI scene is a type of {@link GameScene} that listens to the cursor and registers events handlers for
 * {@link UIScene#onMouseClicked(MouseEvent)}.
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
        this.addMouseEvents(false);
    }

    /**
     * Attaches the UI scene's mouse events to the ref list.
     *
     * @param force if true, will the refs in list. This may be unsafe (duplicate events!) and cause really weird
     *              errors. Ensure safety before calling this.
     */
    public void addMouseEvents(boolean force) {
        this.addHandler(MouseEvent.MOUSE_PRESSED, this::onMousePressed);
        this.addHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClicked);
        this.addHandler(MouseEvent.MOUSE_MOVED, this::onMouseMoved);
        this.addHandler(MouseEvent.MOUSE_RELEASED, this::onMouseRelease);
        this.addHandler(MouseEvent.MOUSE_DRAGGED, this::onMouseDragged);
        if (force) {
            this.bindAllEvents();
        }
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
     * {@link Clickable#onClick(MouseEvent)} event handler.
     *
     * @param e details about the mouse click event. this can be used to get the mouse button pressed, x/y position,
     *          etc.
     *
     * @see MouseEvent
     * @see Clickable#onClick(MouseEvent)
     */
    protected void onMouseClicked(MouseEvent e) {
        float x = (float)e.getX();
        float y = (float)e.getY();

        LOG.debug("{}: Handling mouse click event at ({}, {})", this.getClass().getSimpleName(), x, y);

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
     * @param e javafx mouse event
     *
     * @see MouseEvent#MOUSE_PRESSED
     */
    protected void onMousePressed(MouseEvent e) {
        for (Clickable c: cachedClickables) {
            if (checkHitbox(e, c)) c.onMouseDown(e);
        }
    }

    /**
     * Runs when the mouse is moved within the JavaFX {@link Scene}.
     * <p>
     * This method goes through all attached clickables and, if it is within the clickable's hitbox, runs the
     * {@link Clickable#onMouseEnter(MouseEvent)} or {@link Clickable#onMouseLeave(MouseEvent)} event handler, depending
     * on what just happened relative to said Clickable.
     *
     * @param e details about the mouse click event. this can be used to get the mouse button pressed, x/y position,
     *          etc.
     *
     * @see MouseEvent
     * @see Clickable#onMouseEnter(MouseEvent)
     * @see Clickable#onMouseLeave(MouseEvent)
     */
    protected void onMouseMoved(MouseEvent e) {
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
     * Similar to {@link UIScene#onMouseMoved(MouseEvent)}, but for when the mouse is DOWN, instead of UP.
     *
     * @param e jfx mouse event
     */
    protected void onMouseDragged(MouseEvent e) {
        for (Clickable c: cachedClickables) if (c instanceof Draggable o) o.onMouseDragged(e);
    }

    /**
     * Runs when the mouse is released in a JavaFX {@link Scene}.
     * <p>
     * This method goes through all attached clickables and, if it is within the clickable's hitbox, runs
     * the{@link Draggable#onMouseUp(MouseEvent)} handler
     *
     * @param e details about the mouse click event. this can be used to get the mouse button pressed, x/y position,
     *          etc.
     *
     * @apiNote this event is only used by {@link Draggable}
     * @see MouseEvent
     * @see Draggable#onMouseUp(MouseEvent)
     */
    protected void onMouseRelease(MouseEvent e) {
        for (Clickable c: cachedClickables) if (c instanceof Draggable o) o.onMouseUp(e);
    }

    /**
     * Checks the hitbox of the given clickable and sees if the mouse (from event) is in the hitbox of it.
     *
     * @param e the mouseevent
     * @param c the clickable to check against
     *
     * @return true if the cursor is inside the clickable.
     */
    private boolean checkHitbox(MouseEvent e, Clickable c) {
        Point3D p = e.getPickResult().getIntersectedPoint();
        return p.getX() >= c.getX() && p.getX() <= c.getX() + c.getWidth() &&
               p.getY() >= c.getY() && p.getY() <= c.getY() + c.getHeight();
    }
}
