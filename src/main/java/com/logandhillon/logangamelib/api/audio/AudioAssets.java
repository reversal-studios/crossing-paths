package com.logandhillon.logangamelib.api.audio;

import lombok.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Backend-neutral audio cache used by engine and game asset declarations.
 *
 * @author Logan Dhillon
 */
public final class AudioAssets {
    private static final Map<String, Audio> CACHE = new ConcurrentHashMap<>();
    private static       AudioLoader        loader;

    private AudioAssets() {}

    public static synchronized void configure(@NonNull AudioLoader loader) {
        CACHE.clear();
        AudioAssets.loader = loader;
    }

    public static Audio load(String path) {
        if (loader == null) throw new IllegalStateException("AudioAssets has not been configured");
        return CACHE.computeIfAbsent(path, loader::load);
    }
}
