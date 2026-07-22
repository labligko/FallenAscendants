package com.fallenascendants.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.fallenascendants.FallenAscendantsGame;
import com.fallenascendants.audio.MusicManager;
import com.fallenascendants.audio.SFXManager;
import com.fallenascendants.enumtype.BattleSpeed;
import com.fallenascendants.save.SaveManager;

public class SettingsScreen implements Screen {

    private final FallenAscendantsGame game;
    private Stage stage;
    private Skin skin;
    private Table mainTable;

    private Texture backgroundTexture;
    private Texture panelTexture;
    private Texture buttonNormal;
    private Texture buttonHover;
    private Texture buttonPressed;

    private BitmapFont titleFont;
    private BitmapFont sectionFont;
    private BitmapFont buttonFont;

    public SettingsScreen(FallenAscendantsGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);

        // Load background and panel textures
        backgroundTexture = new Texture(Gdx.files.internal("background/background_lobby/SettingsBackgroundBlur.png"));
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        panelTexture = new Texture(Gdx.files.internal("Panel/SettingPanel.png"));
        panelTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        buttonNormal = new Texture(Gdx.files.internal("Button/PrimaryButton.png"));
        buttonNormal.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        buttonHover = new Texture(Gdx.files.internal("Button/HoverButton.png"));
        buttonHover.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        buttonPressed = new Texture(Gdx.files.internal("Button/PressedButton.png"));
        buttonPressed.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // Add background image
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setSize(1280, 720);
        stage.addActor(backgroundImage);

