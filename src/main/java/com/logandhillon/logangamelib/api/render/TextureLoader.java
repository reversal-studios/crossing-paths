package com.logandhillon.logangamelib.api.render;

/** Loads textures for the active rendering backend. */
@FunctionalInterface
public interface TextureLoader {
    Texture load(String path);
}
