package com.logandhillon.logangamelib.api.render;

/**
 * @author Logan Dhillon
 */
public record Color(float red, float green, float blue, float alpha) {
    public static final Color WHITE = new Color(1, 1, 1, 1);
    public static final Color BLACK = new Color(0, 0, 0, 1);

    public static Color rgb(int red, int green, int blue) {
        return new Color(red / 255f, green / 255f, blue / 255f, 1);
    }

    public static Color rgb(int red, int green, int blue, float alpha) {
        return new Color(red / 255f, green / 255f, blue / 255f, alpha);
    }
}

