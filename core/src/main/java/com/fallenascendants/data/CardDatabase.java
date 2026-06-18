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
        // TANK
        Card fallenSeraph = new Card(
            "fallen_seraph",
            "Fallen Seraph",
            Faction.CELESTIAL_REMNANTS,
            Role.TANK,
            Rarity.RARE,
            150, 20, 15, 70, 110, 1,
            "cards/fallen_seraph.png"
        );
        fallenSeraph.setActiveSkill(new Skill("Divine Taunt", SkillType.TAUNT, TargetType.SELF, 50, 3));
        cards.put(fallenSeraph.getId(), fallenSeraph);

        // DPS
        Card voidPaladin = new Card(
            "void_paladin",
            "Void Paladin",
            Faction.VOID_CORRUPTED,
            Role.DPS,
            Rarity.RARE,
            105, 38, 8, 85, 40, 1,
            "cards/void_paladin.png"
        );
        voidPaladin.setActiveSkill(new Skill("Void Slash", SkillType.DAMAGE, TargetType.ENEMY_BY_AGGRO, 48, 2));
        cards.put(voidPaladin.getId(), voidPaladin);

        // ASSASSIN
        Card shadowSaint = new Card(
            "shadow_saint",
            "Shadow Saint",
            Faction.SHADOW_CONCLAVE,
            Role.ASSASSIN,
            Rarity.EPIC,
            85, 45, 5, 120, 15, 1,
            "cards/shadow_saint.png"
        );
        shadowSaint.setActiveSkill(new Skill("Execute", SkillType.DAMAGE, TargetType.ENEMY_LOWEST_HP, 52, 2));
        cards.put(shadowSaint.getId(), shadowSaint);

        // MAGE
        Card voidBishop = new Card(
            "void_bishop",
            "Void Bishop",
            Faction.ABYSSAL_CHURCH,
            Role.MAGE,
            Rarity.RARE,
            90, 38, 5, 95, 25, 1,
            "cards/void_bishop.png"
        );
        voidBishop.setActiveSkill(new Skill("Dark Prophecy", SkillType.DAMAGE, TargetType.ALL_ENEMIES, 25, 3));
        cards.put(voidBishop.getId(), voidBishop);

        // HEALER
        Card brokenMadonna = new Card(
            "broken_madonna",
            "Broken Madonna",
            Faction.CELESTIAL_REMNANTS,
            Role.HEALER,
            Rarity.EPIC,
            95, 10, 6, 90, 15, 1,
            "cards/broken_madonna.png"
        );
        brokenMadonna.setActiveSkill(new Skill("Holy Light", SkillType.HEAL, TargetType.ALLY_LOWEST_HP, 20, 3));
        cards.put(brokenMadonna.getId(), brokenMadonna);

        // SUPPORT
        Card hereticScribe = new Card(
            "heretic_scribe",
            "Heretic Scribe",
            Faction.ABYSSAL_CHURCH,
            Role.SUPPORT,
            Rarity.COMMON,
            90, 12, 5, 75, 15, 1,
            "cards/heretic_scribe.png"
        );
        hereticScribe.setActiveSkill(new Skill("Forbidden Ward", SkillType.SHIELD, TargetType.SELF, 25, 3));
        cards.put(hereticScribe.getId(), hereticScribe);

        // COMMON
        Card wyvernHatchling = new Card(
            "wyvern_hatchling",
            "Wyvern Hatchling",
            Faction.DRAGON_LINEAGE,
            Role.DPS,
            Rarity.COMMON,
            100, 28, 5, 80, 30, 1,
            "cards/wyvern_hatchling.png"
        );
        cards.put(wyvernHatchling.getId(), wyvernHatchling);

        // LEGENDARY
        Card celestialDragonUndone = new Card(
            "celestial_dragon_undone",
            "Celestial Dragon Undone",
            Faction.DRAGON_LINEAGE,
            Role.MAGE,
            Rarity.LEGENDARY,
            130, 50, 10, 90, 45, 1,
            "cards/celestial_dragon_undone.png"
        );
        celestialDragonUndone.setActiveSkill(new Skill("Heaven Collapse", SkillType.DAMAGE, TargetType.ALL_ENEMIES, 35, 3));
        cards.put(celestialDragonUndone.getId(), celestialDragonUndone);
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

        if (original.getActiveSkill() != null) {
            Skill skill = original.getActiveSkill();

            cloned.setActiveSkill(new Skill(
                skill.getName(),
                skill.getSkillType(),
                skill.getTargetType(),
                skill.getPower(),
                skill.getCooldown()
            ));
        }

        return cloned;
    }
}
