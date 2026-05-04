package com.logandhillon.fptgame.scene.component;

import com.logandhillon.fptgame.entity.ui.component.MenuButton;
import com.logandhillon.fptgame.resource.Colors;
import com.logandhillon.fptgame.scene.menu.MenuContent;
import com.logandhillon.fptgame.scene.menu.MenuHandler;
import com.logandhillon.logangamelib.engine.GameMeta;
import com.logandhillon.logangamelib.entity.Entity;
import com.logandhillon.logangamelib.entity.ui.ModalEntity;
import com.logandhillon.logangamelib.entity.ui.TextEntity;
import javafx.geometry.VPos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * This scene simply shows a modal with a message, and a button to return to the main menu.
 *
 * @author Logan Dhillon, Jack Ross
 */
public class MenuAlertScene implements MenuContent {
    private static final Font TITLE_FONT = Font.font(GameMeta.get().defaultFont.load(), FontWeight.MEDIUM, 20);
    private static final Font BODY_FONT  = Font.font(GameMeta.get().defaultFont.load(), 16);
    private final Entity[] entities;
    public MenuAlertScene(String title, String msg, MenuHandler menu) {
        String t = title.toUpperCase();

        entities = new Entity[]{ new ModalEntity(375, 229.5f, 530, 262),
                new TextEntity.Builder(640, 246).setText(t)
                                               .setFont(TITLE_FONT)
                                               .setColor(Colors.ACTIVE)
                                               .setAlign(TextAlignment.CENTER)
                                               .setBaseline(VPos.TOP)
                                               .build(),
                new TextEntity.Builder(640, 304).setText(msg)
                                               .setFont(BODY_FONT)
                                               .setColor(Colors.ACTIVE)
                                               .setAlign(TextAlignment.CENTER)
                                               .setBaseline(VPos.TOP)
                                               .build(),
                new MenuButton("BACK TO MAIN MENU", 391, 428, 498, 48, () -> menu.getGameHandler().goToMainMenu())};
    }

    @Override
    public Entity[] getEntities() {
        return entities;
    }
}
