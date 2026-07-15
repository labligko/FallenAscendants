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

    public int getDuplicateCount(String cardId, Card excludeCard) {
        int count = 0;
        for (Card card : collection) {
            if (card != excludeCard && card.getId().equals(cardId)) {
                count++;
            }
        }
        return count;
    }

    public boolean upgradeCard(Card targetCard) {
        if (targetCard == null || !collection.contains(targetCard)) {
            return false;
        }

        int requiredDuplicates = targetCard.getLevel();
        int requiredGold = targetCard.getLevel() * 100;

        if (gold < requiredGold) {
            return false;
        }

        int availableDuplicates = getDuplicateCount(targetCard.getId(), targetCard);
        if (availableDuplicates < requiredDuplicates) {
            return false;
        }

        // Spend gold
        spendGold(requiredGold);

        // Remove duplicate cards from collection
        int removedCount = 0;
        for (int i = collection.size() - 1; i >= 0; i--) {
            Card card = collection.get(i);
            if (card != targetCard && card.getId().equals(targetCard.getId())) {
                collection.remove(i);
                removedCount++;
                if (removedCount == requiredDuplicates) {
                    break;
                }
            }
        }

        // Perform level up
        targetCard.levelUp();
        return true;
    }

    public void resetProgress() {
        gold = 0;
        collection.clear();
        deck.clear();
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
