package com.logandhillon.logangamelib.javafx;

import com.logandhillon.logangamelib.api.render.Texture;
import javafx.scene.image.Image;

/** JavaFX-backed texture. Kept out of the public rendering API. */
final class FXTexture implements Texture {
    private final Image image;

    FXTexture(Image image) {
        this.image = image;
    }

    Image image() {
        return image;
    }

    @Override public int width() { return (int) image.getWidth(); }
    @Override public int height() { return (int) image.getHeight(); }
}
