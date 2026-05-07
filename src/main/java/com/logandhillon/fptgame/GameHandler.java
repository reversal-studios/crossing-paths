package com.logandhillon.fptgame;

import com.logandhillon.fptgame.networking.GameClient;
import com.logandhillon.fptgame.networking.GameServer;
import com.logandhillon.fptgame.networking.ServerDiscoverer;
import com.logandhillon.fptgame.networking.proto.ConfigProto;
import com.logandhillon.fptgame.resource.*;
import com.logandhillon.fptgame.scene.SingleplayerGameScene;
import com.logandhillon.fptgame.scene.component.MenuAlertScene;
import com.logandhillon.fptgame.scene.menu.JoinGameContent;
import com.logandhillon.fptgame.scene.menu.LobbyGameContent;
import com.logandhillon.fptgame.scene.menu.MainMenuContent;
import com.logandhillon.fptgame.scene.menu.MenuHandler;
import com.logandhillon.logangamelib.engine.GameMeta;
import com.logandhillon.logangamelib.engine.GameScene;
import com.logandhillon.logangamelib.engine.LGLGameHandler;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Optional;

@Setter
public class GameHandler extends LGLGameHandler<GameHandler> {
    private static final Logger LOG               = LoggerContext.getContext().getLogger(GameHandler.class);
    public static final  int    CANVAS_WIDTH      = 1280; // the width of the rendered canvas
    public static final  int    CANVAS_HEIGHT     = 720; // the height of the rendered canvas
    public static final  float  ASPECT_RATIO      = (float)CANVAS_WIDTH / CANVAS_HEIGHT;
    public static final  float  SCALING_TOLERANCE = 0.05f; // % to prefer maximizing size over aspect ratio

    // game state management
    private volatile boolean isInMenu;

    // networking
    @Getter
    private static GameServer server;
    @Getter
    private static GameClient client;
    private static ServerDiscoverer discoverer;

    private static UserConfigManager      ucm;
    private static ConfigProto.UserConfig userConfig;

    /**
     * Handles communication with JavaFX when this program is signaled to start.
     *
     * @param stage the primary stage for this application, provided by the JavaFX framework.
     */
    @Override
    protected GameScene<GameHandler> onStart(Stage stage) {
        isInMenu = true;

        stage.setWidth(CANVAS_WIDTH);
        stage.setHeight(CANVAS_HEIGHT);
        stage.setMinWidth(CANVAS_WIDTH / 2f);
        stage.setMinHeight(CANVAS_HEIGHT / 2f);

        // initialize resources so they are loaded in memory
        Colors.init();
        Fonts.init();
        Sounds.calcVolume();
        Textures.init();

        return this.isDebugMode() ? new SingleplayerGameScene(Levels.LEVEL_1) : new MenuHandler();
    }

    /**
     * Handles bootstrap and launching the framework + engine.
     *
     * @param args command-line arguments to the Java program.
     *
     * @see GameHandler#start(Stage)
     */
    @SuppressWarnings("unused") // for some reason, intellij thinks main isn't used
    public static void main(String[] args) {
        LGLGameHandler.launchGame(
                GameHandler.class,
                GameMeta.builder()
                        .gameName("Crossing Paths")
                        .defaultFont(Fonts.TREMOLO)
                        .splashIcon(Textures.REVERSAL_STUDIOS_LOGO)
                        .build(),
                () -> {
                    String lglSaveFile = System.getenv("LGL_SAVE_FILE");

                    ucm = lglSaveFile == null || lglSaveFile.isBlank()
                          ? new UserConfigManager()
                          : new UserConfigManager(lglSaveFile);

                    // load user config first
                    userConfig = ucm.load();
                });
    }

    @Override
    protected void onShutdown() {
        LOG.info("Program terminated, exiting cleanly");
        try {
            if (server != null) server.stop();
        } catch (IOException e) {
            LOG.error("Error stopping server during shutdown", e);
        }

        try {
            if (client != null) client.close();
        } catch (IOException e) {
            LOG.error("Error closing client during shutdown", e);
        }

        terminateDiscoverer();
    }

    public void goToMainMenu() {
        Optional<MenuHandler> menu = this.getActiveScene(MenuHandler.class);
        if (menu.isEmpty()) this.setScene(new MenuHandler());
        else menu.get().setContent(new MainMenuContent(menu.get()));
        setInMenu(true);
        terminateClient();
        terminateServer();
        terminateDiscoverer();
    }

