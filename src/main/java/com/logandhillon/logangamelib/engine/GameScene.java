package com.logandhillon.logangamelib.engine;

import com.logandhillon.logangamelib.entity.Entity;
import com.logandhillon.logangamelib.entity.physics.CollisionEntity;
import javafx.animation.AnimationTimer;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.util.*;
import java.util.function.Predicate;

import static com.logandhillon.fptgame.GameHandler.*;

/**
 * A GameScene is the lowest-level of the engine; controlling the game's lifecycle, rendering, and creating a game loop.
 * It represents a "scene" of the game, that being a section of the game that is related (e.g. a game level, the main
 * menu, etc.) GameScenes are rendered with {@link GameScene#build(LGLGameHandler)}, which prepares the engine code for
 * JavaFX, allowing it to be executed and ran.
 *
 * @param <H> expected type of {@link LGLGameHandler} that this game scene shall work with.
 *
 * @author Logan Dhillon
 */
public abstract class GameScene<H extends LGLGameHandler<H>> {
    private static final Logger LOG = LoggerContext.getContext().getLogger(GameScene.class);

    private final List<Entity>          entities          = new ArrayList<>();
    private final Queue<Entity>         deathrow          = new LinkedList<>();
    private final List<CollisionEntity> collisionEntities = new ArrayList<>();
    private final List<HandlerRef<?>>   handlers          = new ArrayList<>();

    private AnimationTimer lifecycle;
    private H              game;
    private Scene          scene;

    public record HandlerRef<T extends Event>(EventType<T> type, EventHandler<? super T> handler) {}

    /**
     * Do not instantiate this class.
     *
     * @see GameScene#build(LGLGameHandler)
     */
    protected GameScene() {}

    /**
     * Called every tick for non-graphics-related updates (Entity lifecycle, etc.) This implementation updates all
     * entities.
     */
    protected void onUpdate(float dt) {
        for (Entity e: entities)
            e.onUpdate(dt);
    }

    /**
     * Called every tick to render the scene. This implementation renders all entities.
     *
     * @param g the graphical context to render to.
     */
    protected void render(GraphicsContext g) {
        for (Entity e: entities) {
            g.save();
            e.render(g);
            g.restore();
        }
    }

    /**
     * Creates a new JavaFX Scene for this GameScene. This should only be called once, as this method creates a new
     * Scene every time.
     *
     * @return Scene containing the GameScene's GUI elements
     */
    public Scene build(H game) {
        LOG.debug("Building game scene {} to stage", this);

        Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        GraphicsContext g = canvas.getGraphicsContext2D();

        // use one-element array as address cannot change once anonymously passed to lifecycle
        final long[] lastTime = { 0 };

        lifecycle = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTime[0] == 0) lastTime[0] = now; // set initial value to now
                float dt = (now - lastTime[0]) / 1_000_000_000f; // nanoseconds to seconds
                lastTime[0] = now;

                onUpdate(dt);
                render(g);

