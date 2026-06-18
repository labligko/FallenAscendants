package com.fallenascendants.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.fallenascendants.FallenAscendantsGame;

public class MainMenuScreen implements Screen {
    private final FallenAscendantsGame game;
    private Stage stage;
    private Skin skin;

    public MainMenuScreen(FallenAscendantsGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label title = new Label("FALLEN ASCENDANTS", skin);
        Label subtitle = new Label("Auto Battle TCG", skin);

        TextButton playButton = new TextButton("Play", skin);
        TextButton collectionButton = new TextButton("Collection", skin);
        TextButton deckButton = new TextButton("Deck Builder", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        exitButton.addListener(event -> {
            if (exitButton.isPressed()) {
                Gdx.app.exit();
            }
            return true;
        });

        table.add(title).padBottom(20).row();
        table.add(subtitle).padBottom(40).row();
        table.add(playButton).width(220).height(50).padBottom(15).row();
        table.add(collectionButton).width(220).height(50).padBottom(15).row();
        table.add(deckButton).width(220).height(50).padBottom(15).row();
        table.add(exitButton).width(220).height(50).row();
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
