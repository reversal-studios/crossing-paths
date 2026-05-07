package com.logandhillon.fptgame.level;

import com.logandhillon.fptgame.GameHandler;
import com.logandhillon.fptgame.entity.game.LevelButtonEntity;
import com.logandhillon.fptgame.entity.game.MovingPlatformEntity;
import com.logandhillon.fptgame.entity.game.PlatformEntity;
import com.logandhillon.fptgame.entity.game.PortalEntity;
import com.logandhillon.fptgame.networking.proto.LevelProto;
import com.logandhillon.logangamelib.entity.Renderable;
import com.logandhillon.logangamelib.gfx.AtlasTile;

import java.util.List;

import static com.logandhillon.fptgame.resource.Textures.OBJ_SCALE;

/**
 * The level factory is responsible for building protobuf types (i.e.
 * {@link com.logandhillon.fptgame.networking.proto.LevelProto.LevelObject}) into valid entities (i.e.
 * {@link LevelObject}).
 *
 * @author Logan Dhillon
 */
public class LevelFactory {
    /**
     * Loads level object data into a level object entity
     *
     * @param msg message data
     *
     * @return entity
     */
    public static LevelObject loadObject(LevelProto.LevelObject msg) {
        return switch (msg.getDataCase()) {
            case PLATFORM -> PlatformEntity.load(msg);
            case PORTAL -> PortalEntity.load(msg);
            case MOVING_PLATFORM -> MovingPlatformEntity.load(msg);
            case LEVEL_BUTTON -> LevelButtonEntity.load(msg);
            default -> throw new IllegalStateException("Illegal LevelObject type");
        };
    }

    /**
     * Loads the entire level data into a list of level object entities
     *
     * @param data raw level data
     *
     * @return level object entities list
     */
    public static List<LevelObject> load(LevelProto.LevelData data) {
        return data.getObjectsList().stream().map(LevelFactory::loadObject).toList();
    }

    /**
     * If the level data contains a background, this creates a new {@link Renderable} entity that contains the
     * background.
     *
     * @param data raw level data (that may contain background)
     *
     * @return null (no background) or background renderable entity
     */
    public static Renderable buildBgOrNull(LevelProto.LevelData data) {
        if (!data.hasBackground()) return null;
        AtlasTile tile = AtlasTile.load(data.getBackground());
        return new Renderable(
                0, 0, (g, x, y) -> {
            for (int i = 0; i < GameHandler.CANVAS_WIDTH / OBJ_SCALE; i++) {
                for (int j = 0; j < GameHandler.CANVAS_HEIGHT / OBJ_SCALE; j++) {
                    tile.draw(g, i * OBJ_SCALE, j * OBJ_SCALE, OBJ_SCALE, OBJ_SCALE);
                }
            }
        });
    }
}
