package com.fallenascendants.screen.deck;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fallenascendants.FallenAscendantsGame;
import com.fallenascendants.data.CardDatabase;
import com.fallenascendants.model.Card;
import com.fallenascendants.model.Player;
import com.fallenascendants.screen.MainMenuScreen;
import com.fallenascendants.screen.card.CardActor; // Di-import dari folder sebelah

import java.util.List;

public class CollectionPoolPanel extends Table {
    private final FallenAscendantsGame game;
    private final Player player;
    private final Skin skin;
    private final DeckSlotPanel rightPanelReference;

    private int currentPage = 0;
    private final int CARDS_PER_PAGE = 8;

    private final Label.LabelStyle cardLabelStyle;
    private final TextButton.TextButtonStyle customButtonStyle;
    private final Label.LabelStyle titleStyle;

    private final Texture commonFrame, rareFrame, epicFrame, legendaryFrame, specialFrame;
    private final Texture buttonNormal, buttonHover, buttonPressed;

    public CollectionPoolPanel(FallenAscendantsGame game, Player player, Skin skin, BitmapFont uiFont, BitmapFont titleFont, DeckSlotPanel rightPanel) {
        this.game = game;
        this.player = player;
        this.skin = skin;
        this.rightPanelReference = rightPanel;

        cardLabelStyle = new Label.LabelStyle(uiFont, Color.WHITE);
        titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);

        buttonNormal = new Texture(Gdx.files.internal("Button/PrimaryButton.png"));
        buttonHover = new Texture(Gdx.files.internal("Button/HoverButton.png"));
        buttonPressed = new Texture(Gdx.files.internal("Button/PressedButton.png"));

        commonFrame = new Texture(Gdx.files.internal("card_frames/commonFrame.png"));
        rareFrame = new Texture(Gdx.files.internal("card_frames/rareFrame.png"));
        epicFrame = new Texture(Gdx.files.internal("card_frames/epicFrame.png"));
        legendaryFrame = new Texture(Gdx.files.internal("card_frames/legendaryFrame.png"));
        specialFrame = new Texture(Gdx.files.internal("card_frames/specialFrame.png"));

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
        this.left().top();

        TextButton backButton = new TextButton("< Back", customButtonStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        this.add(backButton).left().size(100, 40).padBottom(10).row();

        Label panelTitle = new Label("AVAILABLE COLLECTION", titleStyle);
        this.add(panelTitle).left().padBottom(15).row();

        Table gridTable = new Table();
        List<Card> allCards = CardDatabase.getAllCards();

        int startIndex = currentPage * CARDS_PER_PAGE;
        int endIndex = Math.min(startIndex + CARDS_PER_PAGE, allCards.size());
        int columns = 4;
        int currentColumn = 0;

        for (int i = startIndex; i < endIndex; i++) {
            final Card card = allCards.get(i);
            Texture frame = getFrameByRarity(card.getRarity() != null ? card.getRarity().name() : "common");

            CardActor cardActor = new CardActor(card, skin, true, cardLabelStyle, frame);
            cardActor.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    rightPanelReference.tryAddCardToDeck(card);
                }
            });

            gridTable.add(cardActor).pad(6).size(160, 240);
            currentColumn++;
            if (currentColumn >= columns) {
                currentColumn = 0;
                gridTable.row();
            }
        }
        this.add(gridTable).expand().fill().row();

        Table pagination = new Table();
        int maxPage = (int) Math.ceil((double) allCards.size() / CARDS_PER_PAGE);

        TextButton prevBtn = new TextButton("<", customButtonStyle);
        Label pageLbl = new Label(" PAGE " + (currentPage + 1) + " / " + maxPage + " ", cardLabelStyle);
        TextButton nextBtn = new TextButton(">", customButtonStyle);

        if (currentPage == 0) prevBtn.setDisabled(true);
        if (currentPage >= maxPage - 1) nextBtn.setDisabled(true);

        prevBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentPage > 0) { currentPage--; buildPanel(); }
            }
        });
        nextBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentPage < maxPage - 1) { currentPage++; buildPanel(); }
            }
        });

        pagination.add(prevBtn).size(40, 35).padRight(10);
        pagination.add(pageLbl);
        pagination.add(nextBtn).size(40, 35).padLeft(10);
        this.add(pagination).padTop(10).center();
    }

    private Texture getFrameByRarity(String rarityStr) {
        switch (rarityStr.toLowerCase()) {
            case "rare": return rareFrame;
            case "epic": return epicFrame;
            case "legendary": return legendaryFrame;
            case "special": case "limited": return specialFrame;
            default: return commonFrame;
        }
    }

    public void dispose() {
        if (commonFrame != null) commonFrame.dispose();
        if (rareFrame != null) rareFrame.dispose();
        if (epicFrame != null) epicFrame.dispose();
        if (legendaryFrame != null) legendaryFrame.dispose();
        if (specialFrame != null) specialFrame.dispose();
        if (buttonNormal != null) buttonNormal.dispose();
        if (buttonHover != null) buttonHover.dispose();
        if (buttonPressed != null) buttonPressed.dispose();
    }
}
