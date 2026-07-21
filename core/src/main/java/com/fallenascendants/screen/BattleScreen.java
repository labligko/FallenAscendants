package com.fallenascendants.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.fallenascendants.FallenAscendantsGame;
import com.fallenascendants.audio.MusicManager;
import com.fallenascendants.battle.BattleManager;
import com.fallenascendants.battle.FactionCounterGraph;
import com.fallenascendants.enumtype.BattleSpeed;
import com.fallenascendants.enumtype.Faction;
import com.fallenascendants.enumtype.Rarity;
import com.fallenascendants.model.Card;
import com.fallenascendants.model.ProgressionManager;
import com.fallenascendants.model.StatusEffect;
import com.fallenascendants.screen.card.CardActor;

import java.util.List;

public class BattleScreen implements Screen {
    private final FallenAscendantsGame game;
    private final BattleManager battleManager;

    private Stage stage;
    private Skin skin;

    private Texture solidPixel;
    private com.badlogic.gdx.scenes.scene2d.ui.Container<Label> announcePanel;
    private Label announceLabel;
    private Label speedLabel;

    // Font & frame, dipola sama kayak PreBattleScreen biar konsisten
    private BitmapFont titleFont;
    private BitmapFont uiFont;
    private Label.LabelStyle cardLabelStyle;
    private Label.LabelStyle goldTitleStyle;
    private Texture commonFrame, rareFrame, epicFrame, legendaryFrame, specialFrame;
    private Texture backFrameCardTexture;
    private final java.util.Map<Card, CardActor> cardActorCache = new java.util.IdentityHashMap<>();

    private static final int ACTIVE_CARD_WIDTH = 92;
    private static final int ACTIVE_CARD_HEIGHT = 132;
    private static final int SIDE_CARD_WIDTH = 46;
    private static final int SIDE_CARD_HEIGHT = 66;

    private final Table[] enemyActiveSlots = new Table[5];
    private final Table[] playerActiveSlots = new Table[5];
    private Table enemyActiveRow;
    private Table playerActiveRow;
    private Table enemyGraveyardColumn;
    private Table enemyReserveColumn;
    private Table playerGraveyardColumn;
    private Table playerReserveColumn;

    private Label enemySummaryLabel;
    private Label playerSummaryLabel;

    private static final int TURN_ORDER_PREVIEW_SIZE = 6;
    private final Label[] turnOrderLabels = new Label[TURN_ORDER_PREVIEW_SIZE];

    private static final float NORMAL_INTERVAL = 1.2f;
    private static final float FAST_INTERVAL = 0.5f;
    private float stepTimer = 0f;
    private boolean resultDialogShown = false;

    private final Card[] enemyDisplayed = new Card[5];
    private final Card[] playerDisplayed = new Card[5];

