package com.fallenascendants.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.fallenascendants.data.CardDatabase;
import com.fallenascendants.enumtype.BattleSpeed;
import com.fallenascendants.model.Card;
import com.fallenascendants.model.Player;

import java.util.ArrayList;
import java.util.List;

public class SaveManager {

    private static final String SAVE_PATH = "save/save_data.json";

    private static FileHandle getSaveFile() {
        return Gdx.files.local(SAVE_PATH);
    }

    private static int indexOfReference(List<Card> list, Card target) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == target) {
                return i;
            }
        }
        return -1;
    }

    public static void save(Player player, float musicVolume, float sfxVolume, BattleSpeed battleSpeed) {
        SaveData data = new SaveData();
        data.gold = player.getGold();
        data.musicVolume = musicVolume;
        data.sfxVolume = sfxVolume;
        data.battleSpeed = battleSpeed;

        List<Card> collection = player.getCollection();
        for (Card card : collection) {
            data.collection.add(new SaveData.CardEntry(card.getId(), card.getLevel()));
        }

        List<Card> deckCards = player.getDeck().getCards();
        for (Card deckCard : deckCards) {
            int index = indexOfReference(collection, deckCard);
            if (index >= 0) {
                data.deckIndices.add(index);
            }
        }

        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        getSaveFile().writeString(json.prettyPrint(data), false);
    }

    public static SaveData load() {
        FileHandle file = getSaveFile();
        if (!file.exists()) {
            return null; // belum pernah save sebelumnya, ini normal (misal first run)
        }

        Json json = new Json();
        return json.fromJson(SaveData.class, file.readString());
    }

    public static void applyToPlayer(SaveData data, Player player) {
        if (data == null || player == null) {
            return;
        }

        player.addGold(data.gold);

        List<Card> rebuiltCollection = new ArrayList<>();
        for (SaveData.CardEntry entry : data.collection) {
            Card card = CardDatabase.getCardById(entry.cardId); // selalu balikin level 1 fresh
            int levelUps = entry.level - card.getLevel();       // biasanya level - 1
            for (int i = 0; i < levelUps; i++) {
                card.levelUp();
            }
            rebuiltCollection.add(card);
            player.addCardToCollection(card);
        }

        for (Integer index : data.deckIndices) {
            if (index != null && index >= 0 && index < rebuiltCollection.size()) {
                player.getDeck().addCard(rebuiltCollection.get(index));
            }
        }
    }

    public static boolean deleteSave() {
        FileHandle file = getSaveFile();
        return !file.exists() || file.delete();
    }
}
