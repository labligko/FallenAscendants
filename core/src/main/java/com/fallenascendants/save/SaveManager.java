package com.fallenascendants.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.fallenascendants.data.CardDatabase;
import com.fallenascendants.enumtype.BattleSpeed;
import com.fallenascendants.model.Card;
import com.fallenascendants.model.Player;

import java.util.List;

public class SaveManager {

    private static final String SAVE_PATH = "save/save_data.json";

    private static FileHandle getSaveFile() {
        return Gdx.files.local(SAVE_PATH);
    }

    public static void save(Player player, float musicVolume, float sfxVolume, BattleSpeed battleSpeed) {
        SaveData data = new SaveData();
        data.playerName = player.getName();
        data.gold = player.getGold();
        data.musicVolume = musicVolume;
        data.sfxVolume = sfxVolume;
        data.battleSpeed = battleSpeed;

        // Simpan koleksi beserta jumlah duplikatnya
        for (Card card : player.getCollection()) {
            data.collection.add(new SaveData.CardEntry(card.getId(), card.getLevel(), card.getCopies()));
        }

        // Simpan Deck menggunakan ID kartu (Aman dari bug sorting)
        for (Card deckCard : player.getDeck().getCards()) {
            data.deckCardIds.add(deckCard.getId());
        }

        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        getSaveFile().writeString(json.prettyPrint(data), false);
    }

    public static SaveData load() {
        FileHandle file = getSaveFile();
        if (!file.exists()) {
            return null; // Trigger Pemain Baru
        }

        Json json = new Json();
        return json.fromJson(SaveData.class, file.readString());
    }

    public static void applyToPlayer(SaveData data, Player player) {
        if (data == null || player == null) {
            return;
        }

        player.resetProgress(); // Bersihkan player saat ini biar data tidak menumpuk ganda
        player.addGold(data.gold);

        for (SaveData.CardEntry entry : data.collection) {
            Card card = CardDatabase.getCardById(entry.cardId);
            if (card != null) {
                int levelUps = entry.level - card.getLevel();
                for (int i = 0; i < levelUps; i++) {
                    card.levelUp();
                }
                // Pakai method load khusus
                player.loadCardToCollection(card, entry.copies);
            }
        }

        // Load Deck
        for (String cardId : data.deckCardIds) {
            // Cocokkan ID dari koleksi player yang sudah di-load
            for (Card collectionCard : player.getCollection()) {
                if (collectionCard.getId().equals(cardId)) {
                    player.getDeck().addCard(collectionCard);
                    break;
                }
            }
        }
    }

    public static boolean deleteSave() {
        FileHandle file = getSaveFile();
        return !file.exists() || file.delete();
    }
}
