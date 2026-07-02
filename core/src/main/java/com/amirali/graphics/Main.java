package com.amirali.graphics;

import com.amirali.graphics.views.screens.MainMenuScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends Game {
    @Override
    public void create() {
        GameAssetManager.init();
        LanguageManager.init();
        KeyBindings.load();
        BrightnessManager.init();
        BackgroundManager.init();
        uiManager.init(this);

        MainMenuScreen mainMenuScreen = new MainMenuScreen();
        setScreen(mainMenuScreen);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 0);
        super.render();
        BrightnessManager.render();
    }
}
