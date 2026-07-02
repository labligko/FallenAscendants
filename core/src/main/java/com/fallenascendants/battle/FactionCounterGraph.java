package com.fallenascendants.battle;

import com.fallenascendants.enumtype.CounterEffectType;
import com.fallenascendants.enumtype.Faction;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class FactionCounterGraph {
    private static final double COUNTER_MULTIPLIER = 1.20;

    private final Map<Faction, List<Faction>> adjacencyList;
    private final Map<Faction, CounterEffectType> counterEffects;

    public FactionCounterGraph() {
        this.adjacencyList = new EnumMap<>(Faction.class);
        this.counterEffects = new EnumMap<>(Faction.class);

        for (Faction faction : Faction.values()) {
            adjacencyList.put(faction, new ArrayList<>());
            counterEffects.put(faction, CounterEffectType.NONE);
        }

        buildGraph();
    }

    private void buildGraph() {
        addCounter(
            Faction.CELESTIAL_REMNANTS,
            Faction.ABYSSAL_CHURCH,
            CounterEffectType.HEAL_LOWEST_ALLY
        );

        addCounter(
            Faction.ABYSSAL_CHURCH,
            Faction.MORTAL_ASCENDANTS,
            CounterEffectType.POISON_TARGET
        );

        addCounter(
            Faction.MORTAL_ASCENDANTS,
            Faction.DRAGON_LINEAGE,
            CounterEffectType.STUN_TARGET_CHANCE
        );

        addCounter(
            Faction.DRAGON_LINEAGE,
            Faction.VOID_CORRUPTED,
            CounterEffectType.BURN_TARGET
        );

        addCounter(
            Faction.VOID_CORRUPTED,
            Faction.SHADOW_CONCLAVE,
            CounterEffectType.SLOW_TARGET
        );

        addCounter(
            Faction.SHADOW_CONCLAVE,
            Faction.CELESTIAL_REMNANTS,
            CounterEffectType.BONUS_DAMAGE
        );
    }

    private void addCounter(
        Faction attackerFaction,
        Faction targetFaction,
        CounterEffectType effectType
    ) {
        adjacencyList.get(attackerFaction).add(targetFaction);
        counterEffects.put(attackerFaction, effectType);
    }

    public boolean isCounter(Faction attackerFaction, Faction targetFaction) {
        if (attackerFaction == null || targetFaction == null) {
            return false;
        }

        return adjacencyList.get(attackerFaction).contains(targetFaction);
    }

    public CounterEffectType getCounterEffectType(
        Faction attackerFaction,
        Faction targetFaction
    ) {
        if (!isCounter(attackerFaction, targetFaction)) {
            return CounterEffectType.NONE;
        }

        return counterEffects.getOrDefault(
            attackerFaction,
            CounterEffectType.NONE
        );
    }

    public int applyCounterBonus(
        Faction attackerFaction,
        Faction targetFaction,
        int damage
    ) {
        CounterEffectType effectType = getCounterEffectType(
            attackerFaction,
            targetFaction
        );

        if (effectType != CounterEffectType.BONUS_DAMAGE) {
            return damage;
        }

        return (int) Math.round(damage * COUNTER_MULTIPLIER);
    }

    public int calculateBonusDamage(
        Faction attackerFaction,
        Faction targetFaction,
        int damage
    ) {
        int finalDamage = applyCounterBonus(
            attackerFaction,
            targetFaction,
            damage
        );

        return finalDamage - damage;
    }

    public String getCounterReport() {
        StringBuilder report = new StringBuilder();

        report.append("Faction Counter Graph:\n");

        for (Faction faction : adjacencyList.keySet()) {
            report.append("- ")
                .append(faction)
                .append(" counters ")
                .append(adjacencyList.get(faction))
                .append(" with effect ")
                .append(counterEffects.get(faction))
                .append("\n");
        }

        return report.toString();
    }
}
