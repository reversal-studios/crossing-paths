package com.logandhillon.logangamelib.api.render;

import com.logandhillon.logangamelib.resource.ResourceLoader;
import lombok.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Backend-neutral texture cache used by engine and game asset declarations.
 *
 * @author Logan Dhillon
 * @see Texture
 */
public final class TextureAssets {
    private static final Map<String, Texture>    CACHE = new ConcurrentHashMap<>();
    private static       ResourceLoader<Texture> loader;

    private TextureAssets() {}

    public static synchronized void configure(@NonNull ResourceLoader<Texture> loader) {
        CACHE.clear();
        TextureAssets.loader = loader;
    }

    public static Texture load(String path) {
        if (loader == null) throw new IllegalStateException("TextureAssets has not been configured");
        return CACHE.computeIfAbsent(path, loader::load);
    }
}
