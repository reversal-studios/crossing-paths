package com.logandhillon.logangamelib.api.render;

/**
 * @author Logan Dhillon
 */
public interface Renderer {
    void clear(Color color);

    void draw(Texture texture, float x, float y, float width, float height);

    void draw(Texture texture, float x, float y, float width, float height, Color tint);

    void draw(Texture texture, Texture.Region source, float x, float y, float width, float height, Color tint);

    void fill(Shape shape, Color color);

    void stroke(Shape shape, Color color, float lineWidth);

    void text(Font font, String text, float x, float y, TextStyle style);

    void clip(Shape clip, Runnable content);

    /** @deprecated Use {@link #fill(Shape, Color)} or a convenience method that takes a color. */
    @Deprecated
    void setFill(Color color);

    /** @deprecated Use {@link #stroke(Shape, Color, float)}. */
    @Deprecated
    void setStroke(Color color);

    /** @deprecated Pass the width to {@link #stroke(Shape, Color, float)}. */
    @Deprecated
    void setLineWidth(float width);

    /** @deprecated Use {@link #text(Font, String, float, float, TextStyle)}. */
    @Deprecated
    void setFont(Font font);

    /** @deprecated Use {@link #text(Font, String, float, float, TextStyle)}. */
    @Deprecated
    void setTextStyle(TextStyle style);

    /** @deprecated Use {@link #roundRect(float, float, float, float, Color, int)}. */
    @Deprecated
    void fillRoundRect(float x, float y, float width, float height, float arcWidth, float arcHeight);

    /** @deprecated Use {@link #line(float, float, float, float, Color, float)}. */
    @Deprecated
    void strokeLine(float x1, float y1, float x2, float y2);

    /** @deprecated Use {@link #text(Font, String, float, float, TextStyle)}. */
    @Deprecated
    void fillText(String text, float x, float y);

    // default methods for common shapes
    default void rect(float x, float y, float w, float h, Color color) {
        fill(new Shape.Rect(x, y, w, h), color);
    }

    default void strokeRect(float x, float y, float w, float h, float lineWidth, Color color) {
        stroke(new Shape.Rect(x, y, w, h), color, lineWidth);
    }

    default void roundRect(float x, float y, float w, float h, Color color, int radius) {
        fill(new Shape.RoundRect(x, y, w, h, radius), color);
    }

    default void strokeRoundRect(float x, float y, float w, float h, float lineWidth, Color color, int radius) {
        stroke(new Shape.RoundRect(x, y, w, h, radius), color, lineWidth);
    }

    default void line(float x1, float y1, float x2, float y2, Color color, float lineWidth) {
        stroke(new Shape.Line(x1, y1, x2, y2), color, lineWidth);
    }
}
