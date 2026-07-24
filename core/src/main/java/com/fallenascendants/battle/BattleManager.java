package com.fallenascendants.battle;

import com.fallenascendants.model.BattleField;
import com.fallenascendants.model.Card;
import com.fallenascendants.enumtype.ReactiveTrigger;
import com.fallenascendants.enumtype.StatusType;
import com.fallenascendants.model.StatusEffect;

import java.util.ArrayList;
import java.util.List;

public class BattleManager {
    private BattleField playerField;
    private BattleField enemyField;

    private TurnQueue turnQueue;
    private TargetingSystem targetingSystem;
    private DamageCalculator damageCalculator;
    private SkillResolver skillResolver;
    private BattleLog battleLog;
    private StatusEffectResolver statusEffectResolver;
    private FactionSynergyResolver factionSynergyResolver;
    private FactionCounterEffectResolver factionCounterEffectResolver;
    private Card lastAttacker;
    private Card lastTarget;
    private boolean lastActionWasBasicAttack;

    public BattleManager(BattleField playerField, BattleField enemyField) {
        this.playerField = playerField;
        this.enemyField = enemyField;

        this.turnQueue = new TurnQueue();
        this.targetingSystem = new TargetingSystem();
        this.damageCalculator = new DamageCalculator();
        this.skillResolver = new SkillResolver();
        this.battleLog = new BattleLog();
        this.statusEffectResolver = new StatusEffectResolver();
        this.factionSynergyResolver = new FactionSynergyResolver();
        this.factionCounterEffectResolver = new FactionCounterEffectResolver();

        rebuildTurnQueue();
    }

    public void rebuildTurnQueue() {
        resetTemporarySpeedModifiers(playerField.getActiveCards());
        resetTemporarySpeedModifiers(enemyField.getActiveCards());

        applyActiveSlowEffects(playerField.getActiveCards());
        applyActiveSlowEffects(enemyField.getActiveCards());

        turnQueue.buildQueue(
            playerField.getActiveCards(),
            enemyField.getActiveCards()
        );
    }

    private void resetTemporarySpeedModifiers(Card[] cards) {
        for (Card card : cards) {
            if (card != null && !card.isDead()) {
                card.resetTemporarySpdReduction();
            }
        }
    }

    private void applyActiveSlowEffects(Card[] cards) {
        for (Card card : cards) {
            if (card == null || card.isDead()) {
                continue;
            }

            for (StatusEffect effect : card.getStatusEffects()) {
                if (effect.getStatusType() == StatusType.SLOW) {
                    card.addTemporarySpdReduction(effect.getPower());
                }
            }
        }
    }

    // MENGEMBALIKAN LIST OF EVENTS BUKAN STRING
    public List<BattleEvent> processSingleAction() {
        List<BattleEvent> events = new ArrayList<>();
        lastAttacker = null;
        lastTarget = null;
        lastActionWasBasicAttack = false;

        if (isBattleOver()) {
            events.add(new BattleEvent(BattleEvent.EventType.TEXT_MESSAGE, "Battle already finished."));
            return events;
        }

        Card attacker = null;

        while (attacker == null || attacker.isDead()) {
            if (turnQueue.isEmpty()) {
                rebuildTurnQueue();
            }

            attacker = turnQueue.getNextCard();

            if (attacker == null && isBattleOver()) {
                events.add(new BattleEvent(BattleEvent.EventType.TEXT_MESSAGE, "Battle already finished."));
                return events;
            }
        }

        // 1. STATUS EFFECT MULA GILIRAN
        String statusLog = statusEffectResolver.applyStatusEffectsAtTurnStart(attacker);
        if (statusLog != null && !statusLog.isBlank()) {
            events.add(new BattleEvent(BattleEvent.EventType.TEXT_MESSAGE, statusLog));
        }

        if (attacker.isDead()) {
            events.addAll(handleDeaths());
            return events;
        }

        if (statusEffectResolver.shouldSkipTurn()) {
            return events;
        }

        BattleField allyField = isPlayerCard(attacker) ? playerField : enemyField;
        BattleField targetField = isPlayerCard(attacker) ? enemyField : playerField;

        // 2. CEK PENGGUNAAN SKILL
        String skillLog = skillResolver.tryResolveActiveSkill(attacker, allyField, targetField);
        if (skillLog != null && !skillLog.isBlank() && !skillLog.contains("basic attack instead")) {
            events.add(new BattleEvent(BattleEvent.EventType.TEXT_MESSAGE, skillLog));
            events.addAll(handleDeaths());
            turnQueue.rebuildRemainingQueue();
            return events;
        }

        // 3. LOGIKA BASIC ATTACK
        Card target = targetingSystem.selectBasicAttackTarget(targetField.getActiveCards());
        if (target == null) {
            events.add(new BattleEvent(BattleEvent.EventType.TEXT_MESSAGE, attacker.getName() + " has no target."));
            return events;
        }

        lastAttacker = attacker;
        lastTarget = target;
        lastActionWasBasicAttack = true;

        // Event: Kartu Maju/Serang
        events.add(new BattleEvent(BattleEvent.EventType.ACTION_ATTACK, attacker, attacker.getName() + " attacks " + target.getName()));

        DamageResult result = damageCalculator.calculateBasicAttack(attacker, target);
        target.takeDamage(result.getShieldAbsorbed() + result.getHpDamage());

        // Event: Floating Number Damage
        events.add(new BattleEvent(BattleEvent.EventType.DAMAGE_TAKEN, target, result.getRawDamage(), result.getShieldAbsorbed(), result.getHpDamage()));

        // Event: Counter Effect / Reflect
        String counterEffectLog = factionCounterEffectResolver.resolveCounterEffect(attacker, target, allyField, result.getHpDamage());
        if (counterEffectLog != null && !counterEffectLog.isBlank()) {
            events.add(new BattleEvent(BattleEvent.EventType.TEXT_MESSAGE, counterEffectLog));
        }

        // Event: Kematian (Jika Ada)
        events.addAll(handleDeaths());

        turnQueue.rebuildRemainingQueue();
        if (attacker.getActiveSkill() != null) {
            attacker.getActiveSkill().reduceCooldown();
        }

        return events;
    }

