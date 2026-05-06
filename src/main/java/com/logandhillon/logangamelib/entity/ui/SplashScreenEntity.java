package com.logandhillon.logangamelib.entity.ui;

import com.logandhillon.logangamelib.entity.Entity;
import com.logandhillon.logangamelib.resource.base.LGLAssets;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * @author Logan Dhillon
 */
public class SplashScreenEntity extends Entity {
    private static final float ANIMATION_TIME = 2.5f; // seconds

    private final Image logo;

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
    public SplashScreenEntity(Image logo, float size) {
        super(0, 0); // doesn't use position interally, just do whatever here
        this.logoSize = size;
        this.logo = logo == null ? LGLAssets.LGL_ICON : logo;
    }

    @Override
    protected void onRender(GraphicsContext g, float x, float y) {
        g.setFill(Color.rgb(0, 0, 0, bgAlpha));
        g.fillRect(0, 0, 1280, 720);

        g.setGlobalAlpha(logoAlpha);
        g.drawImage(logo, (1280f - logoSize) / 2, (720f - logoSize) / 2, logoSize, logoSize);
        g.setGlobalAlpha(1.0);
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
