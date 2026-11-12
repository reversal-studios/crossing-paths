package com.logandhillon.logangamelib.javafx;

import com.logandhillon.logangamelib.api.render.Texture;
import javafx.scene.image.Image;

/**
 * JavaFX-backed texture, kept out of the public LGL rendering API.
 *
 * @author Logan Dhillon
 * @see FXRenderer
 */
record FXTexture(Image image) implements Texture {
    @Override
    public int width() {
        return (int)image.getWidth();
    }

    @Override
    public int height() {
        return (int)image.getHeight();
    }
}
