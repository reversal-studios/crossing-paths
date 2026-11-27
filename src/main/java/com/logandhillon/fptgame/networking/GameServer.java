package com.logandhillon.fptgame.networking;

import com.logandhillon.fptgame.GameHandler;
import com.logandhillon.fptgame.networking.proto.LevelProto;
import com.logandhillon.fptgame.networking.proto.PlayerProto;
import com.logandhillon.fptgame.resource.Levels;
import com.logandhillon.fptgame.scene.DynamicLevelScene;
import com.logandhillon.fptgame.scene.LevelScene;
import com.logandhillon.fptgame.scene.menu.LobbyGameContent;
import com.logandhillon.fptgame.scene.menu.MenuContent;
import com.logandhillon.fptgame.scene.menu.MenuHandler;
import com.logandhillon.logangamelib.api.platform.Dispatcher;
import com.logandhillon.logangamelib.io.PacketWriter;
import com.logandhillon.logangamelib.scene.GameScene;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A game server handles all outgoing communications to {@link GameClient}s via a valid network connection.
 * <p>
 * The server allows multiple clients to connect (using multi-threading to handle each connection independently) and
 * communicates using {@link GamePacket}s.
 *
 * @author Logan Dhillon
 * @see GameClient
 */
public class GameServer implements Runnable {
    private static final Logger LOG            = LoggerContext.getContext().getLogger(GameServer.class);
    public static final  int    DEFAULT_PORT   = 20670; // default port for game
    public static final  int    ADVERTISE_PORT = 20671; // for UDP broadcast discovery

    private volatile boolean                running; // if the server is running
    private final    GameHandler            game;
    private final    Dispatcher             uiDispatcher;
    public final     Queue<GamePacket.Type> queuedPeerMovements = new ConcurrentLinkedQueue<>();

    private ServerSocket      socket;
    private Thread            advertiser;
    private ConnectionDetails guest;

    /** the list of ALL active client connections, including unregistered ones. */
    private final Set<Socket> clients = Collections.synchronizedSet(new HashSet<>());

    private record ConnectionDetails(Socket socket, String name, PacketWriter out) {}

    public GameServer(GameHandler game, Dispatcher uiDispatcher) {
        this.game = game;
        this.uiDispatcher = uiDispatcher;
    }

    /**
     * Starts the server socket and the acceptor thread, which listens for incoming connections and handles them
     * accordingly.
     *
     * @throws IOException if the server socket fails to start.
     */
    public void start() throws IOException {
        LOG.info("Starting server on port {}...", DEFAULT_PORT);
        socket = new ServerSocket(DEFAULT_PORT);
        running = true;
        new Thread(this, "ServerAcceptor").start();
        startAdvertising(); // start the udp advertiser
    }

    /**
     * Tries to stop the server gracefully, ending the acceptor and closing the thread.
     *
     * @throws IOException if the socket fails to close.
     */
    public void stop() throws IOException {
        LOG.info("Stopping server gracefully");
        broadcast(new GamePacket(GamePacket.Type.SRV_SHUTDOWN));
        running = false;
        if (socket != null) {
            LOG.info("Closing server socket now");
            socket.close();
        }
        stopAdvertising();
        LOG.info("Closing {} client connection(s)", clients.size());
        synchronized (clients) {
            for (Socket c: clients) {
                try {
                    c.close();
                } catch (IOException ignored) {
                }
            }
            clients.clear();
        }
    }

    /**
     * The method that will run when this server is started in the thread.
     *
     * @see GameServer#start()
     */
    @Override
    public void run() {
        while (running) {
            try {
                Socket client = socket.accept();
                handleClient(client);
            } catch (IOException e) {
                if (running) LOG.error(e);
            }
        }
    }

