package com.fallenascendants.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {
    private String name;
    private int gold;
    private Deck deck;
    private ArrayList<Card> collection;

    public Player(String name) {
        this.name = name;
        this.gold = 0;
        this.deck = new Deck();
        this.collection = new ArrayList<>();
    }

    public void addCardToCollection(Card card) {
        if (card != null) {
            collection.add(card);
        }
    }

    public boolean removeCardFromCollection(Card card) {
        return collection.remove(card);
    }

    public void addGold(int amount) {
        if (amount > 0) {
            gold += amount;
        }
    }

    public boolean spendGold(int amount) {
        if (amount <= 0 || gold < amount) {
            return false;
        }

        gold -= amount;
        return true;
    }

    public String getName() {
        return name;
    }

    public int getGold() {
        return gold;
    }

    public Deck getDeck() {
        return deck;
    }

    public List<Card> getCollection() {
        return Collections.unmodifiableList(collection);
    }
}
