package com.fallenascendants.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.fallenascendants.FallenAscendantsGame;
import com.fallenascendants.audio.MusicManager;
import com.fallenascendants.audio.SFXManager;
import com.fallenascendants.battle.BattleEvent;
import com.fallenascendants.battle.BattleManager;
import com.fallenascendants.enumtype.BattleSpeed;
import com.fallenascendants.enumtype.Faction;
import com.fallenascendants.enumtype.Rarity;
import com.fallenascendants.model.Card;
import com.fallenascendants.model.ProgressionManager;
import com.fallenascendants.screen.card.CardActor;

import java.util.*;
import java.util.List;

public class BattleScreen implements Screen {

    // =========================================================================
    // 1. INTI & DEKLARASI VARIABEL
    // =========================================================================
    private final FallenAscendantsGame game;
    private final BattleManager battleManager;

    private Stage stage;
    private Skin skin;

    private Texture solidPixel;
    private Label speedLabel;

    // Font System HD Dinamis
    private BitmapFont titleFont;
    private BitmapFont headerFont;
    private BitmapFont cardActiveFont;
    private BitmapFont cardSideFont;
    private BitmapFont uiFont;
    private BitmapFont dialogFont;

    private Label.LabelStyle cardActiveStyle;
    private Label.LabelStyle cardSideStyle;
    private Label.LabelStyle goldTitleStyle;
    private Label.LabelStyle headerStyle;
    private Label.LabelStyle uiStyle;

    private Texture commonFrame, rareFrame, epicFrame, legendaryFrame, specialFrame;
    private Texture backFrameCardTexture;
    private final Map<Card, CardActor> cardActorCache = new IdentityHashMap<>();
    private final Map<Table, Label> hpLabelBySlot = new IdentityHashMap<>();

    private static final int ACTIVE_CARD_WIDTH = 115;
    private static final int ACTIVE_CARD_HEIGHT = 165;
    private static final int SIDE_CARD_WIDTH = 55;
    private static final int SIDE_CARD_HEIGHT = 78;

    private final Table[] enemyActiveSlots = new Table[5];
    private final Table[] playerActiveSlots = new Table[5];
    private Table enemyGraveyardColumn;
    private Table enemyReserveColumn;
    private Table playerGraveyardColumn;
    private Table playerReserveColumn;

    private Label enemySummaryLabel;
    private Label playerSummaryLabel;
    private Label enemySynergyLabel;
    private Label playerSynergyLabel;

    private static final int TURN_ORDER_PREVIEW_SIZE = 6;
    private final Label[] turnOrderLabels = new Label[TURN_ORDER_PREVIEW_SIZE];

    private static final float NORMAL_INTERVAL = 1.2f;
    private static final float FAST_INTERVAL = 0.5f;
    private float stepTimer = 0f;
    private boolean resultDialogShown = false;
    private boolean isAnimating = false;

    private final Card[] enemyDisplayed = new Card[5];
    private final Card[] playerDisplayed = new Card[5];

    private Texture dialogGradient;
    private Texture backgroundTexture;
    private Texture panelTexture;
    private Texture buttonNormal, buttonHover, buttonPressed;
    private TextButton.TextButtonStyle customButtonStyle;
    private Window.WindowStyle customWindowStyle;
    private BattleLogBanner battleLogBanner;

    private Image pauseOverlayImage;
    private Table pauseOverlayContent;
    private boolean paused = false;

    // =========================================================================
    // 2. KONSTRUKTOR
    // =========================================================================
    public BattleScreen(FallenAscendantsGame game, BattleManager battleManager) {
        this.game = game;
        this.battleManager = battleManager;
    }

    // =========================================================================
    // 3. INISIALISASI ASSET & STAGE
    // =========================================================================
    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        Gdx.input.setCatchKey(Input.Keys.ESCAPE, true);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        MusicManager.play("sound/background_music/battle_sound.mp3", true, game.getMusicVolume() * 0.5f);

        panelTexture = new Texture(Gdx.files.internal("Panel/LargePanel.png"));
        panelTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        backgroundTexture = new Texture(Gdx.files.internal("background/background_lobby/BattleBackgroundBlur.png"));
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        buttonNormal = new Texture(Gdx.files.internal("Button/PrimaryButton.png"));
        buttonNormal.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        buttonHover = new Texture(Gdx.files.internal("Button/HoverButton.png"));
        buttonHover.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        buttonPressed = new Texture(Gdx.files.internal("Button/PressedButton.png"));
        buttonPressed.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        Image backgroundImage = new Image(new TextureRegionDrawable(backgroundTexture));
        backgroundImage.setSize(1280, 720);
        stage.addActor(backgroundImage);

