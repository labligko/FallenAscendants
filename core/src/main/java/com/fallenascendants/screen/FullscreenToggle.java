package com.fallenascendants.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

/** Attach sekali di show() tiap screen biar F11 toggle fullscreen konsisten di semua tempat. */
public class FullscreenToggle {
    public static void attach(Stage stage) {
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.F11) {
                    if (!Gdx.graphics.isFullscreen()) {
                        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                    } else {
                        Gdx.graphics.setWindowedMode(1280, 720);
                    }
                    return true;
                }
                return false;
            }
        });
    }
}
