package com.logandhillon.fptgame.entity.ui;

import com.logandhillon.fptgame.resource.Colors;
import com.logandhillon.fptgame.scene.menu.JoinGameContent;
import com.logandhillon.logangamelib.engine.GameMeta;
import com.logandhillon.logangamelib.entity.ui.ButtonEntity;
import com.logandhillon.logangamelib.entity.ui.DynamicButtonEntity;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import lombok.Setter;

public class ServerEntryEntity extends DynamicButtonEntity {
    private static final Font   ADDRESS_FONT    = Font.font(GameMeta.get().defaultFont.load(), 16);
    private static final double ROUNDING_RADIUS = 38;

    private static final ButtonEntity.Style DEFAULT_STYLE = new ButtonEntity.Style(
            Colors.FOREGROUND, Colors.ACTIVE, ButtonEntity.Variant.SOLID, true,
            Font.font(GameMeta.get().defaultFont.load(), FontWeight.MEDIUM, 16));

    private static final ButtonEntity.Style ACTIVE_STYLE = new ButtonEntity.Style(
            Colors.FOREGROUND, Colors.BUTTON_HOVER, ButtonEntity.Variant.SOLID, true,
            Font.font(GameMeta.get().defaultFont.load(), FontWeight.MEDIUM, 16));

    private String   serverName;
    private String   serverAddress;
    @Setter
    private Runnable onClick;

    public volatile boolean hidden = false;

    /**
     * Creates a new server entry entity
     *
     * @param serverName    the host's server's name
     * @param serverAddress the server's IP address
     * @param w             width
     * @param h             height
     * @param onClick       the action that should happen when this button is clicked by the mouse
     */

    public ServerEntryEntity(float x, float y, float w, float h, String serverName,
                             String serverAddress, Runnable onClick) {
        super(serverName, x, y, w, h, e -> {}, DEFAULT_STYLE, ACTIVE_STYLE);
        this.onClick = onClick;
        this.serverName = serverName;
        this.serverAddress = serverAddress;
    }

    @Override
    public void onClick(MouseEvent e) {
        this.onClick.run();
    }

    /**
     * Renders the button background, then the button text on top of it, based on what was supplied when the button was
     * created.
     *
     * @param g the graphical context to render to.
     * @param x the x position to render the entity at
     * @param y the y position to render the entity at
     */
    @Override
    protected void onRender(GraphicsContext g, float x, float y) {
        if (hidden) return;

        // render button background
        g.setFill(style.buttonColor());
        g.fillRoundRect(x, y, w, h, ROUNDING_RADIUS, ROUNDING_RADIUS);

        // render name
        g.setFill(style.labelColor());
        g.setFont(style.font());
        g.setTextBaseline(VPos.CENTER);
        g.setTextAlign(TextAlignment.LEFT);
        g.fillText(this.serverName, x + 12, y + h / 2);

        // render server address
        g.setFill(Colors.FOREGROUND_TRANS_50);
        g.setFont(ADDRESS_FONT);
        g.fillText(this.serverAddress, x + 216, y + h / 2);
    }

    public void setData(JoinGameContent.ServerEntry data) {
        this.serverName = data.name();
        this.serverAddress = data.address();
    }
}