        FullscreenToggle.attach(stage);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1f, 1f, 1f, 1f);
        pixmap.fill();
        solidPixel = new Texture(pixmap);
        pixmap.dispose();

        Pixmap gradient = new Pixmap(1, 256, Pixmap.Format.RGBA8888);
        for (int y = 0; y < 256; y++) {
            float a = 0.92f;
            if (y > 90 && y < 170) {
                a = 0.82f;
            }
            gradient.setColor(0f, 0f, 0f, a);
            gradient.drawPixel(0, y);
        }
        dialogGradient = new Texture(gradient);
        gradient.dispose();

        // Setup Font HD Supersampling
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/CinzelDecorative-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;

        parameter.size = 26;
        parameter.color = new Color(0.9f, 0.8f, 0.6f, 1f);
        parameter.borderWidth = 2;
        parameter.borderColor = Color.BLACK;
        titleFont = generator.generateFont(parameter);
        titleFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        parameter.size = 12;
        parameter.color = new Color(0.8f, 0.8f, 0.8f, 1f);
        parameter.borderWidth = 1;
        parameter.borderColor = Color.BLACK;
        headerFont = generator.generateFont(parameter);
        headerFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        parameter.size = 28;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1.5f;
        parameter.borderColor = Color.BLACK;
        cardActiveFont = generator.generateFont(parameter);
        cardActiveFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        cardActiveFont.getData().setScale(0.42f);

        cardSideFont = generator.generateFont(parameter);
        cardSideFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        cardSideFont.getData().setScale(0.24f);

        parameter.size = 13;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1;
        parameter.borderColor = Color.BLACK;
        uiFont = generator.generateFont(parameter);
        uiFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        parameter.size = 18;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 0;
        dialogFont = generator.generateFont(parameter);

        generator.dispose();

        goldTitleStyle = new Label.LabelStyle(titleFont, new Color(0.9f, 0.8f, 0.6f, 1f));
        headerStyle = new Label.LabelStyle(headerFont, new Color(0.8f, 0.8f, 0.8f, 1f));
        cardActiveStyle = new Label.LabelStyle(cardActiveFont, Color.WHITE);
        cardSideStyle = new Label.LabelStyle(cardSideFont, Color.WHITE);
        uiStyle = new Label.LabelStyle(uiFont, Color.WHITE);

        customButtonStyle = new TextButton.TextButtonStyle();
        customButtonStyle.up = new TextureRegionDrawable(buttonNormal);
        customButtonStyle.over = new TextureRegionDrawable(buttonHover);
        customButtonStyle.down = new TextureRegionDrawable(buttonPressed);
        customButtonStyle.font = uiFont;
        customButtonStyle.fontColor = Color.WHITE;
        customButtonStyle.overFontColor = new Color(0.9f, 0.8f, 0.6f, 1f);
        customButtonStyle.pressedOffsetX = 1;
        customButtonStyle.pressedOffsetY = -1;

        customWindowStyle = new Window.WindowStyle();
        customWindowStyle.background = new TextureRegionDrawable(panelTexture);
        customWindowStyle.titleFont = titleFont;
        customWindowStyle.titleFontColor = new Color(0.9f, 0.8f, 0.6f, 1f);

        commonFrame = new Texture(Gdx.files.internal("card_frames/commonFrame.png"));
        rareFrame = new Texture(Gdx.files.internal("card_frames/rareFrame.png"));
        epicFrame = new Texture(Gdx.files.internal("card_frames/epicFrame.png"));
        legendaryFrame = new Texture(Gdx.files.internal("card_frames/legendaryFrame.png"));
        specialFrame = new Texture(Gdx.files.internal("card_frames/specialFrame.png"));
        backFrameCardTexture = new Texture(Gdx.files.internal("card_frames/backFrameCard.png"));
        backFrameCardTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        stage.addActor(root);

        TextButton backButton = new TextButton("Back", customButtonStyle);
        backButton.setPosition(20, 675);
        backButton.setSize(85, 32);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!battleManager.isBattleOver() && !resultDialogShown) {
                    showPauseDialog();
                }
            }
        });
        stage.addActor(backButton);

        Table enemyRow = buildBattlefieldRow(false);
        Table playerRow = buildBattlefieldRow(true);

        battleLogBanner = new BattleLogBanner(dialogGradient, solidPixel, dialogFont);

        root.add(enemyRow).fillX().row();
        root.add(battleLogBanner).width(820).height(70).padTop(30).padBottom(30).row();
        root.add(playerRow).fillX().row();

        battleManager.applyPassiveSkillsAtBattleStart();
        battleManager.applyFactionSynergyAtBattleStart();

        Table turnOrderPanel = buildTurnOrderPanel();
        turnOrderPanel.pack();
        turnOrderPanel.setPosition(1280 - turnOrderPanel.getWidth() - 20, 720 - turnOrderPanel.getHeight() - 20);
        stage.addActor(turnOrderPanel);

        refreshBattlefieldPanel();
        refreshSpeedLabel();
        refreshTurnOrderPanel();
        refreshFactionSynergyPanel();
    }

    // =========================================================================
    // 4. PAUSE OVERLAY SYSTEM
    // =========================================================================
    private void showPauseDialog() {
        if (paused) {
            hidePauseOverlay();
            return;
        }

        paused = true;
        MusicManager.pause();

        float width = 480f;
        float height = 260f;
        float x = (1280f - width) / 2f;
        float y = (720f - height) / 2f;

        pauseOverlayImage = new Image(new TextureRegionDrawable(panelTexture));
        pauseOverlayImage.setSize(width, height);
        pauseOverlayImage.setPosition(x, y);
        stage.addActor(pauseOverlayImage);

        pauseOverlayContent = new Table();
        pauseOverlayContent.setSize(width, height);
        pauseOverlayContent.setPosition(x, y);
        pauseOverlayContent.pad(60f, 30f, 30f, 30f);
        stage.addActor(pauseOverlayContent);

        Label pauseTitle = new Label("PAUSED", goldTitleStyle);
        pauseTitle.setAlignment(Align.center);
        pauseOverlayContent.add(pauseTitle).padBottom(16).row();

        Label pauseText = new Label("Battle is paused.", uiStyle);
        pauseText.setAlignment(Align.center);
        pauseOverlayContent.add(pauseText).padBottom(24).row();

        TextButton continueBtn = new TextButton("Continue", customButtonStyle);
        TextButton mainMenuBtn = new TextButton("Main Menu", customButtonStyle);

        Table buttonRow = new Table();
        buttonRow.add(continueBtn).width(160).height(44).padRight(15);
        buttonRow.add(mainMenuBtn).width(160).height(44);
        pauseOverlayContent.add(buttonRow);

        continueBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hidePauseOverlay();
            }
        });

        mainMenuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MusicManager.stop();
                game.setScreen(new MainMenuScreen(game));
            }
        });
    }

    private void hidePauseOverlay() {
        paused = false;
        MusicManager.resume();
        if (pauseOverlayImage != null) {
            pauseOverlayImage.remove();
            pauseOverlayImage = null;
        }
        if (pauseOverlayContent != null) {
            pauseOverlayContent.remove();
            pauseOverlayContent = null;
        }
    }

    // =========================================================================
    // 5. TURN ORDER & SYNERGY PANELS
    // =========================================================================
    private Table buildTurnOrderPanel() {
        Table panel = new Table();
        panel.setBackground(new TextureRegionDrawable(solidPixel).tint(new Color(0f, 0f, 0f, 0.75f)));
        panel.pad(10, 14, 10, 14);

        Label header = new Label("TURN ORDER", headerStyle);
        panel.add(header).padBottom(8).left().row();

        for (int i = 0; i < TURN_ORDER_PREVIEW_SIZE; i++) {
            turnOrderLabels[i] = new Label("--", uiStyle);
            turnOrderLabels[i].setAlignment(Align.left);
            panel.add(turnOrderLabels[i]).width(160).left().padBottom(4).row();
        }

        speedLabel = new Label("", uiStyle);
        speedLabel.setColor(0.9f, 0.8f, 0.6f, 1f);

        panel.add(speedLabel).padTop(6).left();

        return panel;
    }

    private void refreshSpeedLabel() {
        if (speedLabel == null) return;
        speedLabel.setText("Speed: " + formatSpeedLabel(game.getBattleSpeed()));
    }

    private void refreshTurnOrderPanel() {
        List<Card> upcoming = battleManager.getUpcomingTurnOrder(TURN_ORDER_PREVIEW_SIZE);
        List<Card> preview = buildNextRoundPreview();
        int previewIndex = 0;

        for (int i = 0; i < TURN_ORDER_PREVIEW_SIZE; i++) {
            if (i < upcoming.size()) {
                Card card = upcoming.get(i);
                turnOrderLabels[i].setText((i + 1) + ". " + card.getName());
                Color c = isPlayerCard(card) ? new Color(0.4f, 0.75f, 1f, 1f) : new Color(1f, 0.45f, 0.45f, 1f);
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
                    Color base = isPlayerCard(fillerCard) ? new Color(0.4f, 0.75f, 1f, 1f) : new Color(1f, 0.45f, 0.45f, 1f);
                    turnOrderLabels[i].setColor(base.r, base.g, base.b, 0.5f);
                } else {
                    turnOrderLabels[i].setText("--");
                    turnOrderLabels[i].setColor(0.4f, 0.4f, 0.4f, 1f);
                }
            }
        }
    }

    private List<Card> buildNextRoundPreview() {
        List<Card> alive = new ArrayList<>();
        for (Card c : battleManager.getPlayerField().getActiveCards()) {
            if (c != null && !c.isDead()) alive.add(c);
        }
        for (Card c : battleManager.getEnemyField().getActiveCards()) {
            if (c != null && !c.isDead()) alive.add(c);
        }
        alive.sort((a, b) -> Integer.compare(b.getSpd(), a.getSpd()));
        return alive;
    }

    private boolean containsReference(List<Card> list, Card target) {
        for (Card c : list) {
            if (c == target) return true;
        }
        return false;
    }

    private boolean isPlayerCard(Card card) {
        for (Card ownCard : battleManager.getPlayerField().getActiveCards()) {
            if (ownCard == card) return true;
        }
        return false;
    }

    // =========================================================================
    // 6. LAYOUT BATTLEFIELD & SLOT KARTU
    // =========================================================================
    private Table buildBattlefieldRow(boolean isPlayerSide) {
        Table row = new Table();

        Label header = new Label(isPlayerSide ? "YOUR TEAM" : "ENEMY", goldTitleStyle);
        Label summaryLabel = new Label("", uiStyle);
        summaryLabel.setColor(0.7f, 0.7f, 0.7f, 1f);

        Table graveyardColumn = buildSideColumn("GRAVE");
        Table activeRow = new Table();
        Table reserveColumn = buildSideColumn("RESERVE");

        for (int i = 0; i < 5; i++) {
            Table slot = new Table();
            activeRow.add(slot).pad(5);
            if (isPlayerSide) {
                playerActiveSlots[i] = slot;
            } else {
                enemyActiveSlots[i] = slot;
            }
        }

        if (isPlayerSide) {
            playerGraveyardColumn = graveyardColumn;
            playerReserveColumn = reserveColumn;
            playerSummaryLabel = summaryLabel;

            row.add(graveyardColumn).width(70).top().padRight(15);
            row.add(activeRow).top();
            row.add(reserveColumn).width(70).top().padLeft(15);
            row.row();

            row.add(header).colspan(3).padTop(10).padBottom(5).row();
            row.add(summaryLabel).colspan(3).padBottom(5).row();

        } else {
            enemyReserveColumn = reserveColumn;
            enemyGraveyardColumn = graveyardColumn;
            enemySummaryLabel = summaryLabel;

            row.add(header).colspan(3).padBottom(5).row();
            row.add(summaryLabel).colspan(3).padBottom(10).row();

            row.add(reserveColumn).width(70).top().padRight(15);
            row.add(activeRow).top();
            row.add(graveyardColumn).width(70).top().padLeft(15);
            row.row();
        }

        return row;
    }

    private Table buildSideColumn(String headerText) {
        Table column = new Table();
        Label header = new Label(headerText, headerStyle);
        column.add(header).padBottom(4).row();
        return column;
    }

    private void refreshBattlefieldPanel() {
        Card[] enemyActive = battleManager.getEnemyField().getActiveCards();
        Card[] playerActive = battleManager.getPlayerField().getActiveCards();

        for (int i = 0; i < 5; i++) {
            if (enemyDisplayed[i] != enemyActive[i]) {
                enemyDisplayed[i] = enemyActive[i];
                fillActiveSlot(enemyActiveSlots[i], enemyActive[i], true);
            } else {
                updateActiveSlotHp(enemyActiveSlots[i], enemyActive[i]);
            }

            if (playerDisplayed[i] != playerActive[i]) {
                playerDisplayed[i] = playerActive[i];
                fillActiveSlot(playerActiveSlots[i], playerActive[i], false);
            } else {
                updateActiveSlotHp(playerActiveSlots[i], playerActive[i]);
            }
        }

        fillSideColumn(enemyReserveColumn, battleManager.getEnemyField().getReserveCards(), "RESERVE");
        fillSideColumn(enemyGraveyardColumn, battleManager.getEnemyField().getGraveyard(), "GRAVE");
        fillSideColumn(playerReserveColumn, battleManager.getPlayerField().getReserveCards(), "RESERVE");
        fillSideColumn(playerGraveyardColumn, battleManager.getPlayerField().getGraveyard(), "GRAVE");
    }

    private void updateActiveSlotHp(Table slot, Card card) {
        if (card == null) return;

        Label hpLabel = hpLabelBySlot.get(slot);
        if (hpLabel == null) return;

        String hpText = card.isDead()
            ? "[DEFEATED]"
            : card.getCurrentHp() + "/" + card.getMaxHp() + (card.getShield() > 0 ? " (+" + card.getShield() + ")" : "");
        hpLabel.setText(hpText);
        hpLabel.setColor(card.isDead() ? new Color(0.8f, 0.3f, 0.3f, 1f) : Color.WHITE);

        CardActor cardActor = cardActorCache.get(card);
        if (cardActor != null) {
            cardActor.getColor().set(card.isDead() ? new Color(0.35f, 0.35f, 0.35f, 1f) : Color.WHITE);
        }
    }

    private CardActor getOrCreateCardActor(Card card, Label.LabelStyle labelStyle) {
        CardActor cached = cardActorCache.get(card);
        if (cached == null) {
            Texture frame = getFrameByRarity(card.getRarity());
            cached = new CardActor(card, skin, true, labelStyle, frame);

            final CardActor actorRef = cached;
            cached.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (button == Input.Buttons.RIGHT) {
                        if (card.isDead()) return false;
                        if (statsTooltip == null) {
                            showStatsTooltip(actorRef, card);
                        } else {
                            hideStatsTooltip();
                        }
                        return true;
                    }
                    return false;
                }
            });
            cardActorCache.put(card, cached);
        }
        return cached;
    }

    private void fillActiveSlot(Table slot, Card card, boolean isEnemySide) {
        slot.clear();
        hpLabelBySlot.remove(slot);

        if (card == null) {
            Label empty = new Label("--", uiStyle);
            empty.setColor(0.4f, 0.4f, 0.4f, 1f);
            slot.add(empty).size(ACTIVE_CARD_WIDTH, ACTIVE_CARD_HEIGHT);
            return;
        }

        CardActor cardActor = getOrCreateCardActor(card, cardActiveStyle);
        cardActor.clearActions();
        cardActor.setPosition(0, 0);
        cardActor.getColor().set(card.isDead() ? new Color(0.35f, 0.35f, 0.35f, 1f) : Color.WHITE);

        String hpText = card.isDead() ? "[DEFEATED]" : card.getCurrentHp() + "/" + card.getMaxHp() + (card.getShield() > 0 ? " (+" + card.getShield() + ")" : "");
        Label hpLabel = new Label(hpText, uiStyle);
        hpLabel.setAlignment(Align.center);
        hpLabel.setColor(card.isDead() ? new Color(0.8f, 0.3f, 0.3f, 1f) : Color.WHITE);
        hpLabelBySlot.put(slot, hpLabel);

        if (isEnemySide) {
            slot.add(hpLabel).padBottom(4).row();
            slot.add(cardActor).size(ACTIVE_CARD_WIDTH, ACTIVE_CARD_HEIGHT);
        } else {
            slot.add(cardActor).size(ACTIVE_CARD_WIDTH, ACTIVE_CARD_HEIGHT).row();
            slot.add(hpLabel).padTop(4);
        }
    }

    private static final int MAX_STACK_VISIBLE = 4;

    private void fillSideColumn(Table column, List<Card> cards, String headerText) {
        column.clear();

        Label header = new Label(headerText, headerStyle);
        column.add(header).padBottom(4).row();

        if (cards.isEmpty()) {
            Label empty = new Label("--", uiStyle);
            empty.setColor(0.4f, 0.4f, 0.4f, 1f);
            column.add(empty);
            return;
        }

        int total = cards.size();
        List<Card> visibleCards = total > MAX_STACK_VISIBLE ? cards.subList(total - MAX_STACK_VISIBLE, total) : cards;

        int overlapPeek = 18;
        for (int i = 0; i < visibleCards.size(); i++) {
            Card card = visibleCards.get(i);
            float topPad = (i == 0) ? 0f : -(SIDE_CARD_HEIGHT - overlapPeek);

            Image cardImage = new Image(backFrameCardTexture);
            column.add(cardImage).size(SIDE_CARD_WIDTH, SIDE_CARD_HEIGHT).padTop(topPad).row();
        }

        if (total > MAX_STACK_VISIBLE) {
            Label moreLabel = new Label("+" + (total - MAX_STACK_VISIBLE), uiStyle);
            moreLabel.setColor(0.6f, 0.6f, 0.6f, 1f);
            column.add(moreLabel).padTop(4).row();
        }
    }

    private Texture getFrameByRarity(Rarity rarity) {
        if (rarity == null) return commonFrame;
        switch (rarity) {
            case RARE: return rareFrame;
            case EPIC: return epicFrame;
            case LEGENDARY: return legendaryFrame;
            default: return commonFrame;
        }
    }

    // =========================================================================
    // 7. BATTLE ENGINE STEPPING & ANIMATIONS
    // =========================================================================
    private void resolveRemainingActionsInstantly() {
        while (!battleManager.isBattleOver()) {
            battleManager.processSingleAction();
        }

        refreshBattlefieldPanel();
        refreshTurnOrderPanel();

        if (!resultDialogShown) {
            resultDialogShown = true;
            showBattleResultDialog();
        }
    }

    private void stepBattle() {
        if (battleManager.isBattleOver()) return;

        Map<Card, int[]> beforeSnapshot = captureHpShieldSnapshot();

        List<BattleEvent> turnEvents = battleManager.processSingleAction();

        Card animAttacker = battleManager.wasLastActionBasicAttack() ? battleManager.getLastAttacker() : null;
        Card animTarget = battleManager.wasLastActionBasicAttack() ? battleManager.getLastTarget() : null;
        CardActor attackerActor = (animAttacker != null) ? cardActorCache.get(animAttacker) : null;
        CardActor targetActor = (animTarget != null) ? cardActorCache.get(animTarget) : null;

        Vector2 attackerPos = null;
        Vector2 targetPos = null;
        if (attackerActor != null && targetActor != null) {
            attackerPos = attackerActor.localToStageCoordinates(new Vector2(attackerActor.getWidth() / 2f, attackerActor.getHeight() / 2f));
            targetPos = targetActor.localToStageCoordinates(new Vector2(targetActor.getWidth() / 2f, targetActor.getHeight() / 2f));
        }

        final float UI_FADE_IN = 0.15f;
        final float UI_SHOW = 1.2f;
        final float UI_FADE_OUT = 0.5f;
        final float UI_PADDING = 0.2f;
        final float TEXT_DISPLAY_DURATION = UI_FADE_IN + UI_SHOW + UI_FADE_OUT + UI_PADDING;
        final float ATTACK_ANIM_DURATION = 0.6f;

        float currentDelay = 0f;
        float climaxDelay = -1f;
        String pendingSfx = null;

        for (BattleEvent event : turnEvents) {
            final BattleEvent e = event;

            if (e.type == BattleEvent.EventType.TEXT_MESSAGE) {
                String[] lines = e.message.split("\n");
                for (String line : lines) {
                    String cleanLine = line.trim();
                    if (cleanLine.isEmpty()) continue;
                    String msgLower = cleanLine.toLowerCase();

                    if (msgLower.startsWith("raw damage") || msgLower.startsWith("blocked by def") ||
                        msgLower.startsWith("shield absorbed") || msgLower.startsWith("hp damage") ||
                        msgLower.contains(" hp: ") || msgLower.contains(" shield: ") ||
                        msgLower.contains(" atk: ") || msgLower.startsWith("applying ") ||
                        msgLower.startsWith("> ")) {
                        continue;
                    }

                    if (msgLower.contains("burn")) pendingSfx = "sound/sound_effect/burn.mp3";
                    else if (msgLower.contains("poison")) pendingSfx = "sound/sound_effect/poison.mp3";
                    else if (msgLower.contains("slow")) pendingSfx = "sound/sound_effect/slow.mp3";
                    else if (msgLower.contains("stun")) pendingSfx = "sound/sound_effect/stun.mp3";
                    else if (msgLower.contains("heal") || msgLower.contains("restor")) pendingSfx = "sound/sound_effect/heal.mp3";

                    final String textToShow = cleanLine;
                    stage.addAction(Actions.sequence(
                        Actions.delay(currentDelay),
                        Actions.run(() -> announce(textToShow))
                    ));

                    currentDelay += TEXT_DISPLAY_DURATION;
                    if (climaxDelay == -1f) climaxDelay = currentDelay;
                }
            }
            else if (e.type == BattleEvent.EventType.ACTION_ATTACK) {
                boolean canAnimate = attackerPos != null && targetPos != null && attackerActor.getStage() != null;
                if (canAnimate) {
                    final Vector2 aPos = attackerPos;
                    final Vector2 tPos = targetPos;
                    stage.addAction(Actions.sequence(
                        Actions.delay(currentDelay),
                        Actions.run(() -> playLungeAnimation(attackerActor, aPos, tPos))
                    ));
                    climaxDelay = currentDelay + 0.20f;
                    currentDelay += ATTACK_ANIM_DURATION;
                }
            }
        }

        if (climaxDelay == -1f) climaxDelay = currentDelay;

        final String finalSfx = pendingSfx;
        stage.addAction(Actions.sequence(
            Actions.delay(climaxDelay),
            Actions.run(() -> applySnapshotEffects(beforeSnapshot, finalSfx))
        ));

        final float totalDelay = Math.max(currentDelay, climaxDelay + 0.6f);

        stage.addAction(Actions.sequence(
            Actions.delay(totalDelay),
            Actions.run(() -> {
                refreshBattlefieldPanel();
                refreshTurnOrderPanel();
                refreshFactionSynergyPanel();

                if (battleManager.isBattleOver() && !resultDialogShown) {
                    resultDialogShown = true;
                    showBattleResultDialog();
                } else {
                    isAnimating = false;
                }
            })
        ));
    }

    private void playLungeAnimation(CardActor attackerActor, Vector2 attackerPos, Vector2 targetPos) {
        float dx = (targetPos.x - attackerPos.x) * 0.7f;
        float dy = (targetPos.y - attackerPos.y) * 0.7f;

        attackerActor.clearActions();
        attackerActor.addAction(Actions.sequence(
            Actions.moveBy(dx, dy, 0.20f, Interpolation.pow2Out),
            Actions.delay(0.08f),
            Actions.moveBy(-dx, -dy, 0.18f, Interpolation.pow2In)
        ));
    }

    private Map<Card, int[]> captureHpShieldSnapshot() {
        Map<Card, int[]> snapshot = new IdentityHashMap<>();
        for (Card c : battleManager.getPlayerField().getActiveCards()) {
            if (c != null) snapshot.put(c, new int[]{c.getCurrentHp(), c.getShield()});
        }
        for (Card c : battleManager.getEnemyField().getActiveCards()) {
            if (c != null) snapshot.put(c, new int[]{c.getCurrentHp(), c.getShield()});
        }
        return snapshot;
    }

    private void applySnapshotEffects(Map<Card, int[]> beforeSnapshot, String overrideSfx) {
        boolean sfxPlayed = false;

        for (Map.Entry<Card, int[]> entry : beforeSnapshot.entrySet()) {
            Card card = entry.getKey();
            int oldHp = entry.getValue()[0];
            int oldShield = entry.getValue()[1];
            int hpDelta = card.getCurrentHp() - oldHp;
            int shieldDelta = card.getShield() - oldShield;

            if (hpDelta == 0 && shieldDelta == 0) continue;

            CardActor actor = cardActorCache.get(card);
            if (actor == null || actor.getStage() == null) continue;

            if (hpDelta < 0) {
                playDamageVisual(actor);
                spawnFloatingNumber(actor, String.valueOf(hpDelta), Color.RED);
                if (!sfxPlayed) {
                    String sfx = overrideSfx != null ? overrideSfx : "sound/sound_effect/hit.mp3";
                    SFXManager.play(sfx, game.getSfxVolume() * 1.5f);
                    sfxPlayed = true;
                }
            } else if (hpDelta > 0) {
                playHealVisual(actor);
                spawnFloatingNumber(actor, "+" + hpDelta, Color.GREEN);
                if (!sfxPlayed) {
                    String sfx = overrideSfx != null ? overrideSfx : "sound/sound_effect/heal.mp3";
                    SFXManager.play(sfx, game.getSfxVolume() * 1.5f);
                    sfxPlayed = true;
                }
            } else if (shieldDelta > 0) {
                playShieldGainVisual(actor);
                spawnFloatingNumber(actor, "+" + shieldDelta + " Shield", Color.CYAN);
                if (!sfxPlayed) {
                    String sfx = overrideSfx != null ? overrideSfx : "sound/sound_effect/heal.mp3";
                    SFXManager.play(sfx, game.getSfxVolume() * 1.5f);
                    sfxPlayed = true;
                }
            } else if (shieldDelta < 0) {
                playDamageVisual(actor);
                spawnFloatingNumber(actor, "Absorb " + Math.abs(shieldDelta), Color.GRAY);
                if (!sfxPlayed) {
                    String sfx = overrideSfx != null ? overrideSfx : "sound/sound_effect/hit.mp3";
                    SFXManager.play(sfx, game.getSfxVolume() * 1.5f);
                    sfxPlayed = true;
                }
            }
        }
    }

    private void playDamageVisual(CardActor actor) {
        actor.clearActions();
        actor.addAction(Actions.parallel(
            Actions.sequence(
                Actions.moveBy(6, 0, 0.03f),
                Actions.moveBy(-12, 0, 0.05f),
                Actions.moveBy(6, 0, 0.03f)
            ),
            Actions.sequence(
                Actions.color(Color.RED, 0.05f),
                Actions.color(Color.WHITE, 0.12f)
            )
        ));
    }

    private void playHealVisual(CardActor actor) {
        actor.clearActions();
        actor.addAction(Actions.parallel(
            Actions.sequence(
                Actions.moveBy(0, 10, 0.05f, Interpolation.pow2Out),
                Actions.moveBy(0, -10, 0.08f, Interpolation.pow2In)
            ),
            Actions.sequence(
                Actions.color(Color.GREEN, 0.05f),
                Actions.color(Color.WHITE, 0.12f)
            )
        ));
    }

    private void playShieldGainVisual(CardActor actor) {
        actor.clearActions();
        actor.addAction(Actions.parallel(
            Actions.sequence(
                Actions.moveBy(0, 8, 0.05f, Interpolation.circleOut),
                Actions.moveBy(0, -8, 0.08f, Interpolation.circleIn)
            ),
            Actions.sequence(
                Actions.color(Color.CYAN, 0.05f),
                Actions.color(Color.WHITE, 0.15f)
            )
        ));
    }

    private void spawnFloatingNumber(CardActor sourceActor, String text, Color color) {
        Label numberLabel = new Label(text, uiStyle);
        numberLabel.setColor(color);

        Vector2 pos = sourceActor.localToStageCoordinates(
            new Vector2(sourceActor.getWidth() / 2f, sourceActor.getHeight()));

        numberLabel.setPosition(pos.x - 15, pos.y);
        stage.addActor(numberLabel);

        numberLabel.addAction(Actions.sequence(
            Actions.parallel(
                Actions.moveBy(0, 30, 0.8f, Interpolation.pow2Out),
                Actions.sequence(Actions.delay(0.3f), Actions.fadeOut(0.5f))
            ),
            Actions.removeActor()
        ));
    }

    private void refreshFactionSynergyPanel() {
        HashMap<Faction, Integer> playerFactions = new HashMap<>();
        for (Card card : battleManager.getPlayerField().getActiveCards()) {
            if (card != null && !card.isDead()) {
                playerFactions.put(card.getFaction(), playerFactions.getOrDefault(card.getFaction(), 0) + 1);
            }
        }
        StringBuilder pSynergyText = new StringBuilder("TEAM SYNERGY\n");
        boolean hasPlayerSynergy = false;
        for (Faction faction : playerFactions.keySet()) {
            int count = playerFactions.get(faction);
            int bonus = (count >= 5) ? 25 : (count >= 3) ? 15 : (count >= 2) ? 10 : 0;
            if (bonus > 0) {
                hasPlayerSynergy = true;
                pSynergyText.append("- ").append(faction.name())
                    .append(" (").append(count).append("/5) : ATK +").append(bonus).append("%\n");
            }
        }
        if (!hasPlayerSynergy) pSynergyText.append("No active synergy");

        if (playerSynergyLabel == null) {
            playerSynergyLabel = new Label(pSynergyText.toString(), uiStyle);
            playerSynergyLabel.setPosition(950, 30);
            stage.addActor(playerSynergyLabel);
        } else {
            playerSynergyLabel.setText(pSynergyText.toString());
        }

        HashMap<Faction, Integer> enemyFactions = new HashMap<>();
        for (Card card : battleManager.getEnemyField().getActiveCards()) {
            if (card != null && !card.isDead()) {
                enemyFactions.put(card.getFaction(), enemyFactions.getOrDefault(card.getFaction(), 0) + 1);
            }
        }
        StringBuilder eSynergyText = new StringBuilder("ENEMY SYNERGY\n");
        boolean hasEnemySynergy = false;
        for (Faction faction : enemyFactions.keySet()) {
            int count = enemyFactions.get(faction);
            int bonus = (count >= 5) ? 25 : (count >= 3) ? 15 : (count >= 2) ? 10 : 0;
            if (bonus > 0) {
                hasEnemySynergy = true;
                eSynergyText.append("- ").append(faction.name())
                    .append(" (").append(count).append("/5) : ATK +").append(bonus).append("%\n");
            }
        }
        if (!hasEnemySynergy) eSynergyText.append("No active synergy");

        if (enemySynergyLabel == null) {
            enemySynergyLabel = new Label(eSynergyText.toString(), uiStyle);
            enemySynergyLabel.setPosition(30, 390);
            stage.addActor(enemySynergyLabel);
        } else {
            enemySynergyLabel.setText(eSynergyText.toString());
        }
    }

    // =========================================================================
    // 8. BATTLE RESULT & TRANSITION (FIX: POP-UP DIPANGKAS SEPENUHNYA)
    // =========================================================================
    private void showBattleResultDialog() {
        MusicManager.stop();

        // Hitung hadiah & langsung alihkan Screen ke RewardScreen tanpa membuat dialog pertama
        ProgressionManager.BattleRewards rewards =
            ProgressionManager.processBattleRewards(game.getPlayer(), battleManager.isPlayerWin());

        game.setScreen(new RewardScreen(game, rewards));
    }

    private void announce(String text) {
        if (text == null || text.isBlank() || battleLogBanner == null) {
            return;
        }
        battleLogBanner.setText(text);
        battleLogBanner.clearActions();
        battleLogBanner.getColor().a = 0f;
        battleLogBanner.addAction(Actions.sequence(
            Actions.fadeIn(0.15f),
            Actions.delay(1.2f),
            Actions.fadeOut(0.5f)
        ));
    }

    private String formatSpeedLabel(BattleSpeed speed) {
        switch (speed) {
            case FAST: return "2x Fast";
            case INSTANT: return "Skip (Instant)";
            case NORMAL:
            default: return "1x Normal";
        }
    }

    // =========================================================================
    // 9. TOOLTIP & LIFECYCLE MANAGEMENT
    // =========================================================================
    private Table statsTooltip;

    private void showStatsTooltip(Actor sourceActor, Card card) {
        hideStatsTooltip();

        String activeSkillName = card.getActiveSkill() == null ? "None" : card.getActiveSkill().getName();
        boolean isBuffed = card.getAtk() != card.getBaseAtk() || card.getDef() != card.getBaseDef();

        String statsText = card.getName() + "\n"
            + card.getRole() + " | " + card.getRarity() + " | Lvl " + card.getLevel() + "\n"
            + "ATK: " + card.getAtk() + (isBuffed ? " (base " + card.getBaseAtk() + ")" : "") + "\n"
            + "DEF: " + card.getDef() + (isBuffed ? " (base " + card.getBaseDef() + ")" : "") + "\n"
            + "SPD: " + card.getSpd() + "   Aggro: " + card.getAggro() + "\n"
            + "HP: " + card.getCurrentHp() + "/" + card.getMaxHp()
            + (card.getShield() > 0 ? "  Shield: " + card.getShield() : "") + "\n"
            + "Skill: " + activeSkillName;

        Label statsLabel = new Label(statsText, uiStyle);
        statsLabel.setWrap(true);

        Container<Label> content = new Container<>(statsLabel);
        content.pad(10);
        content.fill();
        content.width(260);
        content.setBackground(new TextureRegionDrawable(solidPixel).tint(new Color(0f, 0f, 0f, 0.88f)));

        statsTooltip = new Table();
        statsTooltip.add(content);
        statsTooltip.pack();

        Vector2 pos = sourceActor.localToStageCoordinates(new Vector2(0, 0));
        float clampedX = Math.max(10f, Math.min(pos.x, 1280 - statsTooltip.getWidth() - 10));
        float clampedY = Math.max(10f, Math.min(pos.y + sourceActor.getHeight() + 10, 720 - statsTooltip.getHeight() - 10));

        statsTooltip.setPosition(clampedX, clampedY);
        stage.addActor(statsTooltip);
    }

    private void hideStatsTooltip() {
        if (statsTooltip != null) {
            statsTooltip.remove();
            statsTooltip = null;
        }
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (!battleManager.isBattleOver() && !resultDialogShown) {
                showPauseDialog();
            }
        }

        Gdx.gl.glClearColor(0.04f, 0.04f, 0.06f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!paused && !resultDialogShown) {
            BattleSpeed speed = game.getBattleSpeed();

            if (speed == BattleSpeed.INSTANT) {
                if (!isAnimating && !battleManager.isBattleOver()) {
                    resolveRemainingActionsInstantly();
                }
            } else {
                if (!isAnimating && !battleManager.isBattleOver()) {
                    float interval = (speed == BattleSpeed.FAST) ? FAST_INTERVAL : NORMAL_INTERVAL;
                    stepTimer += delta;

                    if (stepTimer >= interval) {
                        stepTimer = 0f;
                        isAnimating = true;
                        stepBattle();
                    }
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

    @Override
    public void hide() {
        MusicManager.stop();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (titleFont != null) titleFont.dispose();
        if (headerFont != null) headerFont.dispose();
        if (cardActiveFont != null) cardActiveFont.dispose();
        if (cardSideFont != null) cardSideFont.dispose();
        if (uiFont != null) uiFont.dispose();
        if (commonFrame != null) commonFrame.dispose();
        if (rareFrame != null) rareFrame.dispose();
        if (epicFrame != null) epicFrame.dispose();
        if (legendaryFrame != null) legendaryFrame.dispose();
        if (specialFrame != null) specialFrame.dispose();
        if (backFrameCardTexture != null) backFrameCardTexture.dispose();
        if (solidPixel != null) solidPixel.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (panelTexture != null) panelTexture.dispose();
        if (buttonNormal != null) buttonNormal.dispose();
        if (buttonHover != null) buttonHover.dispose();
        if (buttonPressed != null) buttonPressed.dispose();
        if (dialogFont != null) dialogFont.dispose();
        if (dialogGradient != null) dialogGradient.dispose();

        for (CardActor actor : cardActorCache.values()) {
            actor.dispose();
        }
        cardActorCache.clear();
    }
}
