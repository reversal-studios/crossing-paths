package com.logandhillon.logangamelib.engine;

import com.logandhillon.logangamelib.api.audio.AudioAssets;
import com.logandhillon.logangamelib.api.platform.Dispatcher;
import com.logandhillon.logangamelib.api.platform.Window;
import com.logandhillon.logangamelib.api.render.TextureAssets;
import com.logandhillon.logangamelib.api.scene.SceneFactory;
import com.logandhillon.logangamelib.api.scene.SceneHandle;
import com.logandhillon.logangamelib.entity.ui.SplashScreenEntity;
import com.logandhillon.logangamelib.io.PathManager;
import com.logandhillon.logangamelib.javafx.FXAudioLoader;
import com.logandhillon.logangamelib.javafx.FXSceneFactory;
import com.logandhillon.logangamelib.javafx.FXTextureLoader;
import com.logandhillon.logangamelib.resource.base.Colors;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Abstract game handler for the logangamelib game engine.
 * <p>
 * This class will be the entrypoint of your game, handling all primary logic.
 *
 * @author Logan Dhillon
 */
public abstract class FXGameHandler<H extends FXGameHandler<H>> extends Application implements LGLGameHandler<H> {
    private static final Logger LOG = LoggerContext.getContext().getLogger(FXGameHandler.class);

    private static final int FADE_TIME = 200;

    @Getter
    private static FXGameHandler<?> instance;
    private static boolean          isRegistered = false;

    private final PathManager         pathMgr;
    private final SceneFactory<Scene> sceneFactory = new FXSceneFactory();

    protected Stage              stage;
    protected GameScene<H>       activeScene;
    private   SceneHandle<Scene> activeSceneHandle;

    @Getter
    protected boolean debugMode;

    public FXGameHandler() {
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
        Thread.currentThread().setName("LGL-main");

        stage.setTitle(GameMeta.get().gameName);
        stage.setOnCloseRequest(e -> {
            LOG.info("Received window close request");
            try {
                if (activeSceneHandle != null) activeSceneHandle.close();
            } finally {
                try {
                    onShutdown();
                } finally {
                    Platform.exit();
                }
            }
        });

        String flag = System.getenv("LGL_DEBUG_MODE");
        this.debugMode = flag != null && flag.equalsIgnoreCase("true");

        // register shutdown hook (handles SIGTERM/crashes)
        Runtime.getRuntime().addShutdownHook(new Thread(LGLContext.getInstance()::onShutdown, "LGL-ShutdownHook"));

        Window window = getWindow();
        stage.setWidth(window.width());
        stage.setHeight(window.height());
        if (window.minWidth() > 0) stage.setMinWidth(window.minWidth());
        if (window.minHeight() > 0) stage.setMinHeight(window.minHeight());

        setScene(onStart());
        activeScene.addEntity(new SplashScreenEntity(GameMeta.get().splashIcon, 256));

        stage.show();
    }

    protected static void launchGame(
            Class<? extends Application> handler, Supplier<GameMeta> meta, Runnable bootstrap
    ) {
        AudioAssets.configure(new FXAudioLoader());
        TextureAssets.configure(new FXTextureLoader());
        meta.get().register();

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
     * @return the task dispatcher for work that must run on the game UI thread.
     */
    public final Dispatcher uiDispatcher() {
        return Platform::runLater;
    }

    /**
     * @return the logical canvas configuration used by the active scene.
     */
    @Override
    public Window getWindow() {
        return Window.DEFAULT;
    }

    @Override
    public PathManager getPathManager() {
        return pathMgr;
    }

    /**
     * Tries to return the active scene as the (expected) type, casting it to said type, and returning null if such
     * fails.
     *
     * @param type the expected type of {@link GameScene}
     *
     * @return the active {@link GameScene} if it is the right type, or null if it's not
     */
    @Override
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
    @Override
    public void setScene(GameScene<H> scene) {
        activeScene = transitionScene(stage, scene);
    }

    /**
     * Discards the old {@link GameScene}, builds the new {@link GameScene}, and displays the built scene to the
     * {@link Stage}
     *
     * @param stage    the javafx application stage
     * @param newScene the new scene that will be built and displayed
     *
     * @return a pointer to the {@link GameScene} that is now displayed
     */
    private GameScene<H> transitionScene(Stage stage, GameScene<H> newScene) {
        LOG.info("Switching scene to {}", newScene);

        Scene currentScene = stage.getScene();
        Pane overlayPane = new Pane();
        overlayPane.setPrefSize(stage.getWidth(), stage.getHeight());

        Rectangle fadeRect = new Rectangle(
                stage.getWidth(), stage.getHeight(),
                new javafx.scene.paint.Color(
                        Colors.GENERIC_BG.red(), Colors.GENERIC_BG.green(), Colors.GENERIC_BG.blue(),
                        Colors.GENERIC_BG.alpha()));
        fadeRect.setOpacity(0);
        overlayPane.getChildren().add(fadeRect);

        if (currentScene == null) {
            // no previous scene, just immediately show the new one
            SceneHandle<Scene> newHandle = buildScene(newScene);
            activeSceneHandle = newHandle;
            stage.setScene(newHandle.surface());
            return newScene;
        }

        // add fade rect to current scene
        ((Pane)currentScene.getRoot()).getChildren().add(overlayPane);

        // fade the rect to black
        FadeTransition fadeOut = fadeOut(stage, newScene, fadeRect);
        fadeOut.play();

        return newScene;
    }

    /**
     * Builds a fade out transition to switch to a new game scene.
     *
     * @param stage    stage where transition will take place
     * @param newScene new scene to transition to
     * @param fadeRect FX rect object to fade
     *
     * @return FX transition that switches scenes
     */
    private @NonNull FadeTransition fadeOut(Stage stage, GameScene<H> newScene, Rectangle fadeRect) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(FADE_TIME), fadeRect);
        fadeOut.setFromValue(0);
        fadeOut.setToValue(1);

        fadeOut.setOnFinished(e -> {
            if (activeSceneHandle != null) activeSceneHandle.close();

            // Switch scene
            SceneHandle<Scene> newHandle = buildScene(newScene);
            activeSceneHandle = newHandle;
            Scene newFxScene = newHandle.surface();
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
        return fadeOut;
    }

    private SceneHandle<Scene> buildScene(GameScene<H> scene) {
        scene.initialize(self());
        return sceneFactory.build(scene, getWindow());
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
}
