package com.logandhillon.fptgame.resource;

import com.logandhillon.logangamelib.api.render.Color;

import java.util.List;

/**
 * Contains constants for all {@link javafx.scene.paint.Paint} items (colours, gradients, etc.) that are to be used
 * throughout the game but require continuity.
 *
 * @author Logan Dhillon
 */
public final class Colors {
    public static final Color ACTIVE              = Color.WHITE;
    public static final Color ACTIVE_TRANS_50     = Color.rgb(255, 255, 255, 0.5f);
    public static final Color ACTIVE_TRANS_015    = Color.rgb(255, 255, 255, 0.15f);
    public static final Color FOREGROUND          = Color.BLACK;
    public static final Color FOREGROUND_TRANS_40 = Color.rgb(0, 0, 0, 0.4f);
    public static final Color FOREGROUND_TRANS_50 = Color.rgb(0, 0, 0, 0.5f);

    public static final Color BUTTON_NORMAL = Color.rgb(207, 209, 235);
    public static final Color BUTTON_HOVER  = Color.rgb(75, 150, 249);

    public static final Color PLAYER_RED  = Color.rgb(226, 59, 54);
    public static final Color PLAYER_BLUE = Color.rgb(65, 132, 232);

    /**
     * The colour of the player skin, indexed by the order they appear on the main menu.
     */
    public static final List<Color> PLAYER_SKINS = List.of(PLAYER_RED, PLAYER_BLUE);

    /**
     * no-op to force the class to load
     */
    public static void init() {}
}
