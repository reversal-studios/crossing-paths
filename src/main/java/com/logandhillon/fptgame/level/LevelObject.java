package com.logandhillon.fptgame.level;

import com.logandhillon.fptgame.networking.proto.LevelProto;
import com.logandhillon.logangamelib.entity.GameObject;
import com.logandhillon.logangamelib.entity.physics.CollisionEntity;
import com.logandhillon.logangamelib.io.ProtoSerializable;

/**
 * A {@link GameObject} that may be serialized via {@link ProtoSerializable} to a {@link LevelProto.LevelObject}.
 *
 * @author Logan Dhillon
 * @implNote it is recommended to create a {@code load(LevelProto.LevelObject)} static method to convert Messages back
 * into a {@link LevelProto.LevelObject}.
 */
public abstract class LevelObject extends CollisionEntity implements ProtoSerializable<LevelProto.LevelObject> {
    /**
     * Creates a collidable entity at the specified position with the specified hitbox
     *
     * @param x x-position (from left)
     * @param y y-position (from top)
     * @param w width of hitbox
     * @param h height of hitbox
     */
    public LevelObject(float x, float y, float w, float h) {
        super(x, y, w, h);
    }
}
