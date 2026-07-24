package com.fallenascendants.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class BattleLogBanner extends Actor {
    private final Texture gradient;
    private final Texture solid;
    private final BitmapFont font;
    private String text = "";

    BattleLogBanner(Texture gradient, Texture solid, BitmapFont font) {
        this.gradient = gradient;
        this.solid = solid;
        this.font = font;
        getColor().a = 0f;
    }

    void setText(String text) {
        this.text = text == null ? "" : text;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float alpha = getColor().a * parentAlpha;
        if (alpha <= 0.01f || text.isEmpty()) return;

        float x = getX();
        float y = getY();
        float w = getWidth();
        float h = getHeight();

        batch.setColor(0f, 0f, 0f, 0.25f * alpha);
        batch.draw(solid, x + 3, y - 3, w, h);

        batch.setColor(1f, 1f, 1f, alpha);
        batch.draw(gradient, x, y, w, h);

        batch.setColor(1f, 1f, 1f, 0.08f * alpha);
        batch.draw(solid, x, y + h - 1, w, 1);

        batch.setColor(1f, 1f, 1f, 0.05f * alpha);
        batch.draw(solid, x, y, w, 1);

        GlyphLayout layout = new GlyphLayout(font, text);
        font.setColor(1f, 1f, 1f, alpha);
        font.draw(batch, layout, x + 28, y + h / 2f + layout.height / 2f);

        batch.setColor(Color.WHITE);
    }
}
