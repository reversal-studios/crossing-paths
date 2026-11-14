package com.logandhillon.logangamelib.entity.ui;

import com.logandhillon.logangamelib.api.render.Color;
import com.logandhillon.logangamelib.api.render.Renderer;
import com.logandhillon.logangamelib.api.render.Texture;
import com.logandhillon.logangamelib.entity.Entity;
import com.logandhillon.logangamelib.resource.base.LGLAssets;

/**
 * The splash screen entity displays a logo with a specified size for a certain amount of time, animating the logo to
 * zoom in, then fades out after the time passes.
 * <p>
 * This entity kills itself after expiring.
 *
 * @author Logan Dhillon
 * @apiNote This entity should only be appended by the game handler's post-init system.
 */
public class SplashScreenEntity extends Entity {
    private static final float  ANIMATION_TIME = 2.5f; // seconds
    private static final double CANVAS_W       = 1280;
    private static final double CANVAS_H       = 720;

    private final Texture logo;

    private float logoSize;
    private float animationTimer = 0;
    private float logoAlpha      = 0f;
    private float bgAlpha        = 1f;

    /**
     * Creates a new splash screen
     *
     * @param logo your custom splash logo, if null, uses the logangamelib logo
     * @param size size of logo on splash screen
     */
    public SplashScreenEntity(Texture logo, float size) {
        super(0, 0); // doesn't use position interally, just do whatever here
        this.logoSize = size;
        this.logo = logo == null ? LGLAssets.LGL_ICON : logo;
    }

    @Override
    protected void onRender(Renderer g, float x, float y) {
        g.clear(Color.rgb(0, 0, 0, bgAlpha));

        float aspect = (float)logo.width() / logo.height();
        float w = logoSize;
        float h = logoSize;
        if (aspect >= 1.0) h = logoSize / aspect;
        else w = logoSize * aspect;

        g.draw(logo, (float)((CANVAS_W - w) / 2), (float)((CANVAS_H - h) / 2), w, h, new Color(1, 1, 1, logoAlpha));
    }

    @Override
    public void onUpdate(float dt) {
        logoSize += dt * 30;
        animationTimer += dt;

        if (animationTimer <= 0.5f) {
            logoAlpha = animationTimer / 0.5f;
        } else if (animationTimer >= 1.5f) {
            logoAlpha = 1f - ((animationTimer - 1.5f) / 0.5f);
        }

        if (animationTimer >= ANIMATION_TIME / 2f) {
            float fadeTime = ANIMATION_TIME / 2f;
            bgAlpha = 1f - ((animationTimer - fadeTime) / fadeTime);
        } else {
            bgAlpha = 1f;
        }

        if (logoAlpha < 0f) logoAlpha = 0f;
        if (logoAlpha > 1f) logoAlpha = 1f;
        if (bgAlpha < 0f) bgAlpha = 0f;
        if (bgAlpha > 1f) bgAlpha = 1f;

        if (animationTimer >= ANIMATION_TIME) {
            kill();
        }
    }

    @Override
    public void onDestroy() {

    }
}
