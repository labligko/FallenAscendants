package com.fallenascendants.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.fallenascendants.FallenAscendantsGame;
import com.fallenascendants.audio.MusicManager;
import com.fallenascendants.model.Card;
import com.fallenascendants.model.ProgressionManager;

import java.util.List;

public class RewardScreen implements Screen {
    private final FallenAscendantsGame game;
    private final ProgressionManager.BattleRewards rewards;

    private Stage stage;
    private Skin skin;

    private Label revealLabel;
    private TextButton[] cardOptionButtons;
    private boolean rewardClaimed = false;

    public RewardScreen(FallenAscendantsGame game, ProgressionManager.BattleRewards rewards) {
        this.game = game;
        this.rewards = rewards;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        FullscreenToggle.attach(stage);

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Label title = new Label(rewards.isWin() ? "VICTORY!" : "DEFEAT", skin);
        title.setFontScale(2f);
        root.add(title).padTop(60).padBottom(20).row();

        Label goldLabel = new Label(
            "Gold Earned: " + rewards.getGoldEarned()
                + "\nTotal Gold: " + game.getPlayer().getGold(),
            skin
        );
        goldLabel.setFontScale(1.2f);
        root.add(goldLabel).padBottom(30).row();

        if (rewards.isWin()) {
            Label chooseLabel = new Label("Pilih 1 dari 3 Kartu Tertutup:", skin);
            root.add(chooseLabel).padBottom(15).row();

            Table cardRow = new Table();
            List<Card> options = rewards.getCardOptions();
            cardOptionButtons = new TextButton[options.size()];

            for (int i = 0; i < options.size(); i++) {
                final int optionIndex = i;
                TextButton optionButton = new TextButton("Kartu Tertutup " + (char) ('A' + i), skin);
                cardOptionButtons[i] = optionButton;

                optionButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Card claimed = ProgressionManager.claimCardReward(game.getPlayer(), rewards, optionIndex);
                        if (claimed != null) {
                            rewardClaimed = true;

                            revealLabel.setText(
                                "Anda mendapatkan: " + claimed.getName() +
                                    " (" + claimed.getRarity() + ")!"
                            );

                            for (TextButton btn : cardOptionButtons) {
                                btn.setDisabled(true);
                            }
                        }
                    }
                });

                cardRow.add(optionButton).width(220).height(80).padRight(15);
            }

            root.add(cardRow).padBottom(20).row();

            revealLabel = new Label("", skin);
            revealLabel.setFontScale(1.1f);
            root.add(revealLabel).padBottom(30).row();
        } else {
            Label loseLabel = new Label("Kalah pertarungan. Tidak ada kartu didapat.", skin);
            root.add(loseLabel).padBottom(30).row();
        }

        TextButton continueButton = new TextButton("Continue", skin);
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.saveProgress();
                game.setScreen(new MainMenuScreen(game));
            }
        });
        root.add(continueButton).width(200);

        if (rewards.isWin()) {
            MusicManager.play("sound/background_music/victory_theme.mp3", true);
        } else {
            MusicManager.play("sound/background_music/defeat_theme.mp3", true);
        }
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_ENTER)) {
            if (!rewards.isWin() || rewardClaimed) {
                game.saveProgress();
                game.setScreen(new MainMenuScreen(game));
            }
        }
        Gdx.gl.glClearColor(0.05f, 0.04f, 0.07f, 1f);
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
    @Override
    public void hide() {
        MusicManager.stop();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
