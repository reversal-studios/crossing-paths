package com.logandhillon.fptgame.scene.menu;

import com.logandhillon.fptgame.GameHandler;
import com.logandhillon.fptgame.entity.ui.component.MenuButton;
import com.logandhillon.fptgame.entity.ui.component.MenuModalEntity;
import com.logandhillon.fptgame.networking.proto.ConfigProto;
import com.logandhillon.fptgame.resource.Colors;
import com.logandhillon.fptgame.resource.Sounds;
import com.logandhillon.logangamelib.engine.GameMeta;
import com.logandhillon.logangamelib.entity.Entity;
import com.logandhillon.logangamelib.entity.Renderable;
import com.logandhillon.logangamelib.entity.ui.SliderEntity;
import com.logandhillon.logangamelib.entity.ui.TextEntity;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.util.HashMap;

/**
 * The settings menu allows users to customize audio and gameplay settings
 *
 * @author Jack Ross, Logan Dhillon
 */
public class SettingsMenuContent implements MenuContent {
    private static final Logger LOG = LoggerContext.getContext().getLogger(SettingsMenuContent.class);

    private static final Font HEADER_FONT      = Font.font(GameMeta.get().defaultFont.load(), FontWeight.MEDIUM, 32);
    private static final Font SUBHEADER_FONT   = Font.font(GameMeta.get().defaultFont.load(), FontWeight.MEDIUM, 24);
    private static final Font CONTROLS_FONT    = Font.font(GameMeta.get().defaultFont.load(), FontWeight.MEDIUM, 19);
    private static final Font INSTRUCTION_FONT = Font.font(GameMeta.get().defaultFont.load(), FontWeight.MEDIUM, 20);

    private final MenuHandler                  menu;
    private final Entity[]                     entities;
    /**
     * Map of key bind {@link MenuButton} to their corresponding {@link KeyBind} enum value.
     */
    private final HashMap<KeyBind, MenuButton> KEY_BIND_BUTTONS = new HashMap<>();
    /**
     * List of all key binds that are in use; maps {@link KeyBind} to key code string representation.
     */
    private final HashMap<KeyBind, String>     USED_KEY_BINDS   = new HashMap<>();

    private KeyBind currentKeyBind;
    private String  updateKeyBindStatus = "";

    /**
     * Creates content for settings menu
     *
     * @param menu the {@link MenuHandler} responsible for switching active scenes.
     */
    public SettingsMenuContent(MenuHandler menu) {
        this.menu = menu;
        var config = GameHandler.getUserConfig();

        // populate used keybinds list
        USED_KEY_BINDS.put(KeyBind.LEFT, config.getKeyMoveLeft());
        USED_KEY_BINDS.put(KeyBind.RIGHT, config.getKeyMoveRight());
        USED_KEY_BINDS.put(KeyBind.JUMP, config.getKeyMoveJump());
        USED_KEY_BINDS.put(KeyBind.INTERACT, config.getKeyMoveInteract());

        // volume sliders
        SliderEntity master = new SliderEntity(
                32, 227, 327, 6,
                config.getMasterVolume(),
                v -> {
                    GameHandler.updateUserConfig(
                            ConfigProto.UserConfig.newBuilder().setMasterVolume(v).buildPartial());
                    Sounds.calcVolume();
                });
        SliderEntity music = new SliderEntity(
                32, 296, 327, 6,
                config.getMusicVolume(),
                v -> {
                    GameHandler.updateUserConfig(
                            ConfigProto.UserConfig.newBuilder().setMusicVolume(v).buildPartial());
                    Sounds.calcVolume();
                });
        SliderEntity sfx = new SliderEntity(
                32, 365, 327, 6,
                config.getSfxVolume(),
                v -> {
                    GameHandler.updateUserConfig(
                            ConfigProto.UserConfig.newBuilder().setSfxVolume(v).buildPartial());
                    Sounds.calcVolume();
                });

        // key bind buttons
        KEY_BIND_BUTTONS.put(
                KeyBind.LEFT,
                new MenuButton(config.getKeyMoveLeft(), 259, 457, 100, 40,
                               () -> currentKeyBind = KeyBind.LEFT));
        KEY_BIND_BUTTONS.put(
                KeyBind.RIGHT,
                new MenuButton(config.getKeyMoveRight(), 259, 513, 100, 40,
                               () -> currentKeyBind = KeyBind.RIGHT));
        KEY_BIND_BUTTONS.put(
                KeyBind.JUMP,
                new MenuButton(config.getKeyMoveJump(), 259, 569, 100, 40,
                               () -> currentKeyBind = KeyBind.JUMP));
        KEY_BIND_BUTTONS.put(
                KeyBind.INTERACT,
                new MenuButton(config.getKeyMoveInteract(), 259, 625, 100, 40,
                               () -> currentKeyBind = KeyBind.INTERACT));

        entities = new Entity[]{
                new MenuModalEntity(0, 0, 442, GameHandler.CANVAS_HEIGHT, true, menu),

                // labels/menu headers
                new TextEntity.Builder(32, 66)
                        .setColor(Colors.ACTIVE)
                        .setText("SETTINGS")
                        .setFont(HEADER_FONT)
                        .setBaseline(VPos.TOP)
                        .build(),

                new TextEntity.Builder(32, 140)
                        .setColor(Colors.ACTIVE)
                        .setText("AUDIO")
                        .setFont(SUBHEADER_FONT)
                        .setBaseline(VPos.TOP)
                        .build(),

                new TextEntity.Builder(32, 410)
                        .setColor(Colors.ACTIVE)
                        .setText("CONTROLS")
                        .setFont(SUBHEADER_FONT)
                        .setBaseline(VPos.TOP)
                        .build(),

                new TextEntity.Builder(32, 187)
                        .setColor(Colors.ACTIVE)
                        .setText("MASTER VOLUME")
                        .setFont(CONTROLS_FONT)
                        .setBaseline(VPos.TOP)
                        .build(),

                new TextEntity.Builder(32, 256)
                        .setColor(Colors.ACTIVE)
                        .setText("MUSIC VOLUME")
                        .setFont(CONTROLS_FONT)
                        .setBaseline(VPos.TOP)
                        .build(),

                new TextEntity.Builder(32, 325)
                        .setColor(Colors.ACTIVE)
                        .setText("SFX VOLUME")
                        .setFont(CONTROLS_FONT)
                        .setBaseline(VPos.TOP)
                        .build(),

                new TextEntity.Builder(32, 464.5f)
                        .setColor(Colors.ACTIVE)
                        .setText("MOVE LEFT")
                        .setFont(CONTROLS_FONT)
                        .setBaseline(VPos.TOP)
                        .build(),

                new TextEntity.Builder(32, 520.5f)
                        .setColor(Colors.ACTIVE)
                        .setText("MOVE RIGHT")
                        .setFont(CONTROLS_FONT)
                        .setBaseline(VPos.TOP)
                        .build(),

                new TextEntity.Builder(32, 576.5f)
                        .setColor(Colors.ACTIVE)
                        .setText("JUMP")
                        .setFont(CONTROLS_FONT)
                        .setBaseline(VPos.TOP)
                        .build(),

                new TextEntity.Builder(32, 632.5f)
                        .setColor(Colors.ACTIVE)
                        .setText("INTERACT")
                        .setFont(CONTROLS_FONT)
                        .setBaseline(VPos.TOP)
                        .build(),

                // underlines
                new Renderable(32, 168, (g, x, y) -> {
                    g.setStroke(Colors.ACTIVE);
                    g.setLineWidth(2);
                    g.strokeLine(x, y, x + 70, y);
                }),

                new Renderable(32, 439, (g, x, y) -> {
                    g.setStroke(Colors.ACTIVE);
                    g.setLineWidth(2);
                    g.strokeLine(x, y, x + 113, y);
                }),

                // control buttons
                KEY_BIND_BUTTONS.get(KeyBind.LEFT),
                KEY_BIND_BUTTONS.get(KeyBind.RIGHT),
                KEY_BIND_BUTTONS.get(KeyBind.JUMP),
                KEY_BIND_BUTTONS.get(KeyBind.INTERACT),

                master, music, sfx };
    }

