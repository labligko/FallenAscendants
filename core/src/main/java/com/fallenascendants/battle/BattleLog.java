package com.fallenascendants.battle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BattleLog {
    private final ArrayList<String> logs;

    public BattleLog() {
        this.logs = new ArrayList<>();
    }

    public void add(String message) {
        if (message != null && !message.isBlank()) {
            logs.add(message);
        }
    }

    public List<String> getLogs() {
        return Collections.unmodifiableList(logs);
    }

    public String getLatestLog() {
        if (logs.isEmpty()) {
            return "";
        }

        return logs.get(logs.size() - 1);
    }

    public void clear() {
        logs.clear();
    }
}