    public BattleScreen(FallenAscendantsGame game, BattleManager battleManager) {
        this.game = game;
        this.battleManager = battleManager;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchKey(com.badlogic.gdx.Input.Keys.BACK, true);
        Gdx.input.setCatchKey(com.badlogic.gdx.Input.Keys.ESCAPE, true);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        MusicManager.play("sound/background_music/battle_sound.mp3", true, game.getMusicVolume());

        FullscreenToggle.attach(stage);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/CinzelDecorative-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 24;
        parameter.color = new Color(0.9f, 0.8f, 0.6f, 1f);
        parameter.borderWidth = 2;
        parameter.borderColor = Color.BLACK;
        titleFont = generator.generateFont(parameter);

        parameter.size = 12;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1;
        parameter.borderColor = Color.BLACK;
        uiFont = generator.generateFont(parameter);
        generator.dispose();

        cardLabelStyle = new Label.LabelStyle(uiFont, Color.WHITE);
        goldTitleStyle = new Label.LabelStyle(titleFont, new Color(0.9f, 0.8f, 0.6f, 1f));

        commonFrame = new Texture(Gdx.files.internal("card_frames/commonFrame.png"));
        rareFrame = new Texture(Gdx.files.internal("card_frames/rareFrame.png"));
        epicFrame = new Texture(Gdx.files.internal("card_frames/epicFrame.png"));
        legendaryFrame = new Texture(Gdx.files.internal("card_frames/legendaryFrame.png"));
        specialFrame = new Texture(Gdx.files.internal("card_frames/specialFrame.png"));
        backFrameCardTexture = new Texture(Gdx.files.internal("card_frames/backFrameCard.png"));
        backFrameCardTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        Table root = new Table();
        root.setFillParent(true);
        root.top().pad(20);
        stage.addActor(root);

        TextButton backButton = new TextButton("← Back", skin);
        backButton.setPosition(20, 680);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showPauseDialog();
            }
        });

        stage.addActor(backButton);

        Table enemyRow = buildBattlefieldRow(false);
        Table playerRow = buildBattlefieldRow(true);
        root.add(enemyRow).padBottom(10).row();
        root.add(playerRow).padBottom(15).row();

        Label title = new Label("BATTLE LOG", skin);
        title.setFontScale(1.5f);
        root.add(title).padBottom(10).row();

        // Passive/synergy tetep dijalanin (efeknya nempel ke kartu), cuma teksnya udah gak
        // ditampilin lagi di sini — battle log sekarang jadi banner popup per-aksi.
        battleManager.applyPassiveSkillsAtBattleStart();
        battleManager.applyFactionSynergyAtBattleStart();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1f, 1f, 1f, 1f);
        pixmap.fill();
        solidPixel = new Texture(pixmap);
        pixmap.dispose();

        announceLabel = new Label("", cardLabelStyle);
        announceLabel.setFontScale(1f);
        announceLabel.setWrap(true);
        announceLabel.setAlignment(Align.center);

        announcePanel = new com.badlogic.gdx.scenes.scene2d.ui.Container<>(announceLabel);
        announcePanel.pad(10);
        announcePanel.fill();
        announcePanel.width(700);
        announcePanel.setBackground(new TextureRegionDrawable(solidPixel).tint(new Color(0f, 0f, 0f, 0.7f)));
        announcePanel.getColor().a = 0f;

        root.add(announcePanel).width(700).padBottom(15).row();

        Table buttonRow = new Table();
        root.add(buttonRow);

        // Isi panel status dengan kondisi awal (sebelum aksi pertama jalan)
        refreshBattlefieldPanel();

        // Turn order panel: actor terpisah, gak ikut alur "root" sama sekali,
        // biar posisi tombol Next/dkk gak kegeser. Ditaro manual di pojok kanan atas.
        Table turnOrderPanel = buildTurnOrderPanel();
        turnOrderPanel.pack();
        turnOrderPanel.setPosition(1280 - turnOrderPanel.getWidth() - 15, 720 - turnOrderPanel.getHeight() - 15);
        stage.addActor(turnOrderPanel);


        refreshSpeedLabel();
        refreshTurnOrderPanel();
    }

    private Dialog pauseDialog;
    private boolean paused = false;
    private void showPauseDialog() {

        // Kalau sudah pause -> tutup dialog
        if (paused) {
            paused = false;
            MusicManager.resume();

            if (pauseDialog != null) {
                pauseDialog.hide();
            }

            return;
        }

        // Kalau belum pause -> buka dialog
        paused = true;
        MusicManager.pause();

        pauseDialog = new Dialog("Paused", skin) {

            @Override
            protected void result(Object object) {

                boolean continueGame = (Boolean) object;

                if (continueGame) {
                    paused = false;
                    MusicManager.resume();
                } else {
                    MusicManager.stop();
                    game.setScreen(new MainMenuScreen(game));
                }
            }
        };

        pauseDialog.text("Battle is paused.");
        pauseDialog.button("Continue", true);
        pauseDialog.button("Main Menu", false);

        pauseDialog.show(stage);
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

        panel.row();

        speedLabel = new Label("", skin);
        speedLabel.setFontScale(0.75f);

        panel.add(speedLabel)
            .padTop(8)
            .left();

        return panel;
    }

    private void refreshSpeedLabel() {
        if (speedLabel == null) return;

        speedLabel.setText("Speed : " + formatSpeedLabel(game.getBattleSpeed()));
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

    private Table buildBattlefieldRow(boolean isPlayerSide) {
        Table row = new Table();

        Label header = new Label(isPlayerSide ? "YOUR TEAM" : "ENEMY", goldTitleStyle);
        header.setFontScale(0.8f);
        row.add(header).colspan(3).padBottom(6).row();

        Table graveyardColumn = buildSideColumn("GRAVE");
        Table activeRow = new Table();
        Table reserveColumn = buildSideColumn("RESERVE");

        for (int i = 0; i < 5; i++) {
            Table slot = new Table();
            activeRow.add(slot).pad(3);
            if (isPlayerSide) {
                playerActiveSlots[i] = slot;
            } else {
                enemyActiveSlots[i] = slot;
            }
        }

        if (isPlayerSide) {
            row.add(graveyardColumn).width(70).top().padRight(8);
            row.add(activeRow).top();
            row.add(reserveColumn).width(70).top().padLeft(8);
            playerGraveyardColumn = graveyardColumn;
            playerReserveColumn = reserveColumn;
            playerActiveRow = activeRow;
        } else {
            row.add(reserveColumn).width(70).top().padRight(8);
            row.add(activeRow).top();
            row.add(graveyardColumn).width(70).top().padLeft(8);
            enemyReserveColumn = reserveColumn;
            enemyGraveyardColumn = graveyardColumn;
            enemyActiveRow = activeRow;
        }
        row.row();

        Label summaryLabel = new Label("", cardLabelStyle);
        summaryLabel.setFontScale(0.75f);
        summaryLabel.setColor(0.7f, 0.7f, 0.7f, 1f);
        row.add(summaryLabel).colspan(3).padTop(4);

        if (isPlayerSide) {
            playerSummaryLabel = summaryLabel;
        } else {
            enemySummaryLabel = summaryLabel;
        }

        return row;
    }

    private Table buildSideColumn(String headerText) {
        Table column = new Table();
        Label header = new Label(headerText, cardLabelStyle);
        header.setFontScale(0.7f);
        header.setColor(0.65f, 0.65f, 0.65f, 1f);
        column.add(header).padBottom(4).row();
        return column;
    }

    private void refreshBattlefieldPanel() {
        Card[] enemyActive = battleManager.getEnemyField().getActiveCards();
        Card[] playerActive = battleManager.getPlayerField().getActiveCards();

        for (int i = 0; i < 5; i++) {
            if (enemyDisplayed[i] != enemyActive[i]) {
                enemyDisplayed[i] = enemyActive[i];
                fillActiveSlot(enemyActiveSlots[i], enemyActive[i]);
            }

            if (playerDisplayed[i] != playerActive[i]) {
                playerDisplayed[i] = playerActive[i];
                fillActiveSlot(playerActiveSlots[i], playerActive[i]);
            }
        }

        fillSideColumn(enemyReserveColumn, battleManager.getEnemyField().getReserveCards(), "RESERVE");
        fillSideColumn(enemyGraveyardColumn, battleManager.getEnemyField().getGraveyard(), "GRAVE");
        fillSideColumn(playerReserveColumn, battleManager.getPlayerField().getReserveCards(), "RESERVE");
        fillSideColumn(playerGraveyardColumn, battleManager.getPlayerField().getGraveyard(), "GRAVE");

        enemySummaryLabel.setText(
            "Reserve: " + battleManager.getEnemyField().getReserveCards().size() + "  |  "
                + "Graveyard: " + battleManager.getEnemyField().getGraveyard().size()
        );
        playerSummaryLabel.setText(
            "Reserve: " + battleManager.getPlayerField().getReserveCards().size() + "  |  "
                + "Graveyard: " + battleManager.getPlayerField().getGraveyard().size()
        );
    }

    private CardActor getOrCreateCardActor(Card card) {
        CardActor cached = cardActorCache.get(card);
        if (cached == null) {
            Texture frame = getFrameByRarity(card.getRarity());
            cached = new CardActor(card, skin, true, cardLabelStyle, frame);

            final CardActor actorRef = cached;
            cached.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                    if(button == Input.Buttons.RIGHT){
                        if (card.isDead()) {
                            return false;
                        }

                        if(statsTooltip == null){
                            showStatsTooltip(actorRef, card);
                        }else{
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

    private void fillActiveSlot(Table slot, Card card) {
        slot.clear();

        if (card == null) {
            Label empty = new Label("--", cardLabelStyle);
            empty.setFontScale(0.8f);
            empty.setColor(0.4f, 0.4f, 0.4f, 1f);
            slot.add(empty).size(ACTIVE_CARD_WIDTH, ACTIVE_CARD_HEIGHT);
            return;
        }

        CardActor cardActor = getOrCreateCardActor(card);
        cardActor.clearActions();
        cardActor.setPosition(0, 0);
        cardActor.getColor().set(card.isDead() ? new Color(0.4f, 0.4f, 0.4f, 1f) : Color.WHITE);

        Label hpLabel = new Label(
            card.isDead() ? "[DEFEATED]" : card.getCurrentHp() + "/" + card.getMaxHp()
                                           + (card.getShield() > 0 ? " (+" + card.getShield() + ")" : ""),
            cardLabelStyle
        );
        hpLabel.setFontScale(0.72f);
        hpLabel.setAlignment(Align.center);
        hpLabel.setColor(card.isDead() ? new Color(0.6f, 0.3f, 0.3f, 1f) : Color.WHITE);

        slot.add(cardActor).size(ACTIVE_CARD_WIDTH, ACTIVE_CARD_HEIGHT).row();
        slot.add(hpLabel).padTop(2);
    }

    private static final int MAX_STACK_VISIBLE = 4;

    private void fillSideColumn(Table column, List<Card> cards, String headerText) {
        column.clear();

        boolean isReserve = "RESERVE".equals(headerText);

        Label header = new Label(headerText, cardLabelStyle);
        header.setFontScale(0.7f);
        header.setColor(0.65f, 0.65f, 0.65f, 1f);
        column.add(header).padBottom(4).row();

        if (cards.isEmpty()) {
            Label empty = new Label("--", cardLabelStyle);
            empty.setFontScale(0.7f);
            empty.setColor(0.4f, 0.4f, 0.4f, 1f);
            column.add(empty);
            return;
        }

        int total = cards.size();
        List<Card> visibleCards = total > MAX_STACK_VISIBLE
            ? cards.subList(total - MAX_STACK_VISIBLE, total)
            : cards;

        int overlapPeek = 18;
        for (int i = 0; i < visibleCards.size(); i++) {
            Card card = visibleCards.get(i);
            float topPad = (i == 0) ? 0f : -(SIDE_CARD_HEIGHT - overlapPeek);

            if (isReserve) {
                com.badlogic.gdx.scenes.scene2d.ui.Image cardImage =
                    new com.badlogic.gdx.scenes.scene2d.ui.Image(backFrameCardTexture);
                column.add(cardImage).size(SIDE_CARD_WIDTH, SIDE_CARD_HEIGHT).padTop(topPad).row();
            } else {
                CardActor cachedActor = getOrCreateCardActor(card);
                cachedActor.clearActions();
                cachedActor.setPosition(0, 0);
                cachedActor.getColor().a = 0.55f;
                column.add(cachedActor).size(SIDE_CARD_WIDTH, SIDE_CARD_HEIGHT).padTop(topPad).row();
            }
        }

        if (total > MAX_STACK_VISIBLE) {
            Label moreLabel = new Label("+" + (total - MAX_STACK_VISIBLE) + " more", cardLabelStyle);
            moreLabel.setFontScale(0.6f);
            moreLabel.setColor(0.6f, 0.6f, 0.6f, 1f);
            column.add(moreLabel).padTop(6).row();
        }
    }

    private Texture getFrameByRarity(Rarity rarity) {
        if (rarity == null) {
            return commonFrame;
        }
        switch (rarity) {
            case RARE:
                return rareFrame;
            case EPIC:
                return epicFrame;
            case LEGENDARY:
                return legendaryFrame;
            default:
                return commonFrame;
        }
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
        if (battleManager.isBattleOver()) {
            return;
        }

        java.util.Map<Card, int[]> hpShieldBefore = captureHpShieldSnapshot();

        String log = battleManager.processSingleAction();

        Card animAttacker = battleManager.wasLastActionBasicAttack() ? battleManager.getLastAttacker() : null;
        Card animTarget = battleManager.wasLastActionBasicAttack() ? battleManager.getLastTarget() : null;
        CardActor attackerActor = (animAttacker != null) ? cardActorCache.get(animAttacker) : null;
        CardActor targetActor = (animTarget != null) ? cardActorCache.get(animTarget) : null;

        com.badlogic.gdx.math.Vector2 attackerPos = null;
        com.badlogic.gdx.math.Vector2 targetPos = null;
        if (attackerActor != null && targetActor != null) {
            attackerPos = attackerActor.localToStageCoordinates(
                new com.badlogic.gdx.math.Vector2(attackerActor.getWidth() / 2f, attackerActor.getHeight() / 2f));
            targetPos = targetActor.localToStageCoordinates(
                new com.badlogic.gdx.math.Vector2(targetActor.getWidth() / 2f, targetActor.getHeight() / 2f));
        }

        announce(log);

        refreshBattlefieldPanel();
        refreshTurnOrderPanel();

        showFloatingNumbers(hpShieldBefore);

        boolean canAnimate = attackerPos != null && targetPos != null
            && attackerActor.getStage() != null
            && animAttacker != null && !animAttacker.isDead()
            && animTarget != null && !animTarget.isDead();

        if (canAnimate) {
            playLungeAnimation(attackerActor, targetActor, attackerPos, targetPos);
        }

        if (battleManager.isBattleOver() && !resultDialogShown) {
            resultDialogShown = true;
            showBattleResultDialog();
        }
    }

    private void playLungeAnimation(CardActor attackerActor, CardActor targetActor,
                                    com.badlogic.gdx.math.Vector2 attackerPos, com.badlogic.gdx.math.Vector2 targetPos) {
        float dx = (targetPos.x - attackerPos.x) * 0.7f;
        float dy = (targetPos.y - attackerPos.y) * 0.7f;

        attackerActor.clearActions();
        attackerActor.addAction(Actions.sequence(
            Actions.moveBy(dx, dy, 0.20f, com.badlogic.gdx.math.Interpolation.pow2Out),
            Actions.delay(0.08f), // jeda "impact", biar kerasa nubruk beneran, bukan langsung mental balik
            Actions.moveBy(-dx, -dy, 0.18f, com.badlogic.gdx.math.Interpolation.pow2In)
        ));

        // Target kena shake + flash merah, ngasih feedback kalau dia beneran kena hit.
        // Delay 0.20f biar shake-nya kejadian PAS attacker nyampe (nyusul durasi moveBy di atas).
        targetActor.clearActions();
        targetActor.addAction(Actions.sequence(
            Actions.delay(0.20f),
            Actions.parallel(
                Actions.sequence(
                    Actions.moveBy(6, 0, 0.03f),
                    Actions.moveBy(-12, 0, 0.05f),
                    Actions.moveBy(6, 0, 0.03f)
                ),
                Actions.sequence(
                    Actions.color(Color.RED, 0.05f),
                    Actions.color(Color.WHITE, 0.12f)
                )
            )
        ));
    }

    private java.util.Map<Card, int[]> captureHpShieldSnapshot() {
        java.util.Map<Card, int[]> snapshot = new java.util.IdentityHashMap<>();
        for (Card c : battleManager.getPlayerField().getActiveCards()) {
            if (c != null) {
                snapshot.put(c, new int[]{c.getCurrentHp(), c.getShield()});
            }
        }
        for (Card c : battleManager.getEnemyField().getActiveCards()) {
            if (c != null) {
                snapshot.put(c, new int[]{c.getCurrentHp(), c.getShield()});
            }
        }
        return snapshot;
    }

    private void showFloatingNumbers(java.util.Map<Card, int[]> before) {
        for (java.util.Map.Entry<Card, int[]> entry : before.entrySet()) {
            Card card = entry.getKey();
            int hpDelta = card.getCurrentHp() - entry.getValue()[0];
            int shieldDelta = card.getShield() - entry.getValue()[1];

            if (hpDelta == 0 && shieldDelta == 0) {
                continue;
            }

            CardActor actor = cardActorCache.get(card);
            if (actor == null || actor.getStage() == null) {
                continue;
            }

            String text;
            Color color;
            if (hpDelta != 0) {
                text = (hpDelta > 0 ? "+" : "") + hpDelta;
                color = hpDelta > 0 ? new Color(0.4f, 1f, 0.4f, 1f) : new Color(1f, 0.35f, 0.35f, 1f);
            } else {
                text = (shieldDelta > 0 ? "+" : "") + shieldDelta + " Shield";
                color = new Color(0.5f, 0.75f, 1f, 1f);
            }

            spawnFloatingNumber(actor, text, color);
        }
    }

    private void spawnFloatingNumber(CardActor sourceActor, String text, Color color) {
        Label numberLabel = new Label(text, cardLabelStyle);
        numberLabel.setColor(color);
        numberLabel.setFontScale(1.1f);

        com.badlogic.gdx.math.Vector2 pos = sourceActor.localToStageCoordinates(
            new com.badlogic.gdx.math.Vector2(sourceActor.getWidth() / 2f, sourceActor.getHeight()));

        numberLabel.setPosition(pos.x - 15, pos.y);
        stage.addActor(numberLabel);

        numberLabel.addAction(Actions.sequence(
            Actions.parallel(
                Actions.moveBy(0, 30, 0.8f, com.badlogic.gdx.math.Interpolation.pow2Out),
                Actions.sequence(Actions.delay(0.3f), Actions.fadeOut(0.5f))
            ),
            Actions.removeActor()
        ));
    }

    private void showBattleResultDialog() {

        MusicManager.stop();
        if (battleManager.isPlayerWin()) {
            MusicManager.play("sound/background_music/victory_sound.mp3", true, game.getMusicVolume());
        } else {
            MusicManager.play("sound/background_music/defeat_sound.mp3", true, game.getMusicVolume());
        }

        String title = battleManager.isPlayerWin() ? "VICTORY" : "DEFEAT";

        String message = battleManager.isPlayerWin() ? "You have defeated the enemy." : "Your team has fallen.";

        com.badlogic.gdx.scenes.scene2d.ui.Dialog dialog =
            new com.badlogic.gdx.scenes.scene2d.ui.Dialog(title, skin){
                @Override
                protected void result(Object object){
                    ProgressionManager.BattleRewards rewards =
                        ProgressionManager.processBattleRewards(game.getPlayer(), battleManager.isPlayerWin());
                    game.setScreen(new RewardScreen(game, rewards));
                }
            };

        dialog.text(message);
        dialog.button("Continue");
        dialog.key(Input.Keys.ENTER, true);

        dialog.show(stage);
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

    private void announce(String text) {
        if (text == null || text.isBlank() || announceLabel == null) {
            return;
        }
        announceLabel.setText(text);
        announcePanel.clearActions();
        announcePanel.getColor().a = 0f;
        announcePanel.addAction(Actions.sequence(
            Actions.fadeIn(0.15f),
            Actions.delay(1.0f),
            Actions.fadeOut(0.5f)
        ));
    }

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

        Label statsLabel = new Label(statsText, cardLabelStyle);
        statsLabel.setFontScale(0.85f);
        statsLabel.setWrap(true);

        com.badlogic.gdx.scenes.scene2d.ui.Container<Label> content = new com.badlogic.gdx.scenes.scene2d.ui.Container<>(statsLabel);
        content.pad(10);
        content.fill();
        content.width(260);
        content.setBackground(new TextureRegionDrawable(solidPixel).tint(new Color(0f, 0f, 0f, 0.88f)));

        statsTooltip = new Table();
        statsTooltip.add(content);
        statsTooltip.pack();

        com.badlogic.gdx.math.Vector2 pos = sourceActor.localToStageCoordinates(new com.badlogic.gdx.math.Vector2(0, 0));
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
            showPauseDialog();
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!paused && !battleManager.isBattleOver()) {
            BattleSpeed speed = game.getBattleSpeed();

            if (speed == BattleSpeed.INSTANT) {
                resolveRemainingActionsInstantly();
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
    @Override
    public void hide() {
        MusicManager.stop();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (titleFont != null) titleFont.dispose();
        if (uiFont != null) uiFont.dispose();
        if (commonFrame != null) commonFrame.dispose();
        if (rareFrame != null) rareFrame.dispose();
        if (epicFrame != null) epicFrame.dispose();
        if (legendaryFrame != null) legendaryFrame.dispose();
        if (specialFrame != null) specialFrame.dispose();
        if (backFrameCardTexture != null) backFrameCardTexture.dispose();
        if (solidPixel != null) solidPixel.dispose();

        for (CardActor actor : cardActorCache.values()) {
            actor.dispose();
        }
        cardActorCache.clear();
    }
}
