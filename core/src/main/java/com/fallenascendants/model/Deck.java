package com.fallenascendants.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private static final int MAX_DECK_SIZE = 8;

    private final ArrayList<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
    }

    public boolean addCard(Card card) {
        if (card == null) {
            return false;
        }

        if (isFull()) {
            return false;
        }

        cards.add(card);
        return true;
    }

    public boolean removeCard(Card card) {
        return cards.remove(card);
    }

    public Card removeCardByIndex(int index) {
        if (index < 0 || index >= cards.size()) {
            return null;
        }

        return cards.remove(index);
    }

    public Card getCard(int index) {
        if (index < 0 || index >= cards.size()) {
            return null;
        }

        return cards.get(index);
    }

    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    public int size() {
        return cards.size();
    }

    public boolean isFull() {
        return cards.size() >= MAX_DECK_SIZE;
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public boolean isValidForBattle() {
        return cards.size() == MAX_DECK_SIZE;
    }

    public void clear() {
        cards.clear();
    }
}
