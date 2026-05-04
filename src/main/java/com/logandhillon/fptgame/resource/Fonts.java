package com.logandhillon.fptgame.resource;

import com.logandhillon.logangamelib.resource.FontResource;
import javafx.scene.text.Font;

/**
 * Loads and manages all font families. The constants in this class are font families, which can be used with JavaFX's
 * {@link Font#font}.
 *
 * @author Logan Dhillon
 */
public class Fonts {
    public static final FontResource TREMOLO = new FontResource("tremolo", "tremolo-mono.ttf");

    /**
     * no-op to force the class to load
     */
    public static void init() {}
}
