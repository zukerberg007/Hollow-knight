package com.amirali.graphics.views.modals;

import com.amirali.graphics.uiManager;
import com.amirali.graphics.views.screens.AbstractScreen;
import com.amirali.graphics.views.screens.GameScreen;
import com.amirali.graphics.views.screens.MainMenuScreen;
import com.amirali.graphics.views.screens.SettingsMenuScreen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import static com.amirali.graphics.LanguageManager.t;

public class PauseModal extends Modal {

    public PauseModal() {
        super();

        Label title = new Label(t("pause.title"), skin);
        title.setFontScale(1.5f);

        TextButton resumeButton = new TextButton(t("pause.continue"), skin);
        TextButton cheatsButton = new TextButton(t("pause.cheatCodes"), skin);
        TextButton settingsButton = new TextButton(t("menu.settings"), skin);
        TextButton exitButton = new TextButton(t("pause.saveExit"), skin);

        defaults().space(10);

        add(title).padBottom(20).row();
        add(resumeButton).width(200).row();
        add(cheatsButton).width(200).row();
        add(settingsButton).width(200).row();
        add(exitButton).width(200).row();

        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onResume();
            }
        });

        cheatsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onCheats();
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onSettings();
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onExit();
            }
        });
    }

    public void onResume() {
        this.hide();
    }

    public void onCheats() {
        if (uiManager.getScreen() instanceof AbstractScreen) {
            AbstractScreen screen = (AbstractScreen) uiManager.getScreen();
            screen.openToast(t("pause.cheatsToast"));
        }
    }

    public void onSettings() {
        new SettingsModal().show();
    }

    public void onExit() {
        if (uiManager.getScreen() instanceof GameScreen) {
            GameScreen currentScreen = (GameScreen) uiManager.getScreen();
            currentScreen.saveCurrentGame();
        }

        uiManager.setScreen(new MainMenuScreen());
    }
}
