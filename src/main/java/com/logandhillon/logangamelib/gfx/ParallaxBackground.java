package com.logandhillon.logangamelib.gfx;

import com.logandhillon.fptgame.GameHandler;
import com.logandhillon.logangamelib.api.render.Renderer;
import com.logandhillon.logangamelib.api.render.Texture;
import com.logandhillon.logangamelib.api.render.TextureAssets;
import com.logandhillon.logangamelib.entity.Entity;

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
            g.draw(layer.texture, layer.x, y, GameHandler.CANVAS_WIDTH, GameHandler.CANVAS_HEIGHT);
            g.draw(
                    layer.texture, layer.x + GameHandler.CANVAS_WIDTH, y, GameHandler.CANVAS_WIDTH,
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
     * One layer of a {@link ParallaxBackground}, with a {@link Texture} {loaded with
     * {@link TextureAssets#load(String)}} and a variable speed.
     */
    public static final class Layer {
        private final Texture texture;
        private final float   speed;

        private float x = 0;

        /**
         * Loads the provided {@link Texture} and initializes the layer with the speed
         *
         * @param name  image file name (as found in the gfx folder)
         * @param speed speed of the layer (px/s)
         */
        public Layer(String name, float speed) {
            this.texture = TextureAssets.load(name);
            this.speed = speed;
        }
    }
}
