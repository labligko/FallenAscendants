package com.fallenascendants.battle;

import com.fallenascendants.model.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class TurnQueue {
    private final PriorityQueue<TurnEntry> queue;
    private final StatusEffectResolver statusEffectResolver;
    private int orderCounter;

    public TurnQueue() {
        this.statusEffectResolver = new StatusEffectResolver();

        this.queue = new PriorityQueue<>((a, b) -> {
            int speedCompare = Integer.compare(b.effectiveSpeed, a.effectiveSpeed);

            if (speedCompare != 0) {
                return speedCompare;
            }

            return Integer.compare(a.order, b.order);
        });

        this.orderCounter = 0;
    }

    public void buildQueue(Card[] playerCards, Card[] enemyCards) {
        clear();

        addCards(playerCards);
        addCards(enemyCards);
    }

    private void addCards(Card[] cards) {
        if (cards == null) {
            return;
        }

        for (Card card : cards) {
            if (card == null || card.isDead()) {
                continue;
            }

            addCard(card);
        }
    }

    private void addCard(Card card) {
        int effectiveSpeed = statusEffectResolver.calculateEffectiveSpd(card);

        queue.offer(new TurnEntry(
            card,
            effectiveSpeed,
            orderCounter++
        ));
    }

    public Card getNextCard() {
        while (!queue.isEmpty()) {
            TurnEntry entry = queue.poll();

            if (entry.card != null && !entry.card.isDead()) {
                return entry.card;
            }
        }

        return null;
    }

    public void rebuildRemainingQueue() {
        List<Card> remainingCards = new ArrayList<>();

        while (!queue.isEmpty()) {
            TurnEntry entry = queue.poll();

            if (entry.card != null && !entry.card.isDead()) {
                remainingCards.add(entry.card);
            }
        }

        for (Card card : remainingCards) {
            addCard(card);
        }
    }

    public String getQueueReport() {
        StringBuilder report = new StringBuilder();

        report.append("Upcoming Priority Queue:\n");

        PriorityQueue<TurnEntry> copy = new PriorityQueue<>(queue);

        int position = 1;

        while (!copy.isEmpty() && position <= 10) {
            TurnEntry entry = copy.poll();

            report.append(position)
                .append(". ")
                .append(entry.card.getName())
                .append(" | SPD: ")
                .append(entry.card.getSpd())
                .append(" | Effective SPD: ")
                .append(entry.effectiveSpeed);

            if (entry.card.isDead()) {
                report.append(" | DEAD");
            }

            report.append("\n");

            position++;
        }

        if (position == 1) {
            report.append("(Queue empty, next round will rebuild.)\n");
        }

        return report.toString();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void clear() {
        queue.clear();
        orderCounter = 0;
    }

    public int size() {
        return queue.size();
    }

    private static class TurnEntry {
        private final Card card;
        private final int effectiveSpeed;
        private final int order;

        private TurnEntry(Card card, int effectiveSpeed, int order) {
            this.card = card;
            this.effectiveSpeed = effectiveSpeed;
            this.order = order;
        }
    }
}
