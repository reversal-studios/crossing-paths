package com.logandhillon.logangamelib.engine;

import com.logandhillon.logangamelib.api.platform.Dispatcher;
import com.logandhillon.logangamelib.api.platform.Window;
import com.logandhillon.logangamelib.io.PathManager;
import com.logandhillon.logangamelib.scene.GameScene;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Abstract game handler for the logangamelib game engine.
 * <p>
 * This class will be the entrypoint of your game, handling all primary logic.
 *
 * @author Logan Dhillon
 */
public interface LGLGameHandler<H extends LGLGameHandler<H>> {
    /**
     * Runs when this game is launched.
     * <p>
     * Use this method to initialize the {@link Stage} and set up your game.
     */
    GameScene<H> onStart();

    /**
     * @return the task dispatcher for work that must run on the game UI thread.
     */
    Dispatcher uiDispatcher();

    /**
     * @return the logical canvas configuration used by the active scene.
     */
    Window getWindow();

    /**
     * Runs when this game is shutdown or otherwise closed.
     * <p>
     * Use this method to close/terminate any threads, handle last-minute clean up, etc.
     */
    void onShutdown();

    /**
     * @return the name of the game in snake_case.
     */
    default String getGameId() {
        return GameMeta.get().gameName;
    }

    PathManager getPathManager();

    /**
     * Tries to return the active scene as the (expected) type, casting it to said type, and returning null if such
     * fails.
     *
     * @param type the expected type of {@link GameScene}
     *
     * @return the active {@link GameScene} if it is the right type, or null if it's not
     */
    <T extends GameScene<?>> Optional<T> getActiveScene(Class<T> type);

    /**
     * Discards the currently active scene and replaces it with the provided one.
     *
     * @param scene the GameScene to switch
     */
    void setScene(GameScene<H> scene);
}
