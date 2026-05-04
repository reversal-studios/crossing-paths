package com.logandhillon.fptgame.entity.ui.component;

import com.logandhillon.fptgame.resource.Sounds;
import com.logandhillon.fptgame.scene.menu.MenuHandler;
import com.logandhillon.logangamelib.engine.GameMeta;
import com.logandhillon.logangamelib.entity.Clickable;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Button in various menu screens that returns the user to the main menu
 *
 * @author Logan Dhillon
 */
public final class BackButtonEntity extends Clickable {
    private final MenuHandler menu;

    private static final Font  BACK_BTN_FONT  = Font.font(GameMeta.get().defaultFont.load(), 17);
    private static final Color BACK_BTN_COLOR = Color.rgb(189, 197, 251);

    /**
     * Creates a new back button entity
     *
     * @param menu menu scene manger that can set the scene
     */
    public BackButtonEntity(float x, float y, MenuHandler menu) {
        super(x, y, 62, 22);
        this.menu = menu;
    }

    /**
     * Goes to the main menu scene
     *
     * @param e the mouse event provided by JavaFX
     */
    @Override
    public void onClick(MouseEvent e) {
        Sounds.playSfx(Sounds.UI_CLICK);
        // ask the handler to go to main menu, so it can shut down any threads
        menu.getGameHandler().goToMainMenu();
    }

    @Override
    protected void onRender(GraphicsContext g, float x, float y) {
        g.setFont(BACK_BTN_FONT);
        g.setFill(BACK_BTN_COLOR);
        g.setTextBaseline(VPos.TOP);
        g.setTextAlign(TextAlignment.LEFT);
        g.fillText("< BACK", x, y);
    }

    @Override
    public void onUpdate(float dt) {

    }

    @Override
    public void onDestroy() {

    }
}