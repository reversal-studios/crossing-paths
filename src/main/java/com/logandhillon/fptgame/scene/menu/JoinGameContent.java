package com.logandhillon.fptgame.scene.menu;

import com.logandhillon.fptgame.GameHandler;
import com.logandhillon.fptgame.entity.ui.InputBox;
import com.logandhillon.fptgame.entity.ui.ServerEntryEntity;
import com.logandhillon.fptgame.entity.ui.component.MenuButton;
import com.logandhillon.fptgame.entity.ui.component.MenuModalEntity;
import com.logandhillon.fptgame.resource.Colors;
import com.logandhillon.logangamelib.engine.GameMeta;
import com.logandhillon.logangamelib.entity.Entity;
import com.logandhillon.logangamelib.entity.Renderable;
import com.logandhillon.logangamelib.entity.ui.TextEntity;
import javafx.geometry.VPos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The join game menu allows users to join existing servers through manual IP Address searching or local server
 * discovery. When the user has joined a game, they will be transported to the {@link LobbyGameContent}
 *
 * @author Jack Ross, Logan Dhillon
 */
public class JoinGameContent implements MenuContent {
    private static final Logger LOG             = LoggerContext.getContext().getLogger(JoinGameContent.class);
    private static final Font   HEADER_FONT     = Font.font(GameMeta.get().defaultFont.load(), FontWeight.MEDIUM, 32);
    private static final String HEADER          = "Join a Game";
    private static final Font   LABEL_FONT      = Font.font(GameMeta.get().defaultFont.load(), FontWeight.MEDIUM, 18);
    private static final int    ENTITY_GAP      = 16;
    private static final int    CORNER_DIAMETER = 53;

    private final Entity[]            entities;
    private final MenuModalEntity     joinModal;
    private final ServerEntryEntity[] serverButtons = new ServerEntryEntity[4];

    private int               scrollServerIndex;
    private int               currentServerIndex;
    private int               rawCurrentServerIndex;
    private String            selectedServerAddr; // the addr of the selected server in Discovery
    private List<ServerEntry> serverList = new ArrayList<>();

    /**
     * @param menu the {@link MenuHandler} responsible for switching active menus.
     */
    public JoinGameContent(MenuHandler menu, JoinGameHandler onJoin) {
        // rect in background for server list
        Renderable serverListRect = new Renderable(
                32, 326, (g, x, y) -> {
            g.setFill(Colors.BUTTON_NORMAL);
            g.fillRoundRect(x, y, 459, 228, CORNER_DIAMETER, CORNER_DIAMETER);
        });

        // label for server list
        TextEntity serverListLabel = new TextEntity.Builder(32, 295)
                .setText("OR, JOIN A DISCOVERED SERVER")
                .setAlign(TextAlignment.LEFT)
                .setBaseline(VPos.TOP)
                .setFont(LABEL_FONT)
                .setColor(Colors.ACTIVE)
                .build();

        // join server input field
        InputBox joinServer = new InputBox(
                32, 193, 328, "ex. 192.168.0.1",
                "JOIN A SERVER DIRECTLY", 39);

        // join button (direct)
        MenuButton joinDirectButton = new MenuButton(
                "JOIN", 368, 193, 139, 48, () -> {
            LOG.info("Attempting to join {} via manual input", joinServer.getInput());
            onJoin.handleJoin(joinServer.getInput());
        });

        // join button (discovery)
        MenuButton joinDiscoverButton = new MenuButton(
                "JOIN", 32, 640, 459, 48, () -> {
            if (selectedServerAddr == null) {
                LOG.warn("Tried to join discovered server, but no server was selected. Ignoring");
                return;
            }
            LOG.info("Attempting to join {} via discovery", joinServer.getInput());
            onJoin.handleJoin(selectedServerAddr);
        });

        joinModal = new MenuModalEntity(
                0, 0, 585, GameHandler.CANVAS_HEIGHT, true, menu, serverListRect, serverListLabel, joinServer,
                joinDirectButton,
                joinDiscoverButton);

        // creates list of entities to be used by menu handler
        entities = new Entity[]{
                joinModal,
                new TextEntity.Builder(32, 66)
                        .setColor(Colors.ACTIVE)
                        .setText(HEADER.toUpperCase())
                        .setFont(HEADER_FONT)
                        .setBaseline(VPos.TOP).build()
        };

        // create event handler that uses the event and the array of buttons
        menu.addHandler(KeyEvent.KEY_PRESSED, e -> onKeyPressed(e, serverButtons));
    }

