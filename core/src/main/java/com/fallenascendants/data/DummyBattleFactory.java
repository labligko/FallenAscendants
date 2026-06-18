package com.fallenascendants.data;

import com.fallenascendants.battle.BattleManager;
import com.fallenascendants.enumtype.*;
import com.fallenascendants.model.*;

public class DummyBattleFactory {

    public static BattleManager createBattle() {
        Deck playerDeck = createPlayerDeck();
        Deck enemyDeck = createEnemyDeck();

        BattleField playerField = new BattleField(playerDeck);
        BattleField enemyField = new BattleField(enemyDeck);

        return new BattleManager(playerField, enemyField);
    }

    public static Deck createPlayerDeck() {
        Deck deck = new Deck();

        deck.addCard(CardDatabase.getCardById("fallen_seraph"));
        deck.addCard(CardDatabase.getCardById("shadow_saint"));
        deck.addCard(CardDatabase.getCardById("broken_madonna"));
        deck.addCard(CardDatabase.getCardById("void_bishop"));
        deck.addCard(CardDatabase.getCardById("void_paladin"));
        deck.addCard(CardDatabase.getCardById("wyvern_hatchling"));
        deck.addCard(CardDatabase.getCardById("heretic_scribe"));
        deck.addCard(CardDatabase.getCardById("celestial_dragon_undone"));
        
        return deck;
    }

    public static Deck createEnemyDeck() {
        Deck deck = new Deck();

        Card abyssalColossus = new Card("e001", "Abyssal Colossus", Faction.VOID_CORRUPTED, Role.TANK, Rarity.RARE, 170, 22, 14, 60, 120, 1, "");
        abyssalColossus.setActiveSkill(new Skill("Abyssal Guard", SkillType.TAUNT, TargetType.SELF, 50, 3));
        deck.addCard(abyssalColossus);

        Card fallenValkyrie = new Card("e002", "Fallen Valkyrie", Faction.SHADOW_CONCLAVE, Role.ASSASSIN, Rarity.EPIC, 85, 42, 5, 125, 15, 1, "");
        fallenValkyrie.setActiveSkill(new Skill("Wing Pierce", SkillType.DAMAGE, TargetType.ENEMY_LOWEST_HP, 34, 2));
        deck.addCard(fallenValkyrie);

        Card lastApostle = new Card("e003", "Last Apostle", Faction.MORTAL_ASCENDANTS, Role.HEALER, Rarity.EPIC, 105, 12, 6, 88, 15, 1, "");
        lastApostle.setActiveSkill(new Skill("Last Prayer", SkillType.HEAL, TargetType.ALLY_LOWEST_HP, 24, 2));
        deck.addCard(lastApostle);

        Card corruptOracle = new Card("e004", "Corrupt Oracle", Faction.ABYSSAL_CHURCH, Role.MAGE, Rarity.RARE, 90, 34, 5, 95, 25, 1, "");
        corruptOracle.setActiveSkill(new Skill("Black Vision", SkillType.DAMAGE, TargetType.ALL_ENEMIES, 18, 3));
        deck.addCard(corruptOracle);

        Card cursedArchangel = new Card("e005", "Cursed Archangel", Faction.CELESTIAL_REMNANTS, Role.DPS, Rarity.RARE, 110, 36, 7, 90, 40, 1, "");
        cursedArchangel.setActiveSkill(new Skill("Halo Shard", SkillType.DAMAGE, TargetType.ENEMY_BY_AGGRO, 28, 2));
        deck.addCard(cursedArchangel);

        deck.addCard(new Card("e006", "Fallen Wyrm", Faction.DRAGON_LINEAGE, Role.DPS, Rarity.RARE, 115, 30, 8, 75, 35, 1, ""));
        deck.addCard(new Card("e007", "Shadow Disciple", Faction.SHADOW_CONCLAVE, Role.ASSASSIN, Rarity.COMMON, 80, 28, 4, 105, 10, 1, ""));
        deck.addCard(new Card("e008", "Abyssal Priest", Faction.ABYSSAL_CHURCH, Role.SUPPORT, Rarity.COMMON, 95, 14, 5, 80, 15, 1, ""));

        return deck;
    }
}
