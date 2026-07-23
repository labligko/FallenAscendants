package com.fallenascendants.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.fallenascendants.FallenAscendantsGame;
import com.fallenascendants.audio.SFXManager;

public class InputNameScreen implements Screen {
    private final FallenAscendantsGame game;
    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;

    // Deklarasi Tekstur Tombol Custom
    private Texture buttonNormal;
    private Texture buttonHover;
    private Texture buttonPressed;

    private BitmapFont titleFont;
    private BitmapFont buttonFont;

    public InputNameScreen(FallenAscendantsGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);

        // Background Screen
        backgroundTexture = new Texture(Gdx.files.internal("background/background_lobby/MainMenuBackgroundFix.png"));
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setSize(1280, 720);
        stage.addActor(backgroundImage);

        // Load Tekstur Tombol Custom
        buttonNormal = new Texture(Gdx.files.internal("Button/PrimaryButton.png"));
        buttonNormal.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        buttonHover = new Texture(Gdx.files.internal("Button/HoverButton.png"));
        buttonHover.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        buttonPressed = new Texture(Gdx.files.internal("Button/PressedButton.png"));
        buttonPressed.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        FullscreenToggle.attach(stage);

        generateFonts();

        // Setup Tabel Utama di tengah layar
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Label Judul
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, new Color(0.9f, 0.8f, 0.6f, 1f));
        Label titleLabel = new Label("KELAHIRAN BARU", titleStyle);

        // Deskripsi
        Label.LabelStyle descStyle = new Label.LabelStyle(buttonFont, Color.WHITE);
        Label descLabel = new Label("Masukkan Nama Karaktermu:", descStyle);

        // Text Field untuk input (Memakai style bawaan uiskin.json)
        final TextField nameField = new TextField("", skin);
        nameField.setMaxLength(16); // Batasi maksimal karakter nama
        nameField.setAlignment(1); // Rata tengah

        // IMPLEMENTASI STYLE TOMBOL CUSTOM
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.up = new TextureRegionDrawable(buttonNormal);
        btnStyle.over = new TextureRegionDrawable(buttonHover);
        btnStyle.down = new TextureRegionDrawable(buttonPressed);
        btnStyle.font = buttonFont;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.overFontColor = new Color(0.9f, 0.8f, 0.6f, 1f);

        // Pergeseran teks halus saat tombol ditekan
        btnStyle.pressedOffsetX = 1;
        btnStyle.pressedOffsetY = -1;

        TextButton confirmButton = new TextButton("CONFIRM", btnStyle);

        // Event listener saat kursor melintas (Hover Sound)
        confirmButton.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    SFXManager.play("sound/sound_effect/hoverbutton.mp3", game.getSfxVolume());
                }
            }
        });

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    SFXManager.play("sound/sound_effect/clickbutton.mp3", game.getSfxVolume());
                    String typedName = nameField.getText().trim();

                    // Mencegah nama kosong
                    if (typedName.isEmpty()) {
                        Toast.show(stage, "Nama tidak boleh kosong!", 2f);
                        return false;
                    }

                    // Panggil method pembuatan akun baru di Main Game
                    game.processNewPlayer(typedName);
                    return true;
                }
                return false;
            }
        });

        // Logika Klik
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SFXManager.play("sound/sound_effect/clickbutton.mp3", game.getSfxVolume());
                String typedName = nameField.getText().trim();

                // Mencegah nama kosong
                if (typedName.isEmpty()) {
                    Toast.show(stage, "Nama tidak boleh kosong!", 2f);
                    return;
                }

                // Panggil method pembuatan akun baru di Main Game
                game.processNewPlayer(typedName);
            }
        });

        // Menyusun elemen ke dalam Tabel (Ukuran tombol disesuaikan: 216x85)
        table.add(titleLabel).padBottom(20).row();
        table.add(descLabel).padBottom(15).row();
        table.add(nameField).width(300).height(40).padBottom(30).row();
        table.add(confirmButton).width(216).height(85);
    }

    private void generateFonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/CinzelDecorative-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 48;
        parameter.color = new Color(0.9f, 0.8f, 0.6f, 1f);
        parameter.borderWidth = 2;
        parameter.borderColor = Color.BLACK;
        titleFont = generator.generateFont(parameter);

        parameter.size = 18;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1;
        buttonFont = generator.generateFont(parameter);

        generator.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (buttonNormal != null) buttonNormal.dispose();
        if (buttonHover != null) buttonHover.dispose();
        if (buttonPressed != null) buttonPressed.dispose();
        if (titleFont != null) titleFont.dispose();
        if (buttonFont != null) buttonFont.dispose();
    }
}
