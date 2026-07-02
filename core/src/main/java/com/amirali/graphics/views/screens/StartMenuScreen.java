package com.amirali.graphics.views.screens;

import com.amirali.graphics.SaveManager;
import com.amirali.graphics.models.GameData;
import com.amirali.graphics.models.Game;
import com.amirali.graphics.views.SaveCard;
import com.amirali.graphics.uiManager;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import static com.amirali.graphics.LanguageManager.t;

public class StartMenuScreen extends AbstractScreen {
    @Override
    public void show() {
        super.show();

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();

        Label title = new Label(t("start.title"), skin, "title");
        mainTable.add(title).padTop(50).padBottom(30).row();

        Table saveList = new Table();
        saveList.top();
        saveList.defaults().space(20).width(800);

        for (int i = 0; i < 4; i++) {
            final int slotIndex = i + 1;
            // Load it once just to display the card graphics
            final GameData visualData = SaveManager.loadGame(slotIndex);
            SaveCard saveCard = new SaveCard(slotIndex, visualData);

            saveCard.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!event.isCancelled()) {
                        Game game = new Game();

                        // --- FIX: Reload the file the split-second they click Play! ---
                        // If they cleared it, this will safely return null.
                        GameData freshData = SaveManager.loadGame(slotIndex);

                        uiManager.setScreen(new GameScreen(game, freshData, slotIndex));
                    }
                }
            });

            saveList.add(saveCard).row();
        }

        ScrollPane scrollPane = new ScrollPane(saveList, skin);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setVariableSizeKnobs(false);

        mainTable.add(scrollPane).expand().fill().padLeft(40).padRight(40).padBottom(20).row();

        Table backBtn = createHoverButton(t("start.back"), new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                uiManager.setScreen(new MainMenuScreen());
            }
        });
        mainTable.add(backBtn).padBottom(40).row();

        rootTable.add(mainTable).grow();
    }
}
