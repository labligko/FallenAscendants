package com.fallenascendants;

import com.badlogic.gdx.Game;
import com.fallenascendants.screen.MainMenuScreen;

public class FallenAscendantsGame extends Game {
    @Override
    public void create() {
        setScreen(new MainMenuScreen(this));
    }
}
