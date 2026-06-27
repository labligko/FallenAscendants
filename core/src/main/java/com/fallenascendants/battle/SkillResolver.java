package com.fallenascendants.battle;

import com.fallenascendants.enumtype.SkillType;
import com.fallenascendants.enumtype.TargetType;
import com.fallenascendants.model.BattleField;
import com.fallenascendants.model.Card;
import com.fallenascendants.model.Skill;
import com.fallenascendants.enumtype.ReactiveTrigger;
import com.fallenascendants.enumtype.StatusType;
import com.fallenascendants.model.StatusEffect;

import java.util.Random;

public class SkillResolver {
    private static final int SKILL_USE_CHANCE = 50;

    private final Random random;
    private final TargetingSystem targetingSystem;

    public SkillResolver() {
        this.random = new Random();
        this.targetingSystem = new TargetingSystem();
    }

    public String tryResolveActiveSkill(Card caster, BattleField allyField, BattleField enemyField) {
        Skill skill = caster.getActiveSkill();

        if (skill == null) {
            return null;
        }

        if (!skill.isActiveSkill()) {
            return null;
        }

        if (!skill.isReady()) {
            return caster.getName() + " skill " + skill.getName()
                + " is on cooldown: "
                + skill.getCurrentCooldown()
                + " turn(s) remaining.\n"
                + caster.getName() + " uses basic attack instead.";
        }

        int chance = random.nextInt(100) + 1;

        if (chance > SKILL_USE_CHANCE) {
            return caster.getName() + " chooses not to use " + skill.getName()
                + " this turn.\n"
                + caster.getName() + " uses basic attack instead.";
        }

        return resolveSkill(caster, allyField, enemyField, skill);
    }

    private String resolveSkill(Card caster, BattleField allyField, BattleField enemyField, Skill skill) {
        if (skill.getSkillType() == SkillType.DAMAGE) {
            return resolveDamageSkill(caster, enemyField, skill);
        }

        if (skill.getSkillType() == SkillType.HEAL) {
            return resolveHealSkill(caster, allyField, skill);
        }

        if (skill.getSkillType() == SkillType.TAUNT) {
            caster.increaseAggro(skill.getPower());
            skill.use();

            return caster.getName() + " uses " + skill.getName()
                + ". Aggro increased by " + skill.getPower() + ".\n"
                + "Current Aggro: " + caster.getAggro() + "\n"
                + "Skill cooldown: " + skill.getCooldown() + " turn(s).\n";
        }

        if (skill.getSkillType() == SkillType.SHIELD) {
            caster.addShield(skill.getPower());
            skill.use();

            return caster.getName() + " uses " + skill.getName()
                + ". Shield +" + skill.getPower() + ".\n"
                + "Current Shield: " + caster.getShield() + "\n"
                + "Skill cooldown: " + skill.getCooldown() + " turn(s).\n";
        }

        if (skill.getSkillType() == SkillType.BURN) {
            return resolveStatusSkill(caster, enemyField, skill, StatusType.BURN);
        }

        if (skill.getSkillType() == SkillType.POISON) {
            return resolveStatusSkill(caster, enemyField, skill, StatusType.POISON);
        }

        if (skill.getSkillType() == SkillType.SLOW) {
            return resolveStatusSkill(caster, enemyField, skill, StatusType.SLOW);
        }

        if (skill.getSkillType() == SkillType.STUN) {
            return resolveStatusSkill(caster, enemyField, skill, StatusType.STUN);
        }
        skill.use();
        return caster.getName() + " uses " + skill.getName()
            + ", but this skill type is not implemented yet."
            + "\nSkill cooldown: " + skill.getCooldown() + " turn(s).";
    }

    private String resolveDamageSkill(Card caster, BattleField enemyField, Skill skill) {
        if (skill.getTargetType() == TargetType.ALL_ENEMIES) {
            return resolveAllEnemiesDamage(caster, enemyField, skill);
        }

        Card target = selectSkillTarget(skill.getTargetType(), enemyField.getActiveCards());

        if (target == null) {
            return caster.getName() + " tried to use " + skill.getName()
                + ", but there is no target.";
        }

        target.takeDamage(skill.getPower());
        skill.use();

        return caster.getName() + " uses " + skill.getName()
            + " on " + target.getName()
            + " for " + skill.getPower() + " damage."
            + "\n" + target.getName() + " HP: "
            + target.getCurrentHp() + "/" + target.getMaxHp()
            + "\nSkill cooldown: " + skill.getCooldown() + " turn(s).";
    }

    private String resolveAllEnemiesDamage(Card caster, BattleField enemyField, Skill skill) {
        StringBuilder log = new StringBuilder();

        log.append(caster.getName())
            .append(" uses ")
            .append(skill.getName())
            .append(" on all enemies.\n");

        for (Card target : enemyField.getActiveCards()) {
            if (target != null && !target.isDead()) {
                target.takeDamage(skill.getPower());

                log.append("- ")
                    .append(target.getName())
                    .append(" takes ")
                    .append(skill.getPower())
                    .append(" damage. HP: ")
                    .append(target.getCurrentHp())
                    .append("/")
                    .append(target.getMaxHp())
                    .append("\n");
            }
        }

        skill.use();

        log.append("Skill cooldown: ")
            .append(skill.getCooldown())
            .append(" turn(s).");

        return log.toString();
    }

