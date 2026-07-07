package com.fallenascendants.screen.card;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.fallenascendants.model.Card;

public class CardActor extends Table {
    private final Card cardData;
    private Texture cardTexture;
    private final Texture frameTexture;
    private final boolean isOwned;
    private com.badlogic.gdx.scenes.scene2d.ui.Cell<Label> nameCell;

    public CardActor(Card cardData, Skin skin, boolean isOwned, Label.LabelStyle fontStyle, Texture frameTexture) {
        this.cardData = cardData;
        this.frameTexture = frameTexture;
        this.isOwned = isOwned;

        // Izinkan transformasi agar animasi hover pembesaran (1.05x) tidak patah
        this.setTransform(true);
        this.setOrigin(Align.center);

        // Bersihkan total background table bawaan agar tidak ada gambar duplikat yang meluber
        this.setBackground((com.badlogic.gdx.scenes.scene2d.utils.Drawable) null);

        // 1. LOGIKA PATH TEKSTUR ILUSTRASI KARTU
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

        // 2. TATA LETAK TEKS MENU (Tetap menggunakan sistem Table Scene2D terdepan)
        this.top().pad(14);

        Label lvlLabel = new Label("Lvl." + cardData.getLevel(), fontStyle);
        Label nameLabel = new Label(cardData.getName(), fontStyle);
        nameLabel.setAlignment(Align.center);
        nameLabel.setWrap(true);

        this.add(lvlLabel).left().row();
        // PENTING: padBottom TIDAK boleh angka mati (misal 28px), karena tinggi
        // "plat nama" tiap frame beda-beda (lihat Rarity.getInsetBottom()).
        // Nilai awal 0 di sini cuma placeholder — nilai asli dihitung di
        // sizeChanged() begitu actor sudah punya ukuran nyata dari layout.
        nameCell = this.add(nameLabel).expand().fillX().center();
        updateNamePadding();

        // 3. TEMPLATE HOVER & SISTEM KURSOR POINTER
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

    // Dipanggil otomatis oleh Scene2D setiap kali width/height actor berubah
    // (misal saat pertama kali di-layout oleh grid/parent). getHeight() di
    // constructor masih 0, jadi padding baru valid dihitung di sini.
    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        updateNamePadding();
    }

    private void updateNamePadding() {
        if (nameCell == null) return;
        float namePadBottom = getHeight() * cardData.getRarity().getInsetBottom() * 0.55f;
        nameCell.padBottom(namePadBottom);
        invalidate();
    }

    // 4. KUNCI UTAMA: CUSTOM DRAW UNTUK PRESISI GAMBAR VS FRAME
    @Override
    public void draw(Batch batch, float parentAlpha) {
        // PENTING: pasang transform (scale hover) SEBELUM menggambar apa pun,
        // supaya art, frame, DAN teks semua ikut membesar bersamaan saat di-hover.
        // Sebelumnya applyTransform baru terjadi di dalam super.draw(), jadi
        // art+frame yang digambar manual di sini tidak ikut ter-scale.
        boolean usesTransform = isTransform();
        if (usesTransform) applyTransform(batch, computeTransform());

        // Simpan warna batch asli agar tidak merusak elemen UI lain di screen
        Color oldColor = batch.getColor();

        // Tentukan warna render berdasarkan status kepemilikan (Redup total jika tidak punya)
        Color targetColor = isOwned ? Color.WHITE : new Color(0.2f, 0.2f, 0.2f, 1f);
        batch.setColor(targetColor.r, targetColor.g, targetColor.b, targetColor.a * parentAlpha);

        // Karena transform sudah dipasang, gambar digambar relatif ke (0,0) lokal, bukan getX()/getY() global
        float w = getWidth();
        float h = getHeight();

        // LANGKAH A: Gambar Ilustrasi Monster di Lapisan Bawah
        // Inset dihitung berdasarkan PERSENTASE ukuran kartu, bukan piksel tetap,
        // dan diambil dari Rarity karena tiap frame punya ukuran lubang window yang berbeda.
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

        // LANGKAH B: Gambar Bingkai Kelangkaan di Lapisan Atas (Menimpa Gambar)
        // Frame ditarik full 100% mengisi batas terluar sel (160x240)
        batch.draw(frameTexture, 0, 0, w, h);

        // Kembalikan warna batch ke semula sebelum menggambar teks bawaan
        batch.setColor(oldColor);

        // LANGKAH C: Render Teks Lvl & Nama Kartu paling depan (children saja, bukan super.draw()
        // penuh, karena applyTransform sudah kita pasang manual di atas)
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