        // Skin masih dibutuhkan untuk Slider dan Dialog bawaan
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        FullscreenToggle.attach(stage);

        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);
    }

    private void rebuildUI(int width, int height) {
        // Clean up old fonts
        if (titleFont != null) titleFont.dispose();
        if (sectionFont != null) sectionFont.dispose();
        if (buttonFont != null) buttonFont.dispose();

        float scale = (float) height / 720f;

        // Generate custom fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/CinzelDecorative-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = Math.round(34 * scale);
        parameter.color = new Color(0.9f, 0.8f, 0.6f, 1f);
        parameter.borderWidth = 2 * scale;
        parameter.borderColor = Color.BLACK;
        titleFont = generator.generateFont(parameter);

        parameter.size = Math.round(18 * scale);
        parameter.color = new Color(0.9f, 0.8f, 0.6f, 1f);
        parameter.borderWidth = 1.5f * scale;
        parameter.borderColor = Color.BLACK;
        sectionFont = generator.generateFont(parameter);

        parameter.size = Math.round(13 * scale);
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1 * scale;
        parameter.borderColor = Color.BLACK;
        buttonFont = generator.generateFont(parameter);

        generator.dispose();

        titleFont.getData().setScale(1f / scale);
        sectionFont.getData().setScale(1f / scale);
        buttonFont.getData().setScale(1f / scale);

        mainTable.clearChildren();

        // Custom Button Style
        TextButton.TextButtonStyle customButtonStyle = new TextButton.TextButtonStyle();
        customButtonStyle.up = new TextureRegionDrawable(buttonNormal);
        customButtonStyle.over = new TextureRegionDrawable(buttonHover);
        customButtonStyle.down = new TextureRegionDrawable(buttonPressed);
        customButtonStyle.checked = new TextureRegionDrawable(buttonPressed);
        customButtonStyle.font = buttonFont;
        customButtonStyle.fontColor = Color.WHITE;
        customButtonStyle.overFontColor = new Color(0.9f, 0.8f, 0.6f, 1f);
        customButtonStyle.checkedFontColor = new Color(0.9f, 0.8f, 0.6f, 1f);
        customButtonStyle.pressedOffsetX = 1;
        customButtonStyle.pressedOffsetY = -1;

        // Setup setting panel container table
        Table dialogTable = new Table();
        dialogTable.setBackground(new TextureRegionDrawable(panelTexture));
        dialogTable.top().pad(150, 100, 110, 100);

        // ================= TITLE =================
        Label titleLabel = new Label("SETTINGS", new Label.LabelStyle(titleFont, Color.WHITE));
        dialogTable.add(titleLabel).padBottom(5).padTop(40).row();

        // ================= MUSIC VOLUME =================
        Label musicLabel = new Label("MUSIC VOLUME", new Label.LabelStyle(sectionFont, Color.WHITE));
        dialogTable.add(musicLabel).padBottom(5).row();

        TextButton decMusicBtn = new TextButton("<", customButtonStyle);
        final Slider musicSlider = new Slider(0f, 1f, 0.01f, false, skin);
        TextButton incMusicBtn = new TextButton(">", customButtonStyle);
        final Label musicPercLabel = new Label(Math.round(game.getMusicVolume() * 100) + "%", new Label.LabelStyle(buttonFont, Color.WHITE));

        Table musicRow = new Table();
        musicRow.add(decMusicBtn).size(45, 35).padRight(10);
        musicRow.add(musicSlider).width(250);
        musicRow.add(incMusicBtn).size(45, 35).padLeft(10);
        musicRow.add(musicPercLabel).width(50).padLeft(15).center();

        dialogTable.add(musicRow).padBottom(20).padLeft(70).row();

        // ================= SFX VOLUME =================
        Label sfxLabel = new Label("SFX VOLUME", new Label.LabelStyle(sectionFont, Color.WHITE));
        dialogTable.add(sfxLabel).padBottom(5).row();

        TextButton decSfxBtn = new TextButton("<", customButtonStyle);
        final Slider sfxSlider = new Slider(0f, 1f, 0.01f, false, skin);
        TextButton incSfxBtn = new TextButton(">", customButtonStyle);
        final Label sfxPercLabel = new Label(Math.round(game.getSfxVolume() * 100) + "%", new Label.LabelStyle(buttonFont, Color.WHITE));

        Table sfxRow = new Table();
        sfxRow.add(decSfxBtn).size(45, 35).padRight(10);
        sfxRow.add(sfxSlider).width(250);
        sfxRow.add(incSfxBtn).size(45, 35).padLeft(10);
        sfxRow.add(sfxPercLabel).width(50).padLeft(15).center();

        dialogTable.add(sfxRow).padBottom(20).padLeft(70).row();

        // ================= BATTLE SPEED =================
        Label speedLabel = new Label("BATTLE SPEED", new Label.LabelStyle(sectionFont, Color.WHITE));
        dialogTable.add(speedLabel).padBottom(8).row();

        TextButton normalSpeedBtn = new TextButton("1x Normal", customButtonStyle);
        TextButton fastSpeedBtn = new TextButton("2x Fast", customButtonStyle);
        TextButton instantSpeedBtn = new TextButton("Skip Battle", customButtonStyle);

        // Menggunakan ButtonGroup dari temanmu supaya tombol yang aktif terlihat jelas
        ButtonGroup<TextButton> speedGroup = new ButtonGroup<>();
        speedGroup.add(normalSpeedBtn, fastSpeedBtn, instantSpeedBtn);
        speedGroup.setMaxCheckCount(1);
        speedGroup.setMinCheckCount(1);
        speedGroup.setUncheckLast(true);

        // Set status aktif sesuai data game saat ini
        if (game.getBattleSpeed() == BattleSpeed.NORMAL) normalSpeedBtn.setChecked(true);
        else if (game.getBattleSpeed() == BattleSpeed.FAST) fastSpeedBtn.setChecked(true);
        else if (game.getBattleSpeed() == BattleSpeed.INSTANT) instantSpeedBtn.setChecked(true);

        Table speedRow = new Table();
        speedRow.add(normalSpeedBtn).size(150, 42).padRight(10);
        speedRow.add(fastSpeedBtn).size(150, 42).padRight(10);
        speedRow.add(instantSpeedBtn).size(150, 42);

        dialogTable.add(speedRow).padBottom(25).row();

        // ================= ACTION BUTTONS =================
        TextButton resetButton = new TextButton("Reset Save Data", customButtonStyle);
        TextButton backButton = new TextButton("Back", customButtonStyle);

        Table actionRow = new Table();
        actionRow.add(resetButton).size(180, 42).padRight(20);
        actionRow.add(backButton).size(180, 42);

        dialogTable.add(actionRow);

        mainTable.add(dialogTable).size(1080, 780).center();

        // ================= LOGIC & LISTENERS (PERBAIKAN) =================

        // Logic Volume Music
        musicSlider.setValue(game.getMusicVolume());
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float val = musicSlider.getValue();
                game.setMusicVolume(val);
                com.fallenascendants.audio.MusicManager.setVolume(val); // <- Ini yang hilang di kode temanmu
                musicPercLabel.setText(Math.round(val * 100) + "%");
            }
        });

        decMusicBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                musicSlider.setValue(Math.max(0f, musicSlider.getValue() - 0.05f));
            }
        });

        incMusicBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                musicSlider.setValue(Math.min(1f, musicSlider.getValue() + 0.05f));
            }
        });

        // 1. ChangeListener: Khusus update volume & cover ubah nilai tanpa drag (misal via button / klik instan)
        sfxSlider.setValue(game.getSfxVolume());
        sfxSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float val = sfxSlider.getValue();
                game.setSfxVolume(val);
                sfxPercLabel.setText(Math.round(val * 100) + "%");

                // HANYA play sound di sini kalau slider BUKAN lagi di-drag
                // (contoh: nilainya diubah lewat kodingan / tombol step + - / keyboard)
                if (!sfxSlider.isDragging()) {
                    SFXManager.play("sound/sound_effect/sard_slap.wav", val);
                }
            }
        });

