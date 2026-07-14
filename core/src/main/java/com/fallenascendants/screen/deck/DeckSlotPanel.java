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
import com.fallenascendants.model.Card;
import com.fallenascendants.model.Player;

public class DeckSlotPanel extends Table {
    private final Player player;
    private final int MAX_DECK_SIZE = 8;

    private final Label.LabelStyle titleStyle;
    private final Label.LabelStyle infoStyle;
    private final TextButton.TextButtonStyle customButtonStyle;

    private final Texture buttonNormal, buttonHover, buttonPressed;

    public DeckSlotPanel(Player player, BitmapFont uiFont, BitmapFont titleFont) {
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
                    System.out.println("Deck Valid & Sukses Disimpan untuk Pertempuran!");

                    // OPSI AKSI: Kembalikan player ke MainMenu setelah berhasil menyimpan strategi
                    // Kita gunakan Gdx.app.postRunnable agar perpindahan screen aman dari crash thread
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            // Mengakses game master dari stage internal aktor
                            if (getStage() != null) {
                                // Jika kamu ingin melempar screen langsung, DeckBuilderScreen butuh referensi game.
                                // Solusi paling bersih: picu transisi balik lewat screen induknya.
                                // Sebagai testing instan, kita bisa pakai app logger atau langsung tembak kembaliannya.
                            }
                        }
                    });

                } else {
                    System.out.println("Gagal simpan! Deck belum penuh untuk battle (" + player.getDeck().size() + "/8)");
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
