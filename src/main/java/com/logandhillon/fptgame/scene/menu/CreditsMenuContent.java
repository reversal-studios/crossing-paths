package com.logandhillon.fptgame.scene.menu;

import com.logandhillon.fptgame.entity.ui.component.MenuButton;
import com.logandhillon.fptgame.resource.Colors;
import com.logandhillon.logangamelib.api.render.TextStyle;
import com.logandhillon.logangamelib.entity.Entity;
import com.logandhillon.logangamelib.entity.ui.ModalEntity;
import com.logandhillon.logangamelib.entity.ui.TextEntity;
import com.logandhillon.logangamelib.resource.TextResource;
import lombok.Getter;

import java.io.IOException;

import static com.logandhillon.fptgame.GameHandler.CANVAS_WIDTH;

/**
 * @author Logan Dhillon, Jack Ross
 */
@SuppressWarnings("ClassCanBeRecord")
public class CreditsMenuContent implements MenuContent {
    @Getter
    private final Entity[] entities;

    private static final String CREDITS;

    public CreditsMenuContent(MenuHandler menu) {
        entities = new Entity[]{
                new ModalEntity(
                        349, 213, 583, 294,
                        new MenuButton(
                                "BACK TO MENU", 16, 230, 551, 48,
                                () -> menu.getGameHandler().goToMainMenu())),
                new TextEntity.Builder(CANVAS_WIDTH / 2f, 229)
                        .setText(CREDITS)
                        .setColor(Colors.ACTIVE)
                        .setFontSize(18)
                        .setJustification(TextStyle.Justification.CENTER)
                        .setVerticalAlignment(TextStyle.VAlign.TOP)
                        .build()
        };
    }

    static {
        // read credits from resources and store them into static
        try (TextResource res = new TextResource("credits.txt")) {
            CREDITS = res.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load credits.txt", e);
        }
    }
}
