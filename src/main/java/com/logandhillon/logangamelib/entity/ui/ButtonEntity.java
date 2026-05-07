package com.logandhillon.logangamelib.entity.ui;

import com.logandhillon.logangamelib.entity.Clickable;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import lombok.Setter;

/**
 * A button is a stylized {@link Clickable} entity that can have a label and action that runs when it's clicked.
 * <p>
 * To change the way a button looks, you must create a {@link Style}, which is (a) passed to the button in the
 * constructor or (b) changed during the button's lifecycle with the {@link ButtonEntity#setStyle(Style)} method.
 *
 * @author Logan Dhillon
 * @see Style
 */
public class ButtonEntity extends Clickable {
    private static final int STROKE            = 2;
    private static final int ROUNDING_DIAMETER = 50;

    private String label;

    private final MouseEventHandler clickHandler;

    /**
     * the action that will be run when the mouse cursor ENTERS this button.
     */
    @Setter
    private MouseEventHandler mouseEnterHandler;
    /**
     * the action that will be run when the mouse cursor LEAVES this button.
     */
    @Setter
    private MouseEventHandler mouseLeaveHandler;

    /**
     * the currently visible style.
     */
    @Setter
    protected Style style;

    /**
     * Creates a new button entity.
     *
     * @param label   the text to show on the button
     * @param w       width
     * @param h       height
     * @param onClick the action that should happen when this button is clicked
     * @param style   how the button looks
     */
    public ButtonEntity(String label, float x, float y, float w, float h, MouseEventHandler onClick,
                        Style style) {
        super(x, y, w, h);
        this.label = label;
        this.clickHandler = onClick;
        this.style = style;
    }

    @Override
    public void onClick(MouseEvent e) {
        clickHandler.handle(e);
    }

    @Override
    public void onMouseEnter(MouseEvent e) {
        if (mouseEnterHandler != null) mouseEnterHandler.handle(e);
    }

    @Override
    public void onMouseLeave(MouseEvent e) {
        if (mouseLeaveHandler != null) mouseLeaveHandler.handle(e);
    }

    @Override
    public void onUpdate(float dt) {

    }

    /**
     * Renders the button background, then the button text on top of it, based on what was supplied when the button was
     * created.
     *
     * @param g the graphical context to render to.
     * @param x the x position to render the entity at
     * @param y the y position to render the entity at
     */
    @Override
    protected void onRender(GraphicsContext g, float x, float y) {
        float cx = x + w / 2;
        float cy = (y + h / 2);
        // render button background
        if (style.variant == Variant.OUTLINE) {
            g.setStroke(style.buttonColor);
            g.setLineWidth(STROKE);
            g.setLineDashes(0);

            if (style.isRounded) g.strokeRoundRect(x, y, w, h, ROUNDING_DIAMETER, ROUNDING_DIAMETER);
            else g.strokeRect(x, y, w, h);
        } else {
            g.setFill(style.buttonColor);
            if (style.isRounded) g.fillRoundRect(x, y, w, h, ROUNDING_DIAMETER, ROUNDING_DIAMETER);
            else g.fillRect(x, y, w, h);
        }

        // render label
        g.setFill(style.labelColor);
        g.setFont(style.font);
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);
        g.fillText(label, cx, cy);
    }

    @Override
    public void onDestroy() {

    }

    /**
     * Represents the way a {@link ButtonEntity} looks like.
     */
    public enum Variant {
        SOLID, OUTLINE,
    }

    /**
     * A handler that can be (anonymously) created to handle events related to the mouse.
     *
     * @see MouseEvent
     */
    public interface MouseEventHandler {
        void handle(MouseEvent e);
    }

    public void setText(String label) {
        this.label = label;
    }

    /**
     * An immutable style object that represents the look of a {@link ButtonEntity}.
     *
     * @param labelColor  the color of the button text.
     * @param buttonColor the color of the button background.
     * @param variant     the {@link Variant} that the button looks like
     * @param isRounded   if the button has rounded corners
     * @param font        the font of the button's text.
     */
    public record Style(
            Color labelColor,
            Color buttonColor,
            Variant variant,
            boolean isRounded,
            Font font) {}
}
