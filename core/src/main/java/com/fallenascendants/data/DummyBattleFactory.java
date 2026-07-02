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
}