    /**
     * Runs when a new client connects; this starts a dedicated thread to handle this client.
     *
     * @param client the incoming client socket
     */
    private void handleClient(Socket client) {
        new Thread(
                () -> {
                    LOG.info("Incoming client connection at {}", client.getInetAddress().getHostAddress());
                    clients.add(client);

                    try (client;
                         var out = new PacketWriter(client.getOutputStream());
                         DataInputStream dataInputStream = new DataInputStream(client.getInputStream())
                    ) {
                        while (true) {
                            int length;
                            try {
                                length = dataInputStream.readInt();
                            } catch (IOException e) {
                                LOG.info("Client {} disconnected", client.getInetAddress());
                                if (guest.socket == client) guest = null;
                                clients.remove(client);

                                uiDispatcher.execute(() -> {
                                    Optional<MenuHandler> menu = game.getActiveScene(MenuHandler.class);
                                    if (menu.isPresent()) {
                                        MenuContent content = menu.get().getContent();
                                        if (content instanceof LobbyGameContent lobby) {
                                            propagateLobbyUpdate(lobby);
                                        }
                                    }
                                });

                                break; // client disconnected or stream closed
                            }
                            if (length <= 0) {
                                LOG.warn("Received invalid packet length {} from {}", length, client.getInetAddress());
                                break;
                            }
                            byte[] data = new byte[length];
                            dataInputStream.readFully(data);
                            parseRequest(client, GamePacket.deserialize(data), out);
                        }
                    } catch (IOException e) {
                        LOG.error(e);
                    } finally {
                        clients.remove(client);
                    }
                }, "ClientHandler-" + client.getInetAddress()).start();
    }

    /**
     * Parses a request from a client
     *
     * @param client the client socket that this packet is from
     * @param packet the packet itself, from the client
     * @param out    the output stream used to respond to the client.
     */
    private void parseRequest(Socket client, GamePacket packet, PacketWriter out) {
        if (packet == null) return;

        LOG.debug("Received {} from {}", packet.type(), client.getInetAddress());

        try {
            // first, check if the client asked to be registered
            if (packet.type() == GamePacket.Type.CLT_REQ_CONN) handleClientRegistration(client, packet, out);

            // next, if they didn't ask and they still aren't registered, kick them
            if (guest.socket != client) {
                LOG.warn("Got packet from unregistered client; closing connection");
                client.close();
            }

            // finally, parse the request
            switch (packet.type()) {
                // if peer is trying to move, add instruction to queue
                case COM_JUMP, COM_MOVE_L, COM_MOVE_R, COM_STOP_MOVING -> {
                    queuedPeerMovements.add(packet.type());

                    // JUMP packets don't contain movement data
                    if (packet.type() == GamePacket.Type.COM_JUMP) return;

                    // sync other's movement
                    var movement = PlayerProto.PlayerMovementData.parseFrom(packet.payload());
                    uiDispatcher.execute(() -> {
                        Optional<DynamicLevelScene> level = game.getActiveScene(DynamicLevelScene.class);
                        if (level.isEmpty()) {
                            LOG.warn("Tried to sync movement, but was not in DynamicLevelScene; skipping message");
                            return;
                        }
                        level.get().syncMovement(movement);
                    });
                }
                case COM_PRESS_BUTTON -> {
                    uiDispatcher.execute(() -> {
                        Optional<LevelScene> level = game.getActiveScene(LevelScene.class);
                        if (level.isEmpty()) {
                            LOG.warn("Tried to handle COM_PRESS_BUTTON, but was not in LevelScene; skipping");
                            return;
                        }
                        level.get().onButtonPressed();
                    });
                }
                case CLT_DISCONNECT -> {
                    // going to the main menu will shut down the server
                    LOG.info("Client disconnected, returning to main menu");
                    uiDispatcher.execute(() -> game.showAlert(
                            "PARTNER DISCONNECTED", "Your partner has left the game."));
                }
            }
        } catch (IOException e) {
            LOG.error("Failed to close client at {}", client.getInetAddress(), e);
        }
    }

    /**
     * Handles a new client request and tries to register it or defer it. Should be called when receiving a CLT_REQ_CONN
     * packet.
     */
    private void handleClientRegistration(Socket client, GamePacket packet, PacketWriter out) throws IOException {
        if (guest == null) {
            PlayerProto.PlayerData data = PlayerProto.PlayerData.parseFrom(packet.payload());

            // check if name is already used
            if (data.getName().equals(GameHandler.getUserConfig().getName())) {
                LOG.info(
                        "Denying connection from {} (name '{}' in use)", client.getInetAddress(),
                        packet.payload());
                out.send(GamePacket.Type.SRV_DENY_CONN__USERNAME_TAKEN);
                client.close();
                return;
            }

            // get the lobby in advance, so it can get null if we shouldn't do this
            Optional<MenuHandler> menu = game.getActiveScene(MenuHandler.class);
            if (menu.isEmpty() || !(menu.get().getContent() instanceof LobbyGameContent lobby)) {
                // if lobby IS null, then just throw a warn and return early ;)
                LOG.warn(
                        "Server got a registration request, but was not ready for it. Closing client at {}.",
                        client.getInetAddress());
                out.send(GamePacket.Type.SRV_UNEXPECTED);
                client.close();
                return;
            }

            // all good now! register the client
            guest = new ConnectionDetails(client, data.getName(), out);
            LOG.info("Registered new client '{}' at {}!", data.getName(), client.getInetAddress());

            // update everyone's player list
            propagateLobbyUpdate(lobby);
        }

        // check if srv is full
        else {
            LOG.info("Denying connection from {} (server full)", client.getInetAddress());
            out.send(GamePacket.Type.SRV_DENY_CONN__FULL);
            client.close();
        }
    }