                // kill entities on deathrow AFTER all updates have finished
                while (!deathrow.isEmpty()) {
                    Entity e = deathrow.poll();
                    if (!entities.remove(e)) continue;
                    if (e instanceof CollisionEntity ce) collisionEntities.remove(ce);
                    e.onDestroy();
                }
            }
        };
        lifecycle.start();

        Group parent = new Group(canvas);
        StackPane root = new StackPane(parent);
        root.setBackground(Background.fill(Color.BLACK));
        scene = new Scene(root, CANVAS_WIDTH, CANVAS_HEIGHT);

        // update scaling
        scene.widthProperty().addListener((obs, oldVal, newVal) -> updateScale(scene, parent));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> updateScale(scene, parent));

        // register all event handlers
        bindAllEvents();
        onBuild(scene);
        this.game = game;
        return scene;
    }

    /**
     * Abstract method to run code during the build process
     *
     * @param scene JAVAFX SCENE from engine
     */
    public void onBuild(Scene scene) {
    }

    /**
     * Without checking for safety, immediately binds all events to the scene.
     *
     * @apiNote You must ensure it is safe to call this method before using it!!!!
     */
    public void bindAllEvents() {
        if (scene == null) throw new IllegalStateException(
                "GameScene not bound to engine yet, thus cannot bind events");

        LOG.info("Binding {} events to engine", handlers.size());
        for (HandlerRef<?> h: handlers) {
            @SuppressWarnings("unchecked") EventType<Event> t = (EventType<Event>)h.type();
            @SuppressWarnings("unchecked") EventHandler<Event> eh = (EventHandler<Event>)h.handler();

            scene.addEventHandler(t, eh);
        }
    }

    /**
     * Called to discard this scene (i.e., stop its lifecycle, etc.)
     *
     * @param scene the JavaFX scene that is this scene is being removed from. use this for detaching events, etc.
     */
    public void discard(Scene scene) {
        LOG.debug("Discarding scene {}", this);

        // schedule all entities for destruction
        for (Entity e: entities) e.onDestroy();
        entities.clear();

        lifecycle.stop();

        // remove all stored event handlers
        clearAllHandlers();
    }

    /**
     * Updates the scaling of the canvas wrapper (parent) based on the dimensions of the window (scene)
     *
     * @param scene  the {@link Scene} that contains the canvas from {@link GameScene#build(LGLGameHandler)}
     * @param parent the parent of the canvas (not the canvas itself) that has the content
     */
    private void updateScale(Scene scene, Group parent) {
        float windowWidth = (float)scene.getWidth();
        float windowHeight = (float)scene.getHeight();

        float scale = calculateScale(windowWidth, windowHeight);

        parent.setScaleX(scale);
        parent.setScaleY(scale);

        // center the canvas (cropped edges are hidden)
        parent.setLayoutX((windowWidth - CANVAS_WIDTH * scale) / 2);
        parent.setLayoutY((windowHeight - CANVAS_HEIGHT * scale) / 2);
    }

    /**
     * Calculates the scale factor for the canvas based on the window height and width
     *
     * @param w window width
     * @param h window height
     *
     * @return scale factor
     *
     * @see GameScene#updateScale(Scene, Group)
     */
    private static float calculateScale(float w, float h) {
        float currentAspect = w / h;
        float scale;

        if (currentAspect > ASPECT_RATIO * (1 + SCALING_TOLERANCE)) {
            // slightly wider than target, allow cropping horizontally
            scale = h / CANVAS_HEIGHT;
        } else if (currentAspect < ASPECT_RATIO * (1 - SCALING_TOLERANCE)) {
            // slightly taller than target, allow cropping vertically
            scale = w / CANVAS_WIDTH;
        } else {
            // within tolerance; scale uniformly
            scale = Math.max(w / CANVAS_WIDTH, h / CANVAS_HEIGHT);
        }
        return scale;
    }

    /**
     * Adds a new entity to the scene, that will be rendered/updated every tick.
     *
     * @param e the entity to append.
     */
    public void addEntity(Entity e) {
        entities.add(e);
        if (e instanceof CollisionEntity) collisionEntities.add((CollisionEntity)e);
        e.onAttach(this);
    }

    /**
     * Removes all entities from this modal that match the predicate
     *
     * @param discard   if the entities should also be discarded (and trigger {@link Entity#onDestroy()}
     * @param predicate the predicate to only remove entities that match it.
     */
    public void clearEntities(boolean discard, Predicate<Entity> predicate) {
        for (Iterator<Entity> it = entities.iterator(); it.hasNext(); ) {
            var e = it.next();
            if (predicate.test(e)) {
                it.remove(); // safe removal
                if (discard) e.onDestroy();
            }
        }
        LOG.debug("Successfully removed all matching entities from this scene");

        for (Iterator<CollisionEntity> it = collisionEntities.iterator(); it.hasNext(); ) {
            var e = it.next();
            if (predicate.test(e)) {
                it.remove(); // safe removal
                if (discard) e.onDestroy();
            }
        }
        LOG.debug("Successfully removed all matching collision entities from this scene");
    }

    /**
     * Removes the given entity from the scene
     */
    public void killEntity(Entity entity) {
        if (entity == null) throw new NullPointerException("Cannot kill null entity");
        if (deathrow.contains(entity)) {
            LOG.warn("{} already on deathrow", entity);
            return;
        }

        deathrow.add(entity);
    }

    /**
     * Checks for a collision between an entity and a hitbox
     *
     * @param x x pos of hitbox
     * @param y y pos of hitbox
     * @param w width of hitbox
     * @param h height of hitbox
     * @param e other entity to check against
     *
     * @return true if they are colliding
     */
    public boolean checkCollision(float x, float y, float w, float h, CollisionEntity e) {
        return x < e.getX() + e.getWidth() &&
               x + w > e.getX() &&
               y < e.getY() + e.getHeight() &&
               y + h > e.getY();
    }

    /**
     * Checks if entity A is colliding with entity B
     *
     * @param a entity 1
     * @param b entity 2
     *
     * @return is collision happening
     */
    public boolean checkCollision(CollisionEntity a, CollisionEntity b) {
        return a.getX() < b.getX() + b.getWidth() &&
               a.getX() + a.getWidth() > b.getX() &&
               a.getY() < b.getY() + b.getHeight() &&
               a.getY() + a.getHeight() > b.getY();
    }

    /**
     * Checks if an entity is colliding with ANY other entity, given a predicate
     *
     * @param caller    entity to check collisions for
     * @param predicate when to consider a collision valid
     *
     * @return entity that caller is colliding with, or null
     *
     * @see GameScene#checkCollision(CollisionEntity, CollisionEntity)
     */
    public CollisionEntity getEntityCollision(CollisionEntity caller, Predicate<CollisionEntity> predicate) {
        for (CollisionEntity e: collisionEntities) {
            if (e == caller) continue; // skip the caller
            if (predicate.test(e) && checkCollision(caller, e)) return e; // return if collision is found
        }
        return null; // no collision found
    }

    /**
     * Checks a given (x,y) and size for collisions and returns the entity if there is one
     *
     * @param caller the entity that is checking for collisions (can be null)
     *
     * @return entity that target is colliding with, or null
     *
     * @see GameScene#checkCollision(CollisionEntity, CollisionEntity)
     */
    public CollisionEntity getCollisionAt(float x, float y, float w, float h, CollisionEntity caller) {
        for (CollisionEntity e: collisionEntities) {
            if (e == caller) continue; // skip the caller
            if (checkCollision(x, y, w, h, e)) return e;
        }
        return null; // no collision found
    }

    /**
     * Checks a given (x,y) and size for collisions and returns the entity if there is one that also matches the
     * predicate
     *
     * @param caller          the entity that is checking for collisions (can be null)
     * @param entityPredicate only check for collisions against entities that match this predicate
     *
     * @return entity that target is colliding with, or null
     *
     * @see GameScene#checkCollision(CollisionEntity, CollisionEntity)
     */
    public CollisionEntity getCollisionIf(float x, float y, float w, float h, CollisionEntity caller,
                                          Predicate<CollisionEntity> entityPredicate) {
        for (CollisionEntity e: collisionEntities) {
            if (e == caller) continue; // skip the caller
            if (entityPredicate.test(e) && checkCollision(x, y, w, h, e)) return e;
        }
        return null; // no collision found
    }

    /**
     * Registers an event handler that will be attached to the scene when it is built.
     *
     * @param type    the type of event to fire on
     * @param handler the event handler itself (the method that will run)
     */
    public <T extends Event> void addHandler(EventType<T> type, EventHandler<? super T> handler) {
        handlers.add(new HandlerRef<>(type, handler));
    }

    /**
     * Binds an event handler to the scene when the scene is built
     *
     * @param type    the type of event to fire on
     * @param handler the event handler itself (the method that will run)
     */

    public <T extends Event> void bindHandler(EventType<T> type, EventHandler<? super T> handler) {
        this.addHandler(type, handler);
        scene.addEventHandler(type, handler);
    }

    /**
     * Safely unregister and delete all handlers
     */
    public void clearAllHandlers() {
        if (scene == null) {
            LOG.warn("Master scene not found, cannot unregister handlers.");
            return;
        }

        LOG.info("Unregistering all registered handlers from this GameScene");
        // remove all stored event handlers
        for (HandlerRef<?> h: handlers) {
            @SuppressWarnings("unchecked") EventType<Event> t = (EventType<Event>)h.type();
            @SuppressWarnings("unchecked") EventHandler<Event> eh = (EventHandler<Event>)h.handler();

            scene.removeEventHandler(t, eh);
        }
        handlers.clear();
    }

    protected H getParent() {
        return game;
    }
}
