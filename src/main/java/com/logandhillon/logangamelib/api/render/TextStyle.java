package com.logandhillon.logangamelib.api.render;

/**
 * Represents the style of rendereable text, including its colour, alignment, and maximum width.
 *
 * @param color         colour of text
 * @param justification horizontal alignment or rag of text
 * @param vAlign        vertical alignment of text
 * @param maxWidth      maximum width of text before it's cut off
 *
 * @author Logan Dhillon
 */
public record TextStyle(
        Color color,
        Justification justification,
        VAlign vAlign,
        float maxWidth
) {
    /**
     * Horizontal alignment or rag of a typeface.
     */
    public enum Justification { LEFT, CENTER, RIGHT }

    /**
     * Vertical alignment of a typeface.
     */
    public enum VAlign { TOP, CENTER, BASELINE, BOTTOM }
}
