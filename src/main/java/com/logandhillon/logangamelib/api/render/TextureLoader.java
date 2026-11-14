package com.logandhillon.logangamelib.api.render;

/**
 * Loads textures for the active rendering backend.
 *
 * @author Logan Dhillon
 * @see TextureAssets#load(String)
 * @see Texture
 */
@FunctionalInterface
public interface TextureLoader {
    Texture load(String path);
}
