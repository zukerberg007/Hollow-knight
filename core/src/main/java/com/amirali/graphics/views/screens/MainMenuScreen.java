package com.amirali.graphics.views.screens;

import com.amirali.graphics.GameAssetManager;
import com.amirali.graphics.uiManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;

import static com.amirali.graphics.LanguageManager.t;

public class MainMenuScreen extends AbstractScreen {

    @Override
    public void show() {
        super.show();

        Table mainLayout = new Table();
        mainLayout.setFillParent(true);
        mainLayout.top();

        Image titleImg = new Image(GameAssetManager.titleImage);
        titleImg.setScaling(Scaling.contain);

        mainLayout.add(titleImg).size(1050, 210).padTop(70).padBottom(110).row();

        Table menu = new Table();
        menu.defaults().spaceBottom(4).center();

        Table startBtn = createHoverButton(t("menu.start"), new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                uiManager.setScreen(new StartMenuScreen());
            }
        });

        Table settingsBtn = createHoverButton(t("menu.settings"), new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                uiManager.setScreen(new SettingsMenuScreen());
            }
        });

        Table achievementsBtn = createHoverButton(t("menu.achievements"), new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                uiManager.setScreen(new AchievementsMenuScreen());
            }
        });

        Table guideBtn = createHoverButton(t("menu.guide"), new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                uiManager.setScreen(new GuideMenuScreen());
            }
        });

        Table quitBtn = createHoverButton(t("menu.quit"), new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                Gdx.app.exit();
            }
        });

        menu.add(startBtn).row();
        menu.add(settingsBtn).row();
        menu.add(achievementsBtn).row();
        menu.add(guideBtn).row();
        menu.add(quitBtn).row();

        mainLayout.add(menu).row();

        rootTable.add(mainLayout).grow();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
