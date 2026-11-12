package com.logandhillon.logangamelib.javafx;

import com.logandhillon.logangamelib.api.render.Texture;
import com.logandhillon.logangamelib.api.render.TextureLoader;
import javafx.scene.image.Image;

import java.io.InputStream;

/**
 * Loads classpath textures for the JavaFX backend.
 *
 * @author Logan Dhillon
 * @see FXRenderer
 */
public final class FXTextureLoader implements TextureLoader {
    @Override
    public Texture load(String path) {
        String resourcePath = path.startsWith("/") ? path : "/gfx/" + path;
        InputStream stream = FXTextureLoader.class.getResourceAsStream(resourcePath);
        if (stream == null) throw new IllegalArgumentException("Texture resource not found: " + resourcePath);
        return new FXTexture(new Image(stream));
    }
}
