package com.logandhillon.fptgame.resource;

import com.logandhillon.fptgame.GameHandler;
import com.logandhillon.logangamelib.api.audio.Audio;
import com.logandhillon.logangamelib.api.audio.AudioAssets;

/**
 * @author Logan Dhillon
 */
public class Sounds {
    public static final Audio UI_CLICK = AudioAssets.load("ui/click.wav");
    public static final Audio UI_FAIL  = AudioAssets.load("ui/fail.wav");

    public static final Audio GAME_START_LEVEL = AudioAssets.load("game/restart.wav");
    public static final Audio GAME_RESPAWN     = AudioAssets.load("game/respawn.wav");
    public static final Audio GAME_STEP        = AudioAssets.load("game/step.wav");
    public static final Audio GAME_JUMP        = AudioAssets.load("game/jump.wav");
    public static final Audio GAME_LAND        = AudioAssets.load("game/land.wav");

    private static float musicVol;
    private static float sfxVol;

    /**
     * Recalculates volumes from {@link GameHandler#getUserConfig()}
     */
    public static void calcVolume() {
        var c = GameHandler.getUserConfig();
        musicVol = c.getMasterVolume() * c.getMusicVolume();
        sfxVol = c.getMasterVolume() * c.getSfxVolume();
    }

    /**
     * Plays a sound effect with the volume in the user's config.
     *
     * @param sound sound to play
     */
    public static void playSfx(Audio sound) {
        sound.play(sfxVol);
    }

    /**
     * Plays audio with the music volume in the user's config.
     *
     * @param sound audio to play
     */
    public static void playMusic(Audio sound) {
        sound.play(musicVol);
    }
}
