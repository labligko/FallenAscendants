package com.fallenascendants.enumtype;

public enum Faction {
    CELESTIAL_REMNANTS,
    VOID_CORRUPTED,
    ABYSSAL_CHURCH,
    DRAGON_LINEAGE,
    MORTAL_ASCENDANTS,
    SHADOW_CONCLAVE;

    public enum CounterEffect {
        NONE,
        HEAL_LOWEST_ALLY,   // Celestial Remnants -> Abyssal Church
        POISON_TARGET,      // Abyssal Church -> Mortal Ascendants
        STUN_CHANCE,        // Mortal Ascendants -> Dragon Lineage
        BURN_TARGET,        // Dragon Lineage -> Void Corrupted
        SLOW_TARGET,        // Void Corrupted -> Shadow Conclave
        BONUS_DAMAGE        // Shadow Conclave -> Celestial Remnants
    }

    // Satu sumber kebenaran buat tabel counter -- getCounterEffect() dan counters()
    // dua-duanya nurut ke sini, gak ada 2 tempat yang bisa ketinggalan sinkron.
    public CounterEffect getCounterEffect(Faction target) {
        if (target == null) {
            return CounterEffect.NONE;
        }
        switch (this) {
            case CELESTIAL_REMNANTS:
                return target == ABYSSAL_CHURCH ? CounterEffect.HEAL_LOWEST_ALLY : CounterEffect.NONE;
            case ABYSSAL_CHURCH:
                return target == MORTAL_ASCENDANTS ? CounterEffect.POISON_TARGET : CounterEffect.NONE;
            case MORTAL_ASCENDANTS:
                return target == DRAGON_LINEAGE ? CounterEffect.STUN_CHANCE : CounterEffect.NONE;
            case DRAGON_LINEAGE:
                return target == VOID_CORRUPTED ? CounterEffect.BURN_TARGET : CounterEffect.NONE;
            case VOID_CORRUPTED:
                return target == SHADOW_CONCLAVE ? CounterEffect.SLOW_TARGET : CounterEffect.NONE;
            case SHADOW_CONCLAVE:
                return target == CELESTIAL_REMNANTS ? CounterEffect.BONUS_DAMAGE : CounterEffect.NONE;
            default:
                return CounterEffect.NONE;
        }
    }

    /** Dipertahankan biar kode lama (chance-bonus 25%) yang udah kepasang gak perlu diubah. */
    public boolean counters(Faction other) {
        return getCounterEffect(other) != CounterEffect.NONE;
    }
}
