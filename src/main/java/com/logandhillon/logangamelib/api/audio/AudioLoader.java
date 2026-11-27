package com.logandhillon.logangamelib.api.audio;

/**
 * Loads audio clips for the active rendering backend.
 *
 * @author Logan Dhillon
 */
@FunctionalInterface
public interface AudioLoader {
    Audio load(String path);
}
