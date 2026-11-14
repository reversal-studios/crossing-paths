package com.logandhillon.fptgame.scene;

import com.logandhillon.fptgame.GameHandler;
import com.logandhillon.fptgame.entity.game.MovingPlatformEntity;
import com.logandhillon.fptgame.entity.game.PlatformEntity;
import com.logandhillon.fptgame.level.LevelFactory;
import com.logandhillon.fptgame.level.LevelObject;
import com.logandhillon.fptgame.networking.proto.LevelProto;
import com.logandhillon.fptgame.resource.Sounds;
import com.logandhillon.fptgame.scene.menu.MenuHandler;
import com.logandhillon.logangamelib.engine.GameScene;
import com.logandhillon.logangamelib.entity.Renderable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import com.logandhillon.logangamelib.api.render.Color;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.util.ArrayList;

/**
 * A generic level scene contains underlying common methods between the {@link SingleplayerGameScene} and
 * {@link DynamicLevelScene}. Anything displaying {@link com.logandhillon.fptgame.networking.proto.LevelProto.LevelData}
 * is expected to be a generic level scene.
 *
 * @author Logan Dhillon
 */
public abstract class LevelScene extends GameScene<GameHandler> {
    private static final Logger LOG = LoggerContext.getContext().getLogger(LevelScene.class);

    private final LevelProto.LevelData            level;
    private final ArrayList<MovingPlatformEntity> movingPlatforms = new ArrayList<>();
    private final ArrayList<PlatformEntity>       platforms       = new ArrayList<>();

    /**
     * if true, this should not attempt to switch levels anymore.
     */
    private boolean isSwitchingLevels;

    public LevelScene(LevelProto.LevelData level) {
        this.level = level;
        this.isSwitchingLevels = false;

        // show the background or a black bg if no background
        Renderable bg = LevelFactory.buildBgOrNull(level);
        if (bg != null) addEntity(bg);
        else addEntity(new Renderable(0, 0, (g, x, y) -> {
            g.rect(0, 0, GameHandler.CANVAS_WIDTH, GameHandler.CANVAS_HEIGHT, Color.BLACK);
        }));

        for (LevelObject obj: LevelFactory.load(level)) {
            addEntity(obj);
            if (obj instanceof PlatformEntity e) platforms.add(e);
            if (obj instanceof MovingPlatformEntity e) movingPlatforms.add(e);
        }

        // restart on R pressed
        addHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.R) restartLevel();
        });
    }

    protected abstract LevelScene build(LevelProto.LevelData level);

    protected void broadcastLevel(LevelProto.LevelData level) {}

    /**
     * Goes to the next level if there is a next level present in the
     * {@link com.logandhillon.fptgame.networking.proto.LevelProto.LevelData}, otherwise returns to the
     * {@link com.logandhillon.fptgame.scene.menu.MainMenuContent}.
     */
    public void nextLevel() {
        if (level.hasNextLevel()) {
            LOG.info("Going to next level");
            setLevel(level.getNextLevel());
        } else {
            LOG.info("No next level in this level, going to main menu");
            getParent().setScene(new MenuHandler());
        }
    }

    public void restartLevel() {
        LOG.info("Restarting level");
        setLevel(level);
    }

    private void setLevel(LevelProto.LevelData level) {
        if (isSwitchingLevels) return;
        isSwitchingLevels = true;

        Sounds.playSfx(Sounds.GAME_START_LEVEL);
        getParent().setScene(this.build(level));
        broadcastLevel(level);
    }

    /**
     * Moves any {@link MovingPlatformEntity} in the level, inverts colors of any {@link PlatformEntity}, etc.; as per
     * what happens when a {@link com.logandhillon.fptgame.entity.game.LevelButtonEntity} is pressed.
     */
    public void onButtonPressed() {
        for (MovingPlatformEntity e: movingPlatforms) e.invertGoingTowardsDest();
        for (PlatformEntity e: platforms) e.invertColor();
    }
}
