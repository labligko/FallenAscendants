package com.fallenascendants.debug;

import java.util.Scanner;

import com.fallenascendants.battle.BattleManager;
import com.fallenascendants.data.CardDatabase;
import com.fallenascendants.enumtype.Faction;
import com.fallenascendants.enumtype.Rarity;
import com.fallenascendants.enumtype.Role;
import com.fallenascendants.enumtype.SkillType;
import com.fallenascendants.enumtype.TargetType;
import com.fallenascendants.model.BattleField;
import com.fallenascendants.model.Card;
import com.fallenascendants.model.Deck;
import com.fallenascendants.model.Skill;
import com.fallenascendants.data.DummyBattleFactory;

public class BattleTester {

    public static void runTest() {
        Deck playerDeck = DummyBattleFactory.createRandomDeck();
        Deck enemyDeck = DummyBattleFactory.createRandomDeck();

        printDeck("PLAYER DECK", playerDeck);
        printDeck("ENEMY DECK", enemyDeck);

        BattleField playerField = new BattleField(playerDeck);
        BattleField enemyField = new BattleField(enemyDeck);

        BattleManager battleManager = new BattleManager(playerField, enemyField);

        System.out.println(battleManager.applyPassiveSkillsAtBattleStart());
        System.out.println(battleManager.applyFactionSynergyAtBattleStart());

//        System.out.println("Total cards: " + CardDatabase.getAllCards().size());
//        System.out.println("common: " + CardDatabase.getCardsByRarity(Rarity.COMMON).size());
//        System.out.println("rare: " + CardDatabase.getCardsByRarity(Rarity.RARE).size());
//        System.out.println("epic: " + CardDatabase.getCardsByRarity(Rarity.EPIC).size());
//        System.out.println("Legend: " + CardDatabase.getCardsByRarity(Rarity.LEGENDARY).size());
//        System.out.println("tank: " + CardDatabase.getCardsByRole(Role.TANK).size());
//        System.out.println("dps: " + CardDatabase.getCardsByRole(Role.DPS).size());
//        System.out.println("assassin: " + CardDatabase.getCardsByRole(Role.ASSASSIN).size());
//        System.out.println("mage: " + CardDatabase.getCardsByRole(Role.MAGE).size());
//        System.out.println("support: " + CardDatabase.getCardsByRole(Role.SUPPORT).size());
//        System.out.println("healer: " + CardDatabase.getCardsByRole(Role.HEALER).size());

        System.out.println("\n==============================");
        System.out.println("BATTLE START");
        System.out.println("==============================");

        Scanner scanner = new Scanner(System.in);
        int action = 1;

        while (!battleManager.isBattleOver()) {
            System.out.println("\nPress ENTER for next action...");
            scanner.nextLine();

            System.out.println("\n---------- ACTION " + action + " ----------");

            String actionLog = battleManager.processSingleAction();
            System.out.println(actionLog);

            printBattleField("PLAYER", playerField);
            printBattleField("ENEMY", enemyField);

            action++;
        }

        System.out.println("\n==============================");
        System.out.println("BATTLE RESULT");
        System.out.println("==============================");

        if (battleManager.isPlayerWin()) {
            System.out.println("PLAYER WIN!");
        } else {
            System.out.println("PLAYER LOSE!");
        }
    }

    private static Deck createPlayerDeck() {
        Deck deck = new Deck();

        Card fallenSeraph = new Card("p001", "Fallen Seraph", Faction.CELESTIAL_REMNANTS, Role.TANK, Rarity.RARE, 180, 20, 15, 70, 80, 1, "");
        fallenSeraph.setActiveSkill(new Skill("Divine Taunt", SkillType.TAUNT, TargetType.SELF, 30, 3));
        deck.addCard(fallenSeraph);

        Card voidPaladin = new Card("p002", "Void Paladin", Faction.VOID_CORRUPTED, Role.DPS, Rarity.RARE, 120, 35, 8, 85, 40, 1, "");
        voidPaladin.setActiveSkill(new Skill("Void Slash", SkillType.DAMAGE, TargetType.ENEMY_BY_AGGRO, 30, 2));
        deck.addCard(voidPaladin);

        Card shadowSaint = new Card("p003", "Shadow Saint", Faction.SHADOW_CONCLAVE, Role.ASSASSIN, Rarity.EPIC, 90, 45, 5, 120, 25, 1, "");
        shadowSaint.setActiveSkill(new Skill("Execute", SkillType.DAMAGE, TargetType.ENEMY_LOWEST_HP, 35, 2));
        deck.addCard(shadowSaint);

        Card brokenMadonna = new Card("p004", "Broken Madonna", Faction.CELESTIAL_REMNANTS, Role.HEALER, Rarity.EPIC, 100, 10, 6, 90, 20, 1, "");
        brokenMadonna.setActiveSkill(new Skill("Holy Light", SkillType.HEAL, TargetType.ALLY_LOWEST_HP, 25, 2));
        deck.addCard(brokenMadonna);

        Card voidBishop = new Card("p005", "Void Bishop", Faction.ABYSSAL_CHURCH, Role.MAGE, Rarity.RARE, 95, 38, 5, 95, 25, 1, "");
        voidBishop.setActiveSkill(new Skill("Dark Prophecy", SkillType.DAMAGE, TargetType.ALL_ENEMIES, 20, 3));
        deck.addCard(voidBishop);

        deck.addCard(new Card("p006", "Wyvern Hatchling", Faction.DRAGON_LINEAGE, Role.DPS, Rarity.COMMON, 100, 25, 5, 80, 30, 1, ""));
        deck.addCard(new Card("p007", "Heretic Scribe", Faction.ABYSSAL_CHURCH, Role.SUPPORT, Rarity.COMMON, 90, 12, 5, 75, 20, 1, ""));
        deck.addCard(new Card("p008", "Exiled Prophet", Faction.MORTAL_ASCENDANTS, Role.SUPPORT, Rarity.RARE, 100, 15, 7, 85, 25, 1, ""));

        return deck;
    }

    private static Deck createEnemyDeck() {
        Deck deck = new Deck();

        Card abyssalColossus = new Card("e001", "Abyssal Colossus", Faction.VOID_CORRUPTED, Role.TANK, Rarity.RARE, 170, 22, 14, 60, 85, 1, "");
        abyssalColossus.setActiveSkill(new Skill("Abyssal Guard", SkillType.TAUNT, TargetType.SELF, 30, 3));
        deck.addCard(abyssalColossus);

        Card cursedArchangel = new Card("e002", "Cursed Archangel", Faction.CELESTIAL_REMNANTS, Role.DPS, Rarity.RARE, 110, 36, 7, 90, 40, 1, "");
        cursedArchangel.setActiveSkill(new Skill("Halo Shard", SkillType.DAMAGE, TargetType.ENEMY_BY_AGGRO, 28, 2));
        deck.addCard(cursedArchangel);

        Card fallenValkyrie = new Card("e003", "Fallen Valkyrie", Faction.SHADOW_CONCLAVE, Role.ASSASSIN, Rarity.EPIC, 85, 42, 5, 125, 25, 1, "");
        fallenValkyrie.setActiveSkill(new Skill("Wing Pierce", SkillType.DAMAGE, TargetType.ENEMY_LOWEST_HP, 34, 2));
        deck.addCard(fallenValkyrie);

        Card corruptOracle = new Card("e004", "Corrupt Oracle", Faction.ABYSSAL_CHURCH, Role.MAGE, Rarity.RARE, 90, 34, 5, 95, 25, 1, "");
        corruptOracle.setActiveSkill(new Skill("Black Vision", SkillType.DAMAGE, TargetType.ALL_ENEMIES, 18, 3));
        deck.addCard(corruptOracle);

        Card lastApostle = new Card("e005", "Last Apostle", Faction.MORTAL_ASCENDANTS, Role.HEALER, Rarity.EPIC, 105, 12, 6, 88, 20, 1, "");
        lastApostle.setActiveSkill(new Skill("Last Prayer", SkillType.HEAL, TargetType.ALLY_LOWEST_HP, 24, 2));
        deck.addCard(lastApostle);

        deck.addCard(new Card("e006", "Fallen Wyrm", Faction.DRAGON_LINEAGE, Role.DPS, Rarity.RARE, 115, 30, 8, 75, 35, 1, ""));
        deck.addCard(new Card("e007", "Shadow Disciple", Faction.SHADOW_CONCLAVE, Role.ASSASSIN, Rarity.COMMON, 80, 28, 4, 105, 20, 1, ""));
        deck.addCard(new Card("e008", "Abyssal Priest", Faction.ABYSSAL_CHURCH, Role.SUPPORT, Rarity.COMMON, 95, 14, 5, 80, 20, 1, ""));

        return deck;
    }

    private static void printDeck(String title, Deck deck) {
        System.out.println("\n==============================");
        System.out.println(title);
        System.out.println("==============================");

        for (int i = 0; i < deck.size(); i++) {
            Card card = deck.getCard(i);

            String activeSkillName = card.getActiveSkill() == null
                ? "None"
                : card.getActiveSkill().getName();

            System.out.println(
                "[" + (i + 1) + "] " +
                    card.getName() +
                    " | Role: " + card.getRole() +
                    " | Rarity: " + card.getRarity() +
                    " | HP: " + card.getMaxHp() +
                    " | ATK: " + card.getAtk() +
                    " | DEF: " + card.getDef() +
                    " | SPD: " + card.getSpd() +
                    " | Aggro: " + card.getAggro() +
                    " | Active Skill: " + activeSkillName
            );
        }
    }

    private static void printBattleField(String owner, BattleField field) {
        System.out.println("\n" + owner + " FIELD");

        Card[] activeCards = field.getActiveCards();

        for (int i = 0; i < activeCards.length; i++) {
            Card card = activeCards[i];

            if (card == null) {
                System.out.println("Slot " + (i + 1) + ": Empty");
            } else {
                System.out.println(
                    "Slot " + (i + 1) + ": " +
                        card.getName() +
                        " | HP: " + card.getCurrentHp() + "/" + card.getMaxHp() +
                        " | Shield: " + card.getShield() +
                        " | ATK: " + card.getAtk() +
                        " | DEF: " + card.getDef() +
                        " | SPD: " + card.getSpd() +
                        " | Aggro: " + card.getAggro() +
                        " | Role: " + card.getRole()
                );
            }
        }

        System.out.println("Reserve: " + field.getReserveCards().size() + " card(s)");
        System.out.println("Graveyard: " + field.getGraveyard().size() + " card(s)");
    }
}