    /**
     * Shows the lobby screen and starts a server.
     *
     * @param roomName the name of the lobby
     */
    public void createLobby(String roomName) {
        LOG.info("Creating lobby named {}", roomName);
        Optional<MenuHandler> menu = getActiveScene(MenuHandler.class);
        if (menu.isEmpty()) throw new IllegalStateException("Cannot create lobby without active MenuHandler");

        var lobby = new LobbyGameContent(menu.get(), roomName, true);
        menu.get().setContent(lobby); // set content first so we can populate lobby after
        lobby.addPlayer(GameHandler.getUserConfig().getName(), true);

        if (server != null) throw new IllegalStateException("Server already exists, cannot establish connection");

        server = new GameServer(this);
        isInMenu = true;
        try {
            server.start();
        } catch (IOException e) {
            LOG.error("Failed to start server", e);
        }
    }

    public void showJoinGameMenu() {
        discoverer = new ServerDiscoverer(this);
        discoverer.start();
        Optional<MenuHandler> menu = getActiveScene(MenuHandler.class);
        if (menu.isEmpty()) throw new IllegalStateException("Cannot show game menu without active MenuHandler");
        menu.get().setContent(new JoinGameContent(menu.orElse(null), this::joinGame));
    }

    /**
     * Joins a remote server, registers itself, and displays the lobby.
     *
     * @param serverAddress address and port of the server to join (addr:port)
     */
    public void joinGame(String serverAddress) {
        if (serverAddress.isBlank()) {
            LOG.warn("Server address is blank");
            return;
        }

        String host;
        int port;
        int i = serverAddress.lastIndexOf(':');
        if (i == -1) {
            host = serverAddress;
            port = GameServer.DEFAULT_PORT;
        } else {
            host = serverAddress.substring(0, i);
            port = Integer.parseInt(serverAddress.substring(i + 1));
        }

        discoverer.stop();

        LOG.info("Attempting to join game at {}, port {}", host, port);

        if (client != null) throw new IllegalStateException("Client already exists, cannot establish connection");

        setInMenu(false);
        try {
            client = new GameClient(host, port, this);
            client.connect();
        } catch (ConnectException e) {
            terminateClient();
            showAlert("COULD NOT JOIN SERVER", e.getMessage());
        } catch (IOException e) {
            terminateClient();
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes the client and nullifies the pointer.
     */
    private void terminateClient() {
        LOG.info("Terminating client");
        if (client == null) {
            LOG.warn("Client does not exist, skipping termination");
            return;
        }

        try {
            client.close();
        } catch (IOException e) {
            LOG.error("Failed to close socket during termination", e);
        }
        client = null;
    }

    /**
     * Closes the terminator and nullifies the pointer.
     */
    private static void terminateDiscoverer() {
        LOG.info("Terminating discoverer");

        if (discoverer == null) {
            LOG.warn("Server discoverer does not exist, skipping termination");
            return;
        }

        discoverer.stop();
        discoverer = null;
    }

    /**
     * Stops the server and nullifies the pointer.
     */
    private void terminateServer() {
        LOG.info("Terminating server");

        if (server == null) {
            LOG.warn("Server does not exist, skipping termination");
            return;
        }

        try {
            server.stop();
        } catch (IOException e) {
            LOG.error("Failed to close socket during termination", e);
        }
        server = null;
    }

    /**
     * Discards the current scene and shows a new {@link MenuAlertScene} with the provided alert details.
     */
    public void showAlert(String title, String message) {
        LOG.info("Showing alert {}: {}", title, message);
        Optional<MenuHandler> menu = getActiveScene(MenuHandler.class);

        if (menu.isPresent()) {
            menu.get().setContent(new MenuAlertScene(title, message, menu.get()));
        } else {
            LOG.debug("No MenuHandler active, creating new one");
            setScene(MenuHandler.alert(title, message));
        }
    }

    /**
     * Gets the current network role based on the active network manager (server or client)
     *
     * @return SERVER, CLIENT, or NONE
     */
    public static NetworkRole getNetworkRole() {
        if (server != null) return NetworkRole.SERVER;
        else if (client != null) return NetworkRole.CLIENT;
        return NetworkRole.NONE;
    }

    /**
     * A network role is the "active" type of network manager
     */
    public enum NetworkRole {
        SERVER, CLIENT, NONE
    }

    /**
     * @return true if we are in a game, false if we are in the menu
     */
    public boolean isInGame() {
        return !isInMenu;
    }

    /**
     * Gets the user config that is actively loaded in memory, NOT from disk.
     *
     * @return stored user confirm
     *
     * @throws NullPointerException if there is no stored user config (this shouldn't happen)
     */
    public static ConfigProto.UserConfig getUserConfig() {
        if (userConfig == null) throw new NullPointerException("User config is null!");
        return userConfig;
    }

    /**
     * Updates only the fields specified and saves the resulting config.
     *
     * @param partial the partial values, whatever is set here will be updated, otherwise it will remain the same.
     */
    public static void updateUserConfig(ConfigProto.UserConfig partial) {
        userConfig = ucm.update(userConfig, partial);
    }
}