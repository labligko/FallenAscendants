package com.fallenascendants.enumtype;

public enum Rarity {
    // Parameter: insetLeft, insetRight, insetTop, insetBottom (dalam persentase 0f - 1f)
    // Diukur langsung dari file PNG frame masing-masing rarity.
    COMMON(0.126f, 0.083f, 0.142f, 0.089f),
    RARE(0.126f, 0.082f, 0.122f, 0.085f),
    EPIC(0.126f, 0.082f, 0.122f, 0.087f),
    LEGENDARY(0.132f, 0.129f, 0.141f, 0.137f);

    private final float insetLeft;
    private final float insetRight;
    private final float insetTop;
    private final float insetBottom;

    Rarity(float insetLeft, float insetRight, float insetTop, float insetBottom) {
        this.insetLeft = insetLeft;
        this.insetRight = insetRight;
        this.insetTop = insetTop;
        this.insetBottom = insetBottom;
    }

    public float getInsetLeft() { return insetLeft; }
    public float getInsetRight() { return insetRight; }
    public float getInsetTop() { return insetTop; }
    public float getInsetBottom() { return insetBottom; }
}
