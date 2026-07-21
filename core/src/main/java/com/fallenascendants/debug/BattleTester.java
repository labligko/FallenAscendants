package com.fallenascendants.debug;

import java.util.Scanner;

import com.fallenascendants.battle.BattleManager;
import com.fallenascendants.battle.FactionCounterGraph;
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
import com.fallenascendants.model.Player;
import com.fallenascendants.model.ProgressionManager;
import com.fallenascendants.data.DummyBattleFactory;

public class BattleTester {

    public static void runTest() {
        Scanner scanner = new Scanner(System.in);

        // 1. SETUP PLAYER & INITIAL COLLECTION (termasuk duplikat untuk test upgrade)
        Player player = new Player("Player 1");
        player.addGold(300); // Mulai dengan Gold untuk tes upgrade

        // Menambahkan 3 Ashveil Acolyte (1 utama, 2 duplikat)
        Card acolyte1 = CardDatabase.getCardById("ashveil_acolyte");
        Card acolyte2 = CardDatabase.getCardById("ashveil_acolyte");
        Card acolyte3 = CardDatabase.getCardById("ashveil_acolyte");
        player.addCardToCollection(acolyte1);
        player.addCardToCollection(acolyte2);
        player.addCardToCollection(acolyte3);

        // Tambah beberapa kartu lain
        player.addCardToCollection(CardDatabase.getCardById("Veilsworn"));
        player.addCardToCollection(CardDatabase.getCardById("thorngate_sentinel"));

        // 2. MENU UPGRADE SEBELUM BERTARUNG
        boolean upgrading = true;
        while (upgrading) {
            System.out.println("\n=== STATUS PLAYER ===");
            System.out.println("Gold: " + player.getGold() + " Gold");
            System.out.println("Collection:");
            java.util.List<Card> collection = player.getCollection();
            for (int i = 0; i < collection.size(); i++) {
                Card c = collection.get(i);
                int dupCount = c.getCopies() - 1;
                System.out.println("[" + (i + 1) + "] " + c.getName() + " (Lvl: " + c.getLevel() + ") | HP: " + c.getMaxHp() + " | ATK: " + c.getAtk() + " | DEF: " + c.getDef() + " | Duplikat: " + dupCount);
            }

            System.out.println("\nApakah Anda ingin meng-upgrade kartu? (Masukkan nomor kartu untuk upgrade, atau 0 untuk lanjut bertarung)");
            int choice = -1;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                choice = -1;
            }

            if (choice == 0) {
                upgrading = false;
            } else if (choice > 0 && choice <= collection.size()) {
                Card targetCard = collection.get(choice - 1);
                int costGold = targetCard.getLevel() * 100;
                int costDup = targetCard.getLevel();
                System.out.println("Mencoba upgrade " + targetCard.getName() + " ke level " + (targetCard.getLevel() + 1));
                System.out.println("Biaya: " + costGold + " Gold dan " + costDup + " Kartu Duplikat");

                boolean success = ProgressionManager.upgradeCard(player, targetCard);
                if (success) {
                    System.out.println("UPGRADE BERHASIL! Level sekarang: " + targetCard.getLevel() + ", HP: " + targetCard.getMaxHp() + ", ATK: " + targetCard.getAtk() + ", DEF: " + targetCard.getDef());
                } else {
                    System.out.println("UPGRADE GAGAL! Pastikan Gold dan jumlah kartu duplikat mencukupi.");
                }
            } else {
                System.out.println("Pilihan tidak valid.");
            }
        }

        // 3. MEMPERSIAPKAN DECK PLAYER UNTUK BATTLE
        System.out.println("\nMempersiapkan Deck Pertempuran...");
        Deck playerDeck = new Deck();
        java.util.List<Card> collection = player.getCollection();
        for (int i = 0; i < collection.size() && !playerDeck.isFull(); i++) {
            playerDeck.addCard(collection.get(i));
        }

        // Jika kartu kurang dari 8, lengkapi dengan kartu acak dari database
        while (!playerDeck.isFull()) {
            playerDeck.addCard(CardDatabase.getRandomCardByRarity(Rarity.COMMON));
        }

        Deck enemyDeck = DummyBattleFactory.createRandomDeck();

        printDeck("PLAYER DECK", playerDeck);
        printDeck("ENEMY DECK", enemyDeck);

        BattleField playerField = new BattleField(playerDeck);
        BattleField enemyField = new BattleField(enemyDeck);

        BattleManager battleManager = new BattleManager(playerField, enemyField);

        System.out.println(battleManager.applyPassiveSkillsAtBattleStart());
        System.out.println(battleManager.applyFactionSynergyAtBattleStart());

        FactionCounterGraph factionCounterGraph = new FactionCounterGraph();

        System.out.println(factionCounterGraph.getCounterReport());
        System.out.println(factionCounterGraph.getDepthFirstTraversalReport(
            Faction.CELESTIAL_REMNANTS
        ));

        System.out.println("\n==============================");
        System.out.println("BATTLE START");
        System.out.println("==============================");

        System.out.println("Mensimulasikan pertarungan secara instan...");
        boolean isWin = new java.util.Random().nextBoolean();

        System.out.println("\n==============================");
        System.out.println("BATTLE RESULT");
        System.out.println("==============================");

        if (isWin) {
            System.out.println("PLAYER WIN!");
        } else {
            System.out.println("PLAYER LOSE!");
        }

        // 4. BATTLE OUTCOME REWARDS
        System.out.println("\n==============================");
        System.out.println("PROSES REWARD");
        System.out.println("==============================");

        ProgressionManager.BattleRewards rewards = ProgressionManager.processBattleRewards(player, isWin);
        System.out.println("Anda mendapatkan " + rewards.getGoldEarned() + " Gold!");
        System.out.println("Total Gold sekarang: " + player.getGold() + " Gold");

        if (isWin) {
            System.out.println("\nSelamat! Anda berhak memilih 1 dari 3 Kartu Tertutup:");
            System.out.println("[1] Kartu Tertutup A");
            System.out.println("[2] Kartu Tertutup B");
            System.out.println("[3] Kartu Tertutup C");

            int chosenOption = -1;
            while (chosenOption < 1 || chosenOption > 3) {
                System.out.print("Pilih kartu (1-3): ");
                try {
                    chosenOption = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    chosenOption = -1;
                }
                if (chosenOption < 1 || chosenOption > 3) {
                    System.out.println("Pilihan tidak valid. Silakan masukkan angka 1, 2, atau 3.");
                }
            }

            // Claim reward
            Card rewardCard = ProgressionManager.claimCardReward(player, rewards, chosenOption - 1);
            if (rewardCard != null) {
                System.out.println("\n[REVEAL] Anda mendapatkan: " + rewardCard.getName() + " (" + rewardCard.getRarity() + ")!");
            } else {
                System.out.println("Gagal mengklaim reward.");
            }
        } else {
            System.out.println("Kalah pertarungan. Anda tidak mendapatkan kartu.");
        }

        System.out.println("\n=== STATUS PLAYER AKHIR ===");
        System.out.println("Gold: " + player.getGold() + " Gold");
        System.out.println("Collection:");
        collection = player.getCollection();
        for (int i = 0; i < collection.size(); i++) {
            Card c = collection.get(i);
            System.out.println("[" + (i + 1) + "] " + c.getName() + " (Lvl: " + c.getLevel() + ") | HP: " + c.getMaxHp() + " | ATK: " + c.getAtk() + " | DEF: " + c.getDef());
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

    public static void main(String[] args) {
        runTest();
    }
}
