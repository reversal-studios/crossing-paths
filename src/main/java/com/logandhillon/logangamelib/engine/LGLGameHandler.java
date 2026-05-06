package com.logandhillon.logangamelib.engine;

import com.logandhillon.logangamelib.engine.disk.PathManager;
import com.logandhillon.logangamelib.entity.ui.SplashScreenEntity;
import com.logandhillon.logangamelib.resource.base.Colors;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * Abstract game handler for the logangamelib game engine.
 *
 * @author Logan Dhillon
 * @implNote this class shall be the entrypoint of your game, handling all primary logic.
 */
public abstract class LGLGameHandler<H extends LGLGameHandler<H>> extends Application {
    private static final Logger LOG = LoggerContext.getContext().getLogger(LGLGameHandler.class);

    private static final int FADE_TIME = 200;

    private static LGLGameHandler<?> instance;
    private static boolean           isRegistered = false;

    private final PathManager pathMgr;

    protected Stage        stage;
    protected GameScene<H> activeScene;
    protected boolean      debugMode;

    public LGLGameHandler() {
        if (!isRegistered) {
            LGLContext.register(this);
            LOG.info("LGLContext canonical game handler bound to {}", this);
            isRegistered = true;
        } else {
            LOG.debug("Non-canonical registration attempt ignored");
        }

        this.pathMgr = new PathManager(this);
        if (getInstance() == null) instance = this;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        // rename thread to shorten logs
        Thread.currentThread().setName("LGL-FX");

        stage.setTitle(GameMeta.get().gameName);
        stage.setOnCloseRequest(e -> {
            LOG.info("Received window close request");
            onShutdown();
            Platform.exit();
        });

        String flag = System.getenv("LGL_DEBUG_MODE");
        this.debugMode = flag != null && flag.equalsIgnoreCase("true");

        // register shutdown hook (handles SIGTERM/crashes)
        Runtime.getRuntime().addShutdownHook(new Thread(LGLContext.getInstance()::onShutdown, "LGL-ShutdownHook"));

        setScene(onStart(stage));
        activeScene.addEntity(new SplashScreenEntity(GameMeta.get().splashIcon, 256));

        stage.show();
    }

    protected static void launchGame(
            Class<? extends LGLGameHandler<?>> handler, GameMeta meta, Runnable bootstrap
    ) {
        meta.register();

        try {
            handler.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new LGLInitializationException("Failed to instantiate game handler class");
        } catch (NoSuchMethodException e) {
            throw new LGLInitializationException(
                    "Specified game handler class does not have a no-argument constructor");
        }

        LOG.info("Bootstrapping logangamelib...");
        bootstrap.run();
        Application.launch(handler);
    }

    /**
     * Runs when this game is launched.
     * <p>
     * Use this method to initialize the {@link Stage} and set up your game.
     */
    protected abstract GameScene<H> onStart(Stage stage);

    /**
     * Runs when this game is shutdown or otherwise closed.
     * <p>
     * Use this method to close/terminate any threads, handle last-minute clean up, etc.
     */
    protected abstract void onShutdown();

    /**
     * @return the name of the game in snake_case.
     */
    public String getGameId() {
        return GameMeta.get().gameName;
    }

    public static LGLGameHandler<?> getInstance() {
        return instance;
    }

    public PathManager getPathManager() {
        return pathMgr;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Tries to return the active scene as the (expected) type, casting it to said type, and returning null if such
     * fails.
     *
     * @param type the expected type of {@link GameScene}
     *
     * @return the active {@link GameScene} if it is the right type, or null if it's not
     */
    public <T extends GameScene<?>> Optional<T> getActiveScene(Class<T> type) {
        if (!type.isInstance(activeScene))
            return Optional.empty();

        return Optional.of(type.cast(activeScene));
    }

    /**
     * Discards the currently active scene and replaces it with the provided one.
     *
     * @param scene the GameScene to switch
     */
    public void setScene(GameScene<H> scene) {
        activeScene = transitionScene(stage, activeScene, scene);
    }

    /**
     * Discards the old {@link GameScene}, builds the new {@link GameScene}, and displays the built scene to the
     * {@link Stage}
     *
     * @param stage    the javafx application stage
     * @param oldScene the previously active scene that will be discarded
     * @param newScene the new scene that will be built and displayed
     *
     * @return a pointer to the {@link GameScene} that is now displayed
     */
    private GameScene<H> transitionScene(Stage stage, GameScene<H> oldScene, GameScene<H> newScene) {
        LOG.info("Switching scene to {}", newScene);

        Scene currentScene = stage.getScene();
        Pane overlayPane = new Pane();
        overlayPane.setPrefSize(stage.getWidth(), stage.getHeight());

        Rectangle fadeRect = new Rectangle(stage.getWidth(), stage.getHeight(), Colors.GENERIC_BG);
        fadeRect.setOpacity(0);
        overlayPane.getChildren().add(fadeRect);

        if (currentScene == null) {
            // no previous scene, just immediately show the new one
            stage.setScene(newScene.build(self()));
            return newScene;
        }

        // add fade rect to current scene
        ((Pane)currentScene.getRoot()).getChildren().add(overlayPane);

        // fade the rect to black
        FadeTransition fadeOut = new FadeTransition(Duration.millis(FADE_TIME), fadeRect);
        fadeOut.setFromValue(0);
        fadeOut.setToValue(1);

        fadeOut.setOnFinished(e -> {
            if (oldScene != null) oldScene.discard(currentScene);

            // Switch scene
            Scene newFxScene = newScene.build(self());
            stage.setScene(newFxScene);

            // put the rect on the new scene too...
            ((Pane)newFxScene.getRoot()).getChildren().add(fadeRect);

            // ...and fade it in
            FadeTransition fadeIn = new FadeTransition(Duration.millis(FADE_TIME), fadeRect);
            fadeIn.setFromValue(1);
            fadeIn.setToValue(0);
            fadeIn.setOnFinished(ev -> {
                // finally , it can be removed!!!
                ((Pane)newFxScene.getRoot()).getChildren().remove(fadeRect);
            });
            fadeIn.play();
        });

        fadeOut.play();

        return newScene;
    }

    @SuppressWarnings("unchecked")
    private H self() {
        return (H)this;
    }

    static {
        // throw errors if required sys properties aren't set, requires further checks as these are just null checks
        if (System.getProperty("LGL_BASE_PATH") == null)
            throw LGLInitializationException.missingProperty("LGL_BASE_PATH");
    }

    public static class LGLInitializationException extends RuntimeException {
        public LGLInitializationException(String msg) {
            super(msg);
        }

        public static LGLInitializationException missingProperty(String property) {
            return new LGLInitializationException("Required system property '" + property + "' is not set!");
        }
    }
}
