package com.amirali.graphics.views.screens;

import com.amirali.graphics.KeyBindings;
import com.amirali.graphics.uiManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class GuideMenuScreen extends AbstractScreen {

    @Override
    public void show() {
        super.show();

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();

        Label title = new Label("GUIDE", skin, "title");
        title.setFontScale(1.5f); // Make it massive!
        mainTable.add(title).padTop(40).padBottom(30).row();

        Table contentTable = new Table();
        contentTable.top();

        Table leftCol = new Table();
        leftCol.top().left();
        Label controlsTitle = new Label("CONTROLS (Dynamic)", skin, "subtitle");
        leftCol.add(controlsTitle).colspan(2).left().padBottom(20).row();

        addControlRow(leftCol, "Move Left", KeyBindings.keyName(KeyBindings.MOVE_LEFT));
        addControlRow(leftCol, "Move Right", KeyBindings.keyName(KeyBindings.MOVE_RIGHT));
        addControlRow(leftCol, "Jump", KeyBindings.keyName(KeyBindings.JUMP));
        addControlRow(leftCol, "Attack (Nail)", KeyBindings.keyName(KeyBindings.ATTACK));
        addControlRow(leftCol, "Dash", KeyBindings.keyName(KeyBindings.DASH));
        addControlRow(leftCol, "Focus (Heal)", KeyBindings.keyName(KeyBindings.FOCUS));
        addControlRow(leftCol, "Inventory", KeyBindings.keyName(KeyBindings.INVENTORY));

        leftCol.add(new Label("", skin)).colspan(2).height(40).row();

        Label cheatsTitle = new Label("CHEAT CODES (Hold Ctrl)", skin, "subtitle");
        leftCol.add(cheatsTitle).colspan(2).left().padBottom(20).row();

        addCheatRow(leftCol, "Ctrl + K", "Insta-kill / Slow time");
        addCheatRow(leftCol, "Ctrl + G", "God Mode (invincible)");
        addCheatRow(leftCol, "Ctrl + N", "Noclip / Spectator");
        addCheatRow(leftCol, "Ctrl + B", "Teleport to Boss arena");
        addCheatRow(leftCol, "Ctrl + M", "Max Soul + Emergency Heal");

        Table rightCol = new Table();
        rightCol.top().left();
        Label abilitiesTitle = new Label("ABILITIES & MECHANICS", skin, "subtitle");
        rightCol.add(abilitiesTitle).left().padBottom(20).row();

        addAbilityRow(rightCol, "NAIL ATTACK", "Press " + KeyBindings.keyName(KeyBindings.ATTACK) + " to swing your nail. Hitting enemies grants 11 SOUL.");
        addAbilityRow(rightCol, "JUMP & DOUBLE JUMP", "Press " + KeyBindings.keyName(KeyBindings.JUMP) + " to jump. Press again mid-air for double jump. Hold jump key for higher jump.");
        addAbilityRow(rightCol, "DASH", "Press " + KeyBindings.keyName(KeyBindings.DASH) + " to dash horizontally. Short cooldown. Grants invincibility during dash.");
        addAbilityRow(rightCol, "FOCUS / HEAL", "Hold " + KeyBindings.keyName(KeyBindings.FOCUS) + " while standing still. After 1.5 sec, consumes 33 SOUL to restore 1 health mask.");
        addAbilityRow(rightCol, "SOUL VESSEL", "Max 99 SOUL. Displayed as a circular orb. Gain SOUL by hitting enemies.");
        addAbilityRow(rightCol, "HEALTH MASKS", "Max 5 masks. Each enemy hit or hazard reduces one. After taking damage, you are invincible for 1 second.");
        addAbilityRow(rightCol, "DOWNWARD POGO", "While airborne, press DOWN + ATTACK to strike downward. Bouncing off spikes or enemies resets dash and jump.");
        addAbilityRow(rightCol, "WALL CLING & JUMP", "Hold toward a wall while airborne to slide down slowly. Press jump to leap off the wall.");

        contentTable.add(leftCol).width(380).padRight(100).top();
        contentTable.add(rightCol).width(480).top();

        ScrollPane mainScroll = new ScrollPane(contentTable, skin);
        mainScroll.setScrollingDisabled(true, false);
        mainScroll.setFadeScrollBars(false);
        mainScroll.setVariableSizeKnobs(false);

        mainTable.add(mainScroll).expand().fill().pad(20).row();

        // --- CONSISTENCY FIX: Use the Hover Button ---
        Table backBtn = createHoverButton("Back to Main Menu", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                uiManager.setScreen(new MainMenuScreen());
            }
        });

        mainTable.add(backBtn).padTop(10).padBottom(30);

        rootTable.add(mainTable).grow();
    }

    private void addControlRow(Table table, String action, String keyName) {
        Label actionLabel = new Label(action + ":", skin);
        Label keyLabel = new Label(keyName, skin);
        keyLabel.setColor(Color.GOLD);
        table.add(actionLabel).left().padBottom(10).padRight(15);
        table.add(keyLabel).left().padBottom(10).row();
    }

    private void addCheatRow(Table table, String combo, String description) {
        Label comboLabel = new Label(combo, skin);
        comboLabel.setColor(Color.CORAL);
        Label descLabel = new Label(description, skin);
        table.add(comboLabel).left().padBottom(10).padRight(15);
        table.add(descLabel).left().padBottom(10).row();
    }

    private void addAbilityRow(Table table, String title, String description) {
        Label titleLabel = new Label("• " + title, skin);
        titleLabel.setColor(Color.CYAN);
        table.add(titleLabel).left().padBottom(5).row();

        Label descLabel = new Label(description, skin);
        descLabel.setWrap(true);
        table.add(descLabel).growX().left().padBottom(25).padLeft(15).row(); // Increased bottom padding slightly
    }
}
