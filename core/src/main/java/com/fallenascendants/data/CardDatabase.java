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
        // 1. CELESTIAL REMNANTS (10 Cards)
        // Theme: Holy guardian, shield, heal, taunt, defensive support
        // Distribution: 4 Common, 3 Rare, 2 Epic, 1 Legendary
        // ====================================================================

        // COMMON 1
        Card ashveilAcolyte = new Card(
            "ashveil_acolyte", "Ashveil Acolyte", Faction.CELESTIAL_REMNANTS, Role.HEALER, Rarity.COMMON,
            85, 10, 6, 85, 15, 1, "cards/ashveil_acolyte.png"
        );
        // ACTIVE: Lesser Heal
        // Effect: Heal the ally with the lowest HP by 16.
        ashveilAcolyte.setActiveSkill(new Skill("Lesser Heal", SkillType.HEAL, TargetType.ALLY_LOWEST_HP, 16, 3));
        cards.put(ashveilAcolyte.getId(), ashveilAcolyte);

        // COMMON 2
        Card Veilsworn = new Card(
            "Veilsworn", "Veilsworn", Faction.CELESTIAL_REMNANTS, Role.SUPPORT, Rarity.COMMON,
            95, 14, 8, 80, 20, 1, "cards/Veilsworn.png"
        );
        // ACTIVE: Lesser Ward
        // Effect: Gain Shield 18.
        Veilsworn.setActiveSkill(new Skill("Lesser Ward", SkillType.SHIELD, TargetType.SELF, 18, 3));
        cards.put(Veilsworn.getId(), Veilsworn);

        // COMMON 3
        Card thorngateSentinel = new Card(
            "thorngate_sentinel", "Thorngate Sentinel", Faction.CELESTIAL_REMNANTS, Role.TANK, Rarity.COMMON,
            128, 15, 10, 58, 105, 1, "cards/thorngate_sentinel.png"
        );
        // ACTIVE: Brace
        // Effect: Gain Shield 25.
        thorngateSentinel.setActiveSkill(new Skill("Brace", SkillType.SHIELD, TargetType.SELF, 15, 3));
        cards.put(thorngateSentinel.getId(), thorngateSentinel);

        // COMMON 4
        Card fadebornScout = new Card(
            "fadeborn_scout", "Fadeborn Scout", Faction.CELESTIAL_REMNANTS, Role.DPS, Rarity.COMMON,
            90, 30, 5, 92, 30, 1, "cards/fadeborn_scout.png"
        );
        // ACTIVE: Radiant Strike
        // Effect: Deal 28 damage to an enemy based on aggro.
        fadebornScout.setActiveSkill(new Skill("Radiant Strike", SkillType.DAMAGE, TargetType.ENEMY_BY_AGGRO, 28, 2));
        cards.put(fadebornScout.getId(), fadebornScout);

        // RARE 1
        Card fallenSeraph = new Card(
            "fallen_seraph", "Fallen Seraph", Faction.CELESTIAL_REMNANTS, Role.TANK, Rarity.RARE,
            140, 20, 12, 70, 110, 1, "cards/fallen_seraph.png"
        );
        // ACTIVE: Divine Taunt
        // Effect: Increase own aggro by 50.
        fallenSeraph.setActiveSkill(new Skill("Divine Taunt", SkillType.TAUNT, TargetType.SELF, 50, 3));
        cards.put(fallenSeraph.getId(), fallenSeraph);

        // RARE 2
        Card seraphicWarden = new Card(
            "seraphic_warden", "Seraphic Warden", Faction.CELESTIAL_REMNANTS, Role.SUPPORT, Rarity.RARE,
            105, 16, 12, 78, 25, 1, "cards/seraphic_warden.png"
        );
        // ACTIVE: Guardian Prayer
        // Effect: Gain Shield 35.
        seraphicWarden.setActiveSkill(new Skill("Guardian Prayer", SkillType.SHIELD, TargetType.SELF, 35, 3));
        cards.put(seraphicWarden.getId(), seraphicWarden);

        // RARE 3
        Card sanctifiedArcher = new Card(
            "sanctified_archer", "Sanctified Archer", Faction.CELESTIAL_REMNANTS, Role.DPS, Rarity.RARE,
            100, 36, 7, 94, 35, 1, "cards/sanctified_archer.png"
        );
        // ACTIVE: Piercing Light
        // Effect: Deal 42 damage to the enemy with the lowest HP.
        sanctifiedArcher.setActiveSkill(new Skill("Piercing Light", SkillType.DAMAGE, TargetType.ENEMY_LOWEST_HP, 42, 2));
        cards.put(sanctifiedArcher.getId(), sanctifiedArcher);

        // EPIC 1
        Card brokenMadonna = new Card(
            "broken_madonna", "Broken Madonna", Faction.CELESTIAL_REMNANTS, Role.HEALER, Rarity.EPIC,
            95, 10, 6, 90, 15, 1, "cards/broken_madonna.png"
        );
        // ACTIVE: Holy Light
        // Effect: Heal the ally with the lowest HP by 24.

        // PASSIVE: Purity
        // Effect: Gain Shield 10 when passive skills are applied.
        brokenMadonna.setActiveSkill(new Skill("Holy Light", SkillType.HEAL, TargetType.ALLY_LOWEST_HP, 24, 3));
        brokenMadonna.setPassiveSkill(new Skill(
            "Purity",
            SkillType.SHIELD,
            SkillTrigger.PASSIVE,
            ReactiveTrigger.NONE,
            TargetType.SELF,
            10,
            0,
            0
        ));
        cards.put(brokenMadonna.getId(), brokenMadonna);

        // EPIC 2
        Card vaelTheUncrowned = new Card(
            "vael_the_uncrowned", "Vael the Uncrowned", Faction.CELESTIAL_REMNANTS, Role.MAGE, Rarity.EPIC,
            100, 40, 6, 82, 25, 1, "cards/vael_the_uncrowned.png"
        );
        // ACTIVE: Starfall
        // Effect: Deal 24 damage to all enemies.

        // REACTIVE: Dying Radiance
        // Effect: On death, deal 18 damage to all enemies.
        vaelTheUncrowned.setActiveSkill(new Skill("Starfall", SkillType.DAMAGE, TargetType.ALL_ENEMIES, 24, 3));
        vaelTheUncrowned.setDeathSkill(new Skill(
            "Dying Radiance",
            SkillType.DAMAGE,
            SkillTrigger.REACTIVE,
            ReactiveTrigger.ON_DEATH,
            TargetType.ALL_ENEMIES,
            18,
            0,
            0
        ));
        cards.put(vaelTheUncrowned.getId(), vaelTheUncrowned);

        // LEGENDARY 1
        Card Mournveil = new Card(
            "Mournveil", "Mournveil", Faction.CELESTIAL_REMNANTS, Role.HEALER, Rarity.LEGENDARY,
            120, 18, 10, 92, 20, 1, "cards/Mournveil.png"
        );
        // ACTIVE: Final Benediction
        // Effect: Heal the ally with the lowest HP by 34.

        // PASSIVE: Sacred Remnant
        // Effect: Gain Shield 20 when passive skills are applied.

        // REACTIVE: Last Miracle
        // Effect: On death, heal the ally with the lowest HP by 30.
        Mournveil.setActiveSkill(new Skill("Final Benediction", SkillType.HEAL, TargetType.ALLY_LOWEST_HP, 34, 3));
        Mournveil.setPassiveSkill(new Skill(
            "Sacred Remnant",
            SkillType.SHIELD,
            SkillTrigger.PASSIVE,
            ReactiveTrigger.NONE,
            TargetType.SELF,
            20,
            0,
            0
        ));
        Mournveil.setDeathSkill(new Skill(
            "Last Miracle",
            SkillType.HEAL,
            SkillTrigger.REACTIVE,
            ReactiveTrigger.ON_DEATH,
            TargetType.ALLY_LOWEST_HP,
            30,
            0,
            0
        ));
        cards.put(Mournveil.getId(), Mournveil);

        // ====================================================================
        // 2. VOID CORRUPTED (10 Cards)
        // Theme: raw damage, self power, glass cannon, unstable corruption
        // Distribution: 4 Common, 3 Rare, 2 Epic, 1 Legendary
        // ====================================================================

        // COMMON 1
        Card voidCrawler = new Card(
            "void_crawler", "Void Crawler", Faction.VOID_CORRUPTED, Role.ASSASSIN, Rarity.COMMON,
            80, 34, 4, 115, 18, 1, "cards/void_crawler.png"
        );
        /*
        ACTIVE: Void Bite
        Effect: Deal 32 damage to the enemy with the lowest HP.
        */
        voidCrawler.setActiveSkill(new Skill("Void Bite", SkillType.DAMAGE, TargetType.ENEMY_LOWEST_HP, 32, 2));
        cards.put(voidCrawler.getId(), voidCrawler);

        // COMMON 2
        Card hollowGrunt = new Card(
            "hollow_grunt", "Hollow Grunt", Faction.VOID_CORRUPTED, Role.DPS, Rarity.COMMON,
            95, 30, 5, 82, 35, 1, "cards/hollow_grunt.png"
        );
        /*
        ACTIVE: Hollow Strike
        Effect: Deal 30 damage to an enemy based on aggro.
        */
        hollowGrunt.setActiveSkill(new Skill("Hollow Strike", SkillType.DAMAGE, TargetType.ENEMY_BY_AGGRO, 30, 2));
        cards.put(hollowGrunt.getId(), hollowGrunt);

        // COMMON 3
        Card voidbornHulk = new Card(
            "voidborn_hulk", "Voidborn Hulk", Faction.VOID_CORRUPTED, Role.TANK, Rarity.COMMON,
            125, 16, 10, 60, 90, 1, "cards/voidborn_hulk.png"
        );
        /*
        ACTIVE: Distorted Roar
        Effect: Increase own aggro by 30.
        */
        voidbornHulk.setActiveSkill(new Skill("Distorted Roar", SkillType.TAUNT, TargetType.SELF, 30, 3));
        cards.put(voidbornHulk.getId(), voidbornHulk);

        // COMMON 4
        Card riftSpark = new Card(
            "rift_spark", "Rift Spark", Faction.VOID_CORRUPTED, Role.MAGE, Rarity.COMMON,
            85, 32, 4, 92, 25, 1, "cards/rift_spark.png"
        );
        /*
        ACTIVE: Rift Bolt
        Effect: Deal 28 damage to a random enemy.
        */
        riftSpark.setActiveSkill(new Skill("Rift Bolt", SkillType.DAMAGE, TargetType.RANDOM_ENEMY, 28, 2));
        cards.put(riftSpark.getId(), riftSpark);

        // RARE 1
        Card voidPaladin = new Card(
            "void_paladin", "Void Paladin", Faction.VOID_CORRUPTED, Role.DPS, Rarity.RARE,
            105, 38, 8, 85, 40, 1, "cards/void_paladin.png"
        );
        /*
        ACTIVE: Void Slash
        Effect: Deal 48 damage to an enemy based on aggro.
        */
        voidPaladin.setActiveSkill(new Skill("Void Slash", SkillType.DAMAGE, TargetType.ENEMY_BY_AGGRO, 48, 2));
        cards.put(voidPaladin.getId(), voidPaladin);

        // RARE 2
        Card abyssalHulk = new Card(
            "abyssal_hulk", "Abyssal Hulk", Faction.VOID_CORRUPTED, Role.TANK, Rarity.RARE,
            145, 18, 13, 55, 105, 1, "cards/abyssal_hulk.png"
        );
        /*
        ACTIVE: Void Taunt
        Effect: Increase own aggro by 45.
        */
        abyssalHulk.setActiveSkill(new Skill("Void Taunt", SkillType.TAUNT, TargetType.SELF, 45, 3));
        cards.put(abyssalHulk.getId(), abyssalHulk);

        // RARE 3
        Card riftDuelist = new Card(
            "rift_duelist", "Rift Duelist", Faction.VOID_CORRUPTED, Role.ASSASSIN, Rarity.RARE,
            90, 42, 5, 112, 18, 1, "cards/rift_duelist.png"
        );
        /*
        ACTIVE: Rift Pierce
        Effect: Deal 44 damage to the enemy with the lowest HP.
        */
        riftDuelist.setActiveSkill(new Skill("Rift Pierce", SkillType.DAMAGE, TargetType.ENEMY_LOWEST_HP, 44, 2));
        cards.put(riftDuelist.getId(), riftDuelist);

        // EPIC 1
        Card voidWeaver = new Card(
            "void_weaver", "Void Weaver", Faction.VOID_CORRUPTED, Role.MAGE, Rarity.EPIC,
            95, 45, 5, 88, 25, 1, "cards/void_weaver.png"
        );
        /*
        ACTIVE: Void Eruption
        Effect: Deal 28 damage to all enemies.

        REACTIVE: Rift Backlash
        Effect: On death, deal 20 damage to all enemies.
        */
        voidWeaver.setActiveSkill(new Skill("Void Eruption", SkillType.DAMAGE, TargetType.ALL_ENEMIES, 28, 3));
        voidWeaver.setDeathSkill(new Skill(
            "Rift Backlash",
            SkillType.DAMAGE,
            SkillTrigger.REACTIVE,
            ReactiveTrigger.ON_DEATH,
            TargetType.ALL_ENEMIES,
            20,
            0,
            0
        ));
        cards.put(voidWeaver.getId(), voidWeaver);

        // EPIC 2
        Card shatteredVoidKnight = new Card(
            "shattered_void_knight", "Shattered Void Knight", Faction.VOID_CORRUPTED, Role.DPS, Rarity.EPIC,
            115, 45, 10, 86, 38, 1, "cards/shattered_void_knight.png"
        );
        /*
        ACTIVE: Corrupted Cleave
        Effect: Deal 54 damage to an enemy based on aggro.

        PASSIVE: Unstable Core
        Effect: Gain Shield 12 when passive skills are applied.
        */
        shatteredVoidKnight.setActiveSkill(new Skill("Corrupted Cleave", SkillType.DAMAGE, TargetType.ENEMY_BY_AGGRO, 54, 2));
        shatteredVoidKnight.setPassiveSkill(new Skill(
            "Unstable Core",
            SkillType.SHIELD,
            SkillTrigger.PASSIVE,
            ReactiveTrigger.NONE,
            TargetType.SELF,
            12,
            0,
            0
        ));
        cards.put(shatteredVoidKnight.getId(), shatteredVoidKnight);

        // LEGENDARY 1
        Card theNullborn = new Card(
            "the_nullborn", "The Nullborn", Faction.VOID_CORRUPTED, Role.ASSASSIN, Rarity.LEGENDARY,
            110, 58, 8, 125, 18, 1, "cards/the_nullborn.png"
        );
        /*
        ACTIVE: Void Execute
        Effect: Deal 65 damage to the enemy with the lowest HP.

        PASSIVE: Void Shift
        Effect: Gain Shield 25 when passive skills are applied.

        REACTIVE: Void Collapse
        Effect: On death, deal 35 damage to all enemies.
        */
        theNullborn.setActiveSkill(new Skill("Void Execute", SkillType.DAMAGE, TargetType.ENEMY_LOWEST_HP, 65, 2));
        theNullborn.setPassiveSkill(new Skill(
            "Void Shift",
            SkillType.SHIELD,
            SkillTrigger.PASSIVE,
            ReactiveTrigger.NONE,
            TargetType.SELF,
            25,
            0,
            0
        ));
        theNullborn.setDeathSkill(new Skill(
            "Void Collapse",
            SkillType.DAMAGE,
            SkillTrigger.REACTIVE,
            ReactiveTrigger.ON_DEATH,
            TargetType.ALL_ENEMIES,
            35,
            0,
            0
        ));
        cards.put(theNullborn.getId(), theNullborn);

        // ====================================================================
        // 3. ABYSSAL CHURCH (10 Cards)
        // Theme: Curse, Poison, Burn, Dark Priest
        // Distribution: 4 Common, 3 Rare, 2 Epic, 1 Legendary
        // ====================================================================

        // COMMON 1
        Card deepbornAcolyte = new Card(
            "deepborn_acolyte", "Deepborn Acolyte",
            Faction.ABYSSAL_CHURCH, Role.SUPPORT, Rarity.COMMON,
            85, 12, 5, 80, 15, 1,
            "cards/deepborn_acolyte.png"
        );
        /*
        ACTIVE: Toxic Prayer
        Effect: Inflict Poison 4% Max HP for 3 turns to a random enemy.
        */
        deepbornAcolyte.setActiveSkill(new Skill(
            "Toxic Prayer",
            SkillType.POISON,
            SkillTrigger.ACTIVE,
            ReactiveTrigger.NONE,
            TargetType.RANDOM_ENEMY,
            4,
            3,
            3
        ));
        cards.put(deepbornAcolyte.getId(), deepbornAcolyte);

        // COMMON 2
        Card sunkenVessel = new Card(
            "sunken_vessel", "Sunken Vessel",
            Faction.ABYSSAL_CHURCH, Role.MAGE, Rarity.COMMON,
            90, 24, 5, 82, 20, 1,
            "cards/sunken_vessel.png"
        );
        /*
        ACTIVE: Plague Ember
        Effect: Inflict Burn 5 damage for 3 turns to an enemy based on aggro.
        */
        sunkenVessel.setActiveSkill(new Skill(
            "Plague Ember",
            SkillType.BURN,
            SkillTrigger.ACTIVE,
            ReactiveTrigger.NONE,
            TargetType.ENEMY_BY_AGGRO,
            5,
            3,
            3
        ));
        cards.put(sunkenVessel.getId(), sunkenVessel);

        // COMMON 3
        Card Wretchwarden = new Card(
            "Wretchwarden", "Wretchwarden",
            Faction.ABYSSAL_CHURCH, Role.TANK, Rarity.COMMON,
            130, 15, 12, 60, 95, 1,
            "cards/Wretchwarden.png"
        );
        /*
        ACTIVE: Dark Taunt
        Effect: Increase own aggro by 35.
        */
        Wretchwarden.setActiveSkill(new Skill(
            "Dark Taunt",
            SkillType.TAUNT,
            TargetType.SELF,
            35,
            3
        ));
        cards.put(Wretchwarden.getId(), Wretchwarden);

        // COMMON 4
        Card abyssalThrall = new Card(
            "abyssal_thrall", "Abyssal Thrall",
            Faction.ABYSSAL_CHURCH, Role.HEALER, Rarity.COMMON,
            90, 10, 5, 88, 15, 1,
            "cards/abyssal_thrall.png"
        );
        /*
        ACTIVE: Forbidden Ritual
        Effect: Heal the ally with the lowest HP by 18.
        */
        abyssalThrall.setActiveSkill(new Skill(
            "Forbidden Ritual",
            SkillType.HEAL,
            TargetType.ALLY_LOWEST_HP,
            18,
            3
        ));
        cards.put(abyssalThrall.getId(), abyssalThrall);

        // RARE 1
        Card voidBishop = new Card(
            "void_bishop", "Void Bishop",
            Faction.ABYSSAL_CHURCH, Role.MAGE, Rarity.RARE,
            90, 38, 5, 95, 25, 1,
            "cards/void_bishop.png"
        );
        /*
        ACTIVE: Dark Prophecy
        Effect: Deal 22 damage to all enemies.
        */
        voidBishop.setActiveSkill(new Skill(
            "Dark Prophecy",
            SkillType.DAMAGE,
            TargetType.ALL_ENEMIES,
            22,
            3
        ));
        cards.put(voidBishop.getId(), voidBishop);

        // RARE 2
        Card abyssPriest = new Card(
            "abyss_priest", "Abyss Priest",
            Faction.ABYSSAL_CHURCH, Role.SUPPORT, Rarity.RARE,
            100, 16, 8, 80, 18, 1,
            "cards/abyss_priest.png"
        );
        /*
        ACTIVE: Corrupt Blessing
        Effect: Inflict Poison 5% Max HP for 3 turns to an enemy based on aggro.
        */
        abyssPriest.setActiveSkill(new Skill(
            "Corrupt Blessing",
            SkillType.POISON,
            SkillTrigger.ACTIVE,
            ReactiveTrigger.NONE,
            TargetType.ENEMY_BY_AGGRO,
            5,
            3,
            3
        ));
        cards.put(abyssPriest.getId(), abyssPriest);

        // RARE 3
        Card doomReader = new Card(
            "doom_reader", "Doom Reader",
            Faction.ABYSSAL_CHURCH, Role.MAGE, Rarity.RARE,
            92, 36, 5, 92, 20, 1,
            "cards/doom_reader.png"
        );
        /*
        ACTIVE: Doom Flame
        Effect: Inflict Burn 8 damage for 3 turns to the enemy with the lowest HP.
        */
        doomReader.setActiveSkill(new Skill(
            "Doom Flame",
            SkillType.BURN,
            SkillTrigger.ACTIVE,
            ReactiveTrigger.NONE,
            TargetType.ENEMY_LOWEST_HP,
            8,
            3,
            3
        ));
        cards.put(doomReader.getId(), doomReader);

        // EPIC 1
        Card plagueOracle = new Card(
            "plague_oracle", "Plague Oracle",
            Faction.ABYSSAL_CHURCH, Role.MAGE, Rarity.EPIC,
            100, 40, 6, 88, 20, 1,
            "cards/plague_oracle.png"
        );
        /*
        ACTIVE: Infernal Epidemic
        Effect: Inflict Burn 10 damage for 3 turns to all enemies.

        PASSIVE: Dark Barrier
        Effect: Gain Shield 12 when passive skills are applied.
        */
        plagueOracle.setActiveSkill(new Skill(
            "Infernal Epidemic",
            SkillType.BURN,
            SkillTrigger.ACTIVE,
            ReactiveTrigger.NONE,
            TargetType.ALL_ENEMIES,
            10,
            3,
            3
        ));
        plagueOracle.setPassiveSkill(new Skill(
            "Dark Barrier",
            SkillType.SHIELD,
            SkillTrigger.PASSIVE,
            ReactiveTrigger.NONE,
            TargetType.SELF,
            12,
            0,
            0
        ));
        cards.put(plagueOracle.getId(), plagueOracle);

        // EPIC 2
        Card bloodCardinal = new Card(
            "blood_cardinal", "Blood Cardinal",
            Faction.ABYSSAL_CHURCH, Role.SUPPORT, Rarity.EPIC,
            110, 18, 10, 82, 25, 1,
            "cards/blood_cardinal.png"
        );
        /*
        ACTIVE: Blood Curse
        Effect: Inflict Poison 8% Max HP for 3 turns to the enemy with the lowest HP.

        REACTIVE: Final Curse
        Effect: On death, inflict Poison 4% Max HP for 3 turns to all enemies.
        */
        bloodCardinal.setActiveSkill(new Skill(
            "Blood Curse",
            SkillType.POISON,
            SkillTrigger.ACTIVE,
            ReactiveTrigger.NONE,
            TargetType.ENEMY_LOWEST_HP,
            8,
            3,
            3
        ));
        bloodCardinal.setDeathSkill(new Skill(
            "Final Curse",
            SkillType.POISON,
            SkillTrigger.REACTIVE,
            ReactiveTrigger.ON_DEATH,
            TargetType.ALL_ENEMIES,
            4,
            0,
            3
        ));
        cards.put(bloodCardinal.getId(), bloodCardinal);

        // LEGENDARY 1
        Card theLastCongregation = new Card(
            "the_last_congregation", "The Last Congregation",
            Faction.ABYSSAL_CHURCH, Role.MAGE, Rarity.LEGENDARY,
            120, 50, 10, 90, 30, 1,
            "cards/the_last_congregation.png"
        );
        /*
        ACTIVE: Hell Genesis
        Effect: Inflict Burn 12 damage for 3 turns to all enemies.

        PASSIVE: Abyssal Veil
        Effect: Gain Shield 20 when passive skills are applied.

        REACTIVE: Funeral Flame
        Effect: On death, inflict Burn 15 damage for 3 turns to all enemies.
        */
        theLastCongregation.setActiveSkill(new Skill(
            "Hell Genesis",
            SkillType.BURN,
            SkillTrigger.ACTIVE,
            ReactiveTrigger.NONE,
            TargetType.ALL_ENEMIES,
            12,
            3,
            3
        ));
        theLastCongregation.setPassiveSkill(new Skill(
            "Abyssal Veil",
            SkillType.SHIELD,
            SkillTrigger.PASSIVE,
            ReactiveTrigger.NONE,
            TargetType.SELF,
            20,
            0,
            0
        ));
        theLastCongregation.setDeathSkill(new Skill(
            "Funeral Flame",
            SkillType.BURN,
            SkillTrigger.REACTIVE,
            ReactiveTrigger.ON_DEATH,
            TargetType.ALL_ENEMIES,
            15,
            0,
            3
        ));
        cards.put(theLastCongregation.getId(), theLastCongregation);

        // ====================================================================
        // 4. DRAGON LINEAGE (10 Cards)
        // Theme: Dragon bloodline, high HP, AoE, Burn, raw power
        // Distribution: 4 Common, 3 Rare, 2 Epic, 1 Legendary
        // ====================================================================

        // COMMON 1
        Card wyvernHatchling = new Card(
            "wyvern_hatchling", "Wyvern Hatchling",
            Faction.DRAGON_LINEAGE, Role.DPS, Rarity.COMMON,
            100, 29, 6, 80, 30, 1,
            "cards/wyvern_hatchling.png"
        );
        /*
        ACTIVE: Young Claw
        Effect: Deal 30 damage to an enemy based on aggro.
        */
        wyvernHatchling.setActiveSkill(new Skill(
            "Young Claw",
            SkillType.DAMAGE,
            TargetType.ENEMY_BY_AGGRO,
            30,
            2
        ));
        cards.put(wyvernHatchling.getId(), wyvernHatchling);

        // COMMON 2
        Card emberDrake = new Card(
            "ember_drake", "Ember Drake",
            Faction.DRAGON_LINEAGE, Role.MAGE, Rarity.COMMON,
            90, 26, 5, 78, 25, 1,
            "cards/ember_drake.png"
        );
        /*
        ACTIVE: Ember Spit
        Effect: Inflict Burn 5 damage for 3 turns to an enemy based on aggro.
        */
        emberDrake.setActiveSkill(new Skill(
            "Ember Spit",
            SkillType.BURN,
            SkillTrigger.ACTIVE,
            ReactiveTrigger.NONE,
            TargetType.ENEMY_BY_AGGRO,
            5,
            3,
            3
        ));
        cards.put(emberDrake.getId(), emberDrake);

        // COMMON 3
        Card ironscaleGuardian = new Card(
            "ironscale_guardian", "Ironscale Guardian",
            Faction.DRAGON_LINEAGE, Role.TANK, Rarity.COMMON,
            130, 15, 11, 55, 100, 1,
            "cards/ironscale_guardian.png"
        );
        /*
        ACTIVE: Hardened Scale
        Effect: Gain Shield 25.
        */
        ironscaleGuardian.setActiveSkill(new Skill(
            "Hardened Scale",
            SkillType.SHIELD,
            TargetType.SELF,
            18,
            3
        ));
        cards.put(ironscaleGuardian.getId(), ironscaleGuardian);

        // COMMON 4
        Card scalebinder = new Card(
            "scalebinder", "Scalebinder",
            Faction.DRAGON_LINEAGE, Role.SUPPORT, Rarity.COMMON,
            105, 18, 8, 70, 25, 1,
            "cards/scalebinder.png"
        );
        /*
        ACTIVE: Guardian Roar
        Effect: Increase own aggro by 30.
        */
        scalebinder.setActiveSkill(new Skill(
            "Guardian Roar",
            SkillType.TAUNT,
            TargetType.SELF,
            30,
            3
        ));
        cards.put(scalebinder.getId(), scalebinder);

        // RARE 1
        Card ashscaleBulwark = new Card(
            "ashscale_bulwark", "Ashscale Bulwark",
            Faction.DRAGON_LINEAGE, Role.TANK, Rarity.RARE,
            138, 21, 13, 58, 105, 1,
            "cards/ashscale_bulwark.png"
        );
        /*
        ACTIVE: Dragon Scale
        Effect: Gain Shield 45.
        */
        ashscaleBulwark.setActiveSkill(new Skill(
            "Dragon Scale",
            SkillType.SHIELD,
            TargetType.SELF,
            28,
            3
        ));
        cards.put(ashscaleBulwark.getId(), ashscaleBulwark);

        // RARE 2
        Card flameWyrm = new Card(
            "flame_wyrm", "Flame Wyrm",
            Faction.DRAGON_LINEAGE, Role.DPS, Rarity.RARE,
            115, 40, 8, 82, 35, 1,
            "cards/flame_wyrm.png"
        );
        /*
        ACTIVE: Flame Breath
        Effect: Inflict Burn 8 damage for 3 turns to an enemy based on aggro.
        */
        flameWyrm.setActiveSkill(new Skill(
            "Flame Breath",
            SkillType.BURN,
            SkillTrigger.ACTIVE,
            ReactiveTrigger.NONE,
            TargetType.ENEMY_BY_AGGRO,
            8,
            3,
            3
        ));
        cards.put(flameWyrm.getId(), flameWyrm);

        // RARE 3
        Card stormrider = new Card(
            "stormrider", "Stormrider",
            Faction.DRAGON_LINEAGE, Role.ASSASSIN, Rarity.RARE,
            90, 38, 6, 105, 18, 1,
            "cards/stormrider.png"
        );
        /*
        ACTIVE: Diving Fang
        Effect: Deal 44 damage to the enemy with the lowest HP.
        */
        stormrider.setActiveSkill(new Skill(
            "Diving Fang",
            SkillType.DAMAGE,
            TargetType.ENEMY_LOWEST_HP,
            44,
            2
        ));
        cards.put(stormrider.getId(), stormrider);

        // EPIC 1
        Card fallenWyrm = new Card(
            "fallen_wyrm", "Fallen Wyrm",
            Faction.DRAGON_LINEAGE, Role.DPS, Rarity.EPIC,
            120, 48, 11, 85, 35, 1,
            "cards/fallen_wyrm.png"
        );
        /*
        ACTIVE: Inferno Breath
        Effect: Inflict Burn 10 damage for 3 turns to an enemy based on aggro.

        REACTIVE: Burning Corpse
        Effect: On death, inflict Burn 6 damage for 3 turns to all enemies.
        */
        fallenWyrm.setActiveSkill(new Skill(
            "Inferno Breath",
            SkillType.BURN,
            SkillTrigger.ACTIVE,
            ReactiveTrigger.NONE,
            TargetType.ENEMY_BY_AGGRO,
            10,
            3,
            3
        ));
        fallenWyrm.setDeathSkill(new Skill(
            "Burning Corpse",
            SkillType.BURN,
            SkillTrigger.REACTIVE,
            ReactiveTrigger.ON_DEATH,
            TargetType.ALL_ENEMIES,
            6,
            0,
            3
        ));
        cards.put(fallenWyrm.getId(), fallenWyrm);

        // EPIC 2
        Card stormscale = new Card(
            "stormscale", "Stormscale",
            Faction.DRAGON_LINEAGE, Role.MAGE, Rarity.EPIC,
            105, 44, 7, 84, 25, 1,
            "cards/stormscale.png"
        );
        /*
        ACTIVE: Thunderstorm
        Effect: Deal 26 damage to all enemies.

        PASSIVE: Stormhide
        Effect: Gain Shield 14 when passive skills are applied.
        */
        stormscale.setActiveSkill(new Skill(
            "Thunderstorm",
            SkillType.DAMAGE,
            TargetType.ALL_ENEMIES,
            26,
            3
        ));
        stormscale.setPassiveSkill(new Skill(
            "Stormhide",
            SkillType.SHIELD,
            SkillTrigger.PASSIVE,
            ReactiveTrigger.NONE,
            TargetType.SELF,
            14,
            0,
            0
        ));
        cards.put(stormscale.getId(), stormscale);

        // LEGENDARY 1
        Card celestialDragonUndone = new Card(
            "celestial_dragon_undone", "Celestial Dragon Undone",
            Faction.DRAGON_LINEAGE, Role.MAGE, Rarity.LEGENDARY,
            135, 52, 10, 90, 45, 1,
            "cards/celestial_dragon_undone.png"
        );
        /*
        ACTIVE: Heaven Collapse
        Effect: Deal 35 damage to all enemies.

        PASSIVE: Dragon Aura
        Effect: Gain Shield 25 when passive skills are applied.

        REACTIVE: Final Roar
        Effect: On death, inflict Burn 15 damage for 3 turns to all enemies.
        */
        celestialDragonUndone.setActiveSkill(new Skill(
            "Heaven Collapse",
            SkillType.DAMAGE,
            TargetType.ALL_ENEMIES,
            35,
            3
        ));
        celestialDragonUndone.setPassiveSkill(new Skill(
            "Dragon Aura",
            SkillType.SHIELD,
            SkillTrigger.PASSIVE,
            ReactiveTrigger.NONE,
            TargetType.SELF,
            25,
            0,
            0
        ));
        celestialDragonUndone.setDeathSkill(new Skill(
            "Final Roar",
            SkillType.BURN,
            SkillTrigger.REACTIVE,
            ReactiveTrigger.ON_DEATH,
            TargetType.ALL_ENEMIES,
            15,
            0,
            3
        ));
        cards.put(celestialDragonUndone.getId(), celestialDragonUndone);

        // ====================================================================
        // 5. MORTAL ASCENDANTS (10 Cards)
        // Theme: Humans stealing divine power, balanced stats, buff, heal, shield
        // Distribution: 4 Common, 3 Rare, 2 Epic, 1 Legendary
        // ====================================================================

        // COMMON 1
        Card ironVanguard = new Card(
            "iron_vanguard", "Iron Vanguard",
            Faction.MORTAL_ASCENDANTS, Role.TANK, Rarity.COMMON,
            130, 18, 12, 68, 90, 1,
            "cards/iron_vanguard.png"
        );
        /*
        ACTIVE: Guard Stance
        Effect: Gain Shield 22.
        */
        ironVanguard.setActiveSkill(new Skill(
            "Guard Stance",
            SkillType.SHIELD,
            TargetType.SELF,
            15,
            3
        ));
        cards.put(ironVanguard.getId(), ironVanguard);

        // COMMON 2
        Card vagrantBlade = new Card(
            "vagrant_blade", "Vagrant Blade",
            Faction.MORTAL_ASCENDANTS, Role.DPS, Rarity.COMMON,
            98, 32, 6, 82, 30, 1,
            "cards/vagrant_blade.png"
        );
        /*
        ACTIVE: Heavy Slash
        Effect: Deal 34 damage to an enemy based on aggro.
        */
        vagrantBlade.setActiveSkill(new Skill(
            "Heavy Slash",
            SkillType.DAMAGE,
            TargetType.ENEMY_BY_AGGRO,
            34,
            2
        ));
        cards.put(vagrantBlade.getId(), vagrantBlade);

        // COMMON 3
        Card warbrandMender = new Card(
            "warbrand_mender", "Warbrand Mender",
            Faction.MORTAL_ASCENDANTS, Role.HEALER, Rarity.COMMON,
            88, 10, 6, 90, 15, 1,
            "cards/warbrand_mender.png"
        );
        /*
        ACTIVE: First Aid
        Effect: Heal the ally with the lowest HP by 17.
        */
        warbrandMender.setActiveSkill(new Skill(
            "First Aid",
            SkillType.HEAL,
            TargetType.ALLY_LOWEST_HP,
            17,
            3
        ));
        cards.put(warbrandMender.getId(), warbrandMender);

        // COMMON 4
        Card relicScout = new Card(
            "relic_scout", "Relic Scout",
            Faction.MORTAL_ASCENDANTS, Role.SUPPORT, Rarity.COMMON,
            90, 16, 7, 96, 18, 1,
            "cards/relic_scout.png"
        );
        /*
        ACTIVE: Quick Guard
        Effect: Gain Shield 18.
        */
        relicScout.setActiveSkill(new Skill(
            "Quick Guard",
            SkillType.SHIELD,
            TargetType.SELF,
            18,
            2
        ));
        cards.put(relicScout.getId(), relicScout);

        // RARE 1
        Card sacredOutlaw = new Card(
            "sacred_outlaw", "Sacred Outlaw",
            Faction.MORTAL_ASCENDANTS, Role.ASSASSIN, Rarity.RARE,
            92, 38, 6, 108, 18, 1,
            "cards/sacred_outlaw.png"
        );
        /*
        ACTIVE: Stolen Strike
        Effect: Deal 42 damage to the enemy with the lowest HP.
        */
        sacredOutlaw.setActiveSkill(new Skill(
            "Stolen Strike",
            SkillType.DAMAGE,
            TargetType.ENEMY_LOWEST_HP,
            42,
            2
        ));
        cards.put(sacredOutlaw.getId(), sacredOutlaw);

        // RARE 2
        Card oathbreakerKnight = new Card(
            "oathbreaker_knight", "Oathbreaker Knight",
            Faction.MORTAL_ASCENDANTS, Role.TANK, Rarity.RARE,
            125, 20, 13, 70, 95, 1,
            "cards/oathbreaker_knight.png"
        );
        /*
        ACTIVE: Mortal Defiance
        Effect: Increase own aggro by 45.
        */
        oathbreakerKnight.setActiveSkill(new Skill(
            "Mortal Defiance",
            SkillType.TAUNT,
            TargetType.SELF,
            45,
            3
        ));
        cards.put(oathbreakerKnight.getId(), oathbreakerKnight);

        // RARE 3
        Card runeboundSeeker = new Card(
            "runebound_seeker", "Runebound Seeker",
            Faction.MORTAL_ASCENDANTS, Role.SUPPORT, Rarity.RARE,
            100, 18, 8, 86, 20, 1,
            "cards/runebound_seeker.png"
        );
        /*
        ACTIVE: Rune Barrier
        Effect: Gain Shield 35.
        */
        runeboundSeeker.setActiveSkill(new Skill(
            "Rune Barrier",
            SkillType.SHIELD,
            TargetType.SELF,
            35,
            3
        ));
        cards.put(runeboundSeeker.getId(), runeboundSeeker);

        // EPIC 1
        Card stolenGrace = new Card(
            "stolen_grace", "Stolen Grace",
            Faction.MORTAL_ASCENDANTS, Role.HEALER, Rarity.EPIC,
            95, 15, 7, 92, 15, 1,
            "cards/stolen_grace.png"
        );
        /*
        ACTIVE: Elixir of Life
        Effect: Heal the ally with the lowest HP by 30.

        PASSIVE: Blessed Formula
        Effect: Gain Shield 12 when passive skills are applied.
        */
        stolenGrace.setActiveSkill(new Skill(
            "Elixir of Life",
            SkillType.HEAL,
            TargetType.ALLY_LOWEST_HP,
            30,
            3
        ));
        stolenGrace.setPassiveSkill(new Skill(
            "Blessed Formula",
            SkillType.SHIELD,
            SkillTrigger.PASSIVE,
            ReactiveTrigger.NONE,
            TargetType.SELF,
            12,
            0,
            0
        ));
        cards.put(stolenGrace.getId(), stolenGrace);

        // EPIC 2
        Card ascendedChampion = new Card(
            "ascended_champion", "Ascended Champion",
            Faction.MORTAL_ASCENDANTS, Role.DPS, Rarity.EPIC,
            123, 46, 10, 84, 35, 1,
            "cards/ascended_champion.png"
        );
        /*
        ACTIVE: Ascendant Cleave
        Effect: Deal 54 damage to an enemy based on aggro.

        REACTIVE: Last Stand
        Effect: On death, deal 22 damage to all enemies.
        */
        ascendedChampion.setActiveSkill(new Skill(
            "Ascendant Cleave",
            SkillType.DAMAGE,
            TargetType.ENEMY_BY_AGGRO,
            54,
            2
        ));
        ascendedChampion.setDeathSkill(new Skill(
            "Last Stand",
            SkillType.DAMAGE,
            SkillTrigger.REACTIVE,
            ReactiveTrigger.ON_DEATH,
            TargetType.ALL_ENEMIES,
            22,
            0,
            0
        ));
        cards.put(ascendedChampion.getId(), ascendedChampion);

        // LEGENDARY 1
        Card firstAscendant = new Card(
            "first_ascendant", "The First Ascendant",
            Faction.MORTAL_ASCENDANTS, Role.MAGE, Rarity.LEGENDARY,
            125, 52, 9, 88, 25, 1,
            "cards/first_ascendant.png"
        );
        /*
        ACTIVE: Stolen Divinity
        Effect: Deal 40 damage to all enemies.

        PASSIVE: Aura of Ascendancy
        Effect: Gain Shield 22 when passive skills are applied.

        REACTIVE: Divine Shatter
        Effect: On death, deal 45 damage to all enemies.
        */
        firstAscendant.setActiveSkill(new Skill(
            "Stolen Divinity",
            SkillType.DAMAGE,
            TargetType.ALL_ENEMIES,
            40,
            3
        ));
        firstAscendant.setPassiveSkill(new Skill(
            "Aura of Ascendancy",
            SkillType.SHIELD,
            SkillTrigger.PASSIVE,
            ReactiveTrigger.NONE,
            TargetType.SELF,
            22,
            0,
            0
        ));
        firstAscendant.setDeathSkill(new Skill(
            "Divine Shatter",
            SkillType.DAMAGE,
            SkillTrigger.REACTIVE,
            ReactiveTrigger.ON_DEATH,
            TargetType.ALL_ENEMIES,
            45,
            0,
            0
        ));
        cards.put(firstAscendant.getId(), firstAscendant);

        // ====================================================================
        // 6. SHADOW CONCLAVE (10 Cards)
        // Theme: Assassin, execute, slow, stun, speed, fragile burst
        // Distribution: 4 Common, 3 Rare, 2 Epic, 1 Legendary
        // ====================================================================

        // COMMON 1
        Card shadowInitiate = new Card(
            "shadow_initiate", "Shadow Initiate",
            Faction.SHADOW_CONCLAVE, Role.ASSASSIN, Rarity.COMMON,
            82, 32, 4, 105, 18, 1,
            "cards/shadow_initiate.png"
        );
        /*
        ACTIVE: Surprise Attack
        Effect: Deal 34 damage to the enemy with the lowest HP.
        */
        shadowInitiate.setActiveSkill(new Skill(
            "Surprise Attack",
            SkillType.DAMAGE,
            TargetType.ENEMY_LOWEST_HP,
            34,
            2
        ));
        cards.put(shadowInitiate.getId(), shadowInitiate);

        // COMMON 2
        Card smokeveil = new Card(
            "smokeveil", "Smokeveil",
            Faction.SHADOW_CONCLAVE, Role.SUPPORT, Rarity.COMMON,
            85, 16, 5, 112, 15, 1,
            "cards/smokeveil.png"
        );
        /*
        ACTIVE: Smoke Veil
        Effect: Gain Shield 16.
        */
        smokeveil.setActiveSkill(new Skill(
            "Smoke Veil",
            SkillType.SHIELD,
            TargetType.SELF,
            16,
            2
        ));
        cards.put(smokeveil.getId(), smokeveil);

        // COMMON 3
        Card duskCutthroat = new Card(
            "dusk_cutthroat", "Dusk Cutthroat",
            Faction.SHADOW_CONCLAVE, Role.DPS, Rarity.COMMON,
            88, 35, 5, 100, 25, 1,
            "cards/dusk_cutthroat.png"
        );
        /*
        ACTIVE: Quick Slash
        Effect: Deal 30 damage to a random enemy.
        */
        duskCutthroat.setActiveSkill(new Skill(
            "Quick Slash",
            SkillType.DAMAGE,
            TargetType.RANDOM_ENEMY,
            30,
            2
        ));
        cards.put(duskCutthroat.getId(), duskCutthroat);

        // COMMON 4
        Card duskweaver = new Card(
            "duskweaver", "Duskweaver",
            Faction.SHADOW_CONCLAVE, Role.MAGE, Rarity.COMMON,
            82, 24, 4, 98, 20, 1,
            "cards/duskweaver.png"
        );
        /*
        ACTIVE: Crippling Whisper
        Effect: Inflict Slow 10 for 2 turns to an enemy based on aggro.
        */
        duskweaver.setActiveSkill(new Skill(
            "Crippling Whisper",
            SkillType.SLOW,
            SkillTrigger.ACTIVE,
            ReactiveTrigger.NONE,
            TargetType.ENEMY_BY_AGGRO,
            10,
            3,
            2
        ));
        cards.put(duskweaver.getId(), duskweaver);

        // RARE 1
        Card silentBlade = new Card(
            "silent_blade", "Silent Blade",
            Faction.SHADOW_CONCLAVE, Role.DPS, Rarity.RARE,
            113, 41, 6, 100, 25, 1,
            "cards/silent_blade.png"
        );
        /*
        ACTIVE: Backstab
        Effect: Deal 46 damage to the enemy with the lowest HP.
        */
        silentBlade.setActiveSkill(new Skill(
            "Backstab",
            SkillType.DAMAGE,
            TargetType.ENEMY_LOWEST_HP,
            46,
            2
        ));
        cards.put(silentBlade.getId(), silentBlade);

        // RARE 2
        Card nightshadeBinder = new Card(
            "nightshade_binder", "Nightshade Binder",
            Faction.SHADOW_CONCLAVE, Role.SUPPORT, Rarity.RARE,
            90, 18, 6, 102, 15, 1,
            "cards/nightshade_binder.png"
        );
        /*
        ACTIVE: Toxic Smoke
        Effect: Inflict Poison 5% Max HP for 3 turns to all enemies.
        */
        nightshadeBinder.setActiveSkill(new Skill(
            "Toxic Smoke",
            SkillType.POISON,
            SkillTrigger.ACTIVE,
            ReactiveTrigger.NONE,
            TargetType.ALL_ENEMIES,
            5,
            3,
            3
        ));
        cards.put(nightshadeBinder.getId(), nightshadeBinder);

        // RARE 3
        Card shadeBinder = new Card(
            "shade_binder", "Shade Binder",
            Faction.SHADOW_CONCLAVE, Role.MAGE, Rarity.RARE,
            88, 30, 5, 104, 20, 1,
            "cards/shade_binder.png"
        );
        /*
        ACTIVE: Shadow Bind
        Effect: Inflict Stun for 1 turn to a random enemy.
        */
        shadeBinder.setActiveSkill(new Skill(
            "Shadow Bind",
            SkillType.STUN,
            SkillTrigger.ACTIVE,
            ReactiveTrigger.NONE,
            TargetType.RANDOM_ENEMY,
            0,
            3,
            1
        ));
        cards.put(shadeBinder.getId(), shadeBinder);

        // EPIC 1
        Card shadowSaint = new Card(
            "shadow_saint", "Shadow Saint",
            Faction.SHADOW_CONCLAVE, Role.ASSASSIN, Rarity.EPIC,
            100, 48, 6, 120, 15, 1,
            "cards/shadow_saint.png"
        );
        /*
        ACTIVE: Execute
        Effect: Deal 52 damage to the enemy with the lowest HP.

        REACTIVE: Last Shadow
        Effect: On death, deal 25 damage to the enemy with the lowest HP.
        */
        shadowSaint.setActiveSkill(new Skill(
            "Execute",
            SkillType.DAMAGE,
            TargetType.ENEMY_LOWEST_HP,
            52,
            2
        ));
        shadowSaint.setDeathSkill(new Skill(
            "Last Shadow",
            SkillType.DAMAGE,
            SkillTrigger.REACTIVE,
            ReactiveTrigger.ON_DEATH,
            TargetType.ENEMY_LOWEST_HP,
            25,
            0,
            0
        ));
        cards.put(shadowSaint.getId(), shadowSaint);

        // EPIC 2
        Card eclipse = new Card(
            "eclipse", "Eclipse",
            Faction.SHADOW_CONCLAVE, Role.ASSASSIN, Rarity.EPIC,
            98, 53, 5, 124, 15, 1,
            "cards/eclipse.png"
        );
        /*
        ACTIVE: Eclipse Mark
        Effect: Inflict Slow 15 for 2 turns to the enemy with the lowest HP.

        PASSIVE: Hidden Step
        Effect: Gain Shield 12 when passive skills are applied.
        */
        eclipse.setActiveSkill(new Skill(
            "Eclipse Mark",
            SkillType.SLOW,
            SkillTrigger.ACTIVE,
            ReactiveTrigger.NONE,
            TargetType.ENEMY_LOWEST_HP,
            15,
            3,
            2
        ));
        eclipse.setPassiveSkill(new Skill(
            "Hidden Step",
            SkillType.SHIELD,
            SkillTrigger.PASSIVE,
            ReactiveTrigger.NONE,
            TargetType.SELF,
            12,
            0,
            0
        ));
        cards.put(eclipse.getId(), eclipse);

        // LEGENDARY 1
        Card unseenSovereign = new Card(
            "unseen_sovereign", "Unseen Sovereign",
            Faction.SHADOW_CONCLAVE, Role.ASSASSIN, Rarity.LEGENDARY,
            108, 58, 7, 130, 15, 1,
            "cards/unseen_sovereign.png"
        );
        /*
        ACTIVE: Assassinate
        Effect: Deal 70 damage to the enemy with the lowest HP.

        PASSIVE: Shrouded
        Effect: Gain Shield 25 when passive skills are applied.

        REACTIVE: Smoke Bomb
        Effect: On death, inflict Stun for 1 turn to all enemies.
        */
        unseenSovereign.setActiveSkill(new Skill(
            "Assassinate",
            SkillType.DAMAGE,
            TargetType.ENEMY_LOWEST_HP,
            70,
            2
        ));
        unseenSovereign.setPassiveSkill(new Skill(
            "Shrouded",
            SkillType.SHIELD,
            SkillTrigger.PASSIVE,
            ReactiveTrigger.NONE,
            TargetType.SELF,
            25,
            0,
            0
        ));
        unseenSovereign.setDeathSkill(new Skill(
            "Smoke Bomb",
            SkillType.STUN,
            SkillTrigger.REACTIVE,
            ReactiveTrigger.ON_DEATH,
            TargetType.ALL_ENEMIES,
            0,
            0,
            1
        ));
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
            cloned.setActiveSkill(cloneSkill(original.getActiveSkill()));
        }

        // KLONING PASSIVE SKILL
        if (original.getPassiveSkill() != null) {
            cloned.setPassiveSkill(cloneSkill(original.getPassiveSkill()));
        }

        // KLONING DEATH SKILL
        if (original.getDeathSkill() != null) {
            cloned.setDeathSkill(cloneSkill(original.getDeathSkill()));
        }

        return cloned;
    }

    private static Skill cloneSkill(Skill skill) {
        return new Skill(
            skill.getName(),
            skill.getSkillType(),
            skill.getSkillTrigger(),
            skill.getReactiveTrigger(),
            skill.getTargetType(),
            skill.getPower(),
            skill.getCooldown(),
            skill.getDuration()
        );
    }
}
