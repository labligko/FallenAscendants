package com.fallenascendants.battle;

import com.fallenascendants.model.BattleField;
import com.fallenascendants.model.Card;
import com.fallenascendants.enumtype.ReactiveTrigger;
import com.fallenascendants.enumtype.StatusType;
import com.fallenascendants.model.StatusEffect;
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

    public String processSingleAction() {
        if (isBattleOver()) {
            return "Battle already finished.";
        }

        Card attacker = null;

        while (attacker == null || attacker.isDead()) {
            if (turnQueue.isEmpty()) {
                rebuildTurnQueue();
            }

            attacker = turnQueue.getNextCard();

            if (attacker == null && isBattleOver()) {
                return "Battle already finished.";
            }
        }

        String statusLog = statusEffectResolver.applyStatusEffectsAtTurnStart(attacker);

        if (attacker.isDead()) {
            String deathLog = handleDeaths();

            String log = statusLog + deathLog;
            battleLog.add(log);
            return log;
        }

        if (statusEffectResolver.shouldSkipTurn()) {
            battleLog.add(statusLog);
            return statusLog;
        }

        BattleField allyField = isPlayerCard(attacker) ? playerField : enemyField;
        BattleField targetField = isPlayerCard(attacker) ? enemyField : playerField;

        String skillLog = skillResolver.tryResolveActiveSkill(attacker, allyField, targetField);

        if (skillLog != null && !skillLog.contains("basic attack instead")) {
            StringBuilder log = new StringBuilder();

            if (statusLog != null && !statusLog.isBlank()) {
                log.append(statusLog).append("\n");
            }

            log.append(skillLog).append("\n");
            log.append(handleDeaths());

            battleLog.add(log.toString());

            return log.toString();
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

        if (statusLog != null && !statusLog.isBlank()) {
            log.append(statusLog).append("\n");
        }

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

        String deathLog = handleDeaths();

        if (!deathLog.isBlank()) {
            log.append(deathLog);
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

    private String handleDeaths() {
        StringBuilder log = new StringBuilder();

        List<Card> deadPlayerCards = playerField.removeDeadCardsAndReplace();
        List<Card> deadEnemyCards = enemyField.removeDeadCardsAndReplace();

        for (Card deadCard : deadPlayerCards) {
            log.append(deadCard.getName()).append(" is defeated.\n");
            log.append("Reserve replacement checked.\n");

            String deathSkillLog = skillResolver.resolveReactiveSkill(
                deadCard,
                playerField,
                enemyField,
                ReactiveTrigger.ON_DEATH
            );

            log.append(deathSkillLog);
        }

        for (Card deadCard : deadEnemyCards) {
            log.append(deadCard.getName()).append(" is defeated.\n");
            log.append("Reserve replacement checked.\n");

            String deathSkillLog = skillResolver.resolveReactiveSkill(
                deadCard,
                enemyField,
                playerField,
                ReactiveTrigger.ON_DEATH
            );

            log.append(deathSkillLog);
        }

        return log.toString();
    }

    public String applyPassiveSkillsAtBattleStart() {
        StringBuilder log = new StringBuilder();

        log.append("Applying passive skills...\n");

        for (Card card : playerField.getActiveCards()) {
            if (card != null && !card.isDead()) {
                log.append(skillResolver.resolvePassiveSkill(card));
            }
        }

        for (Card card : enemyField.getActiveCards()) {
            if (card != null && !card.isDead()) {
                log.append(skillResolver.resolvePassiveSkill(card));
            }
        }

        battleLog.add(log.toString());

        return log.toString();
    }

    private String applyStatusEffects(Card card) {
        StringBuilder log = new StringBuilder();

        for (StatusEffect effect : card.getStatusEffects()) {
            if (effect.getStatusType() == StatusType.BURN) {
                card.takeDamage(effect.getPower());

                log.append(card.getName())
                    .append(" suffers ")
                    .append(effect.getPower())
                    .append(" burn damage.\n")
                    .append(card.getName())
                    .append(" HP: ")
                    .append(card.getCurrentHp())
                    .append("/")
                    .append(card.getMaxHp())
                    .append("\n");
            }

            effect.reduceDuration();
        }

        card.removeExpiredStatus();

        return log.toString();
    }

    public String applyFactionSynergyAtBattleStart() {
        StringBuilder log = new StringBuilder();

        log.append(factionSynergyResolver.applyFactionSynergy(
            playerField.getActiveCards(),
            "PLAYER"
        ));

        log.append(factionSynergyResolver.applyFactionSynergy(
            enemyField.getActiveCards(),
            "ENEMY"
        ));

        battleLog.add(log.toString());

        return log.toString();
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
