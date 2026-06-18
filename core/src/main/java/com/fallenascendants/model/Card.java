package com.fallenascendants.model;

import com.fallenascendants.enumtype.Faction;
import com.fallenascendants.enumtype.Rarity;
import com.fallenascendants.enumtype.Role;

public class Card {
    private String id;
    private String name;
    private Faction faction;
    private Role role;
    private Rarity rarity;

    private int maxHp;
    private int currentHp;
    private int atk;
    private int def;
    private int spd;
    private int aggro;
    private int level;
    private int shield;

    private Skill passiveSkill;
    private Skill activeSkill;
    private Skill deathSkill;

    private String imagePath;

    public Card(String id, String name, Faction faction, Role role, Rarity rarity,
                int maxHp, int atk, int def, int spd, int aggro, int level,
                String imagePath) {
        this.id = id;
        this.name = name;
        this.faction = faction;
        this.role = role;
        this.rarity = rarity;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.shield = 0;
        this.atk = atk;
        this.def = def;
        this.spd = spd;
        this.aggro = aggro;
        this.level = level;
        this.imagePath = imagePath;
    }

    public void takeDamage(int damage) {
        int remainingDamage = damage;

        if (shield > 0) {
            int absorbed = Math.min(shield, remainingDamage);
            shield -= absorbed;
            remainingDamage -= absorbed;
        }

        currentHp -= remainingDamage;

        if (currentHp < 0) {
            currentHp = 0;
        }
    }

    public void addShield(int amount) {
        if (amount > 0) {
            shield += amount;
        }
    }

    public void increaseAggro(int amount) {
        if (amount > 0) {
            aggro += amount;
        }
    }

    public int getShield() {
        return shield;
    }

    public void heal(int amount) {
        currentHp += amount;

        if (currentHp > maxHp) {
            currentHp = maxHp;
        }
    }

    public boolean isDead() {
        return currentHp <= 0;
    }

    public void levelUp() {
        level++;
        maxHp += 10;
        atk += 3;
        def += 2;
        currentHp = maxHp;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Faction getFaction() {
        return faction;
    }

    public Role getRole() {
        return role;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getAtk() {
        return atk;
    }

    public int getDef() {
        return def;
    }

    public int getSpd() {
        return spd;
    }

    public int getAggro() {
        return aggro;
    }

    public int getLevel() {
        return level;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Skill getPassiveSkill() {
        return passiveSkill;
    }

    public void setPassiveSkill(Skill passiveSkill) {
        this.passiveSkill = passiveSkill;
    }

    public Skill getActiveSkill() {
        return activeSkill;
    }

    public void setActiveSkill(Skill activeSkill) {
        this.activeSkill = activeSkill;
    }

    public Skill getDeathSkill() {
        return deathSkill;
    }

    public void setDeathSkill(Skill deathSkill) {
        this.deathSkill = deathSkill;
    }

    public int calculateShieldAbsorption(int damage) {
        return Math.min(shield, damage);
    }
}