    /**
     * Allows {@link MenuHandler} to access content for this menu
     *
     * @return entity list
     */
    @Override
    public Entity[] getEntities() {
        return entities;
    }

    @Override
    public void onShow() {
        menu.bindHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
    }

    @Override
    public void onRender(GraphicsContext g) {
        if (currentKeyBind != null) {
            g.setFill(Colors.FOREGROUND_TRANS_40);
            g.fillRect(0, 0, GameHandler.CANVAS_WIDTH, GameHandler.CANVAS_HEIGHT);

            g.setFill(Colors.ACTIVE);
            g.setFont(INSTRUCTION_FONT);
            g.setTextAlign(TextAlignment.CENTER);
            g.setTextBaseline(VPos.CENTER);
            g.fillText("PRESS ANY BUTTON", GameHandler.CANVAS_WIDTH / 2f, GameHandler.CANVAS_HEIGHT / 2f);

            if (!updateKeyBindStatus.isEmpty())
                g.fillText(updateKeyBindStatus, GameHandler.CANVAS_WIDTH / 2f, GameHandler.CANVAS_HEIGHT / 2f + 30);
        }
    }

    private enum KeyBind {
        LEFT, RIGHT, JUMP, INTERACT
    }

    private void onKeyPressed(KeyEvent e) {
        if (currentKeyBind == null) return;

        // ignore duplicate keys
        var code = e.getCode().name();
        if (USED_KEY_BINDS.containsValue(code)) {
            updateKeyBindStatus = String.format("'%s' is in-use", code);
            Sounds.playSfx(Sounds.UI_FAIL);
            return;
        }

        LOG.info("Setting {} to {}", currentKeyBind, code);

        switch (currentKeyBind) {
            case LEFT -> GameHandler.updateUserConfig(
                    ConfigProto.UserConfig.newBuilder().setKeyMoveLeft(code).buildPartial());
            case RIGHT -> GameHandler.updateUserConfig(
                    ConfigProto.UserConfig.newBuilder().setKeyMoveRight(code).buildPartial());
            case JUMP -> GameHandler.updateUserConfig(
                    ConfigProto.UserConfig.newBuilder().setKeyMoveJump(code).buildPartial());
            case INTERACT -> GameHandler.updateUserConfig(
                    ConfigProto.UserConfig.newBuilder().setKeyMoveInteract(code).buildPartial());
        }

        Sounds.playSfx(Sounds.UI_CLICK);
        USED_KEY_BINDS.put(currentKeyBind, code); // update map to check duplicates against
        KEY_BIND_BUTTONS.get(currentKeyBind).setText(code); // update btn text
        currentKeyBind = null;
        updateKeyBindStatus = "";
    }
}