    private boolean isPlayerCard(Card card) {
        for (Card playerCard : playerField.getActiveCards()) {
            if (playerCard == card) {
                return true;
            }
        }
        return false;
    }

    // UBAH HANDLE DEATHS AGAR ME-RETURN EVENT
    private List<BattleEvent> handleDeaths() {
        List<BattleEvent> events = new ArrayList<>();
        List<Card> deadPlayerCards = playerField.removeDeadCardsAndReplace();
        List<Card> deadEnemyCards = enemyField.removeDeadCardsAndReplace();

        for (Card deadCard : deadPlayerCards) {
            events.add(new BattleEvent(BattleEvent.EventType.DEATH, deadCard, deadCard.getName() + " is defeated."));
            String deathSkillLog = skillResolver.resolveReactiveSkill(deadCard, playerField, enemyField, ReactiveTrigger.ON_DEATH);
            if (deathSkillLog != null && !deathSkillLog.isBlank()) {
                events.add(new BattleEvent(BattleEvent.EventType.TEXT_MESSAGE, deathSkillLog));
            }
        }

        for (Card deadCard : deadEnemyCards) {
            events.add(new BattleEvent(BattleEvent.EventType.DEATH, deadCard, deadCard.getName() + " is defeated."));
            String deathSkillLog = skillResolver.resolveReactiveSkill(deadCard, enemyField, playerField, ReactiveTrigger.ON_DEATH);
            if (deathSkillLog != null && !deathSkillLog.isBlank()) {
                events.add(new BattleEvent(BattleEvent.EventType.TEXT_MESSAGE, deathSkillLog));
            }
        }
        return events;
    }

    // UBAH START BATTLE PASSIVE AGAR ME-RETURN EVENT
    public List<BattleEvent> applyPassiveSkillsAtBattleStart() {
        List<BattleEvent> events = new ArrayList<>();

        for (Card card : playerField.getActiveCards()) {
            if (card != null && !card.isDead()) {
                String log = skillResolver.resolvePassiveSkill(card);
                if (log != null && !log.isBlank()) events.add(new BattleEvent(BattleEvent.EventType.TEXT_MESSAGE, log));
            }
        }

        for (Card card : enemyField.getActiveCards()) {
            if (card != null && !card.isDead()) {
                String log = skillResolver.resolvePassiveSkill(card);
                if (log != null && !log.isBlank()) events.add(new BattleEvent(BattleEvent.EventType.TEXT_MESSAGE, log));
            }
        }
        return events;
    }

    // UBAH FACTION SYNERGY AGAR ME-RETURN EVENT
    public List<BattleEvent> applyFactionSynergyAtBattleStart() {
        List<BattleEvent> events = new ArrayList<>();

        String pLog = factionSynergyResolver.applyFactionSynergy(playerField.getActiveCards(), "PLAYER");
        if (pLog != null && !pLog.isBlank()) events.add(new BattleEvent(BattleEvent.EventType.TEXT_MESSAGE, pLog));

        String eLog = factionSynergyResolver.applyFactionSynergy(enemyField.getActiveCards(), "ENEMY");
        if (eLog != null && !eLog.isBlank()) events.add(new BattleEvent(BattleEvent.EventType.TEXT_MESSAGE, eLog));

        return events;
    }

    // ... Sisa getter setter dan method isBattleOver dll biarkan sama ...
    public BattleField getPlayerField() {return playerField;}
    public BattleField getEnemyField() {return enemyField;}
    public Card getLastAttacker() {return lastAttacker;}
    public Card getLastTarget() {return lastTarget;}
    public boolean wasLastActionBasicAttack() {return lastActionWasBasicAttack;}
    public List<Card> getUpcomingTurnOrder(int limit) {return turnQueue.getUpcomingOrder(limit);}
    public String getTurnQueueReport() { return turnQueue.getQueueReport(); }
    public boolean isBattleOver() { return !playerField.hasAliveCards() || !enemyField.hasAliveCards(); }
    public boolean isPlayerWin() { return playerField.hasAliveCards() && !enemyField.hasAliveCards(); }
    public boolean isPlayerLose() { return !playerField.hasAliveCards() && enemyField.hasAliveCards(); }
    public BattleLog getBattleLog() { return battleLog; }
}
