package com.logandhillon.fptgame.entity.game;

import com.logandhillon.fptgame.level.LevelObject;
import com.logandhillon.fptgame.networking.proto.LevelProto;
import com.logandhillon.fptgame.resource.Textures;
import com.logandhillon.logangamelib.api.render.Renderer;

/**
 * A platformer button, when pressed, inverts {@link PlatformEntity} colors and moves any available
 * {@link MovingPlatformEntity}.
 *
 * @author Logan Dhillon
 */
public class LevelButtonEntity extends LevelObject {
    /**
     * Creates a platformer button
     *
     * @param x x-position (from left)
     * @param y y-position (from top)
     */
    public LevelButtonEntity(float x, float y) {
        super(x, y, 40, 20);
    }

    @Override
    protected void onRender(Renderer g, float x, float y) {
        g.drawImage(Textures.LEVEL_BUTTON, x, y, w, h);
    }

    @Override
    public void onUpdate(float dt) {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public LevelProto.LevelObject serialize() {
        return LevelProto.LevelObject
                .newBuilder()
                .setX(x)
                .setY(y)
                .setLevelButton(LevelProto.LevelButton.newBuilder().build())
                .build();
    }

    public static LevelButtonEntity load(LevelProto.LevelObject msg) {
        return new LevelButtonEntity(msg.getX(), msg.getY());
    }
}
