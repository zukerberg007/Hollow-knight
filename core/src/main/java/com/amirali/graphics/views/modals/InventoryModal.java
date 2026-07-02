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

public class InventoryModal extends Modal {

    private final Player player;
    private final Label notchLabel;
    private final Label descLabel;

    public InventoryModal(Player player) {
        super();
        this.player = player;

        defaults().space(10).pad(10);

        Label title = new Label("CHARM INVENTORY", skin);
        title.setFontScale(1.3f);
        add(title).colspan(4).center().row();

        // 1. Notch usage tracker
        notchLabel = new Label("", skin);
        updateNotchText();
        add(notchLabel).colspan(4).center().padBottom(15).row();

        // 2. Charms Grid
        Table charmsGrid = new Table();
        // CHANGED: Made the grid cells square (e.g., 60x60 or 70x70) for images
        charmsGrid.defaults().space(15).size(70, 70);

        addCharmButton(charmsGrid, "Soul Catcher", 1);
        addCharmButton(charmsGrid, "Dashmaster", 2);
        addCharmButton(charmsGrid, "Unb. Strength", 3);
        addCharmButton(charmsGrid, "Quick Slash", 4);

        charmsGrid.row();

        addCharmButton(charmsGrid, "Quick Focus", 5);
        addCharmButton(charmsGrid, "Heavy Blow", 6);
        addCharmButton(charmsGrid, "Sharp Shadow", 7);
        addCharmButton(charmsGrid, "Void Heart", 8);

        add(charmsGrid).colspan(4).center().row();

        // 3. Charm description area
        descLabel = new Label("Click on a charm to equip it and see its details.", skin);
        descLabel.setWrap(true);
        descLabel.setColor(Color.LIGHT_GRAY);
        add(descLabel).colspan(4).width(400).padTop(15).center().row();

        // Close Button
        TextButton closeBtn = new TextButton("Close (Press I)", skin);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        add(closeBtn).colspan(4).center().padTop(10);
    }

    private void addCharmButton(Table grid, final String charmName, final int charmId) {
        // Load the specific PNG for this charm
        Texture charmTex = new Texture(Gdx.files.internal("charm_" + charmId + ".png"));
        final Image charmImg = new Image(charmTex);

        boolean isEquipped = checkCharmStatus(charmId);

        // Dim the image if unequipped, light it up if equipped
        charmImg.setColor(isEquipped ? Color.WHITE : Color.DARK_GRAY);

        charmImg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String description = getCharmDescription(charmId);
                boolean currentlyEquipped = checkCharmStatus(charmId);

                if (currentlyEquipped) {
                    setCharmStatus(charmId, false);
                    player.currentNotchesUsed--;
                    charmImg.setColor(Color.DARK_GRAY); // Dim it
                } else {
                    // Equipping requires available notches
                    if (player.currentNotchesUsed < Player.MAX_NOTCHES) {
                        setCharmStatus(charmId, true);
                        player.currentNotchesUsed++;
                        charmImg.setColor(Color.WHITE); // Light it up
                    } else {
                        description = "[NOT ENOUGH NOTCHES! Max 3]\n\n" + description;
                    }
                }

                descLabel.setText(charmName.toUpperCase() + ":\n" + description);
                updateNotchText();
            }
        });

        grid.add(charmImg);
    }

    private void updateNotchText() {
        notchLabel.setText("Notches: " + player.currentNotchesUsed + " / " + Player.MAX_NOTCHES);
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
        return switch (id) {
            case 1 -> "Increases Soul gained from hitting enemies with your Nail.";
            case 2 -> "Allows the user to dash much more frequently.";
            case 3 -> "Strengthens the Knight, doubling Nail damage.";
            case 4 -> "Increases attack speed and reduces slash cooldown.";
            case 5 -> "Increases the speed of focusing SOUL to heal.";
            case 6 -> "Increases knockback force applied to enemies.";
            case 7 -> "Dash through enemies without taking damage while harming them. Increases dash length.";
            case 8 -> "Boosts spell damage by 50% and unlocks dark void variants.";
            default -> "";
        };
    }
}
