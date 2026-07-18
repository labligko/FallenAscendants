package com.fallenascendants.model;

import com.fallenascendants.enumtype.ReactiveTrigger;
import com.fallenascendants.enumtype.SkillTrigger;
import com.fallenascendants.enumtype.SkillType;
import com.fallenascendants.enumtype.TargetType;

public class Skill {
    private String name;
    private SkillType skillType;
    private SkillTrigger skillTrigger;
    private ReactiveTrigger reactiveTrigger;
    private TargetType targetType;
    private int power;
    private int cooldown;
    private int currentCooldown;
    private int duration;

    public Skill(String name, SkillType skillType, TargetType targetType, int power, int cooldown) {
        this(
            name,
            skillType,
            SkillTrigger.ACTIVE,
            ReactiveTrigger.NONE,
            targetType,
            power,
            cooldown,
            0
        );
    }

    public Skill(String name, SkillType skillType, SkillTrigger skillTrigger,
                 ReactiveTrigger reactiveTrigger, TargetType targetType,
                 int power, int cooldown, int duration) {
        this.name = name;
        this.skillType = skillType;
        this.skillTrigger = skillTrigger;
        this.reactiveTrigger = reactiveTrigger;
        this.targetType = targetType;
        this.power = power;
        this.cooldown = cooldown;
        this.currentCooldown = 0;
        this.duration = duration;
    }

    public boolean isReady() {
        return currentCooldown <= 0;
    }

    public void use() {
        currentCooldown = cooldown;
    }

    public void reduceCooldown() {
        if (currentCooldown > 0) {
            currentCooldown--;
        }
    }

    public boolean isActiveSkill() {
        return skillTrigger == SkillTrigger.ACTIVE;
    }

    public boolean isPassiveSkill() {
        return skillTrigger == SkillTrigger.PASSIVE;
    }

    public boolean isReactiveSkill() {
        return skillTrigger == SkillTrigger.REACTIVE;
    }

    public String getName() {
        return name;
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public SkillTrigger getSkillTrigger() {
        return skillTrigger;
    }

    public ReactiveTrigger getReactiveTrigger() {
        return reactiveTrigger;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public int getPower() {
        return power;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getCurrentCooldown() {
        return currentCooldown;
    }

    public int getDuration() {
        return duration;
    }
}
