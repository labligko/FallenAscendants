package com.fallenascendants.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.fallenascendants.FallenAscendantsGame;
import com.fallenascendants.battle.BattleManager;
import com.fallenascendants.battle.FactionCounterGraph;
import com.fallenascendants.enumtype.BattleSpeed;
import com.fallenascendants.enumtype.Faction;
import com.fallenascendants.model.Card;
import com.fallenascendants.model.ProgressionManager;
import com.fallenascendants.model.StatusEffect;

import java.util.List;

public class BattleScreen implements Screen {
    private final FallenAscendantsGame game;
    private final BattleManager battleManager;

    private Stage stage;
    private Skin skin;

    private final StringBuilder fullLog = new StringBuilder();
    private Label logLabel;
    private ScrollPane scrollPane;
    private Label resultLabel;
    private Label speedLabel;
    private TextButton nextButton;
    private TextButton rewardButton;

    private final Label[] enemySlotLabels = new Label[5];
    private final Label[] playerSlotLabels = new Label[5];

    private final Label[] enemyReserveLabels = new Label[3];
    private final Label[] playerReserveLabels = new Label[3];

    private Label enemySummaryLabel;
    private Label playerSummaryLabel;

    private static final int TURN_ORDER_PREVIEW_SIZE = 6;
    private final Label[] turnOrderLabels = new Label[TURN_ORDER_PREVIEW_SIZE];

    private static final float NORMAL_INTERVAL = 1.2f;
    private static final float FAST_INTERVAL = 0.5f;
    private float stepTimer = 0f;

