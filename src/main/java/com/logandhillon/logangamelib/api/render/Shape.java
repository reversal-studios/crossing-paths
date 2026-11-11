package com.logandhillon.logangamelib.api.render;

/**
 * @author Logan Dhillon
 */
public interface Shape {
    record Rect(float x, float y, float width, float height) implements Shape {}

    record RoundRect(float x, float y, float width, float height, float radius) implements Shape {}

    record Arc(float x, float y, float width, float height) implements Shape {}

    record Line(float x1, float y1, float x2, float y2) implements Shape {}

    record Circle(float centerX, float centerY, float radius) implements Shape {}

    /**
     * A polygon can be used to define a custom shape with a set of xy points.
     *
     * @param xPoints vector of all x points
     * @param yPoints vector of all y points
     * @param nPoints number of points
     */
    record Polygon(float[] xPoints, float[] yPoints, int nPoints) implements Shape {}
}
