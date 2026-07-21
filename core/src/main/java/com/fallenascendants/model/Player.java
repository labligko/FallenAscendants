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
        if (card == null) return;

        // 1. Cek apakah kartu sudah ada di koleksi
        for (Card existingCard : collection) {
            if (existingCard.getId().equals(card.getId())) {
                existingCard.addCopies(1); // Kalau sudah ada, cukup tambah 1 lembar
                return;
            }
        }

        // 2. Jika belum ada, masukkan sebagai kartu baru
        card.setCopies(1);
        collection.add(card);
    }

    // Method khusus untuk load data dari JSON (bypass logika "tambah 1")
    public void loadCardToCollection(Card card, int copies) {
        if (card != null) {
            card.setCopies(copies);
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

    public boolean upgradeCard(Card targetCard) {
        if (targetCard == null || !collection.contains(targetCard)) {
            return false;
        }

        int requiredDuplicates = targetCard.getLevel();
        int requiredGold = targetCard.getLevel() * 100;

        if (gold < requiredGold) {
            return false;
        }

        // Cek total lembar. Syaratnya: lembar saat ini dikurangi 1 (kartu asli) harus mencukupi untuk bahan bakar.
        if (targetCard.getCopies() - 1 < requiredDuplicates) {
            return false;
        }

        // Potong gold & potong copies
        spendGold(requiredGold);
        targetCard.removeCopies(requiredDuplicates);

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
