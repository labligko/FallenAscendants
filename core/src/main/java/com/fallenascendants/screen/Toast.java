package com.fallenascendants.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

/**
 * Themed toast notification: shows a short gothic-styled message with a
 * full-screen click-blocker behind it, then fades away on its own.
 *
 * Usage: Toast.show(stage, "Deck kamu belum lengkap!");
 */
public class Toast {

    private static Texture toastTexture;
    private static Texture dimTexture;
    private static Label.LabelStyle toastLabelStyle;

    private static Texture getToastTexture() {
        if (toastTexture == null) {
            toastTexture = new Texture(Gdx.files.internal("Panel/ToolTipPanel.png"));
            toastTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        return toastTexture;
    }

    private static Texture getDimTexture() {
        if (dimTexture == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(0f, 0f, 0f, 0.55f);
            pixmap.fill();
            dimTexture = new Texture(pixmap);
            pixmap.dispose();
        }
        return dimTexture;
    }

    private static Label.LabelStyle getToastLabelStyle() {
        if (toastLabelStyle == null) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/CinzelDecorative-Regular.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 26;
            parameter.color = Color.WHITE;
            parameter.borderWidth = 2;
            parameter.borderColor = Color.BLACK;
            BitmapFont font = generator.generateFont(parameter);
            generator.dispose();

            toastLabelStyle = new Label.LabelStyle(font, parameter.color);
        }
        return toastLabelStyle;
    }

    public static void show(Stage stage, String message) {
        show(stage, message, 2f);
    }

    public static void show(Stage stage, String message, float visibleSeconds) {
        // Blocker: nutupin seluruh layar, nyerep semua klik selagi toast tampil
        Image blocker = new Image(new TextureRegionDrawable(getDimTexture()));
        blocker.setSize(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        blocker.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true; // serap klik, jangan diteruskan ke actor di bawahnya
            }
        });

        Image background = new Image(new TextureRegionDrawable(getToastTexture()));

        Label label = new Label(message, getToastLabelStyle());
        label.setWrap(true);
        label.setAlignment(Align.center);

        Container<Label> textContainer = new Container<>(label);
        textContainer.pad(60, 90, 65, 90);
        textContainer.fill();

        Stack stack = new Stack();
        stack.add(background);
        stack.add(textContainer);

        float width = 500f;
        float height = width / 1.5f;

        stack.setSize(width, height);
        stack.setPosition(
            (stage.getViewport().getWorldWidth() - width) / 2f,
            (stage.getViewport().getWorldHeight() - height) / 2f
        );

        blocker.getColor().a = 0f;
        stack.getColor().a = 0f;
        stage.addActor(blocker);
        stage.addActor(stack);

        blocker.addAction(Actions.sequence(
            Actions.fadeIn(0.25f),
            Actions.delay(visibleSeconds),
            Actions.fadeOut(0.4f),
            Actions.removeActor()
        ));

        stack.addAction(Actions.sequence(
            Actions.fadeIn(0.25f),
            Actions.delay(visibleSeconds),
            Actions.fadeOut(0.4f),
            Actions.removeActor()
        ));
    }
}
