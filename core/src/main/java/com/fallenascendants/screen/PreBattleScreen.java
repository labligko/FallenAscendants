package com.fallenascendants.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.fallenascendants.FallenAscendantsGame;
import com.fallenascendants.audio.MusicManager;
import com.fallenascendants.audio.SFXManager;
import com.fallenascendants.battle.BattleManager;
import com.fallenascendants.data.DummyBattleFactory;
import com.fallenascendants.enumtype.Rarity;
import com.fallenascendants.model.BattleField;
import com.fallenascendants.model.Card;
import com.fallenascendants.model.Deck;
import com.fallenascendants.screen.card.CardActor;

public class PreBattleScreen implements Screen {
    private static final int ACTIVE_CARD_WIDTH = 146;
    private static final int ACTIVE_CARD_HEIGHT = 210;
    private static final int RESERVE_CARD_WIDTH = 105;
    private static final int RESERVE_CARD_HEIGHT = 150;

    private final FallenAscendantsGame game;
    private Stage stage;
    private Skin skin;

    private Texture backgroundTexture;
    private BitmapFont titleFont;
    private BitmapFont uiFont;
    private Label.LabelStyle cardLabelStyle;
    private Label.LabelStyle goldTitleStyle;

    private Texture commonFrame, rareFrame, epicFrame, legendaryFrame, specialFrame;
    private Texture solidPixel;
    private Texture tooltipPanelTexture;

    private Deck playerDeck;
    private Deck enemyDeck;

    private Label hintLabel;
    private float blinkTime = 0f;
    private Table activeTooltip;

    private final java.util.List<CardActor> createdCardActors = new java.util.ArrayList<>();

    public PreBattleScreen(FallenAscendantsGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        MusicManager.play("sound/background_music/prebattle_show.mp3", false, game.getMusicVolume());

        FullscreenToggle.attach(stage);

        backgroundTexture = new Texture(Gdx.files.internal("background/background_lobby/BattleBackground.png"));
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setSize(1280, 720);
        stage.addActor(backgroundImage);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1f, 1f, 1f, 1f);
        pixmap.fill();
        solidPixel = new Texture(pixmap);
        pixmap.dispose();

        Image fogOverlay = new Image(solidPixel);
        fogOverlay.setSize(1280, 720);

        fogOverlay.setColor(0f, 0f, 0f, 0.55f);
        stage.addActor(fogOverlay);

        tooltipPanelTexture = new Texture(Gdx.files.internal("Panel/LargePanel.png"));
        tooltipPanelTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/CinzelDecorative-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;

        parameter.size = 28;
        parameter.color = new Color(0.9f, 0.8f, 0.6f, 1f);
        parameter.borderWidth = 2;
        parameter.borderColor = Color.BLACK;
        titleFont = generator.generateFont(parameter);

        parameter.size = 13;
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

        playerDeck = game.getPlayer().getDeck();
        enemyDeck = DummyBattleFactory.createRandomEnemyDeck(playerDeck.size());

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Label title = new Label("CONFIRM BATTLE", goldTitleStyle);
        title.setFontScale(0.85f);

        Table arena = new Table();
        arena.add(buildDeckSide(playerDeck, true)).top().padRight(30);
        arena.add(buildDeckSide(enemyDeck, false)).top();

        hintLabel = new Label("[ENTER] Fight   |   [ESC] Back", cardLabelStyle);
        hintLabel.setFontScale(0.9f);
        hintLabel.setColor(0.75f, 0.75f, 0.75f, 1f);

