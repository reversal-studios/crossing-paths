package com.logandhillon.fptgame.scene.menu;

import com.logandhillon.fptgame.GameHandler;
import com.logandhillon.fptgame.entity.ui.InputBox;
import com.logandhillon.fptgame.entity.ui.component.MenuButton;
import com.logandhillon.fptgame.entity.ui.component.MenuModalEntity;
import com.logandhillon.fptgame.networking.proto.ConfigProto;
import com.logandhillon.fptgame.resource.Colors;
import com.logandhillon.fptgame.resource.Textures;
import com.logandhillon.logangamelib.engine.GameMeta;
import com.logandhillon.logangamelib.engine.MenuController;
import com.logandhillon.logangamelib.entity.Clickable;
import com.logandhillon.logangamelib.entity.Entity;
import com.logandhillon.logangamelib.entity.ui.ModalEntity;
import com.logandhillon.logangamelib.entity.ui.TextEntity;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import lombok.Getter;

import static com.logandhillon.fptgame.GameHandler.CANVAS_HEIGHT;

/**
 * The main menu allows the user to navigate to other submenus, play or quit the game, and view game branding.
 *
 * @author Logan Dhillon, Jack Ross
 */
public class MainMenuContent implements MenuContent {
    @Getter
    private final        Entity[] entities;
    private static final Font     HEADER_FONT  = Font.font(GameMeta.get().defaultFont.load(), FontWeight.MEDIUM, 40);
    private static final Font     CREDITS_FONT = Font.font(GameMeta.get().defaultFont.load(), FontWeight.MEDIUM, 14);

    private final InputBox userInput;

    /**
     * Creates a new main menu
     *
     * @param menu the main class that can switch scenes, manage connections, etc.
     */
    public MainMenuContent(MenuHandler menu) {
        float x = 30f;
        int dy = 48 + 16; // ∆y per button height
        int y = 448;

        userInput = new InputBox(20, 57, 336, "Player1", "YOUR NAME", 9);
        userInput.setInput(GameHandler.getUserConfig().getName());
        userInput.setOnBlur(() -> GameHandler.updateUserConfig(
                ConfigProto.UserConfig.newBuilder().setName(userInput.getInput()).buildPartial()));

        MenuController controller = new MenuController(
                () -> !userInput.getIsActive(),
                new MenuButton("Host Game", x, y + dy, 256, 48, () -> menu.setContent(new HostGameContent(menu))),

                new MenuButton("Join Game", x, y + 2 * dy, 256, 48, () -> menu.getGameHandler().showJoinGameMenu()),

//                new MenuButton("Level Creator", x, y + 2 * dy, 256, 48, () -> {
//                    throw new IllegalStateException("Level creator does not exist");
//                }),

                new MenuButton(
                        Textures.SETTINGS_ICON, x, y + 3 * dy, 120, 48, 75.84f, 651.17f, 28, 28,
                        () -> menu.setContent(new SettingsMenuContent(menu))),

                new MenuButton(
                        Textures.X_ICON, x + 136, y + 3 * dy, 120, 48, 218, 654, 20, 20,
                        () -> System.exit(0))
        );

        // creates list of entities to be used by menu handler
        entities = new Entity[]{
                new MenuModalEntity(0, 0, 442, CANVAS_HEIGHT, false, menu),

                new ModalEntity(896, 79, 434, 131, 49, userInput),

                new TextEntity.Builder(32, 32)
                        .setColor(Colors.ACTIVE)
                        .setText(GameMeta.get().gameName.toUpperCase())
                        .setFont(HEADER_FONT)
                        .setBaseline(VPos.TOP).build(),

                new Clickable(1209, 672, 59, 22) {

                    @Override
                    public void onClick(MouseEvent e) {
                        menu.setContent(new CreditsMenuContent(menu));
                    }

                    @Override
                    protected void onRender(GraphicsContext g, float x, float y) {
                        g.setFont(CREDITS_FONT);
                        g.setFill(Colors.ACTIVE_TRANS_50);
                        g.setStroke(Colors.ACTIVE_TRANS_50);
                        g.setLineWidth(1);
                        g.strokeLine(x, y + 21, x + 58, y + 21);
                        g.setTextAlign(TextAlignment.LEFT);
                        g.fillText("LICENSE", x, y + 18);
                    }

                    @Override
                    public void onUpdate(float dt) {

                    }

                    @Override
                    public void onDestroy() {

                    }
                }, controller };
    }
}