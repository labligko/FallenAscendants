package com.fallenascendants.screen.card;

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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.fallenascendants.FallenAscendantsGame;
import com.fallenascendants.data.CardDatabase;
import com.fallenascendants.model.Card;
import com.fallenascendants.model.Player;
import com.fallenascendants.screen.MainMenuScreen;

import java.util.List;

public class CollectionScreen implements Screen {
    private final FallenAscendantsGame game;
    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;

    private BitmapFont titleFont;
    private BitmapFont cardFont;
    private Label.LabelStyle cardLabelStyle;

    private Table mainTable;
    private Table cardGridTable;
    private Table paginationTable;

    private Player player;
    private int currentPage = 0;
    private final int CARDS_PER_PAGE = 12;

    // card frame texture asset
    private Texture commonFrame, rareFrame, epicFrame, legendaryFrame, specialFrame;

    // Aset tekstur tombol kustom dari MainMenu
    private Texture buttonNormal;
    private Texture buttonHover;
    private Texture buttonPressed;
    private TextButton.TextButtonStyle customButtonStyle;

    public CollectionScreen(FallenAscendantsGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // Inisialisasi Player untuk manajemen testing fungsionalitas kepemilikan
        player = new Player("Reyzz");
        final List<Card> allCards = CardDatabase.getAllCards();

        // INPUT LISTENER GLOBAL: Menangani ESC (Kembali) dan Panah Keyboard Kanan/Kiri (Halaman)
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }

                int maxPage = (int) Math.ceil((double) (allCards != null ? allCards.size() : 0) / CARDS_PER_PAGE);

