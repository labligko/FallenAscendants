package com.fallenascendants.screen;

import com.badlogic.gdx.Gdx;
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
import com.fallenascendants.battle.BattleManager;
import com.fallenascendants.data.DummyBattleFactory;
import com.fallenascendants.model.BattleField;
import com.fallenascendants.model.Card;
import com.fallenascendants.model.Deck;

public class PreBattleScreen implements Screen {
    private final FallenAscendantsGame game;
    private Stage stage;
    private Skin skin;

    private Deck playerDeck;
    private Deck enemyDeck;

    public PreBattleScreen(FallenAscendantsGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // Deck player diambil dari save state, deck musuh di-generate acak
        // (pola yang sama persis kayak BattleTester.java)
        playerDeck = game.getPlayer().getDeck();
        enemyDeck = DummyBattleFactory.createRandomEnemyDeck(playerDeck.size());

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Label title = new Label("CONFIRM BATTLE", skin);
        title.setFontScale(1.8f);

        Table columns = new Table();
        columns.add(buildDeckColumn("YOUR DECK", playerDeck)).width(500).padRight(40);
        columns.add(buildDeckColumn("ENEMY DECK", enemyDeck)).width(500);

        TextButton fightButton = new TextButton("FIGHT!", skin);
        TextButton backButton = new TextButton("Back", skin);

        fightButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BattleField playerField = new BattleField(playerDeck);
                BattleField enemyField = new BattleField(enemyDeck);
                BattleManager battleManager = new BattleManager(playerField, enemyField);

                game.setScreen(new BattleScreen(game, battleManager));
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        Table buttonRow = new Table();
        buttonRow.add(backButton).width(160).padRight(20);
        buttonRow.add(fightButton).width(160);

        root.add(title).padTop(40).padBottom(20).row();
        root.add(columns).padBottom(30).row();
        root.add(buttonRow).row();
    }

    private Table buildDeckColumn(String headerText, Deck deck) {
        Table column = new Table();

        Label header = new Label(headerText, skin);
        header.setFontScale(1.2f);
        column.add(header).padBottom(10).row();

        for (int i = 0; i < deck.size(); i++) {
            Card card = deck.getCard(i);
            String tag = i < 5 ? "[ACTIVE]" : "[RESERVE]";

            String line = tag + " " + card.getName()
                + " (" + card.getRole() + ", Lvl " + card.getLevel() + ")\n"
                + "HP:" + card.getMaxHp()
                + " ATK:" + card.getAtk()
                + " DEF:" + card.getDef()
                + " SPD:" + card.getSpd();

            Label cardLabel = new Label(line, skin);
            column.add(cardLabel).padBottom(8).left().row();
        }

        return column;
    }

    @Override
    public void render(float delta) {
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
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