    /**
     * Updates the player list and broadcasts the new list to every client
     */
    private void propagateLobbyUpdate(LobbyGameContent lobby) {
        LOG.info("Propagating update for lobby player list");
        lobby.clearPlayers();

        lobby.addPlayer(GameHandler.getUserConfig().getName(), true); // host
        if (guest != null) lobby.addPlayer(guest.name, false); // guest

        // get the players on each team and send them to the client
        var dat = PlayerProto.Lobby.newBuilder()
                                   .setName(lobby.getRoomName())
                                   .setHost(ProtoBuilder.player(GameHandler.getUserConfig().getName()));

        // only set guest if it exists
        if (guest != null) dat.setGuest(ProtoBuilder.player(guest.name));

        broadcast(new GamePacket(GamePacket.Type.SRV_UPDATE_PLAYERLIST, dat.build()));
    }

    /**
     * Without checking who, this broadcasts the same packet to all registered clients.
     *
     * @param pkt the packet to broadcast
     */
    public void broadcast(GamePacket pkt) {
        if (guest != null) guest.out.send(pkt);
    }

    /**
     * Starts the game for the server: broadcasts the level to connected client(s) and sets the
     * {@link GameScene}.
     */
    public void startGame() {
        LevelProto.LevelData level = Levels.LEVEL_1; // XXX: hardcode level for server
        broadcast(new GamePacket(GamePacket.Type.SRV_GAME_STARTING, level));
        game.setInMenu(false);

        uiDispatcher.execute(() -> game.setScene(new DynamicLevelScene(level)));
    }

    /**
     * Starts advertising this server on the IP broadcast channel with UDP discovery packets.
     */
    public void startAdvertising() {
        if (advertiser != null && advertiser.isAlive()) return;

        LOG.info("Starting UDP advertiser on port {}", ADVERTISE_PORT);

        advertiser = new Thread(
                () -> {
                    try (DatagramSocket socket = new DatagramSocket()) {
                        socket.setBroadcast(true);
                        while (running) {
                            // only listen in lobby
                            if (game.isInGame()) continue;
                            Optional<MenuHandler> menu = game.getActiveScene(MenuHandler.class);
                            if (menu.isEmpty() || !(menu.get().getContent() instanceof LobbyGameContent lobby)) {
                                LOG.warn("Supposed to be in lobby, but lobby was null. Will not advertise this frame.");
                                return;
                            }

                            LOG.debug("Broadcasting server advertisement for port");

                            String msg =
                                    "MainServer:" + lobby.getRoomName() + ":" +
                                    DEFAULT_PORT; // incl. lobby name and port
                            byte[] buffer = msg.getBytes();
                            DatagramPacket packet = new DatagramPacket(
                                    buffer,
                                    buffer.length,
                                    InetAddress.getByName("255.255.255.255"),
                                    ADVERTISE_PORT
                            );

                            socket.send(packet);
                            //noinspection BusyWait
                            Thread.sleep(2000); // broadcast every 2 seconds
                        }
                    } catch (IOException e) {
                        LOG.error("IO exception in server advertiser", e);
                    } catch (InterruptedException e) {
                        LOG.info("Advertiser interrupted, stopping");
                        stopAdvertising();
                    }
                }, "ServerAdvertiser");

        advertiser.start();
    }

    /**
     * Stops the ServerAdvertiser immediately.
     */
    public void stopAdvertising() {
        if (advertiser != null) {
            LOG.info("Stopping UDP advertiser on port {}", ADVERTISE_PORT);
            advertiser.interrupt();
            advertiser = null;
        }
    }
}