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
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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
    private BitmapFont headerFont;
    private BitmapFont cardFont;
    private BitmapFont reserveCardFont;
    private BitmapFont tooltipFont;

    private Label.LabelStyle cardLabelStyle;
    private Label.LabelStyle reserveCardLabelStyle;
    private Label.LabelStyle goldTitleStyle;
    private Label.LabelStyle headerStyle;
    private Label.LabelStyle tooltipStyle;

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

        FullscreenToggle.attach(stage);

        // 1. BACKGROUND
        backgroundTexture = new Texture(Gdx.files.internal("background/background_lobby/BattleBackground.png"));
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setSize(1280, 720);
        stage.addActor(backgroundImage);

        // 2. SETUP TEXTURE PIXEL UNTUK FOG
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1f, 1f, 1f, 1f);
        pixmap.fill();
        solidPixel = new Texture(pixmap);
        pixmap.dispose();

        tooltipPanelTexture = new Texture(Gdx.files.internal("Panel/LargePanel.png"));
        tooltipPanelTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // --- RACIK SELURUH FONT HD & RESERVED FONT ---
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/CinzelDecorative-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;

        parameter.size = 28;
        parameter.color = new Color(0.9f, 0.8f, 0.6f, 1f);
        parameter.borderWidth = 2;
        parameter.borderColor = Color.BLACK;
        titleFont = generator.generateFont(parameter);
        titleFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        parameter.size = 14;
        parameter.color = new Color(0.9f, 0.8f, 0.6f, 1f);
        parameter.borderWidth = 1f;
        parameter.borderColor = Color.BLACK;
        headerFont = generator.generateFont(parameter);
        headerFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        parameter.size = 28;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1.5f;
        parameter.borderColor = Color.BLACK;
        cardFont = generator.generateFont(parameter);
        cardFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        cardFont.getData().setScale(0.5f);

        reserveCardFont = generator.generateFont(parameter);
        reserveCardFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        reserveCardFont.getData().setScale(0.35f);

        parameter.size = 14;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1f;
        parameter.borderColor = Color.BLACK;
        tooltipFont = generator.generateFont(parameter);
        tooltipFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        generator.dispose();

        goldTitleStyle = new Label.LabelStyle(titleFont, new Color(0.9f, 0.8f, 0.6f, 1f));
        headerStyle = new Label.LabelStyle(headerFont, new Color(0.9f, 0.8f, 0.6f, 1f));
        cardLabelStyle = new Label.LabelStyle(cardFont, Color.WHITE);
        reserveCardLabelStyle = new Label.LabelStyle(reserveCardFont, Color.WHITE);
        tooltipStyle = new Label.LabelStyle(tooltipFont, Color.WHITE);

        commonFrame = new Texture(Gdx.files.internal("card_frames/commonFrame.png"));
        rareFrame = new Texture(Gdx.files.internal("card_frames/rareFrame.png"));
        epicFrame = new Texture(Gdx.files.internal("card_frames/epicFrame.png"));
        legendaryFrame = new Texture(Gdx.files.internal("card_frames/legendaryFrame.png"));
        specialFrame = new Texture(Gdx.files.internal("card_frames/specialFrame.png"));

        playerDeck = game.getPlayer().getDeck();
        enemyDeck = DummyBattleFactory.createRandomEnemyDeck(playerDeck.size());

        // 3. SETUP UI UTAMA (KARTU DLL)
        Table root = new Table();
        root.setFillParent(true);
        // Buat root transparan (tidak terlihat) di awal
        root.getColor().a = 0f;
        stage.addActor(root); // Tambahkan root DULUAN agar posisinya di bawah Fog

        Label title = new Label("CONFIRM BATTLE", goldTitleStyle);

        Table arena = new Table();
        arena.add(buildDeckSide(playerDeck, true)).top().padRight(30);
        arena.add(buildDeckSide(enemyDeck, false)).top();

        hintLabel = new Label("[ENTER] Fight   |   [ESC] Back", tooltipStyle);
        hintLabel.setColor(0.85f, 0.85f, 0.85f, 1f);

        root.add(title).padTop(8).padBottom(8).row();
        root.add(arena).padBottom(10).row();
        root.add(hintLabel).padBottom(8).row();

        // 4. SETUP KABUT (FOG) KIRI & KANAN
        Image leftFog = new Image(solidPixel);
        leftFog.setSize(640, 720);
        leftFog.setPosition(0, 0);
        leftFog.setColor(0f, 0f, 0f, 1f); // Warna hitam pekat

        Image rightFog = new Image(solidPixel);
        rightFog.setSize(640, 720);
        rightFog.setPosition(640, 0);
        rightFog.setColor(0f, 0f, 0f, 1f); // Warna hitam pekat

        stage.addActor(leftFog);
        stage.addActor(rightFog);
        
        // 5. BLOK ANIMASI (FOG REVEAL & FADE IN UI)
        float startDelay = 0.5f; // Jeda sebelum animasi dimulai
        float fogDuration = 1.2f; // Durasi kabut membuka (1.2 detik)

        // Animasi Fog Kiri bergeser ke kiri (-640px)
        leftFog.addAction(Actions.sequence(
            Actions.delay(startDelay),
            Actions.parallel(
                Actions.moveBy(-640, 0, fogDuration, Interpolation.pow2Out),
                Actions.fadeOut(fogDuration)
            ),
            Actions.removeActor()
        ));

        // Animasi Fog Kanan bergeser ke kanan (+640px)
        rightFog.addAction(Actions.sequence(
            Actions.delay(startDelay),
            Actions.parallel(
                Actions.moveBy(640, 0, fogDuration, Interpolation.pow2Out),
                Actions.fadeOut(fogDuration)
            ),
            Actions.removeActor()
        ));

        // Animasi Musik dan Munculnya UI (Kartu)
        root.addAction(Actions.sequence(
            Actions.delay(startDelay),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    // (Opsional) Putar SFX kabut terbuka jika ada
                    // SFXManager.play("sound/sound_effect/whoosh.mp3", game.getSfxVolume());
                }
            }),
            Actions.delay(fogDuration * 0.3f), // Tunggu sebentar saat kabut mulai terbuka
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    // Putar lagu PreBattle
                    MusicManager.play("sound/background_music/prebattle.mp3", false, game.getMusicVolume());
                }
            }),
            Actions.fadeIn(0.8f) // Fade in kartu pelan-pelan selama 0.8 detik
        ));

        // 6. SETUP INPUT LISTENER
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                // Cegah spam input saat animasi kartu masih fade-in (masih tersembunyi)
                if (root.getColor().a < 0.8f) return false;

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

        Label header = new Label(isPlayerSide ? "YOUR DECK" : "ENEMY DECK", headerStyle);
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
                // Kartu Utama memakai cardLabelStyle (Skala 0.5f)
                activeGrid.add(buildCardTile(deck.getCard(cardIndex), isPlayerSide, ACTIVE_CARD_WIDTH, ACTIVE_CARD_HEIGHT, cardLabelStyle))
                    .size(ACTIVE_CARD_WIDTH, ACTIVE_CARD_HEIGHT).pad(2);
                cardIndex++;
            }

            activeGrid.row();
        }
        side.add(activeGrid).align(isPlayerSide ? Align.left : Align.right).padBottom(6).row();

        if (deck.size() > 5) {
            Table reserveGrid = new Table();
            for (int i = 5; i < deck.size(); i++) {
                // Kartu Cadangan memakai reserveCardLabelStyle (Skala 0.35f)
                reserveGrid.add(buildCardTile(deck.getCard(i), isPlayerSide, RESERVE_CARD_WIDTH, RESERVE_CARD_HEIGHT, reserveCardLabelStyle))
                    .size(RESERVE_CARD_WIDTH, RESERVE_CARD_HEIGHT).pad(2);
            }
            side.add(reserveGrid).align(isPlayerSide ? Align.left : Align.right).row();
        }

        return side;
    }

    private CardActor buildCardTile(Card card, boolean isPlayerSide, int width, int height, Label.LabelStyle labelStyle) {
        Texture frame = getFrameByRarity(card.getRarity());
        CardActor cardActor = new CardActor(card, skin, true, labelStyle, frame);
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
            + "HP: " + card.getMaxHp() + "  ATK: " + card.getAtk() + "  DEF: " + card.getDef() + "\n"
            + "SPD: " + card.getSpd() + "  Aggro: " + card.getAggro() + "\n"
            + "Skill: " + activeSkillName;

        Label statsLabel = new Label(statsText, tooltipStyle);
        statsLabel.setWrap(true);
        statsLabel.setAlignment(Align.left);

        activeTooltip = new Table();
        TextureRegionDrawable panelBg = new TextureRegionDrawable(tooltipPanelTexture);
        panelBg.setMinWidth(0);
        panelBg.setMinHeight(0);
        activeTooltip.setBackground(panelBg);

        activeTooltip.add(statsLabel).width(240).padLeft(35).padRight(35).padTop(30).padBottom(30);
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
        if (headerFont != null) headerFont.dispose();
        if (cardFont != null) cardFont.dispose();
        if (reserveCardFont != null) reserveCardFont.dispose();
        if (tooltipFont != null) tooltipFont.dispose();
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
