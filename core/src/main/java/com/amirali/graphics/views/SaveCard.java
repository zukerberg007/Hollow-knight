package com.amirali.graphics.views;

import com.amirali.graphics.AreaMusicManager;
import com.amirali.graphics.GameAssetManager;
import com.amirali.graphics.SaveManager;
import com.amirali.graphics.models.GameData;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.amirali.graphics.LanguageManager.t;

public class SaveCard extends Table {

    public SaveCard(final int slotIndex, GameData data) {
        final Skin skin = GameAssetManager.skin;

        pad(20);

        Label numberLabel = new Label(slotIndex + ". ", skin, "title");
        add(numberLabel).padRight(20).left();

        if (data == null) {
            Label newGameLabel = new Label(t("save.newGame"), skin, "subtitle");
            add(newGameLabel).expandX().left();
        } else {
            Texture areaBg;
            String locationKey;
            switch (AreaMusicManager.areaAt(data.playerX)) {
                case GREEN_PATH -> {
                    areaBg = GameAssetManager.greenPathBg;
                    locationKey = "save.location.greenpath";
                }
                case CRYSTAL_PEAK -> {
                    areaBg = GameAssetManager.crystalPeakBg;
                    locationKey = "save.location.crystalpeak";
                }
                default -> {
                    areaBg = GameAssetManager.forgottenCrossroadsBg;
                    locationKey = "save.location";
                }
            }
            if (areaBg != null) {
                setBackground(new TextureRegionDrawable(new TextureRegion(areaBg)));
            } else {
                setBackground(skin.getDrawable("window"));
            }

            Table statsTable = new Table();
            Image orb = new Image(GameAssetManager.FULL_ORB);
            statsTable.add(orb).size(80, 75).padRight(15);

            for (int i = 0; i < data.maxMasks; i++) {
                Image mask = new Image(GameAssetManager.filledMask);
                statsTable.add(mask).size(30, 30).padRight(5);
            }

            add(statsTable).expandX().left();

            Table detailsTable = new Table();
            Label locationLabel = new Label(t(locationKey), skin, "subtitle");
            Label progressLabel = new Label(data.progress + "%", skin);
            Label timeLabel = new Label(t("save.realSave"), skin);

            progressLabel.setColor(Color.LIGHT_GRAY);
            timeLabel.setColor(Color.LIGHT_GRAY);

            detailsTable.add(locationLabel).right().padBottom(5).row();
            detailsTable.add(progressLabel).right().row();
            detailsTable.add(timeLabel).right().row();

            add(detailsTable).right().padRight(30);

            TextButton clearBtn = new TextButton(t("save.clear"), skin);
            clearBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    event.cancel();
                    SaveManager.deleteSave(slotIndex);

                    clearChildren();
                    setBackground((TextureRegionDrawable) null);
                    Label numLabel = new Label(slotIndex + ". ", skin, "title");
                    add(numLabel).padRight(20).left();
                    Label newGameLabel = new Label(t("save.newGame"), skin, "subtitle");
                    add(newGameLabel).expandX().left();
                }
            });
            add(clearBtn).right().width(120);
        }
    }
}
