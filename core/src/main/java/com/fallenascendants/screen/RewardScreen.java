package com.fallenascendants.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.math.Interpolation;
import com.fallenascendants.FallenAscendantsGame;
import com.fallenascendants.audio.MusicManager;
import com.fallenascendants.audio.SFXManager;
import com.fallenascendants.enumtype.Rarity;
import com.fallenascendants.model.Card;
import com.fallenascendants.model.ProgressionManager;
import com.fallenascendants.screen.card.CardActor;

import java.util.ArrayList;
import java.util.List;

public class RewardScreen implements Screen {
    private static final int CARD_WIDTH = 110;
    private static final int CARD_HEIGHT = 160;

    private final FallenAscendantsGame game;
    private final ProgressionManager.BattleRewards rewards;

    private Stage stage;
    private Skin skin;

    private Texture backgroundTexture;
    private Texture rewardPanelTexture;
    private Texture backFrameCardTexture;
    private Texture commonFrame, rareFrame, epicFrame, legendaryFrame;
    private Texture buttonNormal, buttonHover, buttonPressed;
    private BitmapFont titleFont;
    private BitmapFont uiFont;
    private Label.LabelStyle cardLabelStyle;
    private Label.LabelStyle titleLabelStyle;
    private Label.LabelStyle bodyLabelStyle;

    private final List<CardActor> createdCardActors = new ArrayList<>();

    private Label revealLabel;
    private Container<com.badlogic.gdx.scenes.scene2d.Actor>[] cardContainers;
    private boolean rewardClaimed = false;
    private TextButton continueButton;

    public RewardScreen(FallenAscendantsGame game, ProgressionManager.BattleRewards rewards) {
        this.game = game;
        this.rewards = rewards;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        FullscreenToggle.attach(stage);

        String backgroundPath = rewards.isWin()
            ? "background/background_lobby/WinBackground.png"
            : "background/background_lobby/LoseBackground.png";
        backgroundTexture = new Texture(Gdx.files.internal(backgroundPath));
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setSize(1280, 780);
        stage.addActor(backgroundImage);

        rewardPanelTexture = new Texture(Gdx.files.internal("Panel/RewardPanel.png"));
        rewardPanelTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        backFrameCardTexture = new Texture(Gdx.files.internal("card_frames/backFrameCard.png"));
        backFrameCardTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        commonFrame = new Texture(Gdx.files.internal("card_frames/commonFrame.png"));
        rareFrame = new Texture(Gdx.files.internal("card_frames/rareFrame.png"));
        epicFrame = new Texture(Gdx.files.internal("card_frames/epicFrame.png"));
        legendaryFrame = new Texture(Gdx.files.internal("card_frames/legendaryFrame.png"));

        buttonNormal = new Texture(Gdx.files.internal("Button/PrimaryButton.png"));
        buttonNormal.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        buttonHover = new Texture(Gdx.files.internal("Button/HoverButton.png"));
        buttonHover.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        buttonPressed = new Texture(Gdx.files.internal("Button/PressedButton.png"));
        buttonPressed.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/CinzelDecorative-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;

        parameter.size = 44;
        parameter.color = new Color(0.9f, 0.8f, 0.6f, 1f);
        parameter.borderWidth = 2;
        parameter.borderColor = Color.BLACK;
        titleFont = generator.generateFont(parameter);

        parameter.size = 18;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1;
        parameter.borderColor = Color.BLACK;
        uiFont = generator.generateFont(parameter);
        generator.dispose();

        cardLabelStyle = new Label.LabelStyle(uiFont, Color.WHITE);
        titleLabelStyle = new Label.LabelStyle(titleFont, new Color(0.9f, 0.8f, 0.6f, 1f));
        bodyLabelStyle = new Label.LabelStyle(uiFont, Color.WHITE);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(buttonNormal);
        buttonStyle.over = new TextureRegionDrawable(buttonHover);
        buttonStyle.down = new TextureRegionDrawable(buttonPressed);
        buttonStyle.font = uiFont;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = new Color(0.9f, 0.8f, 0.6f, 1f);
        buttonStyle.pressedOffsetX = 1;
        buttonStyle.pressedOffsetY = -1;

        int panelWidth = 1050;
        int panelHeight = 700;
        Image panelImage = new Image(new TextureRegionDrawable(rewardPanelTexture));
        panelImage.setSize(panelWidth, panelHeight);
        panelImage.setPosition((1280 - panelWidth) / 2f, (720 - panelHeight) / 2f);
        stage.addActor(panelImage);

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        stage.addActor(root);

        continueButton = new TextButton("Continue", buttonStyle);

        Label title = new Label(rewards.isWin() ? "VICTORY!" : "DEFEAT", titleLabelStyle);
        title.setAlignment(Align.center);
        root.add(title).padBottom(15).row();

        Label goldLabel = new Label(
            "Gold Earned: " + rewards.getGoldEarned()
                + "\nTotal Gold: " + game.getPlayer().getGold(),
            bodyLabelStyle
        );
        goldLabel.setAlignment(Align.center);
        root.add(goldLabel).padBottom(20).row();

        if (rewards.isWin()) {
            Label chooseLabel = new Label("Choose 1 New Card:", bodyLabelStyle);
            chooseLabel.setAlignment(Align.center);
            root.add(chooseLabel).padBottom(12).row();

            Table cardRow = new Table();
            List<Card> options = rewards.getCardOptions();
            cardContainers = new Container[options.size()];

            for (int i = 0; i < options.size(); i++) {
                final int optionIndex = i;

                Image backImage = new Image(backFrameCardTexture);

                Container<com.badlogic.gdx.scenes.scene2d.Actor> container = new Container<>(backImage);
                container.setTransform(true);
                container.setOrigin(Align.center);
                container.size(CARD_WIDTH, CARD_HEIGHT);
                cardContainers[i] = container;

                container.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (rewardClaimed) {
                            return;
                        }
                        onCardChosen(optionIndex, container);
                    }
                });