        root.add(title).padTop(8).padBottom(8).row();
        root.add(arena).padBottom(10).row();
        root.add(hintLabel).padBottom(8).row();

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    SFXManager.play("sound/sound_effect/clickbutton.mp3", game.getSfxVolume());
                    startBattle();
                    return true;
                }
                if (keycode == Input.Keys.ESCAPE) {
                    SFXManager.play("sound/sound_effect/clickbutton.mp3", game.getSfxVolume());
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        });
    }

    private void startBattle() {
        BattleField playerField = new BattleField(playerDeck);
        BattleField enemyField = new BattleField(enemyDeck);
        BattleManager battleManager = new BattleManager(playerField, enemyField);

        game.setScreen(new BattleScreen(game, battleManager));
    }

    private Table buildDeckSide(Deck deck, boolean isPlayerSide) {
        Table side = new Table();

        Label header = new Label(isPlayerSide ? "YOUR DECK" : "ENEMY DECK", goldTitleStyle);
        header.setFontScale(0.45f);
        side.add(header).align(isPlayerSide ? Align.left : Align.right).padBottom(6).row();

        Table activeGrid = new Table();
        int activeCount = Math.min(5, deck.size());
        int columns = 3;
        int rows = (int) Math.ceil(activeCount / (double) columns);
        int cardIndex = 0;

        for (int r = 0; r < rows; r++) {
            int cardsInThisRow = Math.min(columns, activeCount - cardIndex);

            if (!isPlayerSide && cardsInThisRow < columns) {
                int fillerCount = columns - cardsInThisRow;
                for (int f = 0; f < fillerCount; f++) {
                    activeGrid.add().size(ACTIVE_CARD_WIDTH, ACTIVE_CARD_HEIGHT).pad(2);
                }
            }

            for (int c = 0; c < cardsInThisRow; c++) {
                activeGrid.add(buildCardTile(deck.getCard(cardIndex), isPlayerSide, ACTIVE_CARD_WIDTH, ACTIVE_CARD_HEIGHT))
                    .size(ACTIVE_CARD_WIDTH, ACTIVE_CARD_HEIGHT).pad(2);
                cardIndex++;
            }

            activeGrid.row();
        }
        side.add(activeGrid).align(isPlayerSide ? Align.left : Align.right).padBottom(6).row();

        if (deck.size() > 5) {
            Table reserveGrid = new Table();
            for (int i = 5; i < deck.size(); i++) {
                reserveGrid.add(buildCardTile(deck.getCard(i), isPlayerSide, RESERVE_CARD_WIDTH, RESERVE_CARD_HEIGHT))
                    .size(RESERVE_CARD_WIDTH, RESERVE_CARD_HEIGHT).pad(2);
            }
            side.add(reserveGrid).align(isPlayerSide ? Align.left : Align.right).row();
        }

        return side;
    }

    private CardActor buildCardTile(Card card, boolean isPlayerSide, int width, int height) {
        Texture frame = getFrameByRarity(card.getRarity());
        CardActor cardActor = new CardActor(card, skin, true, cardLabelStyle, frame);
        createdCardActors.add(cardActor);

        cardActor.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    SFXManager.play("sound/sound_effect/hoverbutton.mp3", game.getSfxVolume());
                }
                showStatsTooltip(cardActor, card, isPlayerSide, width);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                hideStatsTooltip();
            }
        });

        return cardActor;
    }

    private void showStatsTooltip(CardActor cardActor, Card card, boolean isPlayerSide, int cardWidth) {
        hideStatsTooltip();

        String activeSkillName = card.getActiveSkill() == null ? "None" : card.getActiveSkill().getName();
        String statsText = card.getName() + "\n"
            + card.getRole() + " | " + card.getRarity() + " | Lvl " + card.getLevel() + "\n"
            + "HP:" + card.getMaxHp() + " ATK:" + card.getAtk() + " DEF:" + card.getDef() + "\n"
            + "SPD:" + card.getSpd() + " Aggro:" + card.getAggro() + "\n"
            + "Skill: " + activeSkillName;

        Label statsLabel = new Label(statsText, cardLabelStyle);
        statsLabel.setFontScale(0.85f);
        statsLabel.setWrap(true);
        statsLabel.setAlignment(Align.left);

        activeTooltip = new Table();
        TextureRegionDrawable panelBg = new TextureRegionDrawable(tooltipPanelTexture);
        panelBg.setMinWidth(0);
        panelBg.setMinHeight(0);
        activeTooltip.setBackground(panelBg);
        activeTooltip.add(statsLabel).width(230).padLeft(55).padRight(40).padTop(35).padBottom(30);
        activeTooltip.pack();

        Vector2 cardPos = cardActor.localToStageCoordinates(new Vector2(0, 0));
        float idealX = isPlayerSide ? (cardPos.x + cardWidth + 10) : (cardPos.x - activeTooltip.getWidth() - 10);
        float clampedX = Math.max(10f, Math.min(idealX, 1280 - activeTooltip.getWidth() - 10));
        float tooltipY = Math.max(10f, Math.min(cardPos.y, 720 - activeTooltip.getHeight() - 10));

        activeTooltip.setPosition(clampedX, tooltipY);
        stage.addActor(activeTooltip);
    }

    private void hideStatsTooltip() {
        if (activeTooltip != null) {
            activeTooltip.remove();
            activeTooltip = null;
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

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.04f, 0.07f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (hintLabel != null) {
            blinkTime += delta * 4;
            float alpha = (float) (Math.sin(blinkTime) + 1) / 2f;
            Color color = hintLabel.getColor();
            hintLabel.setColor(color.r, color.g, color.b, alpha);
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
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (titleFont != null) titleFont.dispose();
        if (uiFont != null) uiFont.dispose();
        if (solidPixel != null) solidPixel.dispose();
        if (commonFrame != null) commonFrame.dispose();
        if (rareFrame != null) rareFrame.dispose();
        if (epicFrame != null) epicFrame.dispose();
        if (legendaryFrame != null) legendaryFrame.dispose();
        if (specialFrame != null) specialFrame.dispose();

        if (tooltipPanelTexture != null) tooltipPanelTexture.dispose();

        for (CardActor actor : createdCardActors) {
            actor.dispose();
        }
        createdCardActors.clear();
    }
}
