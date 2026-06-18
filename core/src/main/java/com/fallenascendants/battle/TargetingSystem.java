package com.fallenascendants.battle;

import com.fallenascendants.model.Card;

import java.util.ArrayList;
import java.util.Random;

public class TargetingSystem {
    private final Random random;

    public TargetingSystem() {
        this.random = new Random();
    }

    public Card selectBasicAttackTarget(Card[] enemyCards) {
        int chance = random.nextInt(100) + 1;

        if (chance <= 85) {
            return selectTargetByAggro(enemyCards);
        }

        return selectRandomTarget(enemyCards);
    }

    public Card selectTargetByAggro(Card[] enemyCards) {
        ArrayList<Card> aliveTargets = getAliveCards(enemyCards);
        int totalAggro = 0;

        for (Card card : aliveTargets) {
            totalAggro += card.getAggro();
        }

        if (aliveTargets.isEmpty() || totalAggro <= 0) {
            return null;
        }

        int randomValue = random.nextInt(totalAggro) + 1;
        int currentAggro = 0;

        for (Card card : aliveTargets) {
            currentAggro += card.getAggro();

            if (randomValue <= currentAggro) {
                return card;
            }
        }

        return aliveTargets.get(0);
    }

    public Card selectRandomTarget(Card[] cards) {
        ArrayList<Card> aliveCards = getAliveCards(cards);

        if (aliveCards.isEmpty()) {
            return null;
        }

        return aliveCards.get(random.nextInt(aliveCards.size()));
    }

    public Card selectLowestHpTarget(Card[] cards) {
        Card lowest = null;

        for (Card card : cards) {
            if (card != null && !card.isDead()) {
                if (lowest == null || card.getCurrentHp() < lowest.getCurrentHp()) {
                    lowest = card;
                }
            }
        }

        return lowest;
    }

    private ArrayList<Card> getAliveCards(Card[] cards) {
        ArrayList<Card> aliveCards = new ArrayList<>();

        for (Card card : cards) {
            if (card != null && !card.isDead()) {
                aliveCards.add(card);
            }
        }

        return aliveCards;
    }
}
