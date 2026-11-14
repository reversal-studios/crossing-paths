package com.logandhillon.logangamelib.entity;

import com.logandhillon.logangamelib.api.render.Renderer;

/**
 * An extremely primitive {@link Entity} that only has a render loop. It can be used for more anonymous contexts, such
 * as creating an entity for only one use case.
 *
 * @author Logan Dhillon
 */
public class Renderable extends Entity {
    private final RenderAgent renderer;

    /**
     * Creates an entity at the specified position.
     *
     * @param x x-position (from left)
     * @param y y-position (from top)
     */
    public Renderable(float x, float y, RenderAgent renderer) {
        super(x, y);
        this.renderer = renderer;
    }

    @Override
    protected void onRender(Renderer g, float x, float y) {
        renderer.render(g, x, y);
    }

    @Override
    public void onUpdate(float dt) {
    }

    @Override
    public void onDestroy() {
    }

    public interface RenderAgent {
        void render(Renderer g, float x, float y);
    }
}
