package com.logandhillon.logangamelib.api.render;

/**
 * A texture is a map of external pixels to the engine's graphics buffer. (aka. an image)
 * <p>
 * Textures must belong to the API implementation spec to be used in that API, and cannot be used across mulitple APIs.
 * Textures must also be loaded into memory before use.
 *
 * @author Logan Dhillon
 * @see TextureAssets#load(String)
 */
public interface Texture {
    int width();

    int height();

    /**
     * A texture region is a uv-coordinate index of a buffer matrix to be used in the LGL render API.
     * <p>
     * Simply put, use this to render individual tiles of a larger texture.
     *
     * @param x      x (u) coordinate of texture in atlas
     * @param y      y (v) coordinate of texture in atlas
     * @param width  width of texture in atlas
     * @param height height of texture in atlas
     */
    record Region(float x, float y, float width, float height) {}
}
