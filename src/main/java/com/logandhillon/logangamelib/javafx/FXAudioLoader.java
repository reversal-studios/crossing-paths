package com.logandhillon.logangamelib.javafx;

import com.logandhillon.logangamelib.api.audio.Audio;
import com.logandhillon.logangamelib.resource.ResourceLoader;
import javafx.scene.media.AudioClip;

/**
 * Loads classpath audio clips for the JavaFX backend.
 *
 * @author Logan Dhillon
 */
public final class FXAudioLoader implements ResourceLoader<Audio> {
    @Override
    public Audio load(String path) {
        String classpath = path.startsWith("/") ? path : "/sound/" + path;
        var url = FXAudioLoader.class.getResource(classpath);
        if (url == null) throw new IllegalArgumentException("Audio resource not found: " + classpath);
        return new FXAudio(new AudioClip(url.toExternalForm()));
    }
}
