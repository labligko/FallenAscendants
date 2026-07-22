package com.fallenascendants.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;
import java.util.Map;

public class SFXManager {

    // Cache untuk menyimpan efek suara agar file tidak dibaca berulang-ulang dari disk
    private static final Map<String, Sound> soundCache = new HashMap<>();
    private static float globalVolume = 0.3f;

    public static void play(String path) {
        play(path, 1.0f); // 1.0f artinya 100% dari volume global SFX
    }

    public static void play(String path, float localVolumeMod) {
        if (globalVolume <= 0f) return; // Kalau di-mute, nggak usah putar

        Sound sound = getOrLoadSound(path);
        if (sound != null) {
            // Volume akhir = volume global * modifikasi volume lokal
            float finalVolume = globalVolume * localVolumeMod;
            sound.play(finalVolume);
        }
    }

    private static Sound getOrLoadSound(String path) {
        if (!soundCache.containsKey(path)) {
            try {
                Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
                soundCache.put(path, sound);
            } catch (Exception e) {
                Gdx.app.error("SFXManager", "Gagal memuat SFX dari: " + path, e);
                return null;
            }
        }
        return soundCache.get(path);
    }

    public static void setVolume(float newVolume) {
        globalVolume = Math.max(0.0f, Math.min(1.0f, newVolume)); // Jaga-jaga biar gak lebih dari 1 atau kurang dari 0
    }

    public static float getVolume() {
        return globalVolume;
    }

    public static void dispose() {
        for (Sound sound : soundCache.values()) {
            if (sound != null) {
                sound.dispose();
            }
        }
        soundCache.clear();
    }
}