    public BattleScreen(FallenAscendantsGame game, BattleManager battleManager) {
        this.game = game;
        this.battleManager = battleManager;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        FullscreenToggle.attach(stage);

        Table root = new Table();
        root.setFillParent(true);
        root.top().pad(20);
        stage.addActor(root);

        Label title = new Label("BATTLE LOG", skin);
        title.setFontScale(1.5f);
        root.add(title).padBottom(10).row();

        Table battlefieldPanel = buildBattlefieldPanel();
        root.add(battlefieldPanel).padBottom(15).row();

        appendLog(battleManager.applyPassiveSkillsAtBattleStart());
        appendLog(battleManager.applyFactionSynergyAtBattleStart());

        FactionCounterGraph factionCounterGraph = new FactionCounterGraph();
        appendLog(factionCounterGraph.getCounterReport());
        appendLog(factionCounterGraph.getDepthFirstTraversalReport(Faction.CELESTIAL_REMNANTS));

        appendLog(battleManager.getTurnQueueReport());

        logLabel = new Label(fullLog.toString(), skin);
        logLabel.setWrap(true);
        logLabel.setAlignment(Align.topLeft);

        scrollPane = new ScrollPane(logLabel, skin);
        scrollPane.setFadeScrollBars(false);
        root.add(scrollPane).width(1100).height(320).padBottom(15).row();

        resultLabel = new Label("", skin);
        resultLabel.setFontScale(1.3f);
        root.add(resultLabel).padBottom(10).row();

        speedLabel = new Label("Speed: " + formatSpeedLabel(game.getBattleSpeed()), skin);
        speedLabel.setFontScale(0.95f);
        speedLabel.setColor(0.75f, 0.75f, 0.75f, 1f);
        root.add(speedLabel).padBottom(10).row();

        Table buttonRow = new Table();

        nextButton = new TextButton("Next Action", skin);
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stepTimer = 0f;
                stepBattle();
            }
        });

        TextButton backButton = new TextButton("Back to Menu (Forfeit)", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        rewardButton = new TextButton("Claim Rewards", skin);
        rewardButton.setDisabled(true);
        rewardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (rewardButton.isDisabled()) {
                    return;
                }
                ProgressionManager.BattleRewards rewards =
                    ProgressionManager.processBattleRewards(game.getPlayer(), battleManager.isPlayerWin());
                game.setScreen(new RewardScreen(game, rewards));
            }
        });

        buttonRow.add(nextButton).width(200).padRight(20);
        buttonRow.add(rewardButton).width(200).padRight(20);
        buttonRow.add(backButton).width(220);
        root.add(buttonRow);

        // Isi panel status dengan kondisi awal (sebelum aksi pertama jalan)
        refreshBattlefieldPanel();

        // Turn order panel: actor terpisah, gak ikut alur "root" sama sekali,
        // biar posisi tombol Next/dkk gak kegeser. Ditaro manual di pojok kanan atas.
        Table turnOrderPanel = buildTurnOrderPanel();
        turnOrderPanel.pack();
        turnOrderPanel.setPosition(1280 - turnOrderPanel.getWidth() - 15, 720 - turnOrderPanel.getHeight() - 15);
        stage.addActor(turnOrderPanel);

        refreshTurnOrderPanel();
    }

    private Table buildTurnOrderPanel() {
        Table panel = new Table();
        panel.pad(8);

        Label header = new Label("TURN ORDER", skin);
        header.setFontScale(0.9f);
        panel.add(header).padBottom(6).left().row();

        for (int i = 0; i < TURN_ORDER_PREVIEW_SIZE; i++) {
            turnOrderLabels[i] = new Label("--", skin);
            turnOrderLabels[i].setFontScale(0.75f);
            turnOrderLabels[i].setAlignment(Align.left);
            panel.add(turnOrderLabels[i]).width(160).left().padBottom(3).row();
        }

        return panel;
    }

    private void refreshTurnOrderPanel() {
        List<Card> upcoming = battleManager.getUpcomingTurnOrder(TURN_ORDER_PREVIEW_SIZE);
        List<Card> preview = buildNextRoundPreview();
        int previewIndex = 0;

        for (int i = 0; i < TURN_ORDER_PREVIEW_SIZE; i++) {
            if (i < upcoming.size()) {
                Card card = upcoming.get(i);
                turnOrderLabels[i].setText((i + 1) + ". " + card.getName());
                Color c = isPlayerCard(card) ? new Color(0.4f, 0.7f, 1f, 1f) : new Color(1f, 0.45f, 0.45f, 1f);
                turnOrderLabels[i].setColor(c);
            } else {
                Card fillerCard = null;
                while (previewIndex < preview.size()) {
                    Card candidate = preview.get(previewIndex++);
                    if (!containsReference(upcoming, candidate)) {
                        fillerCard = candidate;
                        break;
                    }
                }

                if (fillerCard != null) {
                    turnOrderLabels[i].setText((i + 1) + ". " + fillerCard.getName());
                    Color base = isPlayerCard(fillerCard) ? new Color(0.4f, 0.7f, 1f, 1f) : new Color(1f, 0.45f, 0.45f, 1f);
                    turnOrderLabels[i].setColor(base.r, base.g, base.b, 0.5f);
                } else {
                    turnOrderLabels[i].setText("--");
                    turnOrderLabels[i].setColor(0.4f, 0.4f, 0.4f, 1f);
                }
            }
        }
    }

    private List<Card> buildNextRoundPreview() {
        List<Card> alive = new java.util.ArrayList<>();

        for (Card c : battleManager.getPlayerField().getActiveCards()) {
            if (c != null && !c.isDead()) {
                alive.add(c);
            }
        }
        for (Card c : battleManager.getEnemyField().getActiveCards()) {
            if (c != null && !c.isDead()) {
                alive.add(c);
            }
        }

        alive.sort((a, b) -> Integer.compare(b.getSpd(), a.getSpd()));
        return alive;
    }

    private boolean containsReference(List<Card> list, Card target) {
        for (Card c : list) {
            if (c == target) {
                return true;
            }
        }
        return false;
    }

    private boolean isPlayerCard(Card card) {
        for (Card ownCard : battleManager.getPlayerField().getActiveCards()) {
            if (ownCard == card) {
                return true;
            }
        }
        return false;
    }

    private Table buildBattlefieldPanel() {
        Table panel = new Table();

        Label enemyHeader = new Label("ENEMY", skin);
        enemyHeader.setFontScale(1.1f);
        panel.add(enemyHeader).colspan(5).padBottom(6).row();

        Table enemyRow = new Table();
        for (int i = 0; i < 5; i++) {
            enemySlotLabels[i] = new Label("", skin);
            enemySlotLabels[i].setFontScale(1f);
            enemySlotLabels[i].setAlignment(Align.center);
            enemyRow.add(enemySlotLabels[i]).width(210).pad(4);
        }
        panel.add(enemyRow).padBottom(4).row();

        Table enemyReserveRow = new Table();
        for (int i = 0; i < 3; i++) {
            enemyReserveLabels[i] = new Label("", skin);
            enemyReserveLabels[i].setFontScale(0.8f);
            enemyReserveLabels[i].setAlignment(Align.center);
            enemyReserveLabels[i].setColor(0.55f, 0.55f, 0.55f, 1f);
            enemyReserveRow.add(enemyReserveLabels[i]).width(210).pad(4);
        }
        panel.add(enemyReserveRow).padBottom(4).row();

        enemySummaryLabel = new Label("", skin);
        enemySummaryLabel.setFontScale(0.85f);
        enemySummaryLabel.setColor(0.7f, 0.7f, 0.7f, 1f);
        panel.add(enemySummaryLabel).colspan(5).padBottom(16).row();

        Label playerHeader = new Label("YOUR TEAM", skin);
        playerHeader.setFontScale(1.1f);
        panel.add(playerHeader).colspan(5).padBottom(6).row();

        Table playerRow = new Table();
        for (int i = 0; i < 5; i++) {
            playerSlotLabels[i] = new Label("", skin);
            playerSlotLabels[i].setFontScale(1f);
            playerSlotLabels[i].setAlignment(Align.center);
            playerRow.add(playerSlotLabels[i]).width(210).pad(4);
        }
        panel.add(playerRow).padBottom(4).row();

        Table playerReserveRow = new Table();
        for (int i = 0; i < 3; i++) {
            playerReserveLabels[i] = new Label("", skin);
            playerReserveLabels[i].setFontScale(0.8f);
            playerReserveLabels[i].setAlignment(Align.center);
            playerReserveLabels[i].setColor(0.55f, 0.55f, 0.55f, 1f);
            playerReserveRow.add(playerReserveLabels[i]).width(210).pad(4);
        }
        panel.add(playerReserveRow).padBottom(4).row();

        playerSummaryLabel = new Label("", skin);
        playerSummaryLabel.setFontScale(0.85f);
        playerSummaryLabel.setColor(0.7f, 0.7f, 0.7f, 1f);
        panel.add(playerSummaryLabel).colspan(5).row();

        return panel;
    }

    private void refreshBattlefieldPanel() {
        Card[] enemyActive = battleManager.getEnemyField().getActiveCards();
        Card[] playerActive = battleManager.getPlayerField().getActiveCards();

        for (int i = 0; i < 5; i++) {
            applySlot(enemySlotLabels[i], enemyActive[i], false);
            applySlot(playerSlotLabels[i], playerActive[i], false);
        }

        List<Card> enemyReserve = battleManager.getEnemyField().getReserveCards();
        List<Card> playerReserve = battleManager.getPlayerField().getReserveCards();

        for (int i = 0; i < 3; i++) {
            Card enemyCard = i < enemyReserve.size() ? enemyReserve.get(i) : null;
            Card playerCard = i < playerReserve.size() ? playerReserve.get(i) : null;
            applySlot(enemyReserveLabels[i], enemyCard, true);
            applySlot(playerReserveLabels[i], playerCard, true);
        }

        enemySummaryLabel.setText(
            "Reserve: " + battleManager.getEnemyField().getReserveCards().size() + " card(s)  |  "
                + "Graveyard: " + battleManager.getEnemyField().getGraveyard().size() + " card(s)"
        );
        playerSummaryLabel.setText(
            "Reserve: " + battleManager.getPlayerField().getReserveCards().size() + " card(s)  |  "
                + "Graveyard: " + battleManager.getPlayerField().getGraveyard().size() + " card(s)"
        );
    }

    private void applySlot(Label label, Card card, boolean isReserve) {
        label.setText(formatSlot(card, isReserve));

        if (isReserve) {
            label.setColor(0.55f, 0.55f, 0.55f, 1f);
        } else if (card == null || card.isDead()) {
            label.setColor(0.4f, 0.4f, 0.4f, 1f);
        } else {
            label.setColor(1f, 1f, 1f, 1f);
        }
    }

    private String formatSlot(Card card, boolean isReserve) {
        if (card == null) {
            return isReserve ? "-- none --" : "-- empty --";
        }

        StringBuilder sb = new StringBuilder();
        if (isReserve) {
            sb.append("(Reserve) ");
        }
        sb.append(card.getName()).append("\n");
        sb.append("HP: ").append(card.getCurrentHp()).append("/").append(card.getMaxHp());

        if (card.isDead()) {
            sb.append("\n[DEFEATED]");
        }

        if (card.getShield() > 0) {
            sb.append("\nShield: ").append(card.getShield());
        }

        if (!card.getStatusEffects().isEmpty()) {
            sb.append("\n");
            for (StatusEffect effect : card.getStatusEffects()) {
                sb.append(effect.getStatusType())
                    .append("(").append(effect.getDuration()).append(") ");
            }
        }

        return sb.toString();
    }

    private void stepBattle() {
        if (battleManager.isBattleOver()) {
            return;
        }

        appendLog(battleManager.processSingleAction());
        logLabel.setText(fullLog.toString());
        scrollPane.layout();
        scrollPane.setScrollPercentY(1f);

        refreshBattlefieldPanel();
        refreshTurnOrderPanel();

        if (battleManager.isBattleOver()) {
            nextButton.setDisabled(true);
            rewardButton.setDisabled(false);
            resultLabel.setText(battleManager.isPlayerWin() ? "PLAYER WIN!" : "PLAYER LOSE!");
        }
    }

    private String formatSpeedLabel(BattleSpeed speed) {
        switch (speed) {
            case FAST:
                return "2x Fast";
            case INSTANT:
                return "Skip (Instant)";
            case NORMAL:
            default:
                return "1x Normal";
        }
    }

    private void appendLog(String text) {
        if (text != null && !text.isBlank()) {
            fullLog.append(text).append("\n\n");
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!battleManager.isBattleOver()) {
            BattleSpeed speed = game.getBattleSpeed();

            if (speed == BattleSpeed.INSTANT) {
                while (!battleManager.isBattleOver()) {
                    stepBattle();
                }
            } else {
                float interval = (speed == BattleSpeed.FAST) ? FAST_INTERVAL : NORMAL_INTERVAL;
                stepTimer += delta;

                if (stepTimer >= interval) {
                    stepTimer = 0f;
                    stepBattle();
                }
            }
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
