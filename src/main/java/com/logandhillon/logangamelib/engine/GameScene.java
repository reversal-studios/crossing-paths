package com.logandhillon.logangamelib.engine;

import com.logandhillon.logangamelib.api.input.InputHandler;
import com.logandhillon.logangamelib.api.render.Renderer;
import com.logandhillon.logangamelib.entity.Entity;
import com.logandhillon.logangamelib.entity.physics.CollisionEntity;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * A GameScene is the lowest-level of the engine; controlling entities, collision checks, updates, and rendering. It
 * represents a "scene" of the game, that being a section of the game that is related (e.g. a game level, the main menu,
 * etc.). A platform backend runs this scene.
 *
 * @param <H> game-specific handler type associated with this scene.
 *
 * @author Logan Dhillon
 */
public abstract class GameScene<H> {
    private final EntityManager entities = new EntityManager(this);

    private H            game;
    private InputHandler input;
    private boolean      started;
    private boolean      stopped;

    /**
     * Do not instantiate this class.
     */
    protected GameScene() {}

    final void initialize(H game) {
        this.game = game;
    }

    public final void start(InputHandler input) {
        if (started || stopped) throw new IllegalStateException("GameScene can only be started once");
        this.input = Objects.requireNonNull(input, "input");
        started = true;
        onStart();
    }

    public final void update(float dt) {
        if (!started || stopped) return;
        onUpdate(dt);
    }

    public final void render(Renderer renderer) {
        if (!started || stopped) return;
        onRender(renderer);
        entities.flush();
    }

    public final void stop() {
        if (stopped) return;
        stopped = true;
        try {
            entities.clear();
            entities.flush();
        } finally {
            try {
                onStop();
            } finally {
                input = null;
            }
        }
    }

    /**
     * Called once after the backend has prepared the scene and input handler.
     */
    protected void onStart() {}

    /**
     * Called once when the backend stops this scene.
     */
    protected void onStop() {}

    /**
     * Called every tick for non-graphics-related updates (Entity lifecycle, etc.) This implementation updates all
     * entities.
     */
    protected void onUpdate(float dt) {
        entities.update(dt);
    }

    /**
     * Called every tick to render the scene. This implementation renders all entities.
     *
     * @param renderer the graphical context to render to.
     */
    protected void onRender(Renderer renderer) {
        entities.render(renderer);
    }

    /**
     * Adds a new entity to the scene, that will be rendered/updated every tick.
     *
     * @param e the entity to append.
     */
    public void addEntity(Entity e) {
        entities.add(e);
    }

    /**
     * Removes all entities from this scene that match the filter
     *
     * @param filter remove entities that match this filter
     */
    public void killEntities(Predicate<Entity> filter) {
        entities.killIf(filter);
    }

    /**
     * Removes all entities from this scene.
     */
    public void clearEntities() {
        entities.clear();
    }

    /**
     * Removes the given entity from the scene
     */
    public void killEntity(Entity entity) {
        entities.kill(entity);
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
     * Checks if an entity is colliding with ANY other entity, given a filter
     *
     * @param caller entity to check collisions for
     * @param filter when to consider a collision valid
     *
     * @return entity that caller is colliding with, or null
     *
     * @see GameScene#checkCollision(CollisionEntity, CollisionEntity)
     */
    public CollisionEntity getEntityCollision(CollisionEntity caller, Predicate<CollisionEntity> filter) {
        return entities.findCollision(e -> e != caller && filter.test(e) && checkCollision(caller, e));
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
        return entities.findCollision(e -> e != caller && checkCollision(x, y, w, h, e));
    }

    /**
     * Checks a given (x,y) and size for collisions and returns the entity if there is one that also matches the
     * predicate
     *
     * @param caller the entity that is checking for collisions (can be null)
     * @param filter only check for collisions against entities that match this predicate
     *
     * @return entity that target is colliding with, or null
     *
     * @see GameScene#checkCollision(CollisionEntity, CollisionEntity)
     */
    public CollisionEntity getCollisionIf(float x, float y, float w, float h,
                                          CollisionEntity caller, Predicate<CollisionEntity> filter) {
        return entities.findCollision(e -> e != caller && filter.test(e) && checkCollision(x, y, w, h, e));
    }

    protected H getParent() {
        return game;
    }

    public InputHandler input() {
        if (input == null)
            throw new IllegalStateException("Input is only available while the scene is active");
        return input;
    }

    /**
     * Called when an entity is added to the scene.
     *
     * @param e entity that was added
     */
    protected void onEntityAdded(Entity e) {}

    /**
     * Called when an entity is removed from the scene.
     *
     * @param e entity that was removed
     */
    protected void onEntityRemoved(Entity e) {}
}
