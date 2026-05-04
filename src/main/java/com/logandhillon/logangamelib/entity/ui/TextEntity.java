package com.logandhillon.logangamelib.entity.ui;

import com.logandhillon.logangamelib.engine.GameMeta;
import com.logandhillon.logangamelib.entity.Entity;
import com.logandhillon.logangamelib.resource.base.Colors;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.function.Supplier;

/**
 * A text entity is a highly-customizable text display.
 *
 * @author Logan Dhillon
 */
public class TextEntity extends Entity {
    private final Supplier<String> dynamicText;
    private final boolean          isStatic;
    private final Font             font;
    private final float            lineHeight;
    private final Color            color;
    private final TextAlignment    align;
    private final VPos             baseline;

    private String[] lines;
    private String   lastDynamicText;

    /**
     * Creates a text entity.
     *
     * @param dynamicText the content of the text to render
     * @param staticText  static non-changing text to render if dynamic text is null
     * @param font        the font of the text to render
     * @param color       the color of the text
     * @param align       the horizontal alignment
     * @param baseline    the vertical alignment
     * @param x           x-position (from left)
     * @param y           y-position (from top)
     */
    private TextEntity(Supplier<String> dynamicText, String staticText, Font font, float lineHeight, Color color,
                       TextAlignment align,
                       VPos baseline, float x, float y) {
        super(x, y);
        this.dynamicText = dynamicText;
        this.isStatic = dynamicText == null;
        this.lines = isStatic ? staticText.split("\n") : dynamicText.get().split("\n");
        this.font = font;
        this.lineHeight = (float)font.getSize() * lineHeight;
        this.color = color;
        this.align = align;
        this.baseline = baseline;
    }

    @Override
    protected void onRender(GraphicsContext g, float x, float y) {
        g.setFill(color);
        g.setFont(font);
        g.setTextAlign(align);
        g.setTextBaseline(baseline);
        // draw every line at increasing y offset
        for (int i = 0; i < lines.length; i++) g.fillText(lines[i], x, y + i * lineHeight);
    }

    @Override
    public void onUpdate(float dt) {
        // update text if not static
        if (!isStatic) {
            String text = dynamicText.get();
            if (!text.equals(lastDynamicText) && text.contains("\n")) {
                // only recalculate it if the text changed and has newlines
                this.lines = text.split("\n");
                this.lastDynamicText = text;
            }
        }
    }

    @Override
    public void onDestroy() {

    }

    public static final class Builder {
        private String           staticText;
        private Supplier<String> dynamicText;
        private Font             font;
        private float            lineHeight = 1.5f;
        private Color            color      = Colors.FOREGROUND;
        private TextAlignment    align      = TextAlignment.LEFT;
        private VPos             baseline   = VPos.BASELINE;

        private final float x;
        private final float y;

        public Builder(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public Builder setText(Supplier<String> text) {
            this.dynamicText = text;
            this.staticText = null;
            return this;
        }

        public Builder setText(String text) {
            this.dynamicText = null;
            this.staticText = text;
            return this;
        }

        public Builder setFont(Font font) {
            this.font = font;
            return this;
        }

        public Builder setFontSize(int fontSize) {
            this.font = Font.font(GameMeta.get().defaultFont.load(), fontSize);
            return this;
        }

        public Builder setColor(Color color) {
            this.color = color;
            return this;
        }

        public Builder setAlign(TextAlignment align) {
            this.align = align;
            return this;
        }

        public Builder setBaseline(VPos baseline) {
            this.baseline = baseline;
            return this;
        }

        public Builder setLineHeight(float lineHeight) {
            this.lineHeight = lineHeight;
            return this;
        }

        public TextEntity build() {
            return new TextEntity(dynamicText, staticText, font, lineHeight, color, align, baseline, x, y);
        }
    }
}
