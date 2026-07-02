package com.amirali.graphics.views.modals;

import com.amirali.graphics.models.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import static com.amirali.graphics.LanguageManager.t;

public class InventoryModal extends Modal {

    private final Player player;
    private final Label notchLabel;
    private final Label descLabel;

    public InventoryModal(Player player) {
        super();
        this.player = player;

        defaults().space(10).pad(10);

        Label title = new Label(t("inv.title"), skin);
        title.setFontScale(1.3f);
        add(title).colspan(4).center().row();

        notchLabel = new Label("", skin);
        updateNotchText();
        add(notchLabel).colspan(4).center().padBottom(15).row();

        Table charmsGrid = new Table();
        charmsGrid.defaults().space(15).size(70, 70);

        addCharmButton(charmsGrid, 1);
        addCharmButton(charmsGrid, 2);
        addCharmButton(charmsGrid, 3);
        addCharmButton(charmsGrid, 4);

        charmsGrid.row();

        addCharmButton(charmsGrid, 5);
        addCharmButton(charmsGrid, 6);
        addCharmButton(charmsGrid, 7);
        addCharmButton(charmsGrid, 8);

        add(charmsGrid).colspan(4).center().row();

        descLabel = new Label(t("inv.hint"), skin);
        descLabel.setWrap(true);
        descLabel.setColor(Color.LIGHT_GRAY);
        add(descLabel).colspan(4).width(400).padTop(15).center().row();

        TextButton closeBtn = new TextButton(t("inv.close"), skin);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        add(closeBtn).colspan(4).center().padTop(10);
    }

    private void addCharmButton(Table grid, final int charmId) {
        Texture charmTex = new Texture(Gdx.files.internal("charm_" + charmId + ".png"));
        final Image charmImg = new Image(charmTex);

        boolean isEquipped = checkCharmStatus(charmId);

        charmImg.setColor(isEquipped ? Color.WHITE : Color.DARK_GRAY);

        charmImg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String description = getCharmDescription(charmId);
                boolean currentlyEquipped = checkCharmStatus(charmId);

                if (currentlyEquipped) {
                    setCharmStatus(charmId, false);
                    player.currentNotchesUsed--;
                    charmImg.setColor(Color.DARK_GRAY);
                } else {
                    if (player.currentNotchesUsed < Player.MAX_NOTCHES) {
                        setCharmStatus(charmId, true);
                        player.currentNotchesUsed++;
                        charmImg.setColor(Color.WHITE);
                    } else {
                        description = t("inv.notEnough") + "\n\n" + description;
                    }
                }

                descLabel.setText(t("charm." + charmId).toUpperCase() + ":\n" + description);
                updateNotchText();
            }
        });

        grid.add(charmImg);
    }

    private void updateNotchText() {
        notchLabel.setText(t("inv.notches", player.currentNotchesUsed, Player.MAX_NOTCHES));
        if (player.currentNotchesUsed >= Player.MAX_NOTCHES) {
            notchLabel.setColor(Color.RED);
        } else {
            notchLabel.setColor(Color.GREEN);
        }
    }

    private boolean checkCharmStatus(int id) {
        return switch (id) {
            case 1 -> player.hasSoulCatcher;
            case 2 -> player.hasDashmaster;
            case 3 -> player.hasUnbreakableStrength;
            case 4 -> player.hasQuickSlash;
            case 5 -> player.hasQuickFocus;
            case 6 -> player.hasHeavyBlow;
            case 7 -> player.hasSharpShadow;
            case 8 -> player.hasVoidHeart;
            default -> false;
        };
    }

    private void setCharmStatus(int id, boolean status) {
        switch (id) {
            case 1 -> player.hasSoulCatcher = status;
            case 2 -> player.hasDashmaster = status;
            case 3 -> player.hasUnbreakableStrength = status;
            case 4 -> player.hasQuickSlash = status;
            case 5 -> player.hasQuickFocus = status;
            case 6 -> player.hasHeavyBlow = status;
            case 7 -> player.hasSharpShadow = status;
            case 8 -> player.hasVoidHeart = status;
        }
    }

    private String getCharmDescription(int id) {
        return (id >= 1 && id <= 8) ? t("charm." + id + ".desc") : "";
    }
}
