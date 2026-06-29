package com.fallenascendants.battle;

import com.fallenascendants.model.Card;

import java.util.LinkedList;
import java.util.Queue;

public class TurnQueue {
    private Queue<Card> actionQueue;

    public TurnQueue() {
        this.actionQueue = new LinkedList<>();
    }

    public void buildQueue(Card[] playerCards, Card[] enemyCards) {
        actionQueue.clear();

        addAliveCards(playerCards);
        addAliveCards(enemyCards);

        sortBySpeed();
    }

    private void addAliveCards(Card[] cards) {
        for (Card card : cards) {
            if (card != null && !card.isDead()) {
                actionQueue.add(card);
            }
        }
    }

    private void sortBySpeed() {
        LinkedList<Card> sortedList = new LinkedList<>(actionQueue);

        sortedList.sort((card1, card2) -> card2.getEffectiveSpd() - card1.getEffectiveSpd());

        actionQueue.clear();
        actionQueue.addAll(sortedList);
    }

    public Card getNextCard() {
        return actionQueue.poll();
    }

    public boolean isEmpty() {
        return actionQueue.isEmpty();
    }

    public void clear() {
        actionQueue.clear();
    }
}
