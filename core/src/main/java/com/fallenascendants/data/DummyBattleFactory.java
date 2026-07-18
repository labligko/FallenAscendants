package com.fallenascendants.data;

import com.fallenascendants.battle.BattleManager;
import com.fallenascendants.model.BattleField;
import com.fallenascendants.model.Card;
import com.fallenascendants.model.Deck;

import java.util.Collections;
import java.util.List;

public class DummyBattleFactory {

    public static BattleManager createBattle() {
        Deck playerDeck = createBalancedPlayerDeck();
        Deck enemyDeck = createRandomDeck();

        return new BattleManager(
            new BattleField(playerDeck),
            new BattleField(enemyDeck)
        );
    }

    public static Deck createBalancedPlayerDeck() {
        Deck deck = new Deck();

        List<Card> allCards = CardDatabase.getAllCards();
        Collections.shuffle(allCards);

        for (int i = 0; i < 8 && i < allCards.size(); i++) {
            deck.addCard(allCards.get(i));
        }

        return deck;
    }

    public static Deck createRandomDeck() {
        Deck deck = new Deck();

        List<Card> allCards = CardDatabase.getAllCards();
        Collections.shuffle(allCards);

        for (int i = 0; i < 8 && i < allCards.size(); i++) {
            deck.addCard(allCards.get(i));
        }

        return deck;
    }

    // Tambahin di DummyBattleFactory.java, gak ganti method createRandomDeck() yang lama
// (biar BattleTester.java yang masih pakai versi lama gak kena dampak)

    public static Deck createRandomEnemyDeck(int playerDeckSize) {
        int enemySize = rollEnemyDeckSize(playerDeckSize);

        Deck deck = new Deck();
        List<Card> allCards = CardDatabase.getAllCards();
        Collections.shuffle(allCards);

        for (int i = 0; i < enemySize && i < allCards.size(); i++) {
            deck.addCard(allCards.get(i));
        }

        return deck;
    }

    private static int rollEnemyDeckSize(int playerDeckSize) {
        final int MAX_DECK_SIZE = 8;
        java.util.Random random = new java.util.Random();
        double roll = random.nextDouble();

        if (roll < 0.50) {
            // 50% - lebih besar dari player, di-skew condong ke 8 (hard lebih sering muncul)
            int lowerBound = Math.min(playerDeckSize + 1, MAX_DECK_SIZE);

            if (lowerBound >= MAX_DECK_SIZE) {
                return MAX_DECK_SIZE;
            }

            int range = MAX_DECK_SIZE - lowerBound;
            double skewed = Math.pow(random.nextDouble(), 2); // dekat 0 = sering (deket 8), dekat 1 = jarang (deket lowerBound)
            int subtraction = (int) Math.floor(skewed * range);

            return MAX_DECK_SIZE - subtraction;
        } else if (roll < 0.85) {
            return Math.max(1, playerDeckSize); // 35% - fair fight
        } else {
            // 15% - lebih kecil dari player, di-skew biar ukuran sangat kecil jarang muncul
            int upperBound = Math.max(1, playerDeckSize - 1);

            if (upperBound <= 1) {
                return 1;
            }

            double skewed = Math.pow(random.nextDouble(), 2);
            int reduction = (int) Math.floor(skewed * (upperBound - 1));

            return upperBound - reduction;
        }
    }
}
