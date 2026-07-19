package com.fallenascendants.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class MusicManager {

    private static Music currentMusic;

    public static void play(String path, boolean looping) {

        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
        }

        currentMusic = Gdx.audio.newMusic(Gdx.files.internal(path));
        currentMusic.setLooping(looping);
        currentMusic.setVolume(0.6f);
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
}
