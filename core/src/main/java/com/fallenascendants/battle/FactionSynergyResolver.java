package com.fallenascendants.battle;

import com.fallenascendants.enumtype.Faction;
import com.fallenascendants.model.Card;

import java.util.HashMap;

public class FactionSynergyResolver {

    public String applyFactionSynergy(Card[] activeCards, String ownerName) {
        HashMap<Faction, Integer> factionCount = countFactions(activeCards);
        StringBuilder log = new StringBuilder();

        log.append("Applying ").append(ownerName).append(" faction synergy...\n");

        for (Faction faction : factionCount.keySet()) {
            int count = factionCount.get(faction);
            int bonusPercent = getBonusPercent(count);

            if (bonusPercent <= 0) {
                continue;
            }

            log.append(applyBonus(activeCards, faction, bonusPercent));

            log.append("- ")
                .append(faction)
                .append(" synergy active: ")
                .append(count)
                .append("/5 cards, ATK +")
                .append(bonusPercent)
                .append("%\n");
        }

        return log.toString();
    }

    private HashMap<Faction, Integer> countFactions(Card[] activeCards) {
        HashMap<Faction, Integer> factionCount = new HashMap<>();

        for (Card card : activeCards) {
            if (card != null && !card.isDead()) {
                factionCount.put(
                    card.getFaction(),
                    factionCount.getOrDefault(card.getFaction(), 0) + 1
                );
            }
        }

        return factionCount;
    }

    private int getBonusPercent(int count) {
        if (count >= 5) {
            return 25;
        }

        if (count >= 3) {
            return 15;
        }

        if (count >= 2) {
            return 10;
        }

        return 0;
    }

    private String applyBonus(Card[] activeCards, Faction faction, int bonusPercent) {
        StringBuilder log = new StringBuilder();

        for (Card card : activeCards) {
            if (card != null && !card.isDead() && card.getFaction() == faction) {
                int beforeAtk = card.getAtk();

                card.increaseAtkByPercent(bonusPercent);

                log.append("  > ")
                    .append(card.getName())
                    .append(" ATK: ")
                    .append(beforeAtk)
                    .append(" -> ")
                    .append(card.getAtk())
                    .append("\n");
            }
        }

        return log.toString();
    }
}
