package com.logandhillon.logangamelib.api.render;

/**
 * Abstract representation of a shape that can be given to the LGL render API.
 *
 * @author Logan Dhillon
 * @see Renderer
 */
public sealed interface Shape
        permits Shape.Arc, Shape.Circle, Shape.Line, Shape.Polygon, Shape.Rect, Shape.RoundRect {
    /**
     * A rectangle.
     *
     * @param x      x-coordinate of left-edge
     * @param y      y-coordinate of top-edge
     * @param width  width of rect
     * @param height height of rect
     */
    record Rect(float x, float y, float width, float height) implements Shape {}

    /**
     * A rectangle with rounded corners.
     *
     * @param x      x-coordinate of left-edge
     * @param y      y-coordinate of top-edge
     * @param width  width of rect
     * @param height height of rect
     * @param radius corner radius
     */
    record RoundRect(float x, float y, float width, float height, float radius) implements Shape {}

    /**
     * An arc, defined by a centre point and angles.
     *
     * @param x       x-coordinate of centre
     * @param y       y-coordinate of centre
     * @param width   width of arc
     * @param height  height of arc
     * @param azimuth initial angle to start arc at
     * @param bearing terminal angle to end arc at
     */
    record Arc(float x, float y, float width, float height, float azimuth, float bearing) implements Shape {}

    /**
     * A line between two points.
     *
     * @param x1 x-coordinate of first point
     * @param y1 y-coordinate of first point
     * @param x2 x-coordinate of second point
     * @param y2 y-coordinate of second point
     */
    record Line(float x1, float y1, float x2, float y2) implements Shape {}

    /**
     * A circle, defined by a centre point and radius.
     *
     * @param centerX x-coordinate of centre
     * @param centerY y-coordinate of centre
     * @param radius  radius of circle (distance between centre point and outer edge)
     */
    record Circle(float centerX, float centerY, float radius) implements Shape {}

    /**
     * A polygon, used to define a custom shape with a set of xy points.
     *
     * @param xPoints vector of all x points
     * @param yPoints vector of all y points
     * @param nPoints number of points
     */
    record Polygon(float[] xPoints, float[] yPoints, int nPoints) implements Shape {}
}
