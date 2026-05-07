package com.logandhillon.fptgame.entity.game;

import com.logandhillon.fptgame.level.LevelObject;
import com.logandhillon.fptgame.networking.proto.LevelProto;
import com.logandhillon.fptgame.resource.Colors;
import com.logandhillon.logangamelib.gfx.AnimationSequence;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.Getter;

import static com.logandhillon.fptgame.resource.Textures.PORTAL_ANIM;

/**
 * @author Logan Dhillon
 */
public class PortalEntity extends LevelObject {
    private final AnimationSequence anim;
    private final Color   color;
    @Getter
    private final boolean isRed;

    /**
     * Creates a collidable entity at the specified position with the specified hitbox
     *
     * @param x     x-position (from left)
     * @param y     y-position (from top)
     * @param isRed true=red, false=blue
     */
    public PortalEntity(float x, float y, boolean isRed) {
        super(x, y, 50, 100);
        this.anim = PORTAL_ANIM.instance();
        this.isRed = isRed;
        this.color = isRed ? Colors.PLAYER_RED : Colors.PLAYER_BLUE;
    }

    @Override
    protected void onRender(GraphicsContext g, float x, float y) {
        anim.draw(g, x - 32, y, w + 64, h, color);
    }

    @Override
    public void onUpdate(float dt) {
        anim.onUpdate(dt);
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
                .setPortal(LevelProto.Portal.newBuilder()
                                            .setIsRed(isRed)
                                            .build())
                .build();
    }

    public static PortalEntity load(LevelProto.LevelObject msg) {
        return new PortalEntity(msg.getX(), msg.getY(), msg.getPortal().getIsRed());
    }
}
