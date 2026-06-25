package com.fallenascendants.model;

import com.fallenascendants.enumtype.StatusType;

public class StatusEffect {

    private StatusType statusType;
    private int power;
    private int duration;

    public StatusEffect(StatusType statusType, int power, int duration) {
        this.statusType = statusType;
        this.power = power;
        this.duration = duration;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    public int getPower() {
        return power;
    }

    public int getDuration() {
        return duration;
    }

    public void reduceDuration() {
        if (duration > 0) {
            duration--;
        }
    }

    public boolean isExpired() {
        return duration <= 0;
    }
}
