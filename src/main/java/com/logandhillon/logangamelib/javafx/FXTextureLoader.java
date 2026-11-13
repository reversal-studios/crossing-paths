package com.logandhillon.logangamelib.javafx;

import com.logandhillon.logangamelib.api.render.Texture;
import com.logandhillon.logangamelib.api.render.TextureLoader;
import javafx.scene.image.Image;

import java.io.IOException;
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
        String classpath = path.startsWith("/") ? path : "/gfx/" + path;
        try (InputStream stream = FXTextureLoader.class.getResourceAsStream(classpath)) {
            if (stream == null) throw new IllegalArgumentException("Texture resource not found: " + classpath);
            return new FXTexture(new Image(stream));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FX texture from classpath", e);
        }
    }
}
