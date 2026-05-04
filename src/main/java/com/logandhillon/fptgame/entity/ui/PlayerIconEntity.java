package com.logandhillon.fptgame.entity.ui;

import com.logandhillon.fptgame.resource.Colors;
import com.logandhillon.fptgame.resource.Textures;
import com.logandhillon.fptgame.scene.menu.LobbyGameContent;
import com.logandhillon.logangamelib.engine.GameMeta;
import com.logandhillon.logangamelib.entity.Entity;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;

/**
 * An entity used in {@link LobbyGameContent} to show a graphical representation of a user and their icon
 *
 * @author Jack Ross
 */
public class PlayerIconEntity extends Entity {
    private static final Font FONT     = Font.font(GameMeta.get().defaultFont.load(), 18);
    private static final int  RADIUS   = 49;
    private static final int  DIAMETER = RADIUS * 2;

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
    protected void onRender(GraphicsContext g, float x, float y) {
        g.save(); // saves space around crop
        g.beginPath();

        g.arc(x + 33, y + 58, RADIUS, RADIUS, 0, 360);
        g.clip(); // crops in circle shape

        g.setFill(Colors.ACTIVE_TRANS_015);
        g.fillArc(x - 16, 153, DIAMETER, DIAMETER, 0, 360, ArcType.ROUND); // bg of icon
        Textures.PLAYER_IDLE.draw(g, 0, 0, x, y, 66, 132, Colors.PLAYER_SKINS.get(color)); // draw icon

        g.restore(); // restores space around crop

        // player name
        g.setFill(Colors.ACTIVE);
        g.setFont(FONT);
        g.setTextBaseline(VPos.TOP);
        g.fillText(name, x - 16, y + 119);
    }

    @Override
    public void onUpdate(float dt) {

    }

    @Override
    public void onDestroy() {

    }
}