    private String resolveHealSkill(Card caster, BattleField allyField, Skill skill) {
        Card target = selectAllyTarget(skill.getTargetType(), allyField.getActiveCards());

        if (target == null) {
            return caster.getName() + " tried to use " + skill.getName()
                + ", but there is no ally target.";
        }

        target.heal(skill.getPower());
        skill.use();

        return caster.getName() + " uses " + skill.getName()
            + " on " + target.getName()
            + " and heals " + skill.getPower() + " HP."
            + "\n" + target.getName() + " HP: "
            + target.getCurrentHp() + "/" + target.getMaxHp()
            + "\nSkill cooldown: " + skill.getCooldown() + " turn(s).";
    }

    private Card selectSkillTarget(TargetType targetType, Card[] enemyCards) {
        if (targetType == TargetType.ENEMY_BY_AGGRO) {
            return targetingSystem.selectTargetByAggro(enemyCards);
        }

        if (targetType == TargetType.ENEMY_LOWEST_HP) {
            return targetingSystem.selectLowestHpTarget(enemyCards);
        }

        if (targetType == TargetType.RANDOM_ENEMY) {
            return targetingSystem.selectRandomTarget(enemyCards);
        }

        return targetingSystem.selectTargetByAggro(enemyCards);
    }

    private Card selectAllyTarget(TargetType targetType, Card[] allyCards) {
        if (targetType == TargetType.ALLY_LOWEST_HP) {
            return targetingSystem.selectLowestHpTarget(allyCards);
        }

        if (targetType == TargetType.SELF) {
            return null;
        }

        return targetingSystem.selectLowestHpTarget(allyCards);
    }

    public String resolveReactiveSkill(Card caster, BattleField allyField, BattleField enemyField, ReactiveTrigger trigger) {
        Skill skill = caster.getDeathSkill();

        if (skill == null || !skill.isReactiveSkill()) {
            return "";
        }

        if (skill.getReactiveTrigger() != trigger) {
            return "";
        }

        if (skill.getSkillType() == SkillType.DAMAGE) {
            return resolveDamageSkill(caster, enemyField, skill);
        }

        if (skill.getSkillType() == SkillType.HEAL) {
            return resolveHealSkill(caster, allyField, skill);
        }

        if (skill.getSkillType() == SkillType.SHIELD) {
            Card target = selectAllyTarget(skill.getTargetType(), allyField.getActiveCards());

            if (target == null) {
                return caster.getName() + "'s death skill " + skill.getName()
                    + " has no valid target.\n";
            }

            target.addShield(skill.getPower());

            return caster.getName() + "'s death skill activates: " + skill.getName()
                + "\n" + target.getName() + " gains Shield +" + skill.getPower()
                + "\nCurrent Shield: " + target.getShield() + "\n";
        }

        return caster.getName() + "'s death skill " + skill.getName()
            + " activates, but the effect is not implemented yet.\n";
    }

    public String resolvePassiveSkill(Card card) {
        Skill skill = card.getPassiveSkill();

        if (skill == null || !skill.isPassiveSkill()) {
            return "";
        }

        if (skill.getSkillType() == SkillType.BUFF) {
            return resolvePassiveBuff(card, skill);
        }

        return card.getName() + " passive " + skill.getName()
            + " is active, but effect is not implemented yet.\n";
    }

    private String resolvePassiveBuff(Card card, Skill skill) {
        if (skill.getTargetType() == TargetType.SELF) {
            card.increaseAtk(skill.getPower());

            return card.getName() + " passive activates: " + skill.getName()
                + "\nATK increased by " + skill.getPower()
                + "\nCurrent ATK: " + card.getAtk() + "\n";
        }

        return card.getName() + " passive " + skill.getName()
            + " has unsupported target.\n";
    }

    private String resolveStatusSkill(Card caster, BattleField enemyField, Skill skill, StatusType statusType) {
        StringBuilder log = new StringBuilder();

        if (skill.getTargetType() == TargetType.ALL_ENEMIES) {
            log.append(caster.getName())
                .append(" uses ")
                .append(skill.getName())
                .append(" on all enemies.\n");

            for (Card target : enemyField.getActiveCards()) {
                if (target != null && !target.isDead()) {
                    target.addStatusEffect(new StatusEffect(statusType, skill.getPower(), skill.getDuration()));

                    log.append("- ")
                        .append(target.getName())
                        .append(" receives ")
                        .append(statusType)
                        .append(" for ")
                        .append(skill.getDuration())
                        .append(" turn(s).\n");
                }
            }

            skill.use();

            log.append("Skill cooldown: ").append(skill.getCooldown()).append(" turn(s).");

            return log.toString();
        }

        Card target = selectSkillTarget(skill.getTargetType(), enemyField.getActiveCards());

        if (target == null) {
            return caster.getName() + " tried to use " + skill.getName()
                + ", but there is no target.";
        }

        target.addStatusEffect(new StatusEffect(statusType, skill.getPower(), skill.getDuration()));
        skill.use();

        return caster.getName() + " uses " + skill.getName()
            + " on " + target.getName()
            + ".\n"
            + target.getName() + " receives " + statusType
            + " for " + skill.getDuration() + " turn(s).\n"
            + "Skill cooldown: " + skill.getCooldown() + " turn(s).";
    }
}
