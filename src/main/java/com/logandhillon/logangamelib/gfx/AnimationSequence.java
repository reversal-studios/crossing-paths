package com.logandhillon.logangamelib.gfx;

import com.logandhillon.logangamelib.api.render.Renderer;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

/**
 * @author Logan Dhillon
 */
public class AnimationSequence {
    private static final Logger LOG = LoggerContext.getContext().getLogger(AnimationSequence.class);

    private final int[]        frames;
    private final float        frameTime;
    private final int          maxFrame;
    private final TextureAtlas atlas;
    private final boolean      isInstance;

    private int   frame     = 0;
    private float animTimer = 0; // delta time since last frame

    private AnimationSequence(TextureAtlas atlas, float frameTime, boolean isInstance, int... frames) {
        if (frames.length % 2 != 0) throw new IllegalArgumentException("frame count must be even! (pairs of row, col)");

        this.atlas = atlas;
        this.frames = frames;
        this.frameTime = frameTime;
        this.maxFrame = (frames.length / 2) - 1;
        this.isInstance = isInstance;
    }

    public AnimationSequence(TextureAtlas atlas, int fps, int... frames) {
        // by default make anim sequences illegal and require instance() method
        this(atlas, fps == 0 ? 0 : 1f / fps, false, frames);
    }

    public void nextFrame() {
        if (frameTime == 0) return; // don't do anything if fps==0
        if (!isInstance) throw new IllegalStateException("cannot set the frame of a static AnimationSequence. " +
                                                         "use instance() on the sequence to get a new instance.");
        if (frame < maxFrame) frame++;
        else frame = 0;
        animTimer = 0;
    }

    public void onUpdate(float dt) {
        animTimer += dt;
        if (animTimer >= frameTime) nextFrame();
    }

    public void draw(Renderer g, float x, float y, float w, float h) {
        if (!isInstance) LOG.warn("Drawing from STATIC AnimationSequence, this is likely a mistake");
        atlas.draw(g, frames[frame * 2], frames[frame * 2 + 1], x, y, w, h);
    }

    public void draw(Renderer g, float x, float y, float w, float h, Color color) {
        if (!isInstance) LOG.warn("Drawing recolored from STATIC AnimationSequence, this is likely a mistake");
        drawFrame(g, this.frame, x, y, w, h, color);
    }

    public void drawFrame(Renderer g, int frame, float x, float y, float w, float h, Color color) {
        atlas.draw(g, frames[frame * 2], frames[frame * 2 + 1], x, y, w, h, color);
    }

    public AnimationSequence instance() {
        return new AnimationSequence(atlas, frameTime, true, frames);
    }
}
