package com.logandhillon.fptgame.resource;

import com.logandhillon.logangamelib.gfx.AnimationSequence;
import com.logandhillon.logangamelib.gfx.AtlasTile;
import com.logandhillon.logangamelib.gfx.ParallaxBackground;
import com.logandhillon.logangamelib.gfx.TextureAtlas;
import com.logandhillon.logangamelib.resource.ImageResource;
import javafx.scene.image.Image;

import java.io.IOException;

/**
 * References to static textures
 *
 * @author Logan Dhillon
 */
public class Textures {
    public static final int OBJ_SCALE = 40;

    public static final TextureAtlas PLAYER_IDLE      = TextureAtlas.load("player/idle.png");
    public static final TextureAtlas PLAYER_RUN_LEFT  = TextureAtlas.load("player/run_left.png");
    public static final TextureAtlas PLAYER_RUN_RIGHT = TextureAtlas.load("player/run.png");
    public static final TextureAtlas PLAYER_JUMP      = TextureAtlas.load("player/jump.png");

    public static final TextureAtlas UNDERGROUND        = TextureAtlas.load("theme/underground/spritesheet.png");
    public static final AtlasTile    UNDERGROUND_BRICKS = new AtlasTile(UNDERGROUND, 5, 1);
    public static final AtlasTile    UNDERGROUND_PIPE   = new AtlasTile(UNDERGROUND, 15, 7);
    public static final AtlasTile    UNDERGROUND_BG     = new AtlasTile(UNDERGROUND, 11, 11);

    public static final TextureAtlas      PORTAL      = TextureAtlas.load("portal.png");
    public static final AnimationSequence PORTAL_ANIM = new AnimationSequence(PORTAL, 4,
                                                                              0, 0,
                                                                              1, 0,
                                                                              2, 0,
                                                                              0, 1,
                                                                              1, 1,
                                                                              2, 1);

    public static final AnimationSequence ANIM_PLAYER_IDLE = new AnimationSequence(PLAYER_IDLE, 2,
                                                                                   0, 0,
                                                                                   1, 0,
                                                                                   2, 0,
                                                                                   1, 0);

    public static final AnimationSequence ANIM_PLAYER_RUN_LEFT = new AnimationSequence(PLAYER_RUN_LEFT, 6,
                                                                                       5, 2,
                                                                                       4, 2,
                                                                                       3, 2,
                                                                                       2, 2,
                                                                                       1, 2,
                                                                                       0, 2);

    public static final AnimationSequence ANIM_PLAYER_RUN_RIGHT = new AnimationSequence(PLAYER_RUN_RIGHT, 6,
                                                                                        0, 2,
                                                                                        1, 2,
                                                                                        2, 2,
                                                                                        3, 2,
                                                                                        4, 2,
                                                                                        5, 2);

    public static final AnimationSequence ANIM_PLAYER_JUMP = new AnimationSequence(PLAYER_JUMP, 0, 3, 0);

    /**
     * no-op to force the class to load
     */
    public static void init() {}

    /**
     * Generates a new instance of the ocean8 {@link ParallaxBackground}
     */
    public static ParallaxBackground ocean8() {
        return new ParallaxBackground(
                new ParallaxBackground.Layer("bg/ocean8/1.png", 10f),
                new ParallaxBackground.Layer("bg/ocean8/5.png", 5f), // moon is 5
                new ParallaxBackground.Layer("bg/ocean8/2.png", 25f),
                new ParallaxBackground.Layer("bg/ocean8/3.png", 50f),
                new ParallaxBackground.Layer("bg/ocean8/4.png", 80f));
    }

    public static final Image LEVEL_BUTTON;
    public static final Image SETTINGS_ICON;
    public static final Image X_ICON;
    public static final Image REVERSAL_STUDIOS_LOGO;

    static {
        try (var res = new ImageResource("menuicons/cog.png")) {
            SETTINGS_ICON = res.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (var res = new ImageResource("menuicons/x.png")) {
            X_ICON = res.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (var res = new ImageResource("button.png")) {
            LEVEL_BUTTON = res.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (var res = new ImageResource("reversalstudios.png")) {
            REVERSAL_STUDIOS_LOGO = res.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
