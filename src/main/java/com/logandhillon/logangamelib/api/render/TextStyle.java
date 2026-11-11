package com.logandhillon.logangamelib.api.render;

public record TextStyle(
        Color color,
        Justification justification,
        VAlign vAlign,
        float maxWidth
) {
    public enum Justification { LEFT, CENTER, RIGHT }

    public enum VAlign { TOP, CENTER, BASELINE, BOTTOM }
}