    @Override
    public void onShow() {
        // attach server buttons (via modal) only once content is shown (so this content has a parent)
        // XXX: this should not be in the constructor (for reasons above)
        for (int i = 0; i < serverButtons.length; i++) {
            // populate with dummy values and hide them
            serverButtons[i] = new ServerEntryEntity(
                    48, 342 + (ENTITY_GAP * i), 427, 37,
                    "...", "...", () -> {
            });
            serverButtons[i].hidden = true;
            LOG.debug("Creating (hidden) server button for this modal. {}/{}", i + 1, serverButtons.length);
            joinModal.addEntity(serverButtons[i]);
        }
    }

    /**
     * Clears the UI discovered server list and repopulates it with the values of {@link JoinGameContent#serverList}
     */
    private void updateServerList() {
        // repopulate items and add to list
        AtomicInteger currentServer = new AtomicInteger();

        for (int i = 0; i < serverButtons.length; i++) {
            if (i >= serverList.size()) {
                serverButtons[i].hidden = true;
                continue;
            }

            // get it immediately so it doesn't change
            var entry = serverList.get(i);
            int finalI = i;

            // set new server button with available information
            LOG.debug("Preparing server button with data: {{}, {}}", entry.name, entry.address);
            serverButtons[i].setData(entry);
            serverButtons[i].setOnClick(() -> {
                // runnable (runs on click)

                // highlight button
                serverButtons[finalI].setFlags(true, true);
                currentServer.set(finalI);

                currentServerIndex = finalI;
                rawCurrentServerIndex = finalI;
                selectedServerAddr = entry.address;

                // reset button highlight for non-clicked buttons
                for (int j = 0; j < serverButtons.length; j++) {
                    if (currentServer.get() != j) {
                        serverButtons[j].setFlags(false, false);
                    }
                }
            });
            serverButtons[i].hidden = false;
        }
    }

    /**
     * Replaces the current server list with a new set of them
     *
     * @param newList list of all discovered servers
     */
    public void setDiscoveredServers(List<ServerEntry> newList) {
        serverList = newList;
        updateServerList();
    }

    /**
     * Allows {@link MenuHandler} to access content for this menu
     *
     * @return entity list
     */
    @Override
    public Entity[] getEntities() {
        return entities;
    }

    /**
     * An entry in the server list of the join game screen.
     *
     * @param name    name of the server/room
     * @param address FQDN or IP address of server
     */
    public record ServerEntry(String name, String address) {}

    /**
     * @param e       any key event registered by javafx
     * @param entries list of buttons on screen
     */
    private void onKeyPressed(KeyEvent e, ServerEntryEntity[] entries) {

        if (e.getCode() != KeyCode.UP && e.getCode() != KeyCode.DOWN) return;

        // increment/decrement the 4 shown servers
        if (e.getCode() == KeyCode.UP) {
            if (scrollServerIndex > 0) {
                rawCurrentServerIndex++;
                // un-highlight all buttons
                for (ServerEntryEntity entry: entries) {
                    entry.setFlags(false, false);
                }
                if (currentServerIndex < entries.length - 1 && rawCurrentServerIndex > 0) {
                    currentServerIndex++;
                    // re-highlight button if it isn't still off-screen
                    entries[currentServerIndex].setFlags(true, true);
                }

                if (currentServerIndex == 0) {
                    if (rawCurrentServerIndex < -1) {
                        // un-highlight all buttons if the selected button is not in the array
                        for (ServerEntryEntity entry: entries) {
                            entry.setFlags(false, false);
                        }
                    }
                    // if the button was put back in the array by moving up, put it at the start
                    if (rawCurrentServerIndex > -1) {
                        currentServerIndex = 0;
                        entries[0].setFlags(true, true);
                    }
                }
                // increments entire list of shown servers
                scrollServerIndex--;
            }
        }
        if (e.getCode() == KeyCode.DOWN) {
            if (scrollServerIndex < serverList.toArray().length - entries.length) {
                // opposite to KeyCode.UP, the index of the current button must decrease when down arrow is pressed
                rawCurrentServerIndex--;
                for (ServerEntryEntity entry: entries) {
                    entry.setFlags(false, false);
                }

                if (currentServerIndex > 0 && rawCurrentServerIndex < entries.length - 1) {
                    currentServerIndex--;

                    entries[currentServerIndex].setFlags(true, true);
                }
                if (currentServerIndex == entries.length - 1) {
                    if (rawCurrentServerIndex > entries.length) {
                        for (ServerEntryEntity entry: entries) {
                            entry.setFlags(false, false);
                        }
                    }
                    if (rawCurrentServerIndex < entries.length) {
                        currentServerIndex = entries.length - 1;
                        entries[entries.length - 1].setFlags(true, true);
                    }
                }
                // decrements entire list of shown servers
                scrollServerIndex++;
            }
        }

        // now that index has changed, re-populate the server list
        for (int i = 0; i < entries.length; i++) {
            entries[i].setData(serverList.get(i + scrollServerIndex));
        }
    }

    public interface JoinGameHandler {
        void handleJoin(String serverAddress);
    }
}
