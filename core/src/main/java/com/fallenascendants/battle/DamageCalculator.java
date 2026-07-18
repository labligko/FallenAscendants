package com.fallenascendants.battle;

import com.fallenascendants.enumtype.CounterEffectType;
import com.fallenascendants.model.Card;

public class DamageCalculator {
    private final FactionCounterGraph factionCounterGraph;

    public DamageCalculator() {
        this.factionCounterGraph = new FactionCounterGraph();
    }

    public DamageResult calculateBasicAttack(Card attacker, Card target) {
        int baseDamage = attacker.getAtk();

        int rawDamage = factionCounterGraph.applyCounterBonus(
            attacker.getFaction(),
            target.getFaction(),
            baseDamage
        );

        int blockedByDefense =
            Math.min(target.getDef(), rawDamage - 1);

        int damageAfterDefense =
            Math.max(1, rawDamage - target.getDef());

        int shieldAbsorbed =
            target.calculateShieldAbsorption(damageAfterDefense);

        int hpDamage =
            damageAfterDefense - shieldAbsorbed;

        return new DamageResult(
            rawDamage,
            blockedByDefense,
            shieldAbsorbed,
            hpDamage
        );
    }

    public boolean hasFactionCounterBonus(Card attacker, Card target) {
        return factionCounterGraph.getCounterEffectType(
            attacker.getFaction(),
            target.getFaction()
        ) == CounterEffectType.BONUS_DAMAGE;
    }

    public int calculateFactionCounterBonusDamage(Card attacker, Card target) {
        return factionCounterGraph.calculateBonusDamage(
            attacker.getFaction(),
            target.getFaction(),
            attacker.getAtk()
        );
    }
}
