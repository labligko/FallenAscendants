package com.fallenascendants.battle;

import com.fallenascendants.model.BattleField;
import com.fallenascendants.model.Card;

public class BattleManager {
    private BattleField playerField;
    private BattleField enemyField;

    private TurnQueue turnQueue;
    private TargetingSystem targetingSystem;
    private DamageCalculator damageCalculator;
    private SkillResolver skillResolver;
    private BattleLog battleLog;

    public BattleManager(BattleField playerField, BattleField enemyField) {
        this.playerField = playerField;
        this.enemyField = enemyField;

        this.turnQueue = new TurnQueue();
        this.targetingSystem = new TargetingSystem();
        this.damageCalculator = new DamageCalculator();
        this.skillResolver = new SkillResolver();
        this.battleLog = new BattleLog();

        rebuildTurnQueue();
    }

    public void rebuildTurnQueue() {
        turnQueue.buildQueue(
            playerField.getActiveCards(),
            enemyField.getActiveCards()
        );
    }

    public String processSingleAction() {
        if (isBattleOver()) {
            return "Battle already finished.";
        }

        if (turnQueue.isEmpty()) {
            rebuildTurnQueue();

            String log = "Action Queue rebuilt for next cycle.";

            battleLog.add(log);

            return log;
        }

        Card attacker = turnQueue.getNextCard();

        if (attacker == null || attacker.isDead()) {
            return "Skipped dead or invalid attacker.";
        }

        BattleField allyField = isPlayerCard(attacker) ? playerField : enemyField;
        BattleField targetField = isPlayerCard(attacker) ? enemyField : playerField;

        String skillLog = skillResolver.tryResolveActiveSkill(attacker, allyField, targetField);

        if (skillLog != null && !skillLog.contains("basic attack instead")) {
            playerField.removeDeadCardsAndReplace();
            enemyField.removeDeadCardsAndReplace();

            battleLog.add(skillLog);

            return skillLog;
        }

        Card target = targetingSystem.selectBasicAttackTarget(targetField.getActiveCards());

        if (target == null) {
            return attacker.getName() + " has no target.";
        }

        DamageResult result = damageCalculator.calculateBasicAttack(attacker, target);
        target.takeDamage(
            result.getShieldAbsorbed()
                + result.getHpDamage()
        );

        StringBuilder log = new StringBuilder();

        if (skillLog != null) {
            log.append(skillLog).append("\n");
        }

        log.append(attacker.getName())
            .append(" attacks ")
            .append(target.getName())
            .append("\n");

        log.append("Raw Damage: ")
            .append(result.getRawDamage())
            .append("\n");

        log.append("Blocked by DEF: ")
            .append(result.getBlockedByDefense())
            .append("\n");

        log.append("Shield Absorbed: ")
            .append(result.getShieldAbsorbed())
            .append("\n");

        log.append("HP Damage: ")
            .append(result.getHpDamage())
            .append("\n");

        log.append(target.getName())
            .append(" HP: ")
            .append(target.getCurrentHp())
            .append("/")
            .append(target.getMaxHp())
            .append("\n");

        log.append(target.getName())
            .append(" Shield: ")
            .append(target.getShield())
            .append("\n");

        boolean targetDead = target.isDead();

        playerField.removeDeadCardsAndReplace();
        enemyField.removeDeadCardsAndReplace();

        if (targetDead) {
            log.append(target.getName()).append(" is defeated.\n");
            log.append("Reserve replacement checked.\n");
        }

        if (attacker.getActiveSkill() != null) {
            attacker.getActiveSkill().reduceCooldown();
        }

        battleLog.add(log.toString());

        return log.toString();
    }

    private boolean isPlayerCard(Card card) {
        for (Card playerCard : playerField.getActiveCards()) {
            if (playerCard == card) {
                return true;
            }
        }

        return false;
    }

    public boolean isBattleOver() {
        return !playerField.hasAliveCards() || !enemyField.hasAliveCards();
    }

    public boolean isPlayerWin() {
        return playerField.hasAliveCards() && !enemyField.hasAliveCards();
    }

    public boolean isPlayerLose() {
        return !playerField.hasAliveCards() && enemyField.hasAliveCards();
    }

    public BattleLog getBattleLog() {
        return battleLog;
    }
}
