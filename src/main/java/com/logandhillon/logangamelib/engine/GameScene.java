package com.logandhillon.logangamelib.engine;

import com.logandhillon.logangamelib.api.input.InputHandler;
import com.logandhillon.logangamelib.api.render.Renderer;
import com.logandhillon.logangamelib.entity.Entity;
import com.logandhillon.logangamelib.entity.physics.CollisionEntity;
import com.logandhillon.logangamelib.javafx.FXInput;
import com.logandhillon.logangamelib.javafx.FXRenderer;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.util.function.Predicate;

import static com.logandhillon.fptgame.GameHandler.*;

/**
 * A GameScene is the lowest-level of the engine; controlling the game's lifecycle, rendering, and creating a game loop.
 * It represents a "scene" of the game, that being a section of the game that is related (e.g. a game level, the main
 * menu, etc.) GameScenes are rendered with {@link GameScene#build(LGLGameHandler)}, which prepares the engine code for
 * the backend, allowing it to be executed and ran.
 *
 * @param <H> expected type of {@link LGLGameHandler} that this game scene shall work with.
 *
 * @author Logan Dhillon
 */
public abstract class GameScene<H extends LGLGameHandler<H>> {
    private static final Logger LOG = LoggerContext.getContext().getLogger(GameScene.class);

    private final EntityManager entities = new EntityManager(this);

    private AnimationTimer lifecycle;
    private H              game;
    private Scene          scene;
    private FXInput        input;

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
        entities.update(dt);
    }

    /**
     * Called every tick to render the scene. This implementation renders all entities.
     *
     * @param renderer the graphical context to render to.
     */
    protected void render(Renderer renderer) {
        entities.render(renderer);
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
        Renderer g = new FXRenderer(canvas.getGraphicsContext2D());
        input = new FXInput(); // construct input before creating entities to prevent NPE

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
                entities.flush();
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

        // bind input handler
        input.attach(scene, canvas);

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
     * Called to discard this scene (i.e., stop its lifecycle, etc.)
     *
     */
    public void discard() {
        LOG.debug("Discarding scene {}", this);

        entities.clear();
        lifecycle.stop();
        input.close();
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

        // centre the canvas (cropped edges are hidden)
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
    public void killEntities() {
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