// 2. ClickListener: Khusus play sound 1x saat selesainya aksi drag (lepas mouse/touch)
        sfxSlider.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);

                // Play SFX preview tepat saat user melepas geseran slider
                SFXManager.play("sound/sound_effect/sard_slap.wav", sfxSlider.getValue());
            }
        });

        decSfxBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sfxSlider.setValue(Math.max(0f, sfxSlider.getValue() - 0.05f));
            }
        });

        incSfxBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sfxSlider.setValue(Math.min(1f, sfxSlider.getValue() + 0.05f));
            }
        });

        // Logic Battle Speed (Dipasang ke custom button, bukan dibuat ulang)
        normalSpeedBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setBattleSpeed(BattleSpeed.NORMAL);
            }
        });

        fastSpeedBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setBattleSpeed(BattleSpeed.FAST);
            }
        });

        instantSpeedBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setBattleSpeed(BattleSpeed.INSTANT);
            }
        });

        // Logic Reset & Back
        resetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Dialog confirmDialog = new Dialog("Confirm Reset", skin) {
                    @Override
                    protected void result(Object confirmed) {
                        if (Boolean.TRUE.equals(confirmed)) {
                            SaveManager.deleteSave();
                            game.getPlayer().resetProgress();
                        }
                    }
                };

                Label dialogText = new Label("Yakin mau hapus semua progress?\nTindakan ini tidak bisa dibatalkan.", new Label.LabelStyle(buttonFont, Color.WHITE));
                dialogText.setAlignment(Align.center);
                confirmDialog.getContentTable().add(dialogText).pad(20);

                confirmDialog.button("Yes, Reset", true, customButtonStyle);
                confirmDialog.button("Cancel", false, customButtonStyle);
                confirmDialog.show(stage);
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.saveProgress();
                game.setScreen(new MainMenuScreen(game));
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.08f, 0.06f, 0.10f, 1f);
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
        if (panelTexture != null) panelTexture.dispose();
        if (buttonNormal != null) buttonNormal.dispose();
        if (buttonHover != null) buttonHover.dispose();
        if (buttonPressed != null) buttonPressed.dispose();
        if (titleFont != null) titleFont.dispose();
        if (sectionFont != null) sectionFont.dispose();
        if (buttonFont != null) buttonFont.dispose();
    }
}
