package com.logandhillon.logangamelib.entity.ui;

import com.logandhillon.logangamelib.api.render.Color;
import com.logandhillon.logangamelib.api.render.Font;
import com.logandhillon.logangamelib.api.render.Renderer;
import com.logandhillon.logangamelib.api.render.TextStyle;
import com.logandhillon.logangamelib.engine.GameMeta;
import com.logandhillon.logangamelib.entity.Entity;
import com.logandhillon.logangamelib.resource.base.Colors;

import java.util.function.Supplier;

/**
 * A text entity is a highly-customizable text display.
 *
 * @author Logan Dhillon
 */
public class TextEntity extends Entity {
    private final Supplier<String>        dynamicText;
    private final boolean                 isStatic;
    private final Font                    font;
    private final float                   lineHeight;
    private final Color                   color;
    private final TextStyle.Justification justification;
    private final TextStyle.VAlign        baseline;

    private String[] lines;
    private String   lastDynamicText;

    /**
     * Creates a text entity.
     *
     * @param dynamicText   the content of the text to render
     * @param staticText    static non-changing text to render if dynamic text is null
     * @param font          the font of the text to render
     * @param color         the color of the text
     * @param justification the horizontal alignment
     * @param valign        the vertical alignment
     * @param x             x-position (from left)
     * @param y             y-position (from top)
     */
    private TextEntity(Supplier<String> dynamicText, String staticText, Font font, float lineHeight, Color color,
                       TextStyle.Justification justification, TextStyle.VAlign valign, float x, float y) {
        super(x, y);
        this.dynamicText = dynamicText;
        this.isStatic = dynamicText == null;
        this.lines = isStatic ? staticText.split("\n") : dynamicText.get().split("\n");
        this.font = font;
        this.lineHeight = font.size() * lineHeight;
        this.color = color;
        this.justification = justification;
        this.baseline = valign;
    }

    @Override
    protected void onRender(Renderer g, float x, float y) {
        // draw every line at increasing y offset
        for (int i = 0; i < lines.length; i++)
            g.text(
                    font, lines[i], x, y + i * lineHeight,
                    new TextStyle(
                            color,
                            justification, baseline,
                            Float.MAX_VALUE)); // TODO: probably shouldnt set the max width to the largest possible
        //                                              number but ill fix it "later"
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
        private String                  staticText;
        private Supplier<String>        dynamicText;
        private Font                    font;
        private float                   lineHeight    = 1.5f;
        private Color                   color         = Colors.FOREGROUND;
        private TextStyle.Justification justification = TextStyle.Justification.LEFT;
        private TextStyle.VAlign        valign        = TextStyle.VAlign.BASELINE;

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
            this.font = new Font(GameMeta.get().defaultFont.load(), fontSize);
            return this;
        }

        public Builder setColor(Color color) {
            this.color = color;
            return this;
        }

        public Builder setJustification(TextStyle.Justification justification) {
            this.justification = justification;
            return this;
        }

        public Builder setVerticalAlignment(TextStyle.VAlign valign) {
            this.valign = valign;
            return this;
        }

        public Builder setLineHeight(float lineHeight) {
            this.lineHeight = lineHeight;
            return this;
        }

        public TextEntity build() {
            return new TextEntity(dynamicText, staticText, font, lineHeight, color, justification, valign, x, y);
        }
    }
}
