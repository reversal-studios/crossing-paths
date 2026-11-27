package com.logandhillon.logangamelib.javafx;

import com.logandhillon.logangamelib.api.audio.Audio;
import javafx.scene.media.AudioClip;

/**
 * JavaFX-backed audio clip.
 *
 * @author Logan Dhillon
 */
final class FXAudio implements Audio {
    private final AudioClip clip;

    FXAudio(AudioClip clip) {
        this.clip = clip;
    }

    @Override
    public void play(float volume) {
        clip.play(volume);
    }

    @Override
    public void stop() {
        clip.stop();
    }
}
