package com.fallenascendants.battle;

import com.fallenascendants.model.Card;

public class BattleEvent {
    public enum EventType {
        TEXT_MESSAGE,  // Untuk nampilin text box banner (skill, status, pasif)
        ACTION_ATTACK, // Untuk mentrigger animasi kartu gerak
        DAMAGE_TAKEN,  // Khusus untuk mentrigger floating number
        DEATH          // Untuk menandai kartu mati (meredup/hilang)
    }

    public EventType type;
    public Card source;
    public Card target;
    public String message;
    public int rawDamage;
    public int shieldAbsorbed;
    public int hpDamage;

    // Constructor untuk TEXT_MESSAGE sederhana
    public BattleEvent(EventType type, String message) {
        this.type = type;
        this.message = message;
    }

    // Constructor untuk ACTION_ATTACK / DEATH
    public BattleEvent(EventType type, Card source, String message) {
        this.type = type;
        this.source = source;
        this.message = message;
    }

    // Constructor untuk DAMAGE_TAKEN (buat floating number)
    public BattleEvent(EventType type, Card target, int rawDamage, int shieldAbsorbed, int hpDamage) {
        this.type = type;
        this.target = target;
        this.rawDamage = rawDamage;
        this.shieldAbsorbed = shieldAbsorbed;
        this.hpDamage = hpDamage;
    }
}
