package com.fallenascendants.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fallenascendants.enumtype.BattleSpeed;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.fallenascendants.save.SaveManager;
import com.fallenascendants.FallenAscendantsGame;

public class SettingsScreen implements Screen {
    private final FallenAscendantsGame game;
    private Stage stage;
    private Skin skin;
    private Table mainTable;

    public SettingsScreen(FallenAscendantsGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        TextButton backButton = new TextButton("Back", skin);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.saveProgress();
                game.setScreen(new MainMenuScreen(game));
            }
        });

        Label title = new Label("SETTINGS", skin);
        title.setFontScale(2f);

        Label musicLabel = new Label("Music Volume", skin);
        final Slider musicSlider = new Slider(0f, 1f, 0.01f, false, skin);
        musicSlider.setValue(game.getMusicVolume());

        Label sfxLabel = new Label("SFX Volume", skin);
        final Slider sfxSlider = new Slider(0f, 1f, 0.01f, false, skin);
        sfxSlider.setValue(game.getSfxVolume());

        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setMusicVolume(musicSlider.getValue());
            }
        });

        sfxSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setSfxVolume(sfxSlider.getValue());
            }
        });

        Label speedLabel = new Label("Battle Speed", skin);

        TextButton normalSpeedBtn = new TextButton("1x Normal", skin);
        TextButton fastSpeedBtn = new TextButton("2x Fast", skin);
        TextButton instantSpeedBtn = new TextButton("Skip", skin);

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

        Table speedRow = new Table();
        speedRow.add(normalSpeedBtn).pad(5);
        speedRow.add(fastSpeedBtn).pad(5);
        speedRow.add(instantSpeedBtn).pad(5);

        TextButton resetButton = new TextButton("Reset Save Data", skin);

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
                confirmDialog.text("Yakin mau hapus semua progress?\nTindakan ini tidak bisa dibatalkan.");
                confirmDialog.button("Yes, Reset", true);
                confirmDialog.button("Cancel", false);
                confirmDialog.show(stage);
            }
        });

        // Semua penyusunan layout dikumpulkan di sini, urut dari atas ke bawah
        mainTable.add(title).padBottom(30).row();
        mainTable.add(musicLabel).padBottom(5).row();
        mainTable.add(musicSlider).width(400).padBottom(20).row();
        mainTable.add(sfxLabel).padBottom(5).row();
        mainTable.add(sfxSlider).width(400).padBottom(20).row();
        mainTable.add(speedLabel).padBottom(5).row();
        mainTable.add(speedRow).padBottom(30).row();
        mainTable.add(resetButton).padBottom(30).row();
        mainTable.add(backButton).padTop(20).row();
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
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
