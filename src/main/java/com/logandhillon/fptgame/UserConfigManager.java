package com.logandhillon.fptgame;

import com.logandhillon.fptgame.networking.proto.ConfigProto;
import com.logandhillon.logangamelib.api.input.KeyCode;
import com.logandhillon.logangamelib.io.SaveFileManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

/**
 * Implementation of logangamelib's {@link SaveFileManager} for game-related save data.
 *
 * @author Logan Dhillon
 */
public class UserConfigManager extends SaveFileManager<ConfigProto.UserConfig> {
    private static final Logger LOG = LoggerContext.getContext().getLogger(UserConfigManager.class);

    /** increment this if a major change occurs and old user configs must be destroyed */
    private static final int VERSION = 2;

    /**
     * Creates a new user config manager with a custom filepath for the save file: the file that is managed by this
     * instance.
     */
    public UserConfigManager() {
        super("crossing-paths", ConfigProto.UserConfig.parser());
    }

    /**
     * Creates a new user config manager with a custom filepath for the save file: the file that is managed by this
     * instance.
     *
     * @param filename name of file manage from this point on.
     */
    public UserConfigManager(String filename) {
        super(filename, ConfigProto.UserConfig.parser());
    }

    @Override
    protected ConfigProto.UserConfig getDefault() {
        return ConfigProto.UserConfig
                .newBuilder().setVersion(VERSION)
                .setName("Player")
                .setKeyMoveLeft(KeyCode.A.name())
                .setKeyMoveRight(KeyCode.D.name())
                .setKeyMoveJump(KeyCode.SPACE.name())
                .setKeyMoveInteract(KeyCode.E.name())
                .setMasterVolume(1f)
                .setMusicVolume(1f)
                .setSfxVolume(1f)
                .build();
    }

    @Override
    public ConfigProto.UserConfig load() {
        ConfigProto.UserConfig data = super.load();

        if (data.getVersion() != VERSION) {
            LOG.error("Save file version (v{}) mismatch! Replacing config with default.", data.getVersion());
            data = this.save(getDefault());
        }

        if (data.getName().isBlank()) {
            LOG.warn("Loaded name is blank, setting name to default (Player)");
            data.toBuilder().setName("Player");
            save(data);
        }

        return data;
    }
}
