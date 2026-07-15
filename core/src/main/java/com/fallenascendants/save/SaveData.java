package com.fallenascendants.save;

import com.fallenascendants.enumtype.BattleSpeed;

import java.util.ArrayList;

public class SaveData {

    // "versi ringkas" dari Card, cuma nyimpen yang perlu buat rebuild nanti
    public static class CardEntry {
        public String cardId;
        public int level;

        public CardEntry() {}

        public CardEntry(String cardId, int level) {
            this.cardId = cardId;
            this.level = level;
        }
    }

    public int gold = 0;
    public ArrayList<CardEntry> collection = new ArrayList<>();
    public ArrayList<Integer> deckIndices = new ArrayList<>();

    public float musicVolume = 1f;
    public float sfxVolume = 1f;
    public BattleSpeed battleSpeed = BattleSpeed.NORMAL;
}