                if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
                    if (currentPage > 0) {
                        currentPage--;
                        refreshScreen();
                    }
                    return true;
                }
                if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
                    if (currentPage < maxPage - 1) {
                        currentPage++;
                        refreshScreen();
                    }
                    return true;
                }
                return false;
            }
        });

        // Pengaturan Background Tekstur Utamax
        backgroundTexture = new Texture(Gdx.files.internal("background/background_lobby/CollectionBackground.png"));
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Image bg = new Image(backgroundTexture);
        bg.setSize(1280, 720);
        stage.addActor(bg);

        // Memuat tekstur tombol kustom agar serasi dengan MainMenu
        buttonNormal = new Texture(Gdx.files.internal("Button/PrimaryButton.png"));
        buttonNormal.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        buttonHover = new Texture(Gdx.files.internal("Button/HoverButton.png"));
        buttonHover.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        buttonPressed = new Texture(Gdx.files.internal("Button/PressedButton.png"));
        buttonPressed.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        commonFrame = new Texture(Gdx.files.internal("card_frames/commonFrame.png"));
        commonFrame.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        rareFrame = new Texture(Gdx.files.internal("card_frames/rareFrame.png"));
        rareFrame.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        epicFrame = new Texture(Gdx.files.internal("card_frames/epicFrame.png"));
        epicFrame.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        legendaryFrame = new Texture(Gdx.files.internal("card_frames/legendaryFrame.png"));
        legendaryFrame.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        specialFrame = new Texture(Gdx.files.internal("card_frames/specialFrame.png"));
        specialFrame.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // Grid Utama Layout Kontainer
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().pad(20);
        stage.addActor(mainTable);

        cardGridTable = new Table();
        paginationTable = new Table();
    }

    private void refreshScreen() {
        displayPage(CardDatabase.getAllCards());
        rebuildUI(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void rebuildUI(int width, int height) {
        if (titleFont != null) titleFont.dispose();
        if (cardFont != null) cardFont.dispose();

        float scale = (float) height / 720f;

        // Racik Font Judul Utama secara HD Dinamis
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/CinzelDecorative-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = Math.round(36 * scale);
        parameter.color = new Color(0.9f, 0.8f, 0.6f, 1f);
        parameter.borderWidth = 2 * scale;
        parameter.borderColor = Color.BLACK;
        titleFont = generator.generateFont(parameter);

        // Racik Font Teks Info Kartu
        parameter.size = Math.round(13 * scale);
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1 * scale;
        parameter.borderColor = Color.BLACK;
        cardFont = generator.generateFont(parameter);
        generator.dispose();

        titleFont.getData().setScale(1f / scale);
        cardFont.getData().setScale(1f / scale);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        cardLabelStyle = new Label.LabelStyle(cardFont, Color.WHITE);

        // Racik ulang gaya tombol kustom menggunakan font tombol yang proporsional
        customButtonStyle = new TextButton.TextButtonStyle();
        customButtonStyle.up = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(buttonNormal);
        customButtonStyle.over = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(buttonHover);
        customButtonStyle.down = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(buttonPressed);
        customButtonStyle.font = cardFont;
        customButtonStyle.fontColor = Color.WHITE;
        customButtonStyle.overFontColor = new Color(0.9f, 0.8f, 0.6f, 1f);
        customButtonStyle.pressedOffsetX = 1;
        customButtonStyle.pressedOffsetY = -1;

        // Rekonstruksi Ulang Elemen Grid Tabel
        mainTable.clearChildren();
        cardGridTable.clearChildren();
        paginationTable.clearChildren();

        // Tombol Kembali dengan Gaya Kustom MainMenu
        TextButton backButton = new TextButton("< Back", customButtonStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        // Disertai penyesuaian ukuran tombol agar proporsional di baris menu atas
        mainTable.add(backButton).left().padBottom(5).size(100, 40).row();

        Label screenTitle = new Label("CARD ALBUM COLLECTION", titleStyle);
        mainTable.add(screenTitle).padBottom(15).row();

        List<Card> allCards = CardDatabase.getAllCards();
        displayPage(allCards);

        mainTable.add(cardGridTable).expand().fill().row();

        buildPaginationButtons(allCards);
        mainTable.add(paginationTable).padBottom(10);
    }

    private void displayPage(List<Card> allCards) {
        cardGridTable.clearChildren();
        if (allCards == null || allCards.isEmpty()) return;

        int startIndex = currentPage * CARDS_PER_PAGE;
        int endIndex = Math.min(startIndex + CARDS_PER_PAGE, allCards.size());

        int columns = 6;
        int currentColumn = 0;

        for (int i = startIndex; i < endIndex; i++) {
            final Card card = allCards.get(i);
            boolean isOwned = (i % 3 != 0);
            final boolean finalIsOwned = isOwned;

            String rarityStr = "common";
            if (card.getRarity() != null) {
                rarityStr = card.getRarity().name().toLowerCase();
            }

            // LOGIKA PEMILIHAN BINGKAI BERDASARKAN RARITY KARTU
            Texture chosenFrame = commonFrame;

            switch (rarityStr) {
                case "rare":
                    chosenFrame = rareFrame;
                    break;
                case "epic":
                    chosenFrame = epicFrame;
                    break;
                case "legendary":
                    chosenFrame = legendaryFrame;
                    break;
                case "special":
                case "limited":
                    chosenFrame = specialFrame;
                    break;
                default:
                    chosenFrame = commonFrame;
                    break;
            }

            CardActor cardActor = new CardActor(card, skin, isOwned, cardLabelStyle, chosenFrame);

            cardActor.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.Arrow);
                    game.setScreen(new CardDetailScreen(game, card, finalIsOwned));
                }
            });

            cardGridTable.add(cardActor).pad(8).size(160, 240);

            currentColumn++;
            if (currentColumn >= columns) {
                currentColumn = 0;
                cardGridTable.row();
            }
        }
    }

    private void buildPaginationButtons(final List<Card> allCards) {
        paginationTable.clearChildren();
        if (allCards == null || allCards.size() <= CARDS_PER_PAGE) return;

        int maxPage = (int) Math.ceil((double) allCards.size() / CARDS_PER_PAGE);

        // Menerapkan gaya kustom pada tombol navigasi halaman
        TextButton prevButton = new TextButton("<", customButtonStyle);
        Label pageLabel = new Label(" PAGE " + (currentPage + 1) + " / " + maxPage + " ", cardLabelStyle);
        TextButton nextButton = new TextButton(">", customButtonStyle);

        if (currentPage == 0) prevButton.setDisabled(true);
        if (currentPage >= maxPage - 1) nextButton.setDisabled(true);

        prevButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentPage > 0) {
                    currentPage--;
                    refreshScreen();
                }
            }
        });

        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int maxPage = (int) Math.ceil((double) allCards.size() / CARDS_PER_PAGE);
                if (currentPage < maxPage - 1) {
                    currentPage++;
                    refreshScreen();
                }
            }
        });

        paginationTable.add(prevButton).padRight(15).size(50, 40);
        paginationTable.add(pageLabel);
        paginationTable.add(nextButton).padLeft(15).size(50, 40);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.07f, 1f);
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
        if (titleFont != null) titleFont.dispose();
        if (cardFont != null) cardFont.dispose();
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
