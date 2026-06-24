package com.fallenascendants.battle;

import com.fallenascendants.model.Card;

public class DamageCalculator {

    public DamageResult calculateBasicAttack(Card attacker, Card target) {

        int rawDamage = attacker.getAtk();

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
}
