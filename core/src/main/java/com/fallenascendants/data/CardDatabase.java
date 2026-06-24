package com.fallenascendants.data;

import com.fallenascendants.enumtype.*;
import com.fallenascendants.model.Card;
import com.fallenascendants.model.Skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CardDatabase {
    private static final HashMap<String, Card> cards = new HashMap<>();
    private static final Random random = new Random();

    static {
        registerCards();
    }

    private static void registerCards() {
        // ====================================================================
        // 1. CELESTIAL REMNANTS (5 Cards)
        // ====================================================================

        // [KARTU LAMA]
        Card fallenSeraph = new Card(
            "fallen_seraph", "Fallen Seraph", Faction.CELESTIAL_REMNANTS, Role.TANK, Rarity.RARE,
            150, 20, 15, 70, 110, 1, "cards/fallen_seraph.png"
        );
        fallenSeraph.setActiveSkill(new Skill("Divine Taunt", SkillType.TAUNT, TargetType.SELF, 50, 3));
        cards.put(fallenSeraph.getId(), fallenSeraph);

        // [KARTU LAMA]
        Card brokenMadonna = new Card(
            "broken_madonna", "Broken Madonna", Faction.CELESTIAL_REMNANTS, Role.HEALER, Rarity.EPIC,
            95, 10, 6, 90, 15, 1, "cards/broken_madonna.png"
        );
        brokenMadonna.setActiveSkill(new Skill("Holy Light", SkillType.HEAL, TargetType.ALLY_LOWEST_HP, 20, 3));
        brokenMadonna.setPassiveSkill(new Skill("Purity", SkillType.SHIELD, TargetType.SELF, 10, 0));
        cards.put(brokenMadonna.getId(), brokenMadonna);

        Card celestialSquire = new Card(
            "celestial_squire", "Celestial Squire", Faction.CELESTIAL_REMNANTS, Role.SUPPORT, Rarity.COMMON,
            90, 15, 8, 85, 15, 1, "cards/celestial_squire.png"
        );
        celestialSquire.setActiveSkill(new Skill("Lesser Ward", SkillType.SHIELD, TargetType.SELF, 15, 2));
        cards.put(celestialSquire.getId(), celestialSquire);

        Card gateDefender = new Card(
            "gate_defender", "Gate Defender", Faction.CELESTIAL_REMNANTS, Role.TANK, Rarity.COMMON,
            140, 12, 18, 60, 100, 1, "cards/gate_defender.png"
        );
        gateDefender.setActiveSkill(new Skill("Brace", SkillType.SHIELD, TargetType.SELF, 25, 3));
        cards.put(gateDefender.getId(), gateDefender);

        Card starfallInvoker = new Card(
            "starfall_invoker", "Starfall Invoker", Faction.CELESTIAL_REMNANTS, Role.MAGE, Rarity.EPIC,
            100, 42, 6, 80, 25, 1, "cards/starfall_invoker.png"
        );
        starfallInvoker.setActiveSkill(new Skill("Starfall", SkillType.DAMAGE, TargetType.ALL_ENEMIES, 22, 3));
        cards.put(starfallInvoker.getId(), starfallInvoker);

        // ====================================================================
        // 2. VOID CORRUPTED (5 Cards)
        // ====================================================================

        // [KARTU LAMA]
        Card voidPaladin = new Card(
            "void_paladin", "Void Paladin", Faction.VOID_CORRUPTED, Role.DPS, Rarity.RARE,
            105, 38, 8, 85, 40, 1, "cards/void_paladin.png"
        );
        voidPaladin.setActiveSkill(new Skill("Void Slash", SkillType.DAMAGE, TargetType.ENEMY_BY_AGGRO, 48, 2));
        cards.put(voidPaladin.getId(), voidPaladin);

        Card voidCrawler = new Card(
            "void_crawler", "Void Crawler", Faction.VOID_CORRUPTED, Role.ASSASSIN, Rarity.COMMON,
            80, 35, 4, 115, 20, 1, "cards/void_crawler.png"
        );
        voidCrawler.setActiveSkill(new Skill("Void Bite", SkillType.DAMAGE, TargetType.ENEMY_LOWEST_HP, 35, 2));
        cards.put(voidCrawler.getId(), voidCrawler);

        Card abyssalHulk = new Card(
            "abyssal_hulk", "Abyssal Hulk", Faction.VOID_CORRUPTED, Role.TANK, Rarity.RARE,
            160, 18, 14, 55, 105, 1, "cards/abyssal_hulk.png"
        );
        abyssalHulk.setActiveSkill(new Skill("Void Taunt", SkillType.TAUNT, TargetType.SELF, 40, 3));
        cards.put(abyssalHulk.getId(), abyssalHulk);

        Card voidWeaver = new Card(
            "void_weaver", "Void Weaver", Faction.VOID_CORRUPTED, Role.MAGE, Rarity.EPIC,
            95, 45, 5, 88, 25, 1, "cards/void_weaver.png"
        );
        voidWeaver.setActiveSkill(new Skill("Void Eruption", SkillType.DAMAGE, TargetType.ALL_ENEMIES, 28, 3));
        cards.put(voidWeaver.getId(), voidWeaver);

        Card voidHarbinger = new Card(
            "void_harbinger", "Void Harbinger", Faction.VOID_CORRUPTED, Role.ASSASSIN, Rarity.LEGENDARY,
            110, 55, 8, 125, 20, 1, "cards/void_harbinger.png"
        );
        voidHarbinger.setActiveSkill(new Skill("Void Execute", SkillType.DAMAGE, TargetType.ENEMY_LOWEST_HP, 60, 2));
        voidHarbinger.setPassiveSkill(new Skill("Void Shift", SkillType.SHIELD, TargetType.SELF, 20, 0));
        voidHarbinger.setDeathSkill(new Skill("Void Collapse", SkillType.DAMAGE, TargetType.ALL_ENEMIES, 30, 0));
        cards.put(voidHarbinger.getId(), voidHarbinger);

        // ====================================================================
        // 3. ABYSSAL CHURCH (5 Cards)
        // ====================================================================

        // [KARTU LAMA]
        Card voidBishop = new Card(
            "void_bishop", "Void Bishop", Faction.ABYSSAL_CHURCH, Role.MAGE, Rarity.RARE,
            90, 38, 5, 95, 25, 1, "cards/void_bishop.png"
        );
        voidBishop.setActiveSkill(new Skill("Dark Prophecy", SkillType.DAMAGE, TargetType.ALL_ENEMIES, 25, 3));
        cards.put(voidBishop.getId(), voidBishop);

        // [KARTU LAMA]
        Card hereticScribe = new Card(
            "heretic_scribe", "Heretic Scribe", Faction.ABYSSAL_CHURCH, Role.SUPPORT, Rarity.COMMON,
            90, 12, 5, 75, 15, 1, "cards/heretic_scribe.png"
        );
        hereticScribe.setActiveSkill(new Skill("Forbidden Ward", SkillType.SHIELD, TargetType.SELF, 25, 3));
        cards.put(hereticScribe.getId(), hereticScribe);

        Card cultistAcolyte = new Card(
            "cultist_acolyte", "Cultist Acolyte", Faction.ABYSSAL_CHURCH, Role.HEALER, Rarity.COMMON,
            85, 12, 5, 80, 15, 1, "cards/cultist_acolyte.png"
        );
        cultistAcolyte.setActiveSkill(new Skill("Dark Mend", SkillType.HEAL, TargetType.ALLY_LOWEST_HP, 18, 2));
        cards.put(cultistAcolyte.getId(), cultistAcolyte);

        Card churchZealot = new Card(
            "church_zealot", "Church Zealot", Faction.ABYSSAL_CHURCH, Role.TANK, Rarity.RARE,
            145, 25, 12, 65, 95, 1, "cards/church_zealot.png"
        );
        churchZealot.setActiveSkill(new Skill("Fanaticism", SkillType.TAUNT, TargetType.SELF, 35, 3));
        cards.put(churchZealot.getId(), churchZealot);

        Card bloodInquisitor = new Card(
            "blood_inquisitor", "Blood Inquisitor", Faction.ABYSSAL_CHURCH, Role.DPS, Rarity.EPIC,
            115, 46, 9, 82, 30, 1, "cards/blood_inquisitor.png"
        );
        bloodInquisitor.setActiveSkill(new Skill("Unholy Strike", SkillType.DAMAGE, TargetType.ENEMY_BY_AGGRO, 55, 2));
        cards.put(bloodInquisitor.getId(), bloodInquisitor);

        // ====================================================================
        // 4. DRAGON LINEAGE (5 Cards)
        // ====================================================================

        // [KARTU LAMA]
        Card wyvernHatchling = new Card(
            "wyvern_hatchling", "Wyvern Hatchling", Faction.DRAGON_LINEAGE, Role.DPS, Rarity.COMMON,
            100, 28, 5, 80, 30, 1, "cards/wyvern_hatchling.png"
        );
        cards.put(wyvernHatchling.getId(), wyvernHatchling);

        // [KARTU LAMA]
        Card celestialDragonUndone = new Card(
            "celestial_dragon_undone", "Celestial Dragon Undone", Faction.DRAGON_LINEAGE, Role.MAGE, Rarity.LEGENDARY,
            130, 50, 10, 90, 45, 1, "cards/celestial_dragon_undone.png"
        );
        celestialDragonUndone.setActiveSkill(new Skill("Heaven Collapse", SkillType.DAMAGE, TargetType.ALL_ENEMIES, 35, 3));
        celestialDragonUndone.setPassiveSkill(new Skill("Dragon Aura", SkillType.SHIELD, TargetType.SELF, 15, 0));
        celestialDragonUndone.setDeathSkill(new Skill("Final Roar", SkillType.DAMAGE, TargetType.ALL_ENEMIES, 40, 0));
        cards.put(celestialDragonUndone.getId(), celestialDragonUndone);

        Card drakeDefender = new Card(
            "drake_defender", "Drake Defender", Faction.DRAGON_LINEAGE, Role.TANK, Rarity.RARE,
            155, 22, 16, 58, 105, 1, "cards/drake_defender.png"
        );
        drakeDefender.setActiveSkill(new Skill("Dragon Scale", SkillType.SHIELD, TargetType.SELF, 45, 3));
        cards.put(drakeDefender.getId(), drakeDefender);

        Card fallenWyrm = new Card(
            "fallen_wyrm", "Fallen Wyrm", Faction.DRAGON_LINEAGE, Role.DPS, Rarity.EPIC,
            120, 48, 10, 85, 35, 1, "cards/fallen_wyrm.png"
        );
        fallenWyrm.setActiveSkill(new Skill("Flame Breath", SkillType.DAMAGE, TargetType.ENEMY_BY_AGGRO, 58, 2));
        cards.put(fallenWyrm.getId(), fallenWyrm);

        Card stormscale = new Card(
            "stormscale", "Stormscale", Faction.DRAGON_LINEAGE, Role.MAGE, Rarity.EPIC,
            105, 44, 7, 84, 25, 1, "cards/stormscale.png"
        );
        stormscale.setActiveSkill(new Skill("Thunderstorm", SkillType.DAMAGE, TargetType.ALL_ENEMIES, 26, 3));
        cards.put(stormscale.getId(), stormscale);

        // ====================================================================
        // 5. MORTAL ASCENDANTS (5 Cards)
        // ====================================================================

        Card rebelSwordsman = new Card(
            "rebel_swordsman", "Rebel Swordsman", Faction.MORTAL_ASCENDANTS, Role.DPS, Rarity.COMMON,
            100, 32, 6, 82, 30, 1, "cards/rebel_swordsman.png"
        );
        rebelSwordsman.setActiveSkill(new Skill("Heavy Slash", SkillType.DAMAGE, TargetType.ENEMY_BY_AGGRO, 38, 2));
        cards.put(rebelSwordsman.getId(), rebelSwordsman);

        Card divineThief = new Card(
            "divine_thief", "Divine Thief", Faction.MORTAL_ASCENDANTS, Role.SUPPORT, Rarity.RARE,
            90, 20, 6, 110, 20, 1, "cards/divine_thief.png"
        );
        divineThief.setActiveSkill(new Skill("Tricky Shield", SkillType.SHIELD, TargetType.SELF, 30, 2));
        cards.put(divineThief.getId(), divineThief);

        Card blessedAlchemist = new Card(
            "blessed_alchemist", "Blessed Alchemist", Faction.MORTAL_ASCENDANTS, Role.HEALER, Rarity.EPIC,
            95, 15, 7, 92, 15, 1, "cards/blessed_alchemist.png"
        );
        blessedAlchemist.setActiveSkill(new Skill("Elixir of Life", SkillType.HEAL, TargetType.ALLY_LOWEST_HP, 30, 3));
        cards.put(blessedAlchemist.getId(), blessedAlchemist);

        Card ascendedChampion = new Card(
            "ascended_champion", "Ascended Champion", Faction.MORTAL_ASCENDANTS, Role.TANK, Rarity.EPIC,
            150, 28, 14, 70, 100, 1, "cards/ascended_champion.png"
        );
        ascendedChampion.setActiveSkill(new Skill("Mortal Defiance", SkillType.TAUNT, TargetType.SELF, 50, 3));
        cards.put(ascendedChampion.getId(), ascendedChampion);

        Card mortalGod = new Card(
            "mortal_god", "Mortal God", Faction.MORTAL_ASCENDANTS, Role.MAGE, Rarity.LEGENDARY,
            125, 52, 9, 88, 25, 1, "cards/mortal_god.png"
        );
        mortalGod.setActiveSkill(new Skill("Stolen Divinity", SkillType.DAMAGE, TargetType.ALL_ENEMIES, 40, 3));
        mortalGod.setPassiveSkill(new Skill("Aura of Ascancy", SkillType.SHIELD, TargetType.SELF, 20, 0));
        mortalGod.setDeathSkill(new Skill("Divine Shatter", SkillType.DAMAGE, TargetType.ALL_ENEMIES, 45, 0));
        cards.put(mortalGod.getId(), mortalGod);

        // ====================================================================
        // 6. SHADOW CONCLAVE (5 Cards)
        // ====================================================================

        // [KARTU LAMA]
        Card shadowSaint = new Card(
            "shadow_saint", "Shadow Saint", Faction.SHADOW_CONCLAVE, Role.ASSASSIN, Rarity.EPIC,
            85, 45, 5, 120, 15, 1, "cards/shadow_saint.png"
        );
        shadowSaint.setActiveSkill(new Skill("Execute", SkillType.DAMAGE, TargetType.ENEMY_LOWEST_HP, 52, 2));
        cards.put(shadowSaint.getId(), shadowSaint);

        Card shadowInitiate = new Card(
            "shadow_initiate", "Shadow Initiate", Faction.SHADOW_CONCLAVE, Role.ASSASSIN, Rarity.COMMON,
            85, 34, 4, 105, 20, 1, "cards/shadow_initiate.png"
        );
        shadowInitiate.setActiveSkill(new Skill("Surprise Attack", SkillType.DAMAGE, TargetType.ENEMY_LOWEST_HP, 40, 2));
        cards.put(shadowInitiate.getId(), shadowInitiate);

        Card silentBlade = new Card(
            "silent_blade", "Silent Blade", Faction.SHADOW_CONCLAVE, Role.DPS, Rarity.RARE,
            95, 40, 6, 100, 25, 1, "cards/silent_blade.png"
        );
        silentBlade.setActiveSkill(new Skill("Backstab", SkillType.DAMAGE, TargetType.ENEMY_LOWEST_HP, 46, 2));
        cards.put(silentBlade.getId(), silentBlade);

        Card nightPoisoner = new Card(
            "night_poisoner", "Night Poisoner", Faction.SHADOW_CONCLAVE, Role.SUPPORT, Rarity.RARE,
            90, 18, 6, 102, 15, 1, "cards/night_poisoner.png"
        );
        nightPoisoner.setActiveSkill(new Skill("Toxic Smoke", SkillType.DAMAGE, TargetType.ALL_ENEMIES, 15, 3));
        cards.put(nightPoisoner.getId(), nightPoisoner);

        Card unseenSovereign = new Card(
            "unseen_sovereign", "Unseen Sovereign", Faction.SHADOW_CONCLAVE, Role.ASSASSIN, Rarity.LEGENDARY,
            105, 58, 7, 130, 15, 1, "cards/unseen_sovereign.png"
        );
        unseenSovereign.setActiveSkill(new Skill("Assassinate", SkillType.DAMAGE, TargetType.ENEMY_LOWEST_HP, 70, 2));
        unseenSovereign.setPassiveSkill(new Skill("Shrouded", SkillType.SHIELD, TargetType.SELF, 25, 0));
        unseenSovereign.setDeathSkill(new Skill("Smoke Bomb", SkillType.DAMAGE, TargetType.ALL_ENEMIES, 35, 0));
        cards.put(unseenSovereign.getId(), unseenSovereign);
    }

    public static Card getCardById(String id) {
        Card original = cards.get(id);
        if (original == null) {
            return null;
        }
        return cloneCard(original);
    }

    public static List<Card> getAllCards() {
        ArrayList<Card> result = new ArrayList<>();
        for (Card card : cards.values()) {
            result.add(cloneCard(card));
        }
        return result;
    }

    public static List<Card> getCardsByRarity(Rarity rarity) {
        ArrayList<Card> result = new ArrayList<>();
        for (Card card : cards.values()) {
            if (card.getRarity() == rarity) {
                result.add(cloneCard(card));
            }
        }
        return result;
    }

    public static List<Card> getCardsByRole(Role role) {
        ArrayList<Card> result = new ArrayList<>();
        for (Card card : cards.values()) {
            if (card.getRole() == role) {
                result.add(cloneCard(card));
            }
        }
        return result;
    }

    public static Card getRandomCardByRarity(Rarity rarity) {
        List<Card> filteredCards = getCardsByRarity(rarity);
        if (filteredCards.isEmpty()) {
            return null;
        }
        return filteredCards.get(random.nextInt(filteredCards.size()));
    }

    private static Card cloneCard(Card original) {
        Card cloned = new Card(
            original.getId(),
            original.getName(),
            original.getFaction(),
            original.getRole(),
            original.getRarity(),
            original.getMaxHp(),
            original.getAtk(),
            original.getDef(),
            original.getSpd(),
            original.getAggro(),
            original.getLevel(),
            original.getImagePath()
        );

        // KLONING ACTIVE SKILL
        if (original.getActiveSkill() != null) {
            Skill skill = original.getActiveSkill();
            cloned.setActiveSkill(new Skill(
                skill.getName(), skill.getSkillType(), skill.getTargetType(), skill.getPower(), skill.getCooldown()
            ));
        }

        // FIX BUG: KLONING PASSIVE SKILL SEKARANG DIDUKUNG
        if (original.getPassiveSkill() != null) {
            Skill skill = original.getPassiveSkill();
            cloned.setPassiveSkill(new Skill(
                skill.getName(), skill.getSkillType(), skill.getTargetType(), skill.getPower(), skill.getCooldown()
            ));
        }

        // FIX BUG: KLONING DEATH SKILL SEKARANG DIDUKUNG
        if (original.getDeathSkill() != null) {
            Skill skill = original.getDeathSkill();
            cloned.setDeathSkill(new Skill(
                skill.getName(), skill.getSkillType(), skill.getTargetType(), skill.getPower(), skill.getCooldown()
            ));
        }

        return cloned;
    }
}
