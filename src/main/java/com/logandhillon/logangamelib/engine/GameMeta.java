package com.logandhillon.logangamelib.engine;

import com.logandhillon.logangamelib.resource.FontResource;
import javafx.scene.image.Image;
import lombok.Builder;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

@Builder
public class GameMeta {
    private static final Logger LOG = LoggerContext.getContext().getLogger(GameMeta.class);

    private static final String DEFAULT_GAME_NAME = "My logangamelib game";

    private static GameMeta instance;

    public final String       gameName;
    public final FontResource defaultFont;
    public final Image        splashIcon;

    public static GameMeta get() {
        if (instance == null) throw new IllegalStateException("GameMeta not registered");
        return instance;
    }

    public void register() {
        // validate metadata
        if (this.gameName.equalsIgnoreCase(DEFAULT_GAME_NAME))
            LOG.warn("GameMeta game name not set! You probably want to change this to your game's name.");
        if (this.defaultFont == null) throw new IllegalStateException("defaultFont not registered");

        // ensure no instance is already setup
        if (GameMeta.instance != null) throw new IllegalStateException("GameMeta already registered");

        // bind instance
        GameMeta.instance = this;
    }
}