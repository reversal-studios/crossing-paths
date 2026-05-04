package com.logandhillon.fptgame.entity.ui;

import com.logandhillon.fptgame.resource.Sounds;
import com.logandhillon.logangamelib.entity.ui.InputBoxEntity;
import javafx.scene.input.MouseEvent;

/**
 * A version of the LGL {@link InputBoxEntity} that plays a click sound when focused
 *
 * @author Logan Dhillon
 * @see InputBoxEntity
 */
public class InputBox extends InputBoxEntity {
    /**
     * Creates an input field at the specified position. THe height will be calculated from a fixed y-margin of 12px and
     * the font size.
     *
     * @param x           x-position (from left)
     * @param y           y-position (from top)
     * @param w           width of the input box
     * @param placeholder placeholder text (shown when box is blank)
     * @param label       the label to show above the input box, will use the same font size.
     * @param charLimit   maximum allowed characters in this field
     */
    public InputBox(float x, float y, float w, String placeholder, String label, int charLimit) {
        super(x, y, w, placeholder, label, charLimit);
    }

    @Override
    public void onClick(MouseEvent e) {
        super.onClick(e);
        Sounds.playSfx(Sounds.UI_CLICK);
    }
}
