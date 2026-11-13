package com.logandhillon.fptgame.entity.ui;

import com.logandhillon.fptgame.resource.Colors;
import com.logandhillon.fptgame.resource.Textures;
import com.logandhillon.fptgame.scene.menu.LobbyGameContent;
import com.logandhillon.logangamelib.api.render.Font;
import com.logandhillon.logangamelib.api.render.Renderer;
import com.logandhillon.logangamelib.api.render.Shape;
import com.logandhillon.logangamelib.api.render.TextStyle;
import com.logandhillon.logangamelib.engine.GameMeta;
import com.logandhillon.logangamelib.entity.Entity;

/**
 * An entity used in {@link LobbyGameContent} to show a graphical representation of a user and their icon
 *
 * @author Jack Ross
 */
public class PlayerIconEntity extends Entity {
    private static final Font      FONT       = new Font(GameMeta.get().defaultFont.load(), 18);
    private static final int       RADIUS     = 49;
    private static final TextStyle NAME_STYLE = new TextStyle(
            Colors.ACTIVE, TextStyle.Justification.LEFT, TextStyle.VAlign.TOP, 100);

    private final String name;
    private final int    color;

    /**
     * Creates an entity at the specified position.
     *
     * @param x     x-position (from left)
     * @param y     y-position (from top)
     * @param color color of player's icon
     */
    public PlayerIconEntity(String name, float x, float y, int color) {
        super(x, y);
        this.name = name.toUpperCase();
        this.color = color;
    }

    @Override
    protected void onRender(Renderer g, float x, float y) {
        // crops in circle shape
        g.clip(
                new Shape.Circle(x + 33, y + 58, RADIUS), () -> {
                    g.fill(new Shape.Circle(x +33, y+58, RADIUS), Colors.ACTIVE_TRANS_015); // bg of icon
                    Textures.PLAYER_IDLE.draw(g, 0, 0, x, y, 66, 132, Colors.PLAYER_SKINS.get(color));
                });

        // player name
        g.text(FONT, name, x - 16, y + 119, NAME_STYLE);
    }

    @Override
    public void onUpdate(float dt) {

    }

    @Override
    public void onDestroy() {

    }
}
