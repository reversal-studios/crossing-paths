package com.logandhillon.fptgame.scene;

import com.logandhillon.fptgame.entity.game.PortalEntity;
import com.logandhillon.fptgame.entity.player.ControllablePlayerEntity;
import com.logandhillon.fptgame.entity.player.PlayerEntity;
import com.logandhillon.fptgame.networking.proto.LevelProto;
import com.logandhillon.logangamelib.entity.ui.TextEntity;
import javafx.scene.Scene;

/**
 * Basic game scene with a player that can run around and interact with physics objects; to aid development.
 *
 * @author Logan Dhillon
 */
public class SingleplayerGameScene extends LevelScene {
    private final PlayerEntity self;

    public SingleplayerGameScene(LevelProto.LevelData level) {
        super(level);
        self = new ControllablePlayerEntity(level.getPlayer1SpawnX(), level.getPlayer1SpawnY(), 0, null);
    }

    @Override
    public void onBuild(Scene scene) {
        addEntity(self);

        addEntity(new TextEntity.Builder(10, 30)
                          .setText(() -> String.format(
                                  """
                                  [PLAYER; YOU]
                                  isGrounded: %s
                                  pos: %.1f, %.1f
                                  vel: %.1f, %.1f
                                  dir: %s""",

                                  self.isGrounded(),
                                  self.getX(), self.getY(),
                                  self.vx, self.vy,
                                  self.getMoveDirection()))
                          .setFontSize(14)
                          .build());
    }

    @Override
    protected void onUpdate(float dt) {
        super.onUpdate(dt);

        PortalEntity selfColl = (PortalEntity)getEntityCollision(self, PortalEntity.class::isInstance);
        if (selfColl != null && selfColl.isRed()) nextLevel();
    }

    @Override
    protected LevelScene build(LevelProto.LevelData level) {
        return new SingleplayerGameScene(level);
    }
}
