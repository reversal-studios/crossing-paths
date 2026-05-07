package com.logandhillon.fptgame.entity.player;

import com.logandhillon.fptgame.entity.game.LevelButtonEntity;
import com.logandhillon.fptgame.entity.game.PlatformEntity;
import com.logandhillon.fptgame.entity.game.PortalEntity;
import com.logandhillon.fptgame.networking.proto.LevelProto;
import com.logandhillon.fptgame.resource.Colors;
import com.logandhillon.fptgame.resource.Sounds;
import com.logandhillon.fptgame.resource.Textures;
import com.logandhillon.logangamelib.engine.GameScene;
import com.logandhillon.logangamelib.entity.physics.CollisionEntity;
import com.logandhillon.logangamelib.entity.physics.PhysicsEntity;
import com.logandhillon.logangamelib.gfx.AnimationSequence;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.Getter;

import java.util.function.Predicate;

/**
 * The player is a physics entity that receives inputs via the methods it provides and moves accordingly.
 *
 * @author Logan Dhillon
 * @see ControllablePlayerEntity
 */
public class PlayerEntity extends PhysicsEntity {
    private static final float JUMP_POWER    = 13.5f * PX_PER_METER; // m/s
    private static final float MOVE_SPEED    = 6f * PX_PER_METER; // m/s
    private static final int   Y_OFFSET      = 12;
    private static final float STRIDE_LENGTH = 60f; // px per footstep

    private final   LevelProto.Color  color;
    private final   Color             tint;
    protected final PlayerInputSender listener;
    private final   float             spawnX;
    private final   float             spawnY;

    private AnimationSequence texture = Textures.ANIM_PLAYER_IDLE.instance();
    private AnimationState    state   = AnimationState.IDLE;

    /**
     * move direction of the player, whereof the player will move towards indefinitely.
     * <p>
     * -1=left, 0=none, 1=right
     */
    @Getter
    private int     moveDirection = 0; // left=-1, 0=none, 1=right
    private boolean didJump;
    private float   lastFootstepX = -100;

    public PlayerEntity(float x, float y, int color, PlayerInputSender listener) {
        super(x, y, 42, 72);
        if (color != 0 && color != 1) throw new IllegalArgumentException("Color must be 0 (red) or 1 (blue)");
        spawnX = x;
        spawnY = y;

        this.color = color == 0 ? LevelProto.Color.RED : LevelProto.Color.BLUE;
        this.tint = Colors.PLAYER_SKINS.get(color);
        this.listener = listener;
    }

    @Override
    protected void onRender(GraphicsContext g, float x, float y) {
        // render the active texture
        if (state == AnimationState.JUMP) texture.drawFrame(g, 0, x, y - Y_OFFSET, w, h + Y_OFFSET, tint);
        else texture.draw(g, x, y - Y_OFFSET, w, h + Y_OFFSET, tint);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onUpdate(float dt) {
        super.onUpdate(dt);
        texture.onUpdate(dt);

        // move player based on move direction
        if (Math.abs(moveDirection) > 0) x += MOVE_SPEED * dt * moveDirection;

        // handle player falling out of map
        if (y > 2000) {
            Sounds.playSfx(Sounds.GAME_RESPAWN);
            setPosition(spawnX, spawnY);
            this.vx = 0;
            this.vy = 0;
            setMoveDirection(0); // update position and communicate to peer
        }

        // update animation state
        if (state == AnimationState.JUMP && isGrounded()) setAnimation(AnimationState.IDLE);
        else if (!isGrounded()) setAnimation(AnimationState.JUMP);
        else if (moveDirection > 0) setAnimation(AnimationState.WALK_RIGHT);
        else if (moveDirection < 0) setAnimation(AnimationState.WALK_LEFT);
        else if (state != AnimationState.JUMP) setAnimation(AnimationState.IDLE);

        // play sounds
        if (isGrounded() && didJump) {
            Sounds.playSfx(Sounds.GAME_LAND);
            didJump = false;
        }

        if (Math.abs(lastFootstepX - x) > STRIDE_LENGTH && isGrounded()) {
            Sounds.playSfx(Sounds.GAME_STEP);
            lastFootstepX = x;
        }
    }

    /**
     * Overrides the default collision handler from {@link CollisionEntity} to ignore collisions with any other
     * {@link PlayerEntity}, and also to ignore any {@link PlatformEntity} with the same color as us.
     *
     * @see GameScene#getCollisionIf(float, float, float, float, CollisionEntity, Predicate)
     */
    @Override
    protected CollisionEntity getCollisionAt(float x, float y, float w, float h, CollisionEntity caller) {
        return parent.getCollisionIf(
                x, y, w, h, caller, e -> !(e instanceof PlayerEntity) &&
                                         (!(e instanceof PlatformEntity p) || p.getColor() != color) &&
                                         !(e instanceof LevelButtonEntity) &&
                                         !(e instanceof PortalEntity));
    }

    /**
     * Makes this player jump, only works if touching the ground
     */
    public void jump() {
        if (this.isGrounded()) {
            this.vy = -JUMP_POWER;
            Sounds.playSfx(Sounds.GAME_JUMP);
            didJump = true;
            if (listener != null) listener.onJump();
        }
    }

    /**
     * Sets the move direction of the player, whereof the player will move towards indefinitely.
     *
     * @param dir -1=left, 0=none, 1=right
     */
    public void setMoveDirection(int dir) {
        moveDirection = dir;
        if (listener != null) listener.onMove(dir, x, y, vx, vy);
    }

    /**
     * Safely updates the active {@link AnimationState} and the currently visible {@link PlayerEntity#texture}.
     * <p>
     * Does not update anything if the current {@link AnimationState} is equal to the new state
     *
     * @param state the new state
     */
    private void setAnimation(AnimationState state) {
        if (this.state == state) return;

        this.state = state;
        switch (state) {
            case IDLE -> texture = Textures.ANIM_PLAYER_IDLE.instance();
            case JUMP -> texture = Textures.ANIM_PLAYER_JUMP.instance();
            case WALK_LEFT -> texture = Textures.ANIM_PLAYER_RUN_LEFT.instance();
            case WALK_RIGHT -> texture = Textures.ANIM_PLAYER_RUN_RIGHT.instance();
        }
    }

    /**
     * The current animation that should be visible on the player, each ordinal represents an {@link AnimationSequence}
     */
    protected enum AnimationState {
        IDLE, JUMP, WALK_LEFT, WALK_RIGHT
    }
}
