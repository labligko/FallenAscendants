package com.fallenascendants.screen.card;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.fallenascendants.FallenAscendantsGame;
import com.fallenascendants.model.Card;

public class CardDetailScreen implements Screen {
    private final FallenAscendantsGame game;
    private final Card card;
    private final boolean isOwned;

    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;

    private BitmapFont titleFont;
    private BitmapFont detailFont;
    private Label.LabelStyle infoStyle;

    private Texture cardFrameTexture;

    private Table mainTable;

    public CardDetailScreen(FallenAscendantsGame game, Card card, boolean isOwned) {
        this.game = game;
        this.card = card;
        this.isOwned = isOwned;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        backgroundTexture = new Texture(Gdx.files.internal("background_lobby/UpgradeBackground.png"));
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Image bg = new Image(backgroundTexture);
        bg.setSize(1280, 720);
        stage.addActor(bg);

        // --- MULAI LOGIKA AMBIL BINGKAI BERDASARKAN RARITY ---
        String rarityStr = "common";
        if (card.getRarity() != null) {
            rarityStr = card.getRarity().name().toLowerCase();
        }

        String framePath = "commonFrame.png"; // Default awal
        switch (rarityStr) {
            case "rare":      framePath = "rareFrame.png"; break;
            case "epic":      framePath = "epicFrame.png"; break;
            case "legendary": framePath = "legendaryFrame.png"; break;
            case "special":
            case "limited":   framePath = "specialFrame.png"; break;
        }

        cardFrameTexture = new Texture(Gdx.files.internal(framePath));
        cardFrameTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        // --- AKHIR LOGIKA BINGKAI ---

        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().pad(30);
        stage.addActor(mainTable);

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACKSPACE) {
                    game.setScreen(new CollectionScreen(game));
                    return true;
                }
                return false;
            }
        });
    }

    private void rebuildUI(int width, int height) {
        if (titleFont != null) titleFont.dispose();
        if (detailFont != null) detailFont.dispose();

        float scale = (float) height / 720f;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/CinzelDecorative-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        // Font Judul Kartu Besar
        parameter.size = Math.round(44 * scale);
        parameter.color = new Color(0.9f, 0.8f, 0.6f, 1f);
        parameter.borderWidth = 2.5f * scale;
        parameter.borderColor = Color.BLACK;
        titleFont = generator.generateFont(parameter);

        // Font Deskripsi Statistik Tajam HD (Ukurannya dinaikkan agar keterbacaannya jernih)
        parameter.size = Math.round(18 * scale);
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1.5f * scale;
        parameter.borderColor = Color.BLACK;
        detailFont = generator.generateFont(parameter);
        generator.dispose();

        titleFont.getData().setScale(1f / scale);
        detailFont.getData().setScale(1f / scale);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        infoStyle = new Label.LabelStyle(detailFont, Color.WHITE);

        mainTable.clearChildren();

        // Baris Atas: Tombol Kembali
        TextButton backButton = new TextButton("< Back to Collection", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new CollectionScreen(game));
            }
        });
        mainTable.add(backButton).left().padBottom(30).row();

        // Kontainer Pembagi Kiri (Gambar) & Kanan (Statistik)
        Table contentTable = new Table();
        contentTable.center();

        // SISI KIRI: Render Kartu Ukuran Besar ( HD Tanpa Kompresi )
        CardActor largeCardView = new CardActor(card, skin, isOwned, infoStyle, cardFrameTexture);
        contentTable.add(largeCardView).size(300, 433).padRight(60).top();

        // SISI KANAN: Panel Informasi Komplit RPG
        Table statsPanel = new Table();
        statsPanel.top().left();

        Label nameLabel = new Label(card.getName().toUpperCase(), titleStyle);

        Label statusLabel = new Label(isOwned ? "[ STATUS: OWNED ]" : "[ STATUS: NOT OWNED ]", infoStyle);
        statusLabel.setColor(isOwned ? Color.GREEN : Color.RED);

        Label rarityLabel = new Label("RARITY       : " + card.getRarity(), infoStyle);
        Label factionLabel = new Label("FACTION      : " + card.getFaction(), infoStyle);
        Label roleLabel = new Label("ROLE         : " + card.getRole(), infoStyle);

        Label hpLabel = new Label("MAX HEALTH   : " + card.getMaxHp(), infoStyle);
        Label atkLabel = new Label("ATTACK POWER : " + card.getAtk(), infoStyle);
        Label defLabel = new Label("DEFENSE RATE : " + card.getDef(), infoStyle);
        Label spdLabel = new Label("SPEED ACTION : " + card.getSpd(), infoStyle);
        Label aggroLabel = new Label("AGGRO WEIGHT : " + card.getAggro(), infoStyle);

        statsPanel.add(nameLabel).left().padBottom(10).row();
        statsPanel.add(statusLabel).left().padBottom(25).row();

        statsPanel.add(rarityLabel).left().padBottom(10).row();
        statsTableLayout(statsPanel, factionLabel, roleLabel, hpLabel, atkLabel, defLabel, spdLabel, aggroLabel);

        contentTable.add(statsPanel).top().left();
        mainTable.add(contentTable).expand().fill();
    }

    private void statsTableLayout(Table statsPanel, Label factionLabel, Label roleLabel, Label hpLabel, Label atkLabel, Label defLabel, Label spdLabel, Label aggroLabel) {
        statsPanel.add(factionLabel).left().padBottom(10).row();
        statsPanel.add(roleLabel).left().padBottom(25).row();
        statsPanel.add(hpLabel).left().padBottom(10).row();
        statsPanel.add(atkLabel).left().padBottom(10).row();
        statsPanel.add(defLabel).left().padBottom(10).row();
        statsPanel.add(spdLabel).left().padBottom(10).row();
        statsPanel.add(aggroLabel).left().padBottom(10).row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.07f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        rebuildUI(width, height);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (cardFrameTexture != null) cardFrameTexture.dispose();
        if (titleFont != null) titleFont.dispose();
        if (detailFont != null) detailFont.dispose();
    }
}
