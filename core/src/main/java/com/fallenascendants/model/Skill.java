package com.fallenascendants.model;

import com.fallenascendants.enumtype.SkillType;
import com.fallenascendants.enumtype.TargetType;

public class Skill {
    private String name;
    private SkillType skillType;
    private TargetType targetType;
    private int power;
    private int cooldown;
    private int currentCooldown;

    public Skill(String name, SkillType skillType, TargetType targetType, int power, int cooldown) {
        this.name = name;
        this.skillType = skillType;
        this.targetType = targetType;
        this.power = power;
        this.cooldown = cooldown;
        this.currentCooldown = 0;
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

    public String getName() {
        return name;
    }

    public SkillType getSkillType() {
        return skillType;
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
}
