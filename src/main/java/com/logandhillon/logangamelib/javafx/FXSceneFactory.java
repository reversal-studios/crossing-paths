package com.logandhillon.logangamelib.javafx;

import com.logandhillon.logangamelib.api.platform.Window;
import com.logandhillon.logangamelib.api.render.Renderer;
import com.logandhillon.logangamelib.api.scene.SceneFactory;
import com.logandhillon.logangamelib.api.scene.SceneHandle;
import com.logandhillon.logangamelib.scene.GameScene;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * JavaFX implementation of the scene backend.
 *
 * @author Logan Dhillon
 * @see GameScene
 * @see SceneHandle
 */
public final class FXSceneFactory implements SceneFactory<Scene> {
    @Override
    public SceneHandle<Scene> build(GameScene<?> scene, Window config) {
        // other used apis
        Canvas canvas = new Canvas(config.width(), config.height());
        Renderer renderer = new FXRenderer(canvas.getGraphicsContext2D());
        FXInput input = new FXInput();

        Group content = new Group(canvas);
        StackPane root = new StackPane(content);
        root.setBackground(Background.fill(Color.BLACK));
        Scene surface = new Scene(root, config.width(), config.height());

        // listen for surface dimension changes and update
        surface.widthProperty().addListener((obs, oldValue, newValue) -> updateScale(surface, content, config));
        surface.heightProperty().addListener((obs, oldValue, newValue) -> updateScale(surface, content, config));

        input.attach(surface, canvas);
        // try starting scene and handle failure
        try {
            scene.start(input);
        } catch (RuntimeException | Error ex) {
            try {
                scene.stop();
            } catch (RuntimeException | Error ex2) {
                ex.addSuppressed(ex2);
            } finally {
                input.close();
            }
            throw ex;
        }

        // start scene lifecycle
        AnimationTimer lifecycle = new AnimationTimer() {
            private long lastTime;

            @Override
            public void handle(long now) {
                if (lastTime == 0) lastTime = now;
                float dt = (now - lastTime) / 1_000_000_000f;
                lastTime = now;

                scene.update(dt);
                scene.render(renderer);
            }
        };

        FXSceneHandle handle = new FXSceneHandle(surface, lifecycle, input, scene);
        // try starting lifecycle and handle failure
        try {
            lifecycle.start();
        } catch (RuntimeException | Error ex) {
            try {
                handle.close();
            } catch (RuntimeException | Error ex2) {
                ex.addSuppressed(ex2);
            }
            throw ex;
        }
        return handle;
    }

    /**
     * Updates the scale of a {@link Group} to match the scale of an FX {@link Scene}.
     *
     * @param scene   FX scene to get new scale from
     * @param content content to rescale
     * @param config  window config for aspect ratio and scaling tolerance
     */
    private static void updateScale(Scene scene, Group content, Window config) {
        float w = (float)scene.getWidth();
        float h = (float)scene.getHeight();
        if (w <= 0 || h <= 0) return;
        float aspect = w / h;
        float targetAspect = config.aspectRatio();

        float scale;
        if (aspect > targetAspect * (1 + config.scalingTolerance())) {
            scale = h / config.height();
        } else if (aspect < targetAspect * (1 - config.scalingTolerance())) {
            scale = w / config.width();
        } else {
            scale = Math.max(w / config.width(), h / config.height());
        }

        content.setScaleX(scale);
        content.setScaleY(scale);
        content.setLayoutX((w - config.width() * scale) / 2);
        content.setLayoutY((h - config.height() * scale) / 2);
    }

    /**
     * JavaFX implementation of the scene handle.
     *
     * @see SceneHandle
     */
    private static final class FXSceneHandle implements SceneHandle<Scene> {
        private final Scene          surface;
        private final AnimationTimer lifecycle;
        private final FXInput        input;
        private final GameScene<?>   scene;

        private boolean closed;

        /**
         * Creates a new JavaFX scene handle
         *
         * @param surface   FX scene containing renderable canvas and lifecycle
         * @param lifecycle FX lifecycle that continously runs {@link GameScene#update} and {@link GameScene#render}
         * @param input     scene input listener/handler
         * @param scene     instantiated logangamelib scene
         */
        private FXSceneHandle(Scene surface, AnimationTimer lifecycle, FXInput input, GameScene<?> scene) {
            this.surface = surface;
            this.lifecycle = lifecycle;
            this.input = input;
            this.scene = scene;
        }

        @Override
        public Scene surface() {
            return surface;
        }

        @Override
        public void close() {
            if (closed) return;
            closed = true;
            lifecycle.stop();
            try {
                scene.stop();
            } finally {
                input.close();
            }
        }
    }
}
