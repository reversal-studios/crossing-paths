package com.logandhillon.logangamelib.entity.ui;

import com.logandhillon.logangamelib.resource.base.Colors;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.ArcType;

import java.util.function.Consumer;

public class SliderEntity extends Draggable {
    private static final int BACKBONE_DIAMETER = 6;
    private static final int CIRCLE_DIAMETER   = 20;

    private final Consumer<Float> updater;

    private float   value;
    private boolean active;

    /**
     * Creates slider at the specified position.
     *
     * @param x            x-position (from left)
     * @param y            y-position (from top)
     * @param defaultValue default value of the slider (0.0-1.0)
     * @param onUpdate     consumer called when the value is changed
     */
    public SliderEntity(float x, float y, float w, float h, float defaultValue, Consumer<Float> onUpdate) {
        super(x, y, w, h);
        this.updater = onUpdate;
        this.value = Math.clamp(defaultValue * w, 0, w);
    }

    @Override
    protected void onRender(GraphicsContext g, float x, float y) {
        // backbone
        g.setFill(Colors.ACTIVE_TRANS_50);
        g.fillRoundRect(x, y, w, h, BACKBONE_DIAMETER, BACKBONE_DIAMETER);

        // backbone filled part
        g.setFill(Colors.BUTTON_HOVER);
        g.fillRoundRect(x, y, value + 5, h, BACKBONE_DIAMETER, BACKBONE_DIAMETER);

        // slider knob
        g.setFill(active ? Colors.SLIDER_HEAD_ACTIVE : Colors.SLIDER_HEAD);
        g.fillArc(value + x, y - BACKBONE_DIAMETER, CIRCLE_DIAMETER, CIRCLE_DIAMETER, 0, 360, ArcType.ROUND);
    }

    @Override
    public void onUpdate(float dt) {}

    @Override
    public void onDestroy() {}

    @Override
    public void onClick(MouseEvent e) {}

    @Override
    public void onMouseDown(MouseEvent e) {
        active = true;
        updateValue((float)e.getX());
    }

    @Override
    public void onMouseUp(MouseEvent e) {
        if (active) {
            active = false;
            // officially "update" this value only when the mouse is released
            updater.accept(getValue());
        }
    }

    @Override
    public void onMouseDragged(MouseEvent e) {
        if (!this.active) return;
        updateValue((float)e.getX());
    }

    /**
     * Sets the value based on the x position of the mouse.
     */
    private void updateValue(float mouseX) {
        this.value = Math.clamp(mouseX - x, 0, w);
    }

    /**
     * Gets the value on a percentage scale.
     *
     * @return value of slider (0.0-1.0)
     */
    public float getValue() {
        return value / w;
    }
}
