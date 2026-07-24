package com.fallenascendants.save;

import com.fallenascendants.enumtype.BattleSpeed;
import java.util.ArrayList;

public class SaveData {

    public String playerName;

    public static class CardEntry {
        public String cardId;
        public int level;
        public int copies; // Menyimpan jumlah duplikat kartu

        public CardEntry() {}

        public CardEntry(String cardId, int level, int copies) {
            this.cardId = cardId;
            this.level = level;
            this.copies = copies;
        }
    }

    public int gold = 0;
    public ArrayList<CardEntry> collection = new ArrayList<>();

    // GANTI: Jangan simpan index, simpan ID Kartu agar Deck kebal dari error saat disorting
    public ArrayList<String> deckCardIds = new ArrayList<>();

    public float musicVolume = 1f;
    public float sfxVolume = 1f;
    public BattleSpeed battleSpeed = BattleSpeed.NORMAL;
}
