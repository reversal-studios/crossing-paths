package com.logandhillon.logangamelib.resource;

import com.logandhillon.logangamelib.api.audio.Audio;
import com.logandhillon.logangamelib.api.audio.AudioAssets;

import java.io.FileNotFoundException;

/**
 * Loads an audio resource from the {@code sound/} folder through the configured audio backend.
 *
 * @author Logan Dhillon
 */
public class AudioResource extends Resource<Audio> {
    private final String filename;

    public AudioResource(String filename) throws FileNotFoundException {
        super("sound/" + filename);
        this.filename = filename;
    }

    public Audio load() {
        return AudioAssets.load(filename);
    }
}
