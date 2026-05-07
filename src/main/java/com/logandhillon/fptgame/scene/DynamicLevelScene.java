package com.logandhillon.fptgame.scene;

import com.logandhillon.fptgame.GameHandler;
import com.logandhillon.fptgame.entity.game.PortalEntity;
import com.logandhillon.fptgame.entity.player.ControllablePlayerEntity;
import com.logandhillon.fptgame.entity.player.PlayerEntity;
import com.logandhillon.fptgame.entity.player.PlayerInputSender;
import com.logandhillon.fptgame.networking.GamePacket;
import com.logandhillon.fptgame.networking.PeerMovementPoller;
import com.logandhillon.fptgame.networking.proto.LevelProto;
import com.logandhillon.fptgame.networking.proto.PlayerProto;
import com.logandhillon.logangamelib.engine.GameScene;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

/**
 * Loads encoded level data into a playable {@link GameScene}
 *
 * @author Logan Dhillon
 */
public class DynamicLevelScene extends LevelScene {
    private static final Logger LOG = LoggerContext.getContext().getLogger(DynamicLevelScene.class);

    private final PlayerEntity       other;
    private final PlayerEntity       self;
    private final boolean            isServer;
    private final PeerMovementPoller movePoller;

    public DynamicLevelScene(LevelProto.LevelData level) {
        super(level);
        GameHandler.NetworkRole role = GameHandler.getNetworkRole();
        if (role == GameHandler.NetworkRole.SERVER) {
            movePoller = GameHandler.getServer().queuedPeerMovements::poll;
        } else if (role == GameHandler.NetworkRole.CLIENT) {
            movePoller = GameHandler.getClient().queuedPeerMovements::poll;
        } else {
            throw new IllegalStateException("GameHandler is neither SERVER nor CLIENT, cannot poll peer");
        }

        isServer = role == GameHandler.NetworkRole.SERVER;
        // dynamically get spawn positions, given server is player 1 and client is player 2
        float[][] spawns = isServer ? new float[][]{ { level.getPlayer1SpawnX(), level.getPlayer1SpawnY() },
                                                     { level.getPlayer2SpawnX(), level.getPlayer2SpawnY() } }
                                    : new float[][]{ { level.getPlayer2SpawnX(), level.getPlayer2SpawnY() },
                                                     { level.getPlayer1SpawnX(), level.getPlayer1SpawnY() } };

        // other player has inverted colours compared to us
        other = new PlayerEntity(spawns[0][0], spawns[0][1], isServer ? 1 : 0, null);
        addEntity(other);

        self = new ControllablePlayerEntity(spawns[1][0], spawns[1][1], isServer ? 0 : 1, new PlayerInputSender());
        addEntity(self); // render self on top of other, we should always be visible first.
    }

    @Override
    public void restartLevel() {
        if (isServer) super.restartLevel();
        else LOG.warn("Client cannot restart level!");
    }

    @Override
    protected LevelScene build(LevelProto.LevelData level) {
        return new DynamicLevelScene(level);
    }

    @Override
    protected void broadcastLevel(LevelProto.LevelData level) {
        if (isServer) {
            LOG.info("Broadcasting level to peer(s)");
            GameHandler.getServer().broadcast(new GamePacket(GamePacket.Type.SRV_GAME_STARTING, level));
        }
    }

    @Override
    protected void onUpdate(float dt) {
        super.onUpdate(dt);

        // poll our peer's move and apply it to our instance.
        GamePacket.Type move = movePoller.poll();
        if (move != null) {
            LOG.debug("Processing peer movement '{}'", move);
            switch (move) {
                case COM_JUMP -> other.jump();
                case COM_MOVE_L -> other.setMoveDirection(-1);
                case COM_MOVE_R -> other.setMoveDirection(1);
                case COM_STOP_MOVING -> other.setMoveDirection(0);
            }
        }

        // isServer also == isRed
        if (isServer) {
            PortalEntity selfColl = (PortalEntity)getEntityCollision(self, PortalEntity.class::isInstance);
            PortalEntity otherColl = (PortalEntity)getEntityCollision(other, PortalEntity.class::isInstance);
            if (selfColl != null && selfColl.isRed() && otherColl != null && !otherColl.isRed()) nextLevel();
        }
    }

    /**
     * Takes in the {@link PlayerProto.PlayerMovementData} from the partner/other, and "synchronizes" their position to
     * the incoming update message.
     *
     * @param update the incoming {@link PlayerProto.PlayerMovementData} update message
     */
    public void syncMovement(PlayerProto.PlayerMovementData update) {
        LOG.debug("Updating movement from remote");
        other.setPosition(update.getX(), update.getY());
        other.vx = update.getVx();
        other.vy = update.getVy();
    }
}
