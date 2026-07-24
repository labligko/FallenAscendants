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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.fallenascendants.FallenAscendantsGame;
import com.fallenascendants.model.Card;
import com.fallenascendants.model.Player;
import com.fallenascendants.screen.upgrade.upgradeScreen;

public class CardDetailScreen implements Screen {
    private final FallenAscendantsGame game;
    private Card card; // Bukan final agar bisa di-update ke referensi kartu resmi milik player
    private final boolean isOwned;

    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;
    private Texture statsBackgroundTexture; // Tekstur Kertas Gulungan

    private BitmapFont titleFont;
    private BitmapFont detailFont;
    private Label.LabelStyle infoStyle;

    private Texture cardFrameTexture;

    private Table mainTable;

    // Aset tekstur tombol kustom dari MainMenu
    private Texture buttonNormal;
    private Texture buttonHover;
    private Texture buttonPressed;
    private TextButton.TextButtonStyle customButtonStyle;

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

        // =========================================================================
        // SINKRONISASI OBJEK KARTU DENGAN INVENTORY PLAYER (LEVEL & STATS AKURAT)
        // =========================================================================
        Player player = game.getPlayer();
        if (player != null && player.getCollection() != null) {
            for (Card c : player.getCollection()) {
                if (c.getId().equals(this.card.getId())) {
                    this.card = c; // Mengarahkan ke referensi kartu asli pemain
                    break;
                }
            }
        }

        backgroundTexture = new Texture(Gdx.files.internal("background/background_lobby/UpgradeBackground.png"));
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Image bg = new Image(backgroundTexture);
        bg.setSize(1280, 720);
        stage.addActor(bg);

        // Load Background Stats Kertas Gulungan dengan Linear Filtering
        statsBackgroundTexture = new Texture(Gdx.files.internal("card_frames/statsBackground.png"));
        statsBackgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // --- LOGIKA AMBIL BINGKAI BERDASARKAN RARITY ---
        String rarityStr = "common";
        if (card.getRarity() != null) {
            rarityStr = card.getRarity().name().toLowerCase();
        }

        String framePath = "card_frames/commonFrame.png"; // Default awal
        switch (rarityStr) {
            case "rare":      framePath = "card_frames/rareFrame.png"; break;
            case "epic":      framePath = "card_frames/epicFrame.png"; break;
            case "legendary": framePath = "card_frames/legendaryFrame.png"; break;
            case "special":
            case "limited":   framePath = "card_frames/specialFrame.png"; break;
        }

        cardFrameTexture = new Texture(Gdx.files.internal(framePath));
        cardFrameTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        buttonNormal = new Texture(Gdx.files.internal("Button/PrimaryButton.png"));
        buttonNormal.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        buttonHover = new Texture(Gdx.files.internal("Button/HoverButton.png"));
        buttonHover.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        buttonPressed = new Texture(Gdx.files.internal("Button/PressedButton.png"));
        buttonPressed.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().pad(25);
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

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/CinzelDecorative-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        // Menggunakan teknik Supersampling Font agar teks Ultra HD Sharp
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;

        // 1. Font Judul Kartu Besar
        parameter.size = 56;
        parameter.color = new Color(0.95f, 0.85f, 0.65f, 1f);
        parameter.borderWidth = 3f;
        parameter.borderColor = Color.BLACK;
        titleFont = generator.generateFont(parameter);
        titleFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        titleFont.getData().setScale(0.5f);

        // 2. Font Deskripsi & Tombol
        parameter.size = 32;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 2f;
        parameter.borderColor = Color.BLACK;
        detailFont = generator.generateFont(parameter);
        detailFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        detailFont.getData().setScale(0.5f);

        generator.dispose();

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        infoStyle = new Label.LabelStyle(detailFont, Color.WHITE);

        mainTable.clearChildren();

        // Racik ulang gaya tombol kustom
        customButtonStyle = new TextButton.TextButtonStyle();
        customButtonStyle.up = new TextureRegionDrawable(buttonNormal);
        customButtonStyle.over = new TextureRegionDrawable(buttonHover);
        customButtonStyle.down = new TextureRegionDrawable(buttonPressed);
        customButtonStyle.font = detailFont;
        customButtonStyle.fontColor = Color.WHITE;
        customButtonStyle.overFontColor = new Color(0.9f, 0.8f, 0.6f, 1f);
        customButtonStyle.pressedOffsetX = 1;
        customButtonStyle.pressedOffsetY = -1;

        // Baris Atas: Tombol Kembali
        TextButton backButton = new TextButton("< Back to Collection", customButtonStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new CollectionScreen(game));
            }
        });
        mainTable.add(backButton).left().size(220, 42).padBottom(15).row();

        // Kontainer Utama Pembagi Kiri (Gambar) & Kanan (Statistik)
        Table contentTable = new Table();
        contentTable.center();

        // 1. SISI KIRI (Gambar Kartu & Tombol Upgrade)
        Table leftGroup = new Table();
        leftGroup.top();

        CardActor largeCardView = new CardActor(card, skin, isOwned, infoStyle, cardFrameTexture);
        leftGroup.add(largeCardView).size(300, 433).padBottom(20).row();

        TextButton upgradeButton = new TextButton("Upgrade Card", customButtonStyle);
        upgradeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new upgradeScreen(game, card, isOwned));
            }
        });

        if (!isOwned) {
            upgradeButton.setDisabled(true);
        }

        leftGroup.add(upgradeButton).size(180, 42).row();
        contentTable.add(leftGroup).size(300, 500).top().padRight(40);

        // 2. SISI KANAN: Panel Informasi Ber-Background Kertas Gulungan
        Table statsPanel = new Table();

        TextureRegionDrawable panelBg = new TextureRegionDrawable(statsBackgroundTexture);
        statsPanel.setBackground(panelBg);

        // Padding Presisi
        statsPanel.pad(120, 110, 75, 70);
        statsPanel.top().left();

        Label nameLabel = new Label(card.getName().toUpperCase(), titleStyle);

        Label statusLabel = new Label(isOwned ? "[ STATUS: OWNED ]" : "[ STATUS: NOT OWNED ]", infoStyle);
        statusLabel.setColor(isOwned ? Color.GREEN : Color.RED);

        // Tambahkan Judul & Status di atas
        statsPanel.add(nameLabel).colspan(2).left().padBottom(2).row();
        statsPanel.add(statusLabel).colspan(2).left().padBottom(12).row();

        // Tambahkan Baris Atribut (Membaca data level dan statistik yang sudah ter-sync)
        addDetailRow(statsPanel, "RARITY", card.getRarity() != null ? card.getRarity().toString() : "-", infoStyle);
        addDetailRow(statsPanel, "FACTION", card.getFaction() != null ? card.getFaction().toString() : "-", infoStyle);
        addDetailRow(statsPanel, "ROLE", card.getRole() != null ? card.getRole().toString() : "-", infoStyle);
        addDetailRow(statsPanel, "MAX HEALTH", String.valueOf(card.getMaxHp()), infoStyle);
        addDetailRow(statsPanel, "ATTACK POWER", String.valueOf(card.getAtk()), infoStyle);
        addDetailRow(statsPanel, "DEFENSE RATE", String.valueOf(card.getDef()), infoStyle);
        addDetailRow(statsPanel, "SPEED ACTION", String.valueOf(card.getSpd()), infoStyle);
        addDetailRow(statsPanel, "AGGRO WEIGHT", String.valueOf(card.getAggro()), infoStyle);

        contentTable.add(statsPanel).size(690, 610).top().left();
        mainTable.add(contentTable).expand().fill();
    }

    private void addDetailRow(Table table, String title, String value, Label.LabelStyle style) {
        Label titleLbl = new Label(title, style);
        Label valLbl = new Label(":  " + value, style);

        table.add(titleLbl).width(160).left().padBottom(5);
        table.add(valLbl).left().padBottom(5).row();
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
        if (statsBackgroundTexture != null) statsBackgroundTexture.dispose();
        if (cardFrameTexture != null) cardFrameTexture.dispose();
        if (buttonNormal != null) buttonNormal.dispose();
        if (buttonHover != null) buttonHover.dispose();
        if (buttonPressed != null) buttonPressed.dispose();
        if (titleFont != null) titleFont.dispose();
        if (detailFont != null) detailFont.dispose();
    }
}
