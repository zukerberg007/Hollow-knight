package com.amirali.graphics.views.screens;

import com.amirali.graphics.GameAssetManager;
import com.amirali.graphics.models.GameData;
import com.amirali.graphics.models.Game;
import com.amirali.graphics.uiManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class EndScreen extends AbstractScreen {

    private final GameData finalData;

    public EndScreen(GameData finalData) {
        this.finalData = finalData;
    }

    @Override
    protected boolean showBackground() {
        return false; // Will use the solid black screen of AbstractScreen
    }

    @Override
    public void show() {
        super.show();

        // 1. Play the Custom Win Music
        if (GameAssetManager.winMusic != null) {
            GameAssetManager.winMusic.setLooping(false);
            GameAssetManager.winMusic.setVolume(0.8f);
            GameAssetManager.winMusic.play();
        }

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();

        Label title = new Label("VICTORY", skin, "title");
        title.setColor(Color.GOLD);
        mainTable.add(title).padBottom(40).row();

        // 2. Format the stats
        int minutes = (int) (finalData.playTime / 60);
        int seconds = (int) (finalData.playTime % 60);
        String timeString = String.format("%02d:%02d", minutes, seconds);

        Label timeLabel = new Label("Total Playtime: " + timeString, skin, "subtitle");
        Label deathsLabel = new Label("Total Deaths: " + finalData.deaths, skin, "subtitle");
        Label killsLabel = new Label("Enemies Defeated: " + finalData.killedEnemiesCount, skin, "subtitle");

        mainTable.add(timeLabel).padBottom(15).row();
        mainTable.add(deathsLabel).padBottom(15).row();
        mainTable.add(killsLabel).padBottom(50).row();

        // 3. Navigation Buttons
        Table restartBtn = createHoverButton("Restart Game", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (GameAssetManager.winMusic != null) GameAssetManager.winMusic.stop();

                // Clear the slot and start fresh on the same slot
                com.amirali.graphics.SaveManager.deleteSave(finalData.slotIndex);
                Game game = new Game();
                uiManager.setScreen(new GameScreen(game, null, finalData.slotIndex));
            }
        });

        Table menuBtn = createHoverButton("Main Menu", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (GameAssetManager.winMusic != null) GameAssetManager.winMusic.stop();
                uiManager.setScreen(new MainMenuScreen());
            }
        });

        mainTable.add(restartBtn).padBottom(15).row();
        mainTable.add(menuBtn);

        rootTable.add(mainTable).grow();
    }
}
