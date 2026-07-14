package com.fallenascendants.screen.deck;

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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.fallenascendants.FallenAscendantsGame;
import com.fallenascendants.model.Player;
import com.fallenascendants.screen.MainMenuScreen;

public class DeckBuilderScreen implements Screen {
    private final FallenAscendantsGame game;
    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;

    private BitmapFont titleFont;
    private BitmapFont uiFont;

    private Table mainSplitTable;
    private CollectionPoolPanel collectionPoolPanel;
    private DeckSlotPanel deckSlotPanel;
    private Player player;

    public DeckBuilderScreen(FallenAscendantsGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        player = new Player("Reyzz");

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        });

        backgroundTexture = new Texture(Gdx.files.internal("background/background_lobby/UpgradeBackground.png"));
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Image bg = new Image(backgroundTexture);
        bg.setSize(1280, 720);
        stage.addActor(bg);

        mainSplitTable = new Table();
        mainSplitTable.setFillParent(true);
        mainSplitTable.top().pad(20);
        stage.addActor(mainSplitTable);
    }

    private void rebuildUI(int width, int height) {
        float scale = (float) height / 720f;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/CinzelDecorative-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = Math.round(30 * scale);
        parameter.color = new Color(0.9f, 0.8f, 0.6f, 1f);
        parameter.borderWidth = 2 * scale;
        parameter.borderColor = Color.BLACK;
        titleFont = generator.generateFont(parameter);

        parameter.size = Math.round(13 * scale);
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1 * scale;
        parameter.borderColor = Color.BLACK;
        uiFont = generator.generateFont(parameter);
        generator.dispose();

        titleFont.getData().setScale(1f / scale);
        uiFont.getData().setScale(1f / scale);

        mainSplitTable.clearChildren();

        deckSlotPanel = new DeckSlotPanel(player, uiFont, titleFont);
        collectionPoolPanel = new CollectionPoolPanel(game, player, skin, uiFont, titleFont, deckSlotPanel);

        mainSplitTable.add(collectionPoolPanel).width(880).expandY().fillY().top();
        mainSplitTable.add(deckSlotPanel).width(360).expandY().fillY().top().padLeft(20);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.07f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); rebuildUI(width, height); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
        skin.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (titleFont != null) titleFont.dispose();
        if (uiFont != null) uiFont.dispose();
        if (collectionPoolPanel != null) collectionPoolPanel.dispose();
    }
}
