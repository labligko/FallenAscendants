package com.fallenascendants.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class MusicManager {

    private static Music currentMusic;
    private static float volume = 0.6f;

    /** Overload lama tetep ada, biar pemanggilan yang belum sempet diupdate gak error compile — pakai volume terakhir yang di-set. */
    public static void play(String path, boolean looping) {
        play(path, looping, volume);
    }

    public static void play(String path, boolean looping, float initialVolume) {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
        }

        volume = initialVolume;
        currentMusic = Gdx.audio.newMusic(Gdx.files.internal(path));
        currentMusic.setLooping(looping);
        currentMusic.setVolume(volume);
        currentMusic.play();
    }

    public static void pause() {
        if (currentMusic != null) {
            currentMusic.pause();
        }
    }

    public static void resume() {
        if (currentMusic != null) {
            currentMusic.play();
        }
    }

    public static void stop() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
            currentMusic = null;
        }
    }

    /** Dipanggil dari SettingsScreen tiap slider volume digeser, biar musik yang LAGI JALAN ikut berubah live. */
    public static void setVolume(float newVolume) {
        volume = newVolume;
        if (currentMusic != null) {
            currentMusic.setVolume(volume);
        }
    }
}
