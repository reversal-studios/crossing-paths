package com.logandhillon.logangamelib.entity.ui;

import javafx.scene.input.MouseEvent;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

/**
 * A dynamic button is the same as a regular {@link ButtonEntity}, except that it has TWO styles: the default style, and
 * a style to show when the button is being hovered.
 *
 * @author Logan Dhillon
 */
public class DynamicButtonEntity extends ButtonEntity {
    private static final Logger LOG = LoggerContext.getContext().getLogger(DynamicButtonEntity.class);

    private final Style defaultStyle;
    private final Style activeStyle;

    /**
     * if the mouse is currently in this button (if the button is active)
     */
    @Getter
    private boolean active;
    @Getter
    private boolean locked;

    /**
     * Creates a new dynamic button entity.
     *
     * @param label        the text to show on the button
     * @param w            width
     * @param h            height
     * @param onClick      the action that should happen when this button is clicked
     * @param defaultStyle how the button looks when it's NOT hovered
     * @param activeStyle  how the button looks when it IS hovered
     */
    public DynamicButtonEntity(String label, float x, float y, float w, float h, MouseEventHandler onClick,
                               Style defaultStyle, Style activeStyle) {
        super(label, x, y, w, h, onClick, defaultStyle);
        this.defaultStyle = defaultStyle;
        this.activeStyle = activeStyle;
    }

    /**
     * Updates the active and locked flags of this button
     *
     * @param active if active
     * @param locked if locked
     */
    public void setFlags(boolean active, boolean locked) {
        setActive(active);
        setLocked(locked);
    }

    /**
     * Sets the {@link DynamicButtonEntity#active} flag and updates the currently visible style of the button.
     */
    public void setActive(boolean active) {
        this.active = active;
        this.setStyle(active ? activeStyle : defaultStyle);
    }

    public void setLocked(boolean locked) {
        LOG.debug("{}ing button: {}", locked ? "Lock" : "Unlock", this);
        this.locked = locked;
    }

    @Override
    public void onMouseEnter(MouseEvent e) {
        if (!this.locked) this.setActive(true);
        super.onMouseEnter(e); // call event handler after changing style
    }

    @Override
    public void onMouseLeave(MouseEvent e) {
        if (!this.locked) this.setActive(false);
        super.onMouseLeave(e); // call event handler after changing style
    }
}
