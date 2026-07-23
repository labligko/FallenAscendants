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
import com.fallenascendants.screen.InputNameScreen;
import com.fallenascendants.screen.MainMenuScreen;

public class FallenAscendantsGame extends Game {
    private Player player;

    private float musicVolume = 1f;
    private float sfxVolume = 1f;
    private BattleSpeed battleSpeed = BattleSpeed.NORMAL;

    // TAMBAHKAN: Method khusus untuk mereset variabel RAM ke default
    public void resetSettingsToDefault() {
        this.musicVolume = 1f;
        this.sfxVolume = 1f;
        this.battleSpeed = BattleSpeed.NORMAL;
    }

    @Override
    public void create() {
        player = new Player("");

        SaveData saveData = SaveManager.load();

        boolean isOldPlayer = saveData != null
            && saveData.playerName != null
            && !saveData.playerName.trim().isEmpty()
            && saveData.collection != null
            && !saveData.collection.isEmpty();

        if (isOldPlayer) {
            musicVolume = saveData.musicVolume;
            sfxVolume = saveData.sfxVolume;
            battleSpeed = saveData.battleSpeed;

            player.setName(saveData.playerName);
            SaveManager.applyToPlayer(saveData, player);
            setScreen(new MainMenuScreen(this));
        } else {
            // PEMAIN BARU: Reset setting & Panggil Custom Screen Input Nama!
            resetSettingsToDefault();
            setScreen(new InputNameScreen(this));
        }
    }

    // PASTIKAN INI PUBLIC
    public void processNewPlayer(final String playerName) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                player = new Player(playerName);

                for (int i = 0; i < 2; i++) {
                    Card randomCard = CardDatabase.getRandomCardByRarity(Rarity.COMMON);
                    player.addCardToCollection(randomCard);
                    player.getDeck().addCard(randomCard);
                }

                Card randomCard = CardDatabase.getRandomCardByRarity(Rarity.RARE);
                player.addCardToCollection(randomCard);
                player.getDeck().addCard(randomCard);

                saveProgress();
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
