package com.fallenascendants;

import com.badlogic.gdx.Game;
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
        if (saveData != null) {
            // ==========================================
            // PEMAIN LAMA (Load Data)
            // ==========================================
            SaveManager.applyToPlayer(saveData, player);
            musicVolume = saveData.musicVolume;
            sfxVolume = saveData.sfxVolume;
            battleSpeed = saveData.battleSpeed;
        } else {
            // ==========================================
            // PEMAIN BARU (Starter Pack)
            // ==========================================
            // Berikan 2 kartu acak dengan Rarity COMMON
            for (int i = 0; i < 2; i++) {
                Card randomCard = CardDatabase.getRandomCardByRarity(Rarity.COMMON);

                // Tambahkan ke koleksi
                player.addCardToCollection(randomCard);

                // Tambahkan ke deck secara otomatis
                player.getDeck().addCard(randomCard);
            }

            // Berikan 1 kartu acak dengan Rarity RARE
            Card randomCard = CardDatabase.getRandomCardByRarity(Rarity.RARE);

            // Tambahkan ke koleksi
            player.addCardToCollection(randomCard);

            // Tambahkan ke deck secara otomatis
            player.getDeck().addCard(randomCard);

            // Langsung save agar file JSON terbentuk dengan 3 kartu ini
            saveProgress();
        }

        setScreen(new MainMenuScreen(this));
    }

    public void saveProgress() {
        SaveManager.save(player, musicVolume, sfxVolume, battleSpeed);
    }

    public Player getPlayer() {
        return player;
    }

    public float getMusicVolume() { return musicVolume; }
    public void setMusicVolume(float musicVolume) { this.musicVolume = musicVolume; }

    public float getSfxVolume() { return sfxVolume; }
    public void setSfxVolume(float sfxVolume) { this.sfxVolume = sfxVolume; }

    public BattleSpeed getBattleSpeed() { return battleSpeed; }
    public void setBattleSpeed(BattleSpeed battleSpeed) { this.battleSpeed = battleSpeed; }
}
