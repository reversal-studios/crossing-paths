package com.logandhillon.logangamelib.gfx;

import com.logandhillon.fptgame.GameHandler;
import com.logandhillon.logangamelib.entity.Entity;
import com.logandhillon.logangamelib.resource.ImageResource;
import com.logandhillon.logangamelib.api.render.Renderer;
import javafx.scene.image.Image;

import java.io.IOException;

/**
 * A parallax background is an infinite scrolling background where layers move at different speed creating the illusion
 * of depth.
 *
 * @author Logan Dhillon
 * @apiNote this is an entity, and thus should not be referenced statically.
 * @see ParallaxBackground.Layer
 */
public class ParallaxBackground extends Entity {
    private final Layer[] layers;

    /**
     * Creates a new parallax background instance.
     *
     * @param layers layers (with resource name and speed), ordered by z-index
     */
    public ParallaxBackground(Layer... layers) {
        super(0, 0);
        this.layers = layers;
    }

    @Override
    protected void onRender(Renderer g, float x, float y) {
        for (Layer layer: layers) {
            g.drawImage(layer.image, layer.x, y, GameHandler.CANVAS_WIDTH, GameHandler.CANVAS_HEIGHT);
            g.drawImage(
                    layer.image, layer.x + GameHandler.CANVAS_WIDTH, y, GameHandler.CANVAS_WIDTH,
                    GameHandler.CANVAS_HEIGHT);
        }
    }

    @Override
    public void onUpdate(float dt) {
        for (Layer layer: layers) {
            if (layer.x < -GameHandler.CANVAS_WIDTH) layer.x = 0;
            else layer.x -= layer.speed * dt;
        }
    }

    @Override
    public void onDestroy() {

    }

    /**
     * One layer of a {@link ParallaxBackground}, with an image {loaded with a {@link ImageResource} loader} and a
     * variable speed.
     */
    public static final class Layer {
        private final Image image;
        private final float speed;

        private float x = 0;

        /**
         * Loads the provided {@link ImageResource} and initializes the layer with the speed
         *
         * @param name  image file name (as found in the gfx folder)
         * @param speed speed of the layer (px/s)
         */
        public Layer(String name, float speed) {
            try (var res = new ImageResource(name)) {
                this.image = res.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.speed = speed;
        }
    }
}
