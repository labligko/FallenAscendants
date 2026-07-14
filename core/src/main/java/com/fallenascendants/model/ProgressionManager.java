package com.fallenascendants.model;

import com.fallenascendants.data.CardDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProgressionManager {
    private static final int WIN_GOLD = 100;
    private static final int LOSE_GOLD = 20;
    private static final Random random = new Random();

    /**
     * Represents the outcome rewards for a player.
     */
    public static class BattleRewards {
        private final boolean win;
        private final int goldEarned;
        private final List<Card> cardOptions;
        private Card claimedCard;

        public BattleRewards(boolean win, int goldEarned, List<Card> cardOptions) {
            this.win = win;
            this.goldEarned = goldEarned;
            this.cardOptions = cardOptions;
            this.claimedCard = null;
        }

        public boolean isWin() {
            return win;
        }

        public int getGoldEarned() {
            return goldEarned;
        }

        public List<Card> getCardOptions() {
            return cardOptions;
        }

        public Card getClaimedCard() {
            return claimedCard;
        }

        public void setClaimedCard(Card claimedCard) {
            this.claimedCard = claimedCard;
        }
    }

    /**
     * Generates battle rewards based on the outcome (win or lose).
     * Automatically adds gold to the player.
     * If won, generates 3 closed card options for the player to choose from.
     *
     * @param player The player receiving the rewards
     * @param isWin True if the player won the battle, false otherwise
     * @return BattleRewards object containing details of the earned rewards
     */
    public static BattleRewards processBattleRewards(Player player, boolean isWin) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }

        int goldEarned = isWin ? WIN_GOLD : LOSE_GOLD;
        player.addGold(goldEarned);

        List<Card> cardOptions = new ArrayList<>();
        if (isWin) {
            // Generate 3 random card options from the database
            List<Card> allCards = CardDatabase.getAllCards();
            if (!allCards.isEmpty()) {
                for (int i = 0; i < 3; i++) {
                    Card randomCard = allCards.get(random.nextInt(allCards.size()));
                    cardOptions.add(randomCard);
                }
            }
        }

        return new BattleRewards(isWin, goldEarned, cardOptions);
    }

    /**
     * Claims one of the card options from the battle rewards and adds it to the player's collection.
     *
     * @param player The player claiming the card
     * @param rewards The battle rewards object
     * @param optionIndex The index of the chosen card (0, 1, or 2)
     * @return The card that was claimed and added, or null if invalid
     */
    public static Card claimCardReward(Player player, BattleRewards rewards, int optionIndex) {
        if (player == null || rewards == null) {
            return null;
        }

        if (!rewards.isWin()) {
            return null;
        }

        List<Card> options = rewards.getCardOptions();
        if (optionIndex < 0 || optionIndex >= options.size()) {
            return null;
        }

        if (rewards.getClaimedCard() != null) {
            // Card has already been claimed for these rewards
            return null;
        }

        Card chosenCard = options.get(optionIndex);
        rewards.setClaimedCard(chosenCard);
        player.addCardToCollection(chosenCard);
        return chosenCard;
    }

    /**
     * Performs a duplicate card upgrade for a specific target card.
     *
     * @param player The player who owns the card and gold
     * @param targetCard The specific card instance to upgrade
     * @return True if upgrade was successful, false otherwise
     */
    public static boolean upgradeCard(Player player, Card targetCard) {
        if (player == null || targetCard == null) {
            return false;
        }
        return player.upgradeCard(targetCard);
    }
}
