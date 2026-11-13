package com.logandhillon.logangamelib.api.render;

/**
 * A font is a size, weight, and style of a typeface,
 *
 * @param family font family name, must be loaded into engine context
 * @param size   size of font in (pt)
 * @param weight {@link FontWeight}
 *
 * @author Logan Dhillon
 */
public record Font(String family, float size, FontWeight weight) {
    public enum FontWeight { NORMAL, MEDIUM, BOLD }

    public Font(String family, float size) {
        this(family, size, FontWeight.NORMAL);
    }
}
