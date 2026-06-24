package com.fallenascendants.model;

import java.util.LinkedList;

public class BattleField {
    private static final int ACTIVE_SLOT_SIZE = 5;
    private static final int RESERVE_SLOT_SIZE = 3;

    private Card[] activeCards;
    private LinkedList<Card> reserveCards;
    private LinkedList<Card> graveyard;

    public BattleField(Deck deck) {
        this.activeCards = new Card[ACTIVE_SLOT_SIZE];
        this.reserveCards = new LinkedList<>();
        this.graveyard = new LinkedList<>();

        setupFromDeck(deck);
    }

    private void setupFromDeck(Deck deck) {
        for (int i = 0; i < deck.size(); i++) {
            Card card = deck.getCard(i);

            if (i < ACTIVE_SLOT_SIZE) {
                activeCards[i] = card;
            } else if (reserveCards.size() < RESERVE_SLOT_SIZE) {
                reserveCards.add(card);
            }
        }
    }

    public Card[] getActiveCards() {
        return activeCards;
    }

    public LinkedList<Card> getReserveCards() {
        return reserveCards;
    }

    public LinkedList<Card> getGraveyard() {
        return graveyard;
    }

    public void removeDeadCardsAndReplace() {
        for (int i = 0; i < activeCards.length; i++) {
            Card card = activeCards[i];

            if (card != null && card.isDead()) {
                graveyard.add(card);

                if (!reserveCards.isEmpty()) {
                    activeCards[i] = reserveCards.removeFirst();
                } else {
                    activeCards[i] = null;
                }
            }
        }
    }

    public boolean hasAliveCards() {
        for (Card card : activeCards) {
            if (card != null && !card.isDead()) {
                return true;
            }
        }

        return false;
    }
}
