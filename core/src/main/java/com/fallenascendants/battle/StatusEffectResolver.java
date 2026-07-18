package com.fallenascendants.battle;

import com.fallenascendants.enumtype.StatusType;
import com.fallenascendants.model.Card;
import com.fallenascendants.model.StatusEffect;

public class StatusEffectResolver {
    private boolean shouldSkipTurn;

    public String applyStatusEffectsAtTurnStart(Card card) {
        StringBuilder log = new StringBuilder();

        shouldSkipTurn = false;
        boolean isStunned = false;

        for (StatusEffect effect : card.getStatusEffects()) {
            if (effect.getStatusType() == StatusType.BURN) {
                log.append(applyBurn(card, effect));
            }

            if (effect.getStatusType() == StatusType.POISON) {
                log.append(applyPoison(card, effect));
            }

            if (effect.getStatusType() == StatusType.SLOW) {
                log.append(applySlow(card, effect));
            }

            if (effect.getStatusType() == StatusType.STUN) {
                shouldSkipTurn = true;
                log.append(applyStun(card, effect));
            }

            effect.reduceDuration();
        }

        card.removeExpiredStatus();

        if (isStunned) {
            log.append(card.getName())
                .append(" cannot act this turn.\n");
        }

        return log.toString();
    }

    public boolean isStunned(Card card) {
        for (StatusEffect effect : card.getStatusEffects()) {
            if (effect.getStatusType() == StatusType.STUN) {
                return true;
            }
        }

        return false;
    }

    private String applyBurn(Card card, StatusEffect effect) {
        card.takeDirectDamage(effect.getPower());

        return card.getName() + " suffers " + effect.getPower() + " burn damage.\n"
            + card.getName() + " HP: " + card.getCurrentHp() + "/" + card.getMaxHp() + "\n";
    }

    private String applyPoison(Card card, StatusEffect effect) {
        int poisonDamage = Math.max(1, card.getMaxHp() * effect.getPower() / 100);

        card.takeDirectDamage(poisonDamage);

        return card.getName() + " suffers " + poisonDamage + " poison damage.\n"
            + card.getName() + " HP: " + card.getCurrentHp() + "/" + card.getMaxHp() + "\n";
    }

    private String applySlow(Card card, StatusEffect effect) {
        return card.getName() + " is slowed. SPD -" + effect.getPower() + "\n"
            + "Effective SPD: " + card.getEffectiveSpd() + "\n";
    }

    public int calculateEffectiveSpd(Card card) {
        int effectiveSpd = card.getSpd();

        for (StatusEffect statusEffect : card.getStatusEffects()) {
            if (statusEffect.getStatusType() == StatusType.SLOW) {
                effectiveSpd -= statusEffect.getPower();
            }
        }

        return Math.max(1, effectiveSpd);
    }

    private String applyStun(Card card, StatusEffect effect) {
        return card.getName() + " is stunned.\n";
    }

    public boolean shouldSkipTurn() {
        return shouldSkipTurn;
    }
}
