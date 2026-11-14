package com.logandhillon.logangamelib.api.render;

/**
 * A vector-representation of a colour to be used in the LGL render api.
 *
 * @param red   red channel, 0.0-1.0 (float)
 * @param green green channel, 0.0-1.0 (float)
 * @param blue  blue channel, 0.0-1.0 (float)
 * @param alpha alpha channel, 0.0-1.0 (float)
 *
 * @author Logan Dhillon
 * @see Renderer
 */
public record Color(float red, float green, float blue, float alpha) {
    public static final Color WHITE = new Color(1, 1, 1, 1);
    public static final Color BLACK = new Color(0, 0, 0, 1);

    /**
     * Builds a colour vector using RGB CSS4 (0-255) components.
     * <p>
     * Alpha channel defaults to 100% (opaque).
     *
     * @param red   red channel, 0-255 (int)
     * @param green green channel, 0-255 (int)
     * @param blue  blue channel, 0-255 (int)
     */
    public static Color rgb(int red, int green, int blue) {
        return new Color(red / 255f, green / 255f, blue / 255f, 1);
    }

    /**
     * Builds a colour vector using RGB CSS4 (0-255) components, including an alpha channel.
     *
     * @param red   red channel, 0-255 (int)
     * @param green green channel, 0-255 (int)
     * @param blue  blue channel, 0-255 (int)
     * @param alpha alpha channel, 0.0-1.0 (float)
     */
    public static Color rgb(int red, int green, int blue, float alpha) {
        return new Color(red / 255f, green / 255f, blue / 255f, alpha);
    }
}

