package com.logandhillon.logangamelib.api.render;

public record TextStyle(
        Color color,
        HorizontalAlign horizontalAlign,
        VerticalAlign verticalAlign,
        float maxWidth
) {
    public enum HorizontalAlign { LEFT, CENTER, RIGHT }

    public enum VerticalAlign { TOP, CENTER, BASELINE, BOTTOM }
}