                cardRow.add(container).size(CARD_WIDTH, CARD_HEIGHT).padRight(12);
            }

            root.add(cardRow).padBottom(15).row();

            revealLabel = new Label("", bodyLabelStyle);
            revealLabel.setAlignment(Align.center);
            root.add(revealLabel).padBottom(15).row();
        } else {
            Label loseLabel = new Label("Your Team Has Fallen. No New Cards.", bodyLabelStyle);
            loseLabel.setAlignment(Align.center);
            root.add(loseLabel).padBottom(25).row();
        }

        if (rewards.isWin()) {
            continueButton.setDisabled(true);
        }
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (rewards.isWin() && !rewardClaimed) {
                    return;
                }
                game.saveProgress();
                game.setScreen(new MainMenuScreen(game));
            }
        });
        root.add(continueButton).width(200).height(70);

        if (rewards.isWin()) {
            MusicManager.play("sound/background_music/victory_theme.mp3", true, game.getMusicVolume());
        } else {
            MusicManager.play("sound/background_music/defeat_theme.mp3", true, game.getMusicVolume());
        }
    }

    private void onCardChosen(int optionIndex, Container<com.badlogic.gdx.scenes.scene2d.Actor> container) {
        Card claimed = ProgressionManager.claimCardReward(game.getPlayer(), rewards, optionIndex);
        if (claimed == null) {
            return;
        }

        rewardClaimed = true;
        continueButton.setDisabled(false);

        revealLabel.setText("You got: " + claimed.getName() + " (" + claimed.getRarity() + ")!");

        for (Container<com.badlogic.gdx.scenes.scene2d.Actor> other : cardContainers) {
            other.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);
        }

        Texture frame = getFrameByRarity(claimed.getRarity());
        CardActor revealedCardActor = new CardActor(claimed, skin, true, cardLabelStyle, frame);

        // Menyembunyikan Label (Nama & Level) beserta latar hitam transparannya
        revealedCardActor.setLabelsVisible(false);

        createdCardActors.add(revealedCardActor);

        container.addAction(Actions.sequence(
            Actions.scaleTo(0f, 1f, 0.15f, Interpolation.pow2In),
            Actions.run(() -> {
                // Mainkan sound effect persis ketika kartu di tengah animasi balik (skala X = 0)
                SFXManager.play("sound/sound_effect/flip.mp3");
                container.setActor(revealedCardActor);
            }),
            Actions.scaleTo(1f, 1f, 0.15f, Interpolation.pow2Out)
        ));
    }

    private Texture getFrameByRarity(Rarity rarity) {
        if (rarity == null) {
            return commonFrame;
        }
        switch (rarity) {
            case RARE:
                return rareFrame;
            case EPIC:
                return epicFrame;
            case LEGENDARY:
                return legendaryFrame;
            default:
                return commonFrame;
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
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (rewardPanelTexture != null) rewardPanelTexture.dispose();
        if (backFrameCardTexture != null) backFrameCardTexture.dispose();
        if (commonFrame != null) commonFrame.dispose();
        if (rareFrame != null) rareFrame.dispose();
        if (epicFrame != null) epicFrame.dispose();
        if (legendaryFrame != null) legendaryFrame.dispose();
        if (buttonNormal != null) buttonNormal.dispose();
        if (buttonHover != null) buttonHover.dispose();
        if (buttonPressed != null) buttonPressed.dispose();
        if (titleFont != null) titleFont.dispose();
        if (uiFont != null) uiFont.dispose();

        for (CardActor actor : createdCardActors) {
            actor.dispose();
        }
        createdCardActors.clear();
    }
}
