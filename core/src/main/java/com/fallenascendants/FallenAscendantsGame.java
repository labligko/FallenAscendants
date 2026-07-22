package com.fallenascendants;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.fallenascendants.data.CardDatabase;
import com.fallenascendants.enumtype.BattleSpeed;
import com.fallenascendants.enumtype.Rarity;
import com.fallenascendants.model.Card;
import com.fallenascendants.model.Player;
import com.fallenascendants.save.SaveData;
import com.fallenascendants.save.SaveManager;
import com.fallenascendants.screen.MainMenuScreen;

public class FallenAscendantsGame extends Game {
    private Player player;

    private float musicVolume = 1f;
    private float sfxVolume = 1f;
    private BattleSpeed battleSpeed = BattleSpeed.NORMAL;

    @Override
    public void create() {
        player = new Player("");

        SaveData saveData = SaveManager.load();

        // 1. Muat Setting dulu jika ada (walaupun itu pemain baru yang barusan reset lalu ganti setting)
        if (saveData != null) {
            musicVolume = saveData.musicVolume;
            sfxVolume = saveData.sfxVolume;
            battleSpeed = saveData.battleSpeed;
        }

        // 2. Cek apakah ini benar-benar Pemain Lama yang punya progress
        boolean isOldPlayer = saveData != null
            && saveData.playerName != null
            && !saveData.playerName.isEmpty()
            && !saveData.collection.isEmpty(); // Pengecekan krusial: punya kartu gak?

        if (isOldPlayer) {
            // ==========================================
            // PEMAIN LAMA (Load Data)
            // ==========================================
            player.setName(saveData.playerName);
            SaveManager.applyToPlayer(saveData, player);
            setScreen(new MainMenuScreen(this));
        } else {
            // ==========================================
            // PEMAIN BARU (Munculkan Dialog Nama)
            // ==========================================
            showNameInputDialog();
        }
    }

    private void showNameInputDialog() {
        // Memunculkan dialog text input bawaan OS (Android/Windows/Desktop)
        Gdx.input.getTextInput(new Input.TextInputListener() {
            @Override
            public void input(String text) {
                // Jika pemain tidak mengisi nama, beri nama default
                if (text == null || text.trim().isEmpty()) {
                    text = "Ascendant";
                }
                processNewPlayer(text);
            }

            @Override
            public void canceled() {
                // Jika pemain menekan Cancel, tetap beri nama default agar tidak error
                processNewPlayer("Ascendant");
            }
        }, "Kelahiran Baru", "", "Masukkan Nama Karakter...");
    }

    private void processNewPlayer(final String playerName) {
        // WAJIB menggunakan postRunnable!
        // Karena dialog text input berjalan di luar thread utama OpenGL (UI Thread OS),
        // sedangkan LibGDX membutuhkan thread utama untuk memuat aset dan mengubah screen.
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                player.setName(playerName);

                // Berikan 2 kartu acak COMMON
                for (int i = 0; i < 2; i++) {
                    Card randomCard = CardDatabase.getRandomCardByRarity(Rarity.COMMON);
                    player.addCardToCollection(randomCard);
                    player.getDeck().addCard(randomCard);
                }

                // Berikan 1 kartu acak RARE
                Card randomCard = CardDatabase.getRandomCardByRarity(Rarity.RARE);
                player.addCardToCollection(randomCard);
                player.getDeck().addCard(randomCard);

                // Langsung save progress awal
                saveProgress();

                // Masuk ke Main Menu
                setScreen(new MainMenuScreen(FallenAscendantsGame.this));
            }
        });
    }

    public void saveProgress() {
        SaveManager.save(player, musicVolume, sfxVolume, battleSpeed);
    }

    public Player getPlayer() { return player; }
    public float getMusicVolume() { return musicVolume; }
    public void setMusicVolume(float musicVolume) { this.musicVolume = musicVolume; }
    public float getSfxVolume() { return sfxVolume; }
    public void setSfxVolume(float sfxVolume) { this.sfxVolume = sfxVolume; }
    public BattleSpeed getBattleSpeed() { return battleSpeed; }
    public void setBattleSpeed(BattleSpeed battleSpeed) { this.battleSpeed = battleSpeed; }
}
