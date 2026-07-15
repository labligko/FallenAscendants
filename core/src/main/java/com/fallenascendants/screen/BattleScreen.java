package com.fallenascendants.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.fallenascendants.FallenAscendantsGame;
import com.fallenascendants.battle.BattleManager;

public class BattleScreen implements Screen {
    private final FallenAscendantsGame game;
    private final BattleManager battleManager;

    private Stage stage;
    private Skin skin;

    private final StringBuilder fullLog = new StringBuilder();
    private Label logLabel;
    private ScrollPane scrollPane;
    private Label resultLabel;
    private TextButton nextButton;

    public BattleScreen(FallenAscendantsGame game, BattleManager battleManager) {
        this.game = game;
        this.battleManager = battleManager;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Table root = new Table();
        root.setFillParent(true);
        root.top().pad(20);
        stage.addActor(root);

        Label title = new Label("BATTLE LOG", skin);
        title.setFontScale(1.5f);
        root.add(title).padBottom(10).row();

        // Pola sama kayak BattleTester.java, cuma outputnya ke Label bukan println
        appendLog(battleManager.applyPassiveSkillsAtBattleStart());
        appendLog(battleManager.applyFactionSynergyAtBattleStart());
        appendLog(battleManager.getTurnQueueReport());

        logLabel = new Label(fullLog.toString(), skin);
        logLabel.setWrap(true);
        logLabel.setAlignment(Align.topLeft);

        scrollPane = new ScrollPane(logLabel, skin);
        scrollPane.setFadeScrollBars(false);
        root.add(scrollPane).width(1100).height(480).padBottom(15).row();

        resultLabel = new Label("", skin);
        resultLabel.setFontScale(1.3f);
        root.add(resultLabel).padBottom(10).row();

        Table buttonRow = new Table();

        nextButton = new TextButton("Next Action", skin);
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stepBattle();
            }
        });

        TextButton backButton = new TextButton("Back to Menu", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        buttonRow.add(nextButton).width(200).padRight(20);
        buttonRow.add(backButton).width(200);
        root.add(buttonRow);
    }

    private void stepBattle() {
        if (battleManager.isBattleOver()) {
            return;
        }

        appendLog(battleManager.processSingleAction());
        logLabel.setText(fullLog.toString());
        scrollPane.layout();
        scrollPane.setScrollPercentY(1f); // auto-scroll ke paling bawah

        if (battleManager.isBattleOver()) {
            nextButton.setDisabled(true);
            resultLabel.setText(battleManager.isPlayerWin() ? "PLAYER WIN!" : "PLAYER LOSE!");
        }
    }

    private void appendLog(String text) {
        if (text != null && !text.isBlank()) {
            fullLog.append(text).append("\n\n");
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
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
