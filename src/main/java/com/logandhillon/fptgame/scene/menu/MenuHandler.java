package com.logandhillon.fptgame.scene.menu;

import com.logandhillon.fptgame.GameHandler;
import com.logandhillon.fptgame.resource.Textures;
import com.logandhillon.fptgame.scene.component.MenuAlertScene;
import com.logandhillon.logangamelib.engine.UIScene;
import com.logandhillon.logangamelib.entity.Entity;
import com.logandhillon.logangamelib.gfx.ParallaxBackground;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

/**
 * The menu handler is the only {@link UIScene} in the menu screens. It allows users to switch between menus without
 * switching scenes by changing between each menu's {@link MenuContent}, allowing for easier menu management and
 * smoother transitions.
 *
 * @author Jack Ross
 * @see MenuContent
 */
public class MenuHandler extends UIScene<GameHandler> {
    private static final Logger LOG = LoggerContext.getContext().getLogger(MenuHandler.class);

    private final ParallaxBackground background = Textures.ocean8();

    /**
     * menu content that will be set or disposed of
     */
    @Getter
    private MenuContent content;

    /**
     * Creates a new menu handler that has default content of {@link MainMenuContent}
     */
    public MenuHandler() {
        this.content = new MainMenuContent(this);
    }

    /**
     * Internal constructor to create alert scene
     */
    private MenuHandler(String title, String msg) {
        this.content = new MenuAlertScene(title, msg, this);
    }

    /**
     * Creates a menu handler that has starting content as a {@link MenuAlertScene}
     *
     * @param title title of alert
     * @param msg   body of alert
     *
     * @return new menu content
     */
    public static MenuHandler alert(String title, String msg) {
        return new MenuHandler(title, msg);
    }

    /**
     * Runs when a new scene is initialized
     *
     * @param scene JAVAFX SCENE from engine
     */
    @Override
    public void onBuild(Scene scene) {
        super.onBuild(scene);
        setContent(content); // set the content to the default content
    }

    /**
     * Resets the content of the game scene to display the current menu
     *
     * @param content The content (entities) of any given menu
     */
    public void setContent(MenuContent content) {
        this.content = content; // store ptr to content for future reference
        this.clearEntities(true, (e) -> true);
        this.clearAllHandlers();

        for (Entity e: content.getEntities()) addEntity(e);
        this.addMouseEvents(true); // re-bind the mouse events (they were just removed)

        content.onShow();
    }

    /**
     * Renders the constants for all menus
     *
     * @param g the graphical context to render to.
     */
    @Override
    protected void render(GraphicsContext g) {
        background.render(g);
        super.render(g);
        content.onRender(g);
    }

    @Override
    protected void onUpdate(float dt) {
        super.onUpdate(dt);
        background.onUpdate(dt);
    }

    public GameHandler getGameHandler() {
        if (getParent() == null) LOG.warn("Current GameHandler is null");
        return getParent();
    }
}