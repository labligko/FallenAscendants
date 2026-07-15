package com.fallenascendants.screen.deck;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fallenascendants.FallenAscendantsGame;
import com.fallenascendants.model.Card;
import com.fallenascendants.model.Player;
import com.fallenascendants.screen.MainMenuScreen;

public class DeckSlotPanel extends Table {
    private final FallenAscendantsGame game;
    private final Player player;
    private final int MAX_DECK_SIZE = 8;

    private final Label.LabelStyle titleStyle;
    private final Label.LabelStyle infoStyle;
    private final TextButton.TextButtonStyle customButtonStyle;

    private final Texture buttonNormal, buttonHover, buttonPressed;

    public DeckSlotPanel(FallenAscendantsGame game, Player player, BitmapFont uiFont, BitmapFont titleFont) {
        this.game = game;
        this.player = player;

        titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        infoStyle = new Label.LabelStyle(uiFont, new Color(0.9f, 0.8f, 0.6f, 1f));

        buttonNormal = new Texture(Gdx.files.internal("Button/PrimaryButton.png"));
        buttonHover = new Texture(Gdx.files.internal("Button/HoverButton.png"));
        buttonPressed = new Texture(Gdx.files.internal("Button/PressedButton.png"));

        customButtonStyle = new TextButton.TextButtonStyle();
        customButtonStyle.up = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(buttonNormal);
        customButtonStyle.over = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(buttonHover);
        customButtonStyle.down = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(buttonPressed);
        customButtonStyle.font = uiFont;
        customButtonStyle.fontColor = Color.WHITE;

        buildPanel();
    }

    public void buildPanel() {
        this.clearChildren();
        this.center().top();

        Label deckTitle = new Label("CURRENT DECK", titleStyle);
        this.add(deckTitle).center().padBottom(5).row();

        Label countLabel = new Label("(" + player.getDeck().size() + " / " + MAX_DECK_SIZE + " CARDS)", infoStyle);
        this.add(countLabel).center().padBottom(20).row();

        Table listTable = new Table();
        listTable.top();

        for (int i = 0; i < player.getDeck().size(); i++) {
            final int index = i;
            Card card = player.getDeck().getCard(index);

            if (card != null) {
                TextButton cardRowItem = new TextButton(card.getName() + " [" + card.getRarity() + "]", customButtonStyle);
                cardRowItem.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        player.getDeck().removeCardByIndex(index);
                        buildPanel();
                    }
                });
                listTable.add(cardRowItem).width(300).height(40).padBottom(5).row();
            }
        }

        this.add(listTable).expand().top().row();

        TextButton saveButton = new TextButton("SAVE STRATEGY", customButtonStyle);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (player.getDeck().isValidForBattle()) {
                    game.saveProgress();
                    game.setScreen(new MainMenuScreen(game));
                } else {
                    System.out.println("Deck is Empty");
                }
            }
        });
        this.add(saveButton).size(220, 50).padBottom(10);
    }

    public void tryAddCardToDeck(Card card) {
        boolean success = player.getDeck().addCard(card);
        if (success) {
            buildPanel();
        }
    }
}
