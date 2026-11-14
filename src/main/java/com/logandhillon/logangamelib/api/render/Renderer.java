package com.logandhillon.logangamelib.api.render;

/**
 * The base of the LGL render API, providing an abstraction for all render-related tasks.
 * <p>
 * This interface is passed to an entity's {@code onRender} method to draw to the graphics buffer.
 *
 * @author Logan Dhillon
 */
public interface Renderer {
    /**
     * Clears the graphics buffer and sets the clear colour to the specified colour.
     *
     * @param color new clear colour
     */
    void clear(Color color);

    /**
     * Draws a texture to the screen.
     *
     * @param texture texture to draw
     * @param x       x-pos to draw at
     * @param y       y-pos to draw at
     * @param width   width of destination
     * @param height  height of destination
     */
    void draw(Texture texture, float x, float y, float width, float height);

    /**
     * Draws a recoloured/tinted texture to the screen.
     *
     * @param texture texture to draw
     * @param x       x-pos to draw at
     * @param y       y-pos to draw at
     * @param width   width of destination
     * @param height  height of destination
     * @param tint    colour to tint the texture with
     */
    void draw(Texture texture, float x, float y, float width, float height, Color tint);

    /**
     * Draws a recoloured/tinted texture to the screen from a texture atlas.
     *
     * @param texture texture to draw
     * @param source  source region to fetch texture from
     * @param x       x-pos to draw at
     * @param y       y-pos to draw at
     * @param width   width of destination
     * @param height  height of destination
     * @param tint    colour to tint the texture with. set to white for no tint.
     */
    void draw(Texture texture, Texture.Region source, float x, float y, float width, float height, Color tint);

    /**
     * Draws a filled-in shape to the screen.
     *
     * @param shape shape to draw
     * @param color colour to fill in the shape with
     */
    void fill(Shape shape, Color color);

    /**
     * Draws an outlined/stroked shape to the screen
     *
     * @param shape     shape to draw
     * @param color     colour to outline with (stroke colour)
     * @param lineWidth width of outline/stroke
     */
    void stroke(Shape shape, Color color, float lineWidth);

    /**
     * Draws specified text to the screen.
     *
     * @param font  font to write text in
     * @param text  text to draw
     * @param x     x-pos of text
     * @param y     y-pos of text
     * @param style font style
     */
    void text(Font font, String text, float x, float y, TextStyle style);

    /**
     * Allows drawing inside a clipping boundary. Any calls to the renderer inside clip content will be graphically
     * clipped to the clipping region.
     *
     * @param clip    clipping region/shape
     * @param content render calls bound to clipping region
     */
    void clip(Shape clip, Runnable content);

    // methods for JavaFX backwards compatability

    /** @deprecated Use {@link #fill(Shape, Color)} or a convenience method that takes a colour. */
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

    /**
     * Draws a filled-in rectangle to the screen.
     *
     * @param x     left edge of rectangle
     * @param y     top edge of rectangle
     * @param w     width of rectangle
     * @param h     height of rectangle
     * @param color colour of rectangle
     */
    default void rect(float x, float y, float w, float h, Color color) {
        fill(new Shape.Rect(x, y, w, h), color);
    }

    /**
     * Draws an outlined rectangle to the screen.
     *
     * @param x         left edge of rectangle
     * @param y         top edge of rectangle
     * @param w         width of rectangle
     * @param h         height of rectangle
     * @param lineWidth outline/stroke width
     * @param color     colour of rectangle
     */
    default void strokeRect(float x, float y, float w, float h, float lineWidth, Color color) {
        stroke(new Shape.Rect(x, y, w, h), color, lineWidth);
    }

    /**
     * Draws a filled-in rectangle with rounded edges to the screen.
     *
     * @param x      left edge of rectangle
     * @param y      top edge of rectangle
     * @param w      width of rectangle
     * @param h      height of rectangle
     * @param color  colour of rectangle
     * @param radius corner radius of rounded area
     */
    default void roundRect(float x, float y, float w, float h, Color color, int radius) {
        fill(new Shape.RoundRect(x, y, w, h, radius), color);
    }

    /**
     * Draws an outlined rectangle with rounded edges to the screen.
     *
     * @param x         left edge of rectangle
     * @param y         top edge of rectangle
     * @param w         width of rectangle
     * @param h         height of rectangle
     * @param lineWidth outline/stroke width
     * @param color     colour of rectangle
     * @param radius    corner radius of rounded area
     */
    default void strokeRoundRect(float x, float y, float w, float h, float lineWidth, Color color, int radius) {
        stroke(new Shape.RoundRect(x, y, w, h, radius), color, lineWidth);
    }

    /**
     * Draws a line from one point to another.
     *
     * @param x1        x-coordinate of first point
     * @param y1        y-coordinate of first point
     * @param x2        x-coordinate of second point
     * @param y2        y-coordinate of second point
     * @param color     line colour
     * @param lineWidth line thickness
     */
    default void line(float x1, float y1, float x2, float y2, Color color, float lineWidth) {
        stroke(new Shape.Line(x1, y1, x2, y2), color, lineWidth);
    }
}
