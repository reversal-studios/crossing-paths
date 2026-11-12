package com.logandhillon.logangamelib.javafx;

import com.logandhillon.logangamelib.api.render.Texture;
import com.logandhillon.logangamelib.api.render.TextureLoader;
import java.io.InputStream;
import javafx.scene.image.Image;

/** Loads classpath textures for the JavaFX backend. */
public final class FXTextureLoader implements TextureLoader {
    @Override
    public Texture load(String path) {
        String resourcePath = path.startsWith("/") ? path : "/gfx/" + path;
        InputStream stream = FXTextureLoader.class.getResourceAsStream(resourcePath);
        if (stream == null) throw new IllegalArgumentException("Texture resource not found: " + resourcePath);
        return new FXTexture(new Image(stream));
    }
}
