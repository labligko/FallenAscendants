package com.fallenascendants.screen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.fallenascendants.audio.MusicManager;
import com.fallenascendants.audio.SFXManager;
import com.fallenascendants.screen.card.CollectionScreen;
import com.fallenascendants.screen.deck.DeckBuilderScreen;

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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.audio.Music;
import com.fallenascendants.FallenAscendantsGame;

public class MainMenuScreen implements Screen {
    private final FallenAscendantsGame game;
    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;

    private BitmapFont titleFont;
    private BitmapFont buttonFont;

    private Table mainTable;
    private Table footerTable;
    private Table headerTable;

    private Texture logoTexture;
    private Image logoImage;
    private Label blinkLabel;
    private Label playerNameLabel;
    private Label playerGoldLabel;

    private Texture buttonNormal;
    private Texture buttonHover;
    private Texture buttonPressed;

    private TextButton playButton, collectionButton, deckButton, settingsButton, exitButton;
    private float blinkTime = 0;

    private Music lobbyMusic;

    public MainMenuScreen(FallenAscendantsGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);

        MusicManager.play("sound/background_music/lobby_sound.mp3", true, game.getMusicVolume());

        backgroundTexture = new Texture(Gdx.files.internal("background/background_lobby/MainMenuBackgroundFix.png"));
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        buttonNormal = new Texture(Gdx.files.internal("Button/PrimaryButton.png"));
        buttonNormal.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        buttonHover = new Texture(Gdx.files.internal("Button/HoverButton.png"));
        buttonHover.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        buttonPressed = new Texture(Gdx.files.internal("Button/PressedButton.png"));
        buttonPressed.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // AMAN DARI LEAK: Pindahkan inisialisasi awal Logo ke show() satu kali saja
        logoTexture = new Texture(Gdx.files.internal("Logo/LogoFallenAcsendants.png"));
        logoTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        logoImage = new Image(logoTexture);

        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setSize(1280, 720);
        stage.addActor(backgroundImage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        FullscreenToggle.attach(stage);

        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        footerTable = new Table();
        footerTable.setFillParent(true);
        // Naikkan padding bottom footer agar teks panduan F11 tidak terlalu mepet lantai bawah monitor
        footerTable.bottom().padBottom(25);
        stage.addActor(footerTable);

        headerTable = new Table();
        headerTable.setFillParent(true);
        headerTable.top().left(); // Rata atas-kiri
        stage.addActor(headerTable);
    }

