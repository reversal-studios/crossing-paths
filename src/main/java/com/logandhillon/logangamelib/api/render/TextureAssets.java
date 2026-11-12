package com.logandhillon.logangamelib.api.render;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Backend-neutral texture cache used by engine and game asset declarations. */
public final class TextureAssets {
    private static final Map<String, Texture> CACHE = new ConcurrentHashMap<>();
    private static       TextureLoader        loader;

    private TextureAssets() {}

    public static synchronized void configure(TextureLoader textureLoader) {
        if (textureLoader == null) throw new NullPointerException("textureLoader");
        CACHE.clear();
        loader = textureLoader;
    }

    public static Texture load(String path) {
        if (loader == null) throw new IllegalStateException("TextureAssets has not been configured");
        return CACHE.computeIfAbsent(path, loader::load);
    }
}
