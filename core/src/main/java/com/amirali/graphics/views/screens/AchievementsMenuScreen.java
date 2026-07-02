package com.amirali.graphics.views.screens;

import com.amirali.graphics.uiManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class AchievementsMenuScreen extends AbstractScreen {

    @Override
    public void show() {
        super.show();

        // --- NEW: Load Global Achievements ---
        Preferences prefs = Gdx.app.getPreferences("HollowKnightAchievements");

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();

        Label title = new Label("ACHIEVEMENTS", skin, "title");
        title.setFontScale(1.5f);
        mainTable.add(title).padTop(40).padBottom(30).row();

        Table listTable = new Table();
        listTable.top();
        listTable.defaults().padBottom(15).fillX();

        // Read dynamically from Preferences
        addAchievement(listTable, "Completion", "Finish the game.",
            prefs.getBoolean("Completion", false), "Achievements/achievement__0000_100_complete.png");

        addAchievement(listTable, "Speedrun", "Finish the game in under 10 minutes.",
            prefs.getBoolean("Speedrun", false), "Achievements/achievement_fast_finish.png");

        addAchievement(listTable, "True Hunter", "Kill all enemy types in the game.",
            prefs.getBoolean("True Hunter", false), "Achievements/achievement_Hunter_Journal.png");

        addAchievement(listTable, "Defeat False Knight", "Defeat the False Knight boss.",
            prefs.getBoolean("Defeat False Knight", false), "Achievements/achievement_false_knight.png");

        addAchievement(listTable, "Soul Vessel", "Reach max capacity (99 SOUL) for the first time.",
            prefs.getBoolean("Soul Vessel", false), "Achievements/achievement__0018_vessel_01.png");

        ScrollPane scrollPane = new ScrollPane(listTable, skin);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setVariableSizeKnobs(false);

        mainTable.add(scrollPane).width(550).height(400).padBottom(30).row();
        Table backBtn = createHoverButton("Back to Main Menu", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                uiManager.setScreen(new MainMenuScreen());
            }
        });

        mainTable.add(backBtn).padBottom(30);
        rootTable.add(mainTable).grow();
    }

    private void addAchievement(Table table, String title, String description, boolean isUnlocked, String iconPath) {
        Table card = new Table();
        card.pad(15).left();

        Texture iconTex;
        try {
            iconTex = new Texture(Gdx.files.internal(iconPath));
            iconTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        } catch (Exception e) {
            iconTex = new Texture(Gdx.files.internal("ui/uiskin.png"));
        }
        Image iconImage = new Image(iconTex);

        if (!isUnlocked) {
            iconImage.setColor(Color.DARK_GRAY);
        }

        Color titleColor = isUnlocked ? Color.GOLD : Color.DARK_GRAY;
        Color descColor = isUnlocked ? Color.WHITE : Color.LIGHT_GRAY;
        String statusIcon = isUnlocked ? "[UNLOCKED] " : "[LOCKED] ";

        Table textTable = new Table();
        Label titleLabel = new Label(statusIcon + title, skin, "subtitle");
        titleLabel.setColor(titleColor);

        Label descLabel = new Label(description, skin);
        descLabel.setColor(descColor);
        descLabel.setWrap(true);

        textTable.add(titleLabel).left().padBottom(5).row();
        textTable.add(descLabel).growX().left();

        card.add(iconImage).size(64, 64).padRight(20);
        card.add(textTable).growX();

        table.add(card).row();
    }
}