    private void rebuildUI(int width, int height) {
        if (titleFont != null) titleFont.dispose();
        if (buttonFont != null) buttonFont.dispose();

        float scale = (float) height / 720f;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/CinzelDecorative-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = Math.round(48 * scale);
        parameter.color = new Color(0.9f, 0.8f, 0.6f, 1f);
        parameter.borderWidth = 2 * scale;
        parameter.borderColor = Color.BLACK;
        titleFont = generator.generateFont(parameter);

        // PROPORSIONAL: Ukuran font tombol dikurangi dari 20 ke 15 agar seimbang di tombol ramping
        parameter.size = Math.round(15 * scale);
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1 * scale;
        parameter.borderColor = Color.BLACK;
        buttonFont = generator.generateFont(parameter);

        generator.dispose();

        titleFont.getData().setScale(1f / scale);
        buttonFont.getData().setScale(1f / scale);

        skin.get("default", Label.LabelStyle.class).font = titleFont;
        skin.get("default", TextButton.TextButtonStyle.class).font = buttonFont;

        mainTable.clearChildren();
        footerTable.clearChildren();
        headerTable.clearChildren();

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(buttonNormal);
        buttonStyle.over = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(buttonHover);
        buttonStyle.down = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(buttonPressed);

        buttonStyle.font = buttonFont;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = new Color(0.9f, 0.8f, 0.6f, 1f);

        // Pergeseran teks halus saat diklik agar terasa mekanis responsif
        buttonStyle.pressedOffsetX = 1;
        buttonStyle.pressedOffsetY = -1;

        playButton = new TextButton("PLAY BATTLE", buttonStyle);
        collectionButton = new TextButton("CARD ALBUM", buttonStyle);
        deckButton = new TextButton("DECK BUILDER", buttonStyle);
        settingsButton = new TextButton("SETTINGS", buttonStyle);
        exitButton = new TextButton("EXIT GAME", buttonStyle);

        TextButton[] allButtons = { playButton, collectionButton, deckButton, settingsButton, exitButton };
        for (TextButton btn : allButtons) {
            btn.getLabelCell().padTop(-1 * scale);

            btn.addListener(new InputListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    // pointer == -1 artinya kursor mouse masuk (bukan hasil drag)
                    if (pointer == -1) {
                        SFXManager.play("sound/sound_effect/hoverbutton.mp3", game.getSfxVolume());
                    }
                }
            });
        }

        Label.LabelStyle nameStyle = new Label.LabelStyle(buttonFont, new Color(0.9f, 0.8f, 0.6f, 1f));
        String playerName = game.getPlayer().getName();
        if (playerName == null || playerName.isEmpty()) {
            playerName = "Unknown";
        }
        playerNameLabel = new Label("Player: " + playerName, nameStyle);
        playerGoldLabel = new Label("Gold: " + game.getPlayer().getGold(), nameStyle);
        headerTable.add(playerNameLabel).padTop(15).padLeft(20).left().row();
        headerTable.add(playerGoldLabel).padTop(0).padLeft(20).left().row();

        // REKAYASA TOTAL STRUKTUR LAYOUT GRID 1280x720
        // Logo diturunkan ke posisi tengah atas ideal (padTop: 40) dan jarak bottom dinormalisasi (padBottom: -30)
        mainTable.add(logoImage).size(227, 227).padBottom(0).padTop(40).row();

        // Kalibrasi bumper padBottom disesuaikan presisi agar tidak tumpang tindih ekstrem
        mainTable.add(playButton).width(216).height(85).padBottom(0).row();
        mainTable.add(collectionButton).width(216).height(85).padBottom(0).row();
        mainTable.add(deckButton).width(216).height(85).padBottom(0).row();
        mainTable.add(settingsButton).width(216).height(85).padBottom(0).row();

        // Tombol terakhir diberi padBottom positif (55) untuk mendorong rangkaian tombol menjauh dari teks F11
        mainTable.add(exitButton).width(216).height(85).padBottom(50).row();

        Label.LabelStyle footerStyle = new Label.LabelStyle(buttonFont, new Color(0.6f, 0.6f, 0.6f, 1f));
        blinkLabel = new Label("Press [F11] for Fullscreen", footerStyle);
        footerTable.add(blinkLabel).padBottom(-10).row();

        // Blok listener aksi klik tombol
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SFXManager.play("sound/sound_effect/clickbutton.mp3", game.getSfxVolume());
                if (!game.getPlayer().getDeck().isValidForBattle()) {
                    Toast.show(stage, "Deck Not Found!.", 1f);
                    return;
                }
                MusicManager.stop();

                game.setScreen(new PreBattleScreen(game));
            }
        });

        collectionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SFXManager.play("sound/sound_effect/clickbutton.mp3", game.getSfxVolume());
                MusicManager.stop();
                game.setScreen(new CollectionScreen(game));
            }
        });

        deckButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SFXManager.play("sound/sound_effect/clickbutton.mp3", game.getSfxVolume());
                MusicManager.stop();
                game.setScreen(new DeckBuilderScreen(game));
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SFXManager.play("sound/sound_effect/clickbutton.mp3", game.getSfxVolume());
                game.setScreen(new SettingsScreen(game));
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SFXManager.play("sound/sound_effect/clickbutton.mp3", game.getSfxVolume());
                game.saveProgress();
                MusicManager.stop();
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.08f, 0.06f, 0.10f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (blinkLabel != null) {
            blinkTime += delta * 4;
            float alpha = (float) (Math.sin(blinkTime) + 1) / 2f;
            Color color = blinkLabel.getColor();
            blinkLabel.setColor(color.r, color.g, color.b, alpha);
        }

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
        if (titleFont != null) titleFont.dispose();
        if (buttonFont != null) buttonFont.dispose();
        if (logoTexture != null) logoTexture.dispose();
        if (buttonNormal != null) buttonNormal.dispose();
        if (buttonHover != null) buttonHover.dispose();
        if (buttonPressed != null) buttonPressed.dispose();
    }
}
