package com.logandhillon.fptgame.entity.ui.component;

import com.logandhillon.fptgame.resource.Sounds;
import com.logandhillon.fptgame.scene.menu.MenuHandler;
import com.logandhillon.logangamelib.api.render.Color;
import com.logandhillon.logangamelib.api.render.Font;
import com.logandhillon.logangamelib.api.render.Renderer;
import com.logandhillon.logangamelib.api.render.TextStyle;
import com.logandhillon.logangamelib.engine.GameMeta;
import com.logandhillon.logangamelib.entity.Clickable;
import javafx.scene.input.MouseEvent;

/**
 * Button in various menu screens that returns the user to the main menu
 *
 * @author Logan Dhillon
 */
public final class BackButtonEntity extends Clickable {
    private final MenuHandler menu;

    private static final Font  BACK_BTN_FONT  = new Font(GameMeta.get().defaultFont.load(), 17);
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
    protected void onRender(Renderer r, float x, float y) {
        r.text(
                BACK_BTN_FONT, "< BACK", x, y,
                new TextStyle(BACK_BTN_COLOR, TextStyle.Justification.LEFT, TextStyle.VAlign.TOP, w));
    }

    @Override
    public void onUpdate(float dt) {

    }

    @Override
    public void onDestroy() {

    }
}