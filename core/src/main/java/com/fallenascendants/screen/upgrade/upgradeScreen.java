package com.fallenascendants.screen.upgrade;

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
import com.fallenascendants.enumtype.Rarity;
import com.fallenascendants.model.Card;
import com.fallenascendants.model.Player;
import com.fallenascendants.screen.FullscreenToggle;
import com.fallenascendants.screen.card.CardActor;
import com.fallenascendants.screen.card.CardDetailScreen;

public class upgradeScreen implements Screen {

    // =========================================================================
    // 1. INTI & DEKLARASI VARIABEL
    // =========================================================================
    private final FallenAscendantsGame game;
    private Card card; // Bukan final agar bisa disinkronisasi dengan objek player
    private final boolean isOwned;

    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;
    private Texture cardFrameTexture;
    private Texture statsBackgroundTexture;

    private BitmapFont titleFont;
    private BitmapFont labelFont;

    private Texture buttonNormal, buttonHover, buttonPressed;
    private TextButton.TextButtonStyle customButtonStyle;

    private Table mainTable;

    // =========================================================================
    // 2. KONSTRUKTOR
    // =========================================================================
    public upgradeScreen(FallenAscendantsGame game, Card card, boolean isOwned) {
        this.game = game;
        this.card = card;
        this.isOwned = isOwned;
    }

    // =========================================================================
    // 3. INISIALISASI ASSET & STAGE
    // =========================================================================
    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        FullscreenToggle.attach(stage);

        // --- SINKRONISASI OBJEK KARTU DENGAN INVENTORY PLAYER ---
        // Mencari referensi kartu asli dari Player.getCollection()
        Player player = game.getPlayer();
        if (player != null && player.getCollection() != null) {
            for (Card c : player.getCollection()) {
                if (c.getId().equals(this.card.getId())) {
                    this.card = c; // Ganti ke referensi kartu asli pemain!
                    break;
                }
            }
        }

        // Load background tempa/forge
        backgroundTexture = new Texture(Gdx.files.internal("background/background_lobby/UpgradeBackground.png"));
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Image bg = new Image(backgroundTexture);
        bg.setSize(1280, 720);
        stage.addActor(bg);

        // Load kertas gulungan untuk panel kanan
        statsBackgroundTexture = new Texture(Gdx.files.internal("card_frames/statsBackground.png"));
        statsBackgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // Cek bingkai kartu berdasarkan rarity
        String rarityStr = (card.getRarity() != null) ? card.getRarity().name().toLowerCase() : "common";
        String framePath = "card_frames/commonFrame.png";
        switch (rarityStr) {
            case "rare":      framePath = "card_frames/rareFrame.png"; break;
            case "epic":      framePath = "card_frames/epicFrame.png"; break;
            case "legendary": framePath = "card_frames/legendaryFrame.png"; break;
            case "special":
            case "limited":   framePath = "card_frames/specialFrame.png"; break;
        }

        cardFrameTexture = new Texture(Gdx.files.internal(framePath));
        cardFrameTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // Asset tombol
        buttonNormal = new Texture(Gdx.files.internal("Button/PrimaryButton.png"));
        buttonHover = new Texture(Gdx.files.internal("Button/HoverButton.png"));
        buttonPressed = new Texture(Gdx.files.internal("Button/PressedButton.png"));

        // Setup Font HD Supersampling
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/CinzelDecorative-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;

        // Font Judul
        parameter.size = 56;
        parameter.color = new Color(0.95f, 0.85f, 0.65f, 1f);
        parameter.borderWidth = 3f;
        parameter.borderColor = Color.BLACK;
        titleFont = generator.generateFont(parameter);
        titleFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        titleFont.getData().setScale(0.5f);

        // Font Detail Teks & Tombol
        parameter.size = 32;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 2f;
        parameter.borderColor = Color.BLACK;
        labelFont = generator.generateFont(parameter);
        labelFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        labelFont.getData().setScale(0.5f);

        generator.dispose();

        // Style Tombol Kustom
        customButtonStyle = new TextButton.TextButtonStyle();
        customButtonStyle.up = new TextureRegionDrawable(buttonNormal);
        customButtonStyle.over = new TextureRegionDrawable(buttonHover);
        customButtonStyle.down = new TextureRegionDrawable(buttonPressed);
        customButtonStyle.font = labelFont;
        customButtonStyle.fontColor = Color.WHITE;
        customButtonStyle.overFontColor = new Color(0.9f, 0.8f, 0.6f, 1f);

        // Table Utama Layout
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().pad(25);
        stage.addActor(mainTable);

        // Render UI pertama kali
        rebuildUI();

        // Shortcut keyboard ESC / Backspace
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACKSPACE) {
                    game.setScreen(new CardDetailScreen(game, card, isOwned));
                    return true;
                }
                return false;
            }
        });
    }

    // =========================================================================
    // 4. PENYUSUNAN LAYOUT UI & LOGIKA UPGRADE
    // =========================================================================
    private void rebuildUI() {
        mainTable.clearChildren();

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label.LabelStyle infoStyle = new Label.LabelStyle(labelFont, Color.WHITE);

        // --- SECTION: TOMBOL BACK & TITLE ---
        TextButton backButton = new TextButton("< Back", customButtonStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new CardDetailScreen(game, card, isOwned));
            }
        });
        mainTable.add(backButton).left().size(160, 42).padBottom(15).row();

        Label screenTitle = new Label("CARD FORGE SYSTEM", titleStyle);
        mainTable.add(screenTitle).padBottom(20).row();

        Table contentTable = new Table();
        contentTable.center();

        // --- SECTION KIRI: GAMBAR KARTU ---
        Table leftGroup = new Table();
        leftGroup.top();
        CardActor cardView = new CardActor(card, skin, isOwned, infoStyle, cardFrameTexture);
        leftGroup.add(cardView).size(280, 404).row();

        contentTable.add(leftGroup).size(280, 450).top().padRight(40);

        // --- SECTION KANAN: PANEL GULUNGAN KERTAS (STATS & REQUIREMENT) ---
        Table statsPanel = new Table();
        TextureRegionDrawable panelBg = new TextureRegionDrawable(statsBackgroundTexture);
        statsPanel.setBackground(panelBg);
        statsPanel.pad(70, 100, 60, 70); // Padding dalam gulungan
        statsPanel.top().left();

        Label cardName = new Label(card.getName().toUpperCase(), titleStyle);
        statsPanel.add(cardName).colspan(2).left().padBottom(15).row();

        // --- BACA DATA SINKRON DARI MODEL PLAYER & CARD ---
        Player player = game.getPlayer();

        int currentLvl = card.getLevel();
        int nextLvl = currentLvl + 1;

        int maxAtkCap = getMaxAtkByRarity(card.getRarity());
        int maxHpCap = getMaxHpByRarity(card.getRarity());

        // Estimasi Peningkatan Stats (Sesuai dengan levelUp di Card.java)
        int currentHp = card.getMaxHp();
        int nextHp = Math.min(maxHpCap, currentHp + 10);

        int currentAtk = card.getAtk();
        int nextAtk = Math.min(maxAtkCap, currentAtk + 3);

        int currentDef = card.getDef();
        int nextDef = currentDef + 2;

        // Syarat Upgrade Sesuai Aturan di Player.java
        int requiredDuplicates = currentLvl; // di Player.java: requiredDuplicates = targetCard.getLevel()
        int requiredGold = currentLvl * 100;  // di Player.java: requiredGold = targetCard.getLevel() * 100

        int currentCopies = card.getCopies();
        int playerGold = (player != null) ? player.getGold() : 0;

        // Cek Syarat: Gold cukup DAN (Copies - 1) >= requiredDuplicates
        boolean hasEnoughGold = playerGold >= requiredGold;
        boolean hasEnoughCopies = (currentCopies - 1) >= requiredDuplicates;
        boolean canUpgrade = (player != null) && hasEnoughGold && hasEnoughCopies;

        // Tampilkan Perbandingan Stats
        addStatRow(statsPanel, "LEVEL", String.valueOf(currentLvl), String.valueOf(nextLvl), infoStyle);
        addStatRow(statsPanel, "MAX HP", String.valueOf(currentHp), nextHp + " (Cap: " + maxHpCap + ")", infoStyle);
        addStatRow(statsPanel, "ATTACK", String.valueOf(currentAtk), nextAtk + " (Cap: " + maxAtkCap + ")", infoStyle);
        addStatRow(statsPanel, "DEFENSE", String.valueOf(currentDef), String.valueOf(nextDef), infoStyle);

        // Status Syarat Gold
        Label goldLabel = new Label("GOLD REQUIRED: " + requiredGold + "  (Held: " + playerGold + ")", infoStyle);
        goldLabel.setColor(hasEnoughGold ? Color.GREEN : Color.RED);
        statsPanel.add(goldLabel).colspan(2).left().padTop(10).padBottom(4).row();

        // Status Syarat Copies Bahan
        Label copiesLabel = new Label("COPIES HELD  : " + currentCopies + "  (Need: " + (requiredDuplicates + 1) + ")", infoStyle);
        copiesLabel.setColor(hasEnoughCopies ? Color.GREEN : Color.RED);
        statsPanel.add(copiesLabel).colspan(2).left().padBottom(15).row();

        // Tombol Confirm Upgrade
        TextButton upgradeBtn = new TextButton("FUSE & UPGRADE", customButtonStyle);
        if (!canUpgrade) {
            upgradeBtn.setDisabled(true);
        }

        upgradeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!canUpgrade || player == null) return;

                // Panggil method resmi upgradeCard milik Player.java!
                boolean success = player.upgradeCard(card);

                if (success) {
                    // Rebuild UI secara instant untuk menampilkan statistik ter-update
                    rebuildUI();
                }
            }
        });

        statsPanel.add(upgradeBtn).colspan(2).left().size(220, 44).row();

        contentTable.add(statsPanel).size(580, 560).top().left();
        mainTable.add(contentTable).expand().fill();
    }

    // =========================================================================
    // 5. HELPER METHODS & FORMULA
    // =========================================================================

    // Batas Max Attack per Rarity
    private int getMaxAtkByRarity(Rarity rarity) {
        if (rarity == null) return 60;
        switch (rarity) {
            case LEGENDARY: return 140;
            case EPIC:      return 120;
            case RARE:      return 90;
            default:        return 60; // COMMON
        }
    }

    // Batas Max HP per Rarity
    private int getMaxHpByRarity(Rarity rarity) {
        if (rarity == null) return 100;
        switch (rarity) {
            case LEGENDARY: return 200;
            case EPIC:      return 160;
            case RARE:      return 130;
            default:        return 100; // COMMON
        }
    }

    // Membuat baris komparasi stats sejajar dan rapi
    private void addStatRow(Table table, String statName, String currentVal, String nextVal, Label.LabelStyle style) {
        Label nameLbl = new Label(statName, style);
        Label valLbl = new Label(":  " + currentVal + "  ->  " + nextVal, style);

        table.add(nameLbl).width(120).left().padBottom(6);
        table.add(valLbl).left().padBottom(6).row();
    }

    // =========================================================================
    // 6. RENDER & LIFECYCLE
    // =========================================================================
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
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    // =========================================================================
    // 7. CLEANUP MEMORI
    // =========================================================================
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (statsBackgroundTexture != null) statsBackgroundTexture.dispose();
        if (cardFrameTexture != null) cardFrameTexture.dispose();
        if (titleFont != null) titleFont.dispose();
        if (labelFont != null) labelFont.dispose();
        if (buttonNormal != null) buttonNormal.dispose();
        if (buttonHover != null) buttonHover.dispose();
        if (buttonPressed != null) buttonPressed.dispose();
    }
}
