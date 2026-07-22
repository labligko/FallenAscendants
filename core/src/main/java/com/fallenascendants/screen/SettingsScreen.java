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
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
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

        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setSize(1280, 720);
        stage.addActor(backgroundImage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        FullscreenToggle.attach(stage);

        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);
    }

    private void rebuildUI(int width, int height) {
        if (titleFont != null) titleFont.dispose();
        if (sectionFont != null) sectionFont.dispose();
        if (buttonFont != null) buttonFont.dispose();

        float scale = (float) height / 720f;

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

        Table dialogTable = new Table();
        dialogTable.setBackground(new TextureRegionDrawable(panelTexture));
        dialogTable.top().pad(150, 100, 110, 100);

        // TITLE
        Label titleLabel = new Label("SETTINGS", new Label.LabelStyle(titleFont, Color.WHITE));
        dialogTable.add(titleLabel).padBottom(5).padTop(40).row();

        // MUSIC VOLUME
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

        // SFX VOLUME
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

        // BATTLE SPEED
        Label speedLabel = new Label("BATTLE SPEED", new Label.LabelStyle(sectionFont, Color.WHITE));
        dialogTable.add(speedLabel).padBottom(8).row();

        TextButton normalSpeedBtn = new TextButton("1x Normal", customButtonStyle);
        TextButton fastSpeedBtn = new TextButton("2x Fast", customButtonStyle);
        TextButton instantSpeedBtn = new TextButton("Skip Battle", customButtonStyle);

        ButtonGroup<TextButton> speedGroup = new ButtonGroup<>();
        speedGroup.add(normalSpeedBtn, fastSpeedBtn, instantSpeedBtn);
        speedGroup.setMaxCheckCount(1);
        speedGroup.setMinCheckCount(1);
        speedGroup.setUncheckLast(true);

        if (game.getBattleSpeed() == BattleSpeed.NORMAL) normalSpeedBtn.setChecked(true);
        else if (game.getBattleSpeed() == BattleSpeed.FAST) fastSpeedBtn.setChecked(true);
        else if (game.getBattleSpeed() == BattleSpeed.INSTANT) instantSpeedBtn.setChecked(true);

        Table speedRow = new Table();
        speedRow.add(normalSpeedBtn).size(150, 42).padRight(10);
        speedRow.add(fastSpeedBtn).size(150, 42).padRight(10);
        speedRow.add(instantSpeedBtn).size(150, 42);

        dialogTable.add(speedRow).padBottom(25).row();

        // ACTION BUTTONS
        TextButton resetButton = new TextButton("Reset Save Data", customButtonStyle);
        TextButton backButton = new TextButton("Back & Save", customButtonStyle);

        Table actionRow = new Table();
        actionRow.add(resetButton).size(180, 42).padRight(20);
        actionRow.add(backButton).size(180, 42);

        dialogTable.add(actionRow);

        mainTable.add(dialogTable).size(1080, 780).center();

        // ================= PASANG HOVER SFX UNTUK SEMUA TOMBOL =================
        TextButton[] allButtons = {
            decMusicBtn, incMusicBtn, decSfxBtn, incSfxBtn,
            normalSpeedBtn, fastSpeedBtn, instantSpeedBtn,
            resetButton, backButton
        };

        for (TextButton btn : allButtons) {
            btn.addListener(new InputListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if (pointer == -1) {
                        SFXManager.play("sound/sound_effect/hoverbutton.mp3", game.getSfxVolume());
                    }
                }
            });
        }

        // ================= LOGIC & LISTENERS =================

        // Music Slider
        musicSlider.setValue(game.getMusicVolume());
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float val = musicSlider.getValue();
                game.setMusicVolume(val);
                MusicManager.setVolume(val);
                musicPercLabel.setText(Math.round(val * 100) + "%");
            }
        });

        decMusicBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SFXManager.play("sound/sound_effect/clickbutton.mp3", game.getSfxVolume());
                musicSlider.setValue(Math.max(0f, musicSlider.getValue() - 0.05f));
            }
        });

        incMusicBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SFXManager.play("sound/sound_effect/clickbutton.mp3", game.getSfxVolume());
                musicSlider.setValue(Math.min(1f, musicSlider.getValue() + 0.05f));
            }
        });

        // SFX Slider Logic
        sfxSlider.setValue(game.getSfxVolume());
        sfxSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float val = sfxSlider.getValue();
                game.setSfxVolume(val);
                sfxPercLabel.setText(Math.round(val * 100) + "%");

                // Preview SFX hanya jalan kalau BUKAN lagi di-drag
                if (!sfxSlider.isDragging()) {
                    SFXManager.play("sound/sound_effect/sard_slap.wav", val);
                }
            }
        });

        sfxSlider.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                // Preview SFX tepat saat lepas geseran slider
                SFXManager.play("sound/sound_effect/sard_slap.wav", sfxSlider.getValue());
            }
        });

        // Tombol < dan > SFX TANPA clickbutton.mp3 agar tidak bertabrakan dengan sard_slap.wav
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

        // Battle Speed Buttons
        normalSpeedBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SFXManager.play("sound/sound_effect/clickbutton.mp3", game.getSfxVolume());
                game.setBattleSpeed(BattleSpeed.NORMAL);
            }
        });

        fastSpeedBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SFXManager.play("sound/sound_effect/clickbutton.mp3", game.getSfxVolume());
                game.setBattleSpeed(BattleSpeed.FAST);
            }
        });

        instantSpeedBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SFXManager.play("sound/sound_effect/clickbutton.mp3", game.getSfxVolume());
                game.setBattleSpeed(BattleSpeed.INSTANT);
            }
        });

        // Reset & Back
        resetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SFXManager.play("sound/sound_effect/clickbutton.mp3", game.getSfxVolume());

                // 1. Overlay Layar Gelap (Dim)
                Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
                pixmap.setColor(0f, 0f, 0f, 0.75f);
                pixmap.fill();
                final Texture dimTexture = new Texture(pixmap);
                pixmap.dispose();

                final Image blocker = new Image(new TextureRegionDrawable(dimTexture));
                blocker.setSize(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
                blocker.addListener(new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        return true;
                    }
                });

                // 2. Texture Frame Toast
                final Texture toastTex = new Texture(Gdx.files.internal("Panel/ToolTipPanel.png"));
                toastTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                Image background = new Image(new TextureRegionDrawable(toastTex));

                // 3. Generate Font Murni Putih (Pure White) khusus Dialog
                FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/CinzelDecorative-Regular.ttf"));
                FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
                float scale = (float) Gdx.graphics.getHeight() / 720f;
                parameter.size = Math.round(18 * scale);
                parameter.color = Color.WHITE; // Murni Putih
                parameter.borderWidth = 1.5f * scale;
                parameter.borderColor = Color.BLACK;
                final BitmapFont whiteFont = generator.generateFont(parameter);
                generator.dispose();
                whiteFont.getData().setScale(1f / scale);

                // 4. Label & Tombol
                Label dialogText = new Label("Yakin mau hapus semua progress?\nTindakan ini tidak bisa dibatalkan.",
                    new Label.LabelStyle(whiteFont, Color.WHITE));
                dialogText.setAlignment(Align.center);

                TextButton yesBtn = new TextButton("Yes, Reset", customButtonStyle);
                TextButton cancelBtn = new TextButton("Cancel", customButtonStyle);

                InputListener dialogBtnHover = new InputListener() {
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        if (pointer == -1) SFXManager.play("sound/sound_effect/hoverbutton.mp3", game.getSfxVolume());
                    }
                };
                yesBtn.addListener(dialogBtnHover);
                cancelBtn.addListener(dialogBtnHover);

                // 5. Layout Table dengan Padding Atas & Samping yang Lebih Longgar
                Table contentTable = new Table();
                // pad(Top, Left, Bottom, Right) -> Top 75px & Left/Right 85px memberi ruang jauh dari ukiran bingkai
                contentTable.pad(75, 85, 50, 85);
                contentTable.add(dialogText).colspan(2).padBottom(30).row();
                contentTable.add(yesBtn).size(160, 42).padRight(20);
                contentTable.add(cancelBtn).size(160, 42);

                // 6. Stack & Ukuran Toast (Dilebarkan ke 680px)
                final Stack stack = new Stack();
                stack.add(background);
                stack.add(contentTable);

                float width = 680f;  // Melebarkan panel dari 550f ke 680f
                float height = 350f; // Menyesuaikan tinggi
                stack.setSize(width, height);
                stack.setPosition(
                    (stage.getViewport().getWorldWidth() - width) / 2f,
                    (stage.getViewport().getWorldHeight() - height) / 2f
                );

                // 7. Close Logic & Disposal
                Runnable closeDialog = new Runnable() {
                    @Override
                    public void run() {
                        blocker.addAction(Actions.sequence(Actions.fadeOut(0.2f), Actions.removeActor()));
                        stack.addAction(Actions.sequence(Actions.fadeOut(0.2f), Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                stack.remove();
                                dimTexture.dispose();
                                toastTex.dispose();
                                whiteFont.dispose(); // Membersihkan memory font putih
                            }
                        })));
                    }
                };

                cancelBtn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        SFXManager.play("sound/sound_effect/clickbutton.mp3", game.getSfxVolume());
                        closeDialog.run();
                    }
                });

                yesBtn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        SFXManager.play("sound/sound_effect/clickbutton.mp3", game.getSfxVolume());

                        // 1. Hapus data & reset progress
                        SaveManager.deleteSave();
                        game.getPlayer().resetProgress();
                        closeDialog.run();

                        // 2. Tampilkan Toast pemberitahuan selama 3 detik
                        Toast.show(stage, "Progress berhasil di-reset!\nMemuat ulang dalam 3 detik...", 3.0f);

                        // 3. Tunda eksekusi game.create() selama 3 detik di background thread LibGDX
                        stage.getRoot().addAction(Actions.sequence(
                            Actions.delay(3.0f),
                            Actions.run(new Runnable() {
                                @Override
                                public void run() {
                                    game.create();
                                }
                            })
                        ));
                    }
                });

                // 8. Tampilkan dengan Fade In
                blocker.getColor().a = 0f;
                stack.getColor().a = 0f;
                stage.addActor(blocker);
                stage.addActor(stack);

                blocker.addAction(Actions.fadeIn(0.2f));
                stack.addAction(Actions.fadeIn(0.2f));
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SFXManager.play("sound/sound_effect/clickbutton.mp3", game.getSfxVolume());
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
