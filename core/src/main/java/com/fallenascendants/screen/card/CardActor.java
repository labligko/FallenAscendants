package com.fallenascendants.screen.card;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.fallenascendants.model.Card;

public class CardActor extends Table {
    private static Texture solidPixel;

    private final Card cardData;
    private Texture cardTexture;
    private final Texture frameTexture;
    private final boolean isOwned;

    private Cell<Label> lvlCell;
    private Cell<Label> nameCell;
    private Label lvlLabel;
    private Label nameLabel;

    public CardActor(Card cardData, Skin skin, boolean isOwned, Label.LabelStyle fontStyle, Texture frameTexture) {
        this.cardData = cardData;
        this.frameTexture = frameTexture;
        this.isOwned = isOwned;

        this.setTransform(true);
        this.setOrigin(Align.center);

        this.setTouchable(Touchable.enabled);

        this.setBackground((com.badlogic.gdx.scenes.scene2d.utils.Drawable) null);

        String formattedName = cardData.getName().toLowerCase().replace(" ", "_");
        String factionFolder = "Void Corrupted";
        if (cardData.getFaction() != null) {
            String enumStr = cardData.getFaction().toString().toLowerCase().replace("_", " ");
            StringBuilder sb = new StringBuilder();
            for (String word : enumStr.split(" ")) {
                if (!word.isEmpty()) {
                    sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
                }
            }
            factionFolder = sb.toString().trim();
        }

        String texturePath = "card/gambar_kartu/" + factionFolder + "/" + formattedName + ".png";

        if (Gdx.files.internal(texturePath).exists()) {
            cardTexture = new Texture(Gdx.files.internal(texturePath), true);
            cardTexture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        } else {
            Pixmap pixmap = new Pixmap(160, 240, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.DARK_GRAY);
            pixmap.fill();
            cardTexture = new Texture(pixmap);
            pixmap.dispose();
        }

        lvlLabel = new Label("Lvl." + cardData.getLevel(), fontStyle);
        nameLabel = new Label(cardData.getName(), fontStyle);
        nameLabel.setAlignment(Align.center);
        nameLabel.setWrap(true);

        lvlCell = this.add(lvlLabel).left();
        this.row();
        this.add().expandY().row();
        nameCell = this.add(nameLabel).expandX().fillX().center();

        updateLayout();

        this.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                CardActor.this.addAction(Actions.parallel(
                    Actions.scaleTo(1.05f, 1.05f, 0.1f)
                ));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                CardActor.this.addAction(Actions.scaleTo(1f, 1f, 0.1f));
            }
        });
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        updateLayout();
    }

    private void updateLayout() {
        if (nameCell == null || lvlCell == null) return;

        float h = getHeight();
        float w = getWidth();

        com.fallenascendants.enumtype.Rarity rarity = cardData.getRarity();
        float insetLeft = w * rarity.getInsetLeft() * 0.85f;
        float insetRight  = w * rarity.getInsetRight() * 0.85f;
        float insetTop = h * rarity.getInsetTop();
        float insetBottom = h * rarity.getInsetBottom();

        float maxTextWidth = w - insetLeft - insetRight;
        nameCell.width(maxTextWidth);

        nameCell.padBottom(insetBottom + 20f)
            .padLeft(insetLeft)
            .padRight(insetRight);

        lvlCell.padLeft(insetLeft + 14f)
            .padTop(insetTop + 7f);

        invalidate();
    }

    private static Texture getSolidPixel() {
        if (solidPixel == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(1f, 1f, 1f, 1f);
            pixmap.fill();
            solidPixel = new Texture(pixmap);
            pixmap.dispose();
        }
        return solidPixel;
    }

    // Fungsi baru untuk dipanggil dari RewardScreen
    public void setLabelsVisible(boolean visible) {
        if (nameLabel != null) nameLabel.setVisible(visible);
        if (lvlLabel != null) lvlLabel.setVisible(visible);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        this.validate();

        boolean usesTransform = isTransform();
        if (usesTransform) applyTransform(batch, computeTransform());

        Color oldColor = batch.getColor();

        Color targetColor = isOwned ? Color.WHITE : new Color(0.2f, 0.2f, 0.2f, 1f);
        batch.setColor(targetColor.r, targetColor.g, targetColor.b, targetColor.a * parentAlpha);

        float w = getWidth();
        float h = getHeight();

        com.fallenascendants.enumtype.Rarity rarity = cardData.getRarity();
        float insetLeft   = w * rarity.getInsetLeft();
        float insetRight  = w * rarity.getInsetRight();
        float insetTop    = h * rarity.getInsetTop();
        float insetBottom = h * rarity.getInsetBottom();

        batch.draw(cardTexture,
            insetLeft,
            insetBottom,
            w - insetLeft - insetRight,
            h - insetTop - insetBottom);

        batch.setColor(0f, 0f, 0f, 0f * parentAlpha);

        float platePad = 4f;

        // Tambahkan pengecekan nameLabel.isVisible()
        if (nameLabel.isVisible() && nameCell.getActorHeight() > 0) {
            batch.draw(getSolidPixel(),
                insetLeft,
                nameCell.getActorY() - platePad,
                w - insetLeft - insetRight,
                nameCell.getActorHeight() + platePad * 2);
        }

        // Tambahkan pengecekan lvlLabel.isVisible()
        if (lvlLabel.isVisible() && lvlCell.getActorHeight() > 0) {
            batch.draw(getSolidPixel(),
                lvlCell.getActorX() - platePad,
                lvlCell.getActorY() - platePad,
                lvlCell.getActorWidth() + platePad * 2,
                lvlCell.getActorHeight() + platePad * 2);
        }

        batch.setColor(targetColor.r, targetColor.g, targetColor.b, targetColor.a * parentAlpha);
        batch.draw(frameTexture, 0, 0, w, h);

        batch.setColor(oldColor);

        drawChildren(batch, parentAlpha);

        if (usesTransform) resetTransform(batch);
    }

    public Card getCardData() {
        return cardData;
    }

    public void dispose() {
        if (cardTexture != null) cardTexture.dispose();
    }
}
