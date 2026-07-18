package com.fallenascendants;

import com.badlogic.gdx.Game;
//import com.fallenascendants.debug.BattleTester;
import com.fallenascendants.enumtype.BattleSpeed;
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
        player = new Player("Reyzz");

        SaveData saveData = SaveManager.load();
        if (saveData != null) {
            SaveManager.applyToPlayer(saveData, player);
            musicVolume = saveData.musicVolume;
            sfxVolume = saveData.sfxVolume;
            battleSpeed = saveData.battleSpeed;
        }
//        BattleTester.runTest();
        setScreen(new MainMenuScreen(this));
    }

    //fungsi pemanggil save
    public void saveProgress() {
        SaveManager.save(player, musicVolume, sfxVolume, battleSpeed);
    }
    
    //Getter/setter biar screen lain bisa akses
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
