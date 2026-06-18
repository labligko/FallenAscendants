package com.fallenascendants.battle;

public class DamageResult {
    private final int rawDamage;
    private final int blockedByDefense;
    private final int shieldAbsorbed;
    private final int hpDamage;

    public DamageResult(
        int rawDamage,
        int blockedByDefense,
        int shieldAbsorbed,
        int hpDamage
    ) {
        this.rawDamage = rawDamage;
        this.blockedByDefense = blockedByDefense;
        this.shieldAbsorbed = shieldAbsorbed;
        this.hpDamage = hpDamage;
    }

    public int getRawDamage() {
        return rawDamage;
    }

    public int getBlockedByDefense() {
        return blockedByDefense;
    }

    public int getShieldAbsorbed() {
        return shieldAbsorbed;
    }

    public int getHpDamage() {
        return hpDamage;
    }
}
