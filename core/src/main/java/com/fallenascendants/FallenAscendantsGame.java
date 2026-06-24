//package com.fallenascendants;
//
//import com.badlogic.gdx.Game;
//import com.fallenascendants.screen.MainMenuScreen;
//
//public class FallenAscendantsGame extends Game {
//    @Override
//    public void create() {
//        setScreen(new MainMenuScreen(this));
//    }
//}


package com.fallenascendants;

import com.badlogic.gdx.Game;
import com.fallenascendants.debug.BattleTester;
import com.fallenascendants.screen.MainMenuScreen;

public class FallenAscendantsGame extends Game {
    @Override
    public void create() {
        BattleTester.runTest();
        setScreen(new MainMenuScreen(this));
    }
}
