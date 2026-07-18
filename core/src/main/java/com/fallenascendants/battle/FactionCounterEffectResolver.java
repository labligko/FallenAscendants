package com.fallenascendants.battle;

import com.fallenascendants.enumtype.CounterEffectType;
import com.fallenascendants.enumtype.StatusType;
import com.fallenascendants.model.BattleField;
import com.fallenascendants.model.Card;
import com.fallenascendants.model.StatusEffect;

import java.util.Random;

public class FactionCounterEffectResolver {
    private final FactionCounterGraph factionCounterGraph;
    private final Random random;

    public FactionCounterEffectResolver() {
        this.factionCounterGraph = new FactionCounterGraph();
        this.random = new Random();
    }

    public String resolveCounterEffect(
        Card attacker,
        Card target,
        BattleField allyField,
        int hpDamage
    ) {
        if (attacker == null || target == null || allyField == null) {
            return "";
        }

        if (hpDamage <= 0) {
            return "";
        }

        CounterEffectType effectType = factionCounterGraph.getCounterEffectType(
            attacker.getFaction(),
            target.getFaction()
        );

        if (effectType == CounterEffectType.NONE) {
            return "";
        }

        if (effectType == CounterEffectType.BONUS_DAMAGE) {
            return "";
        }

        StringBuilder log = new StringBuilder();

        log.append("Faction Counter Effect: ")
            .append(attacker.getFaction())
            .append(" counters ")
            .append(target.getFaction())
            .append("\n");

        switch (effectType) {
            case HEAL_LOWEST_ALLY:
                return resolveHealLowestAlly(log, allyField);

            case POISON_TARGET:
                target.addStatusEffect(new StatusEffect(
                    StatusType.POISON,
                    4,
                    1
                ));

                log.append(target.getName())
                    .append(" receives POISON for 1 turn(s).\n");

                return log.toString();

            case STUN_TARGET_CHANCE:
                int chance = random.nextInt(100) + 1;

                if (chance <= 25) {
                    target.addStatusEffect(new StatusEffect(
                        StatusType.STUN,
                        0,
                        1
                    ));

                    log.append(target.getName())
                        .append(" receives STUN for 1 turn(s).\n");
                } else {
                    log.append("Stun attempt failed.\n");
                }

                return log.toString();

            case BURN_TARGET:
                target.addStatusEffect(new StatusEffect(
                    StatusType.BURN,
                    6,
                    1
                ));

                log.append(target.getName())
                    .append(" receives BURN for 1 turn(s).\n");

                return log.toString();

            case SLOW_TARGET:
                target.addStatusEffect(new StatusEffect(
                    StatusType.SLOW,
                    12,
                    1
                ));

                log.append(target.getName())
                    .append(" receives SLOW for 1 turn(s).\n");

                return log.toString();

            default:
                return "";
        }
    }

    private String resolveHealLowestAlly(
        StringBuilder log,
        BattleField allyField
    ) {
        Card allyToHeal = selectLowestDamagedAlly(allyField.getActiveCards());

        if (allyToHeal == null) {
            log.append("No damaged ally to heal.\n");
            return log.toString();
        }

        int healAmount = 8;

        allyToHeal.heal(healAmount);

        log.append(allyToHeal.getName())
            .append(" is healed by ")
            .append(healAmount)
            .append(" HP.\n")
            .append(allyToHeal.getName())
            .append(" HP: ")
            .append(allyToHeal.getCurrentHp())
            .append("/")
            .append(allyToHeal.getMaxHp())
            .append("\n");

        return log.toString();
    }

    private Card selectLowestDamagedAlly(Card[] cards) {
        Card lowest = null;

        for (Card card : cards) {
            if (card == null || card.isDead()) {
                continue;
            }

            if (card.getCurrentHp() >= card.getMaxHp()) {
                continue;
            }

            if (lowest == null || card.getCurrentHp() < lowest.getCurrentHp()) {
                lowest = card;
            }
        }

        return lowest;
    }
}
