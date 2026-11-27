package com.logandhillon.logangamelib.scene;

import com.logandhillon.fptgame.entity.ui.component.MenuButton;
import com.logandhillon.logangamelib.api.input.KeyCode;
import com.logandhillon.logangamelib.api.input.KeyboardEvent;
import com.logandhillon.logangamelib.api.render.Renderer;
import com.logandhillon.logangamelib.entity.Entity;

import java.util.function.Supplier;

/**
 * A MenuController controls an ordered list of {@link MenuButton}s, providing keyboard controls and a simplified method
 * for entity registration.
 * <p>
 * MenuControllers are immutable— the buttons it shall control cannot change.
 *
 * @author Logan Dhillon
 */
public class MenuController extends Entity {
    private final MenuButton[]      buttons;
    private final Supplier<Boolean> active;

    /** idx of currently active button; -1 means none */
    private int activeBtnIdx;

    /**
     * Creates a new immutable menu controller
     *
     * @param buttons ordered list of buttons from top to bottom, which will be used to sort navigation.
     */
    public MenuController(Supplier<Boolean> isActive, MenuButton... buttons) {
        super(0, 0); // pos doesn't matter, do whatever
        this.active = isActive;
        this.buttons = buttons;
        this.activeBtnIdx = -1;

        for (int i = 0; i < buttons.length; i++) {
            int idx = i; // effectively final instance of i

            // select this button if it's hovered by cursor
            buttons[i].setMouseEnterHandler(e -> {
                if (activeBtnIdx >= 0) buttons[activeBtnIdx].setActive(false);
                activeBtnIdx = idx;
            });

            // unselect all buttons when unselecting button with cursor
            buttons[i].setMouseLeaveHandler(e -> activeBtnIdx = -1);
        }
    }

    /**
     * Handles key presses from logangamelib, used to change the actively selected button or press it.
     *
     * @apiNote only runs if isActive returns true
     */
    public void onKeyPressed(KeyboardEvent e) {
        if (!active.get()) return;

        // when W/UP/SHIFT+TAB pressed, go up (-1) in buttons
        if (e.key() == KeyCode.UP || e.key() == KeyCode.W ||
            (e.modifiers().shift() && e.key() == KeyCode.TAB)) {
            if (activeBtnIdx >= 0)
                buttons[activeBtnIdx].setActive(false); // deselect old active button (if any)
            activeBtnIdx = Math.max(0, activeBtnIdx - 1);   // decrement idx (no lower than 0)
            buttons[activeBtnIdx].setActive(true); // select new active button
        }
        // when S/DOWN/TAB pressed, go down (+1) in buttons
        else if (e.key() == KeyCode.DOWN || e.key() == KeyCode.S ||
                 (!e.modifiers().shift() && e.key() == KeyCode.TAB)) {
            if (activeBtnIdx >= 0)
                buttons[activeBtnIdx].setActive(false); // deselect old active button (if any)
            activeBtnIdx = Math.min(buttons.length - 1, activeBtnIdx + 1); // increment idx (no higher than highest idx)
            buttons[activeBtnIdx].setActive(true);  // select new active button
        }
        // when ENTER/SPACE pressed, simulate a click on the active button
        else if ((e.key() == KeyCode.ENTER || e.key() == KeyCode.SPACE) && activeBtnIdx >= 0) {
            buttons[activeBtnIdx].onPress();
        }
    }

    @Override
    protected void onRender(Renderer g, float x, float y) {

    }

    @Override
    public void onUpdate(float dt) {

    }

    @Override
    public void onDestroy() {

    }

    /**
     * Attaches this entity and all buttons controlled by it to the parent.
     *
     * @param parent the parent that this object is now attached to.
     */
    @Override
    public void onAttach(GameScene<?> parent) {
        super.onAttach(parent);

        // add all buttons to parent
        for (var btn: buttons) parent.addEntity(btn);

        // register key press event
        parent.input().onKeyDown(this::onKeyPressed);
    }
}
