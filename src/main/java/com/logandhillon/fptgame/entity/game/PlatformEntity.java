package com.logandhillon.fptgame.entity.game;

import com.logandhillon.fptgame.level.LevelObject;
import com.logandhillon.fptgame.networking.proto.LevelProto;
import com.logandhillon.fptgame.resource.Colors;
import com.logandhillon.logangamelib.gfx.AtlasTile;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.Getter;

import static com.logandhillon.fptgame.resource.Textures.OBJ_SCALE;

/**
 * A platform entity is a static {@link com.logandhillon.logangamelib.entity.physics.CollisionEntity} that is available
 * in {@link com.logandhillon.fptgame.networking.proto.LevelProto.LevelData} and can be added to levels dynamically.
 *
 * @author Logan Dhillon
 */
public class PlatformEntity extends LevelObject {
    protected final AtlasTile        texture;
    @Getter
    protected       LevelProto.Color color;
    private         Color            tint;

    /**
     * Creates a collidable entity at the specified position with the specified hitbox
     *
     * @param texture texture to tile over this platform
     * @param x       x-position (from left)
     * @param y       y-position (from top)
     * @param w       width of hitbox
     * @param h       height of hitbox
     * @param color   (optional) color of platform: changes which player can interact with it.
     */
    public PlatformEntity(AtlasTile texture, float x, float y, float w, float h, LevelProto.Color color) {
        super(x, y, w, h);

        if (w % OBJ_SCALE != 0 || h % OBJ_SCALE != 0)
            throw new IllegalArgumentException("Platform must have a width and height divisible by " + OBJ_SCALE);

        this.texture = texture;
        setColor(color);
    }

    public PlatformEntity(AtlasTile texture, float x, float y, float w, float h) {
        this(texture, x, y, w, h, LevelProto.Color.NONE);
    }

    @Override
    protected void onRender(GraphicsContext g, float x, float y) {
        g.setFill(Colors.FOREGROUND_TRANS_40);
        if (w > h) { // render right
            for (int i = 0; i < w / OBJ_SCALE; i++) {
                texture.draw(g, x + (i * OBJ_SCALE), y, OBJ_SCALE, OBJ_SCALE, tint);
            }
        } else { // render down
            for (int i = 0; i < h / OBJ_SCALE; i++) {
                texture.draw(g, x, y + (i * OBJ_SCALE), OBJ_SCALE, OBJ_SCALE, tint);
            }
        }
    }

    @Override
    public void onUpdate(float dt) {

    }

    @Override
    public void onDestroy() {

    }

    /**
     * Sets this color and recalculates the tint
     */
    public void setColor(LevelProto.Color color) {
        this.color = color;
        this.tint = color == LevelProto.Color.RED ? Colors.PLAYER_RED :
                    color == LevelProto.Color.BLUE ? Colors.PLAYER_BLUE : null;
    }

    /**
     * Inverts the color from RED to BLUE, or does nothing if the color is NONE.
     */
    public void invertColor() {
        if (color == null || color == LevelProto.Color.NONE) return;
        setColor(color == LevelProto.Color.RED ? LevelProto.Color.BLUE : LevelProto.Color.RED);
    }

    @Override
    public LevelProto.LevelObject serialize() {
        return LevelProto.LevelObject
                .newBuilder()
                .setX(x)
                .setY(y)
                .setPlatform(LevelProto.Platform
                                     .newBuilder()
                                     .setH(h)
                                     .setW(w)
                                     .setTexture(texture.serialize())
                                     .setColor(color)
                                     .build())
                .build();
    }

    public static PlatformEntity load(LevelProto.LevelObject msg) {
        return new PlatformEntity(
                AtlasTile.load(msg.getPlatform().getTexture()),
                msg.getX(),
                msg.getY(),
                msg.getPlatform().getW(),
                msg.getPlatform().getH(),
                msg.getPlatform().getColor());
    }
}
