package com.amirali.graphics.views.screens;

import com.amirali.graphics.KeyBindings;
import com.amirali.graphics.uiManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import static com.amirali.graphics.LanguageManager.t;

public class GuideMenuScreen extends AbstractScreen {

    @Override
    public void show() {
        super.show();

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();

        Label title = new Label(t("guide.title"), skin, "title");
        title.setFontScale(1.5f);
        mainTable.add(title).padTop(40).padBottom(30).row();

        Table contentTable = new Table();
        contentTable.top();

        Table leftCol = new Table();
        leftCol.top().left();
        Label controlsTitle = new Label(t("guide.controls"), skin, "subtitle");
        leftCol.add(controlsTitle).colspan(2).left().padBottom(20).row();

        addControlRow(leftCol, t("action.move_left"), KeyBindings.keyName(KeyBindings.MOVE_LEFT));
        addControlRow(leftCol, t("action.move_right"), KeyBindings.keyName(KeyBindings.MOVE_RIGHT));
        addControlRow(leftCol, t("action.jump"), KeyBindings.keyName(KeyBindings.JUMP));
        addControlRow(leftCol, t("guide.attackNail"), KeyBindings.keyName(KeyBindings.ATTACK));
        addControlRow(leftCol, t("action.dash"), KeyBindings.keyName(KeyBindings.DASH));
        addControlRow(leftCol, t("guide.focusHeal"), KeyBindings.keyName(KeyBindings.FOCUS));
        addControlRow(leftCol, t("action.inventory"), KeyBindings.keyName(KeyBindings.INVENTORY));

        leftCol.add(new Label("", skin)).colspan(2).height(40).row();

        Label cheatsTitle = new Label(t("guide.cheats"), skin, "subtitle");
        leftCol.add(cheatsTitle).colspan(2).left().padBottom(20).row();

        addCheatRow(leftCol, "Ctrl + K", t("guide.cheat.k"));
        addCheatRow(leftCol, "Ctrl + G", t("guide.cheat.g"));
        addCheatRow(leftCol, "Ctrl + N", t("guide.cheat.n"));
        addCheatRow(leftCol, "Ctrl + B", t("guide.cheat.b"));
        addCheatRow(leftCol, "Ctrl + M", t("guide.cheat.m"));
        addCheatRow(leftCol, "Ctrl + H", t("guide.cheat.h"));

        Table rightCol = new Table();
        rightCol.top().left();
        Label abilitiesTitle = new Label(t("guide.abilities"), skin, "subtitle");
        rightCol.add(abilitiesTitle).left().padBottom(20).row();

        addAbilityRow(rightCol, t("guide.ability.nail"), t("guide.ability.nail.desc", KeyBindings.keyName(KeyBindings.ATTACK)));
        addAbilityRow(rightCol, t("guide.ability.jump"), t("guide.ability.jump.desc", KeyBindings.keyName(KeyBindings.JUMP)));
        addAbilityRow(rightCol, t("guide.ability.dash"), t("guide.ability.dash.desc", KeyBindings.keyName(KeyBindings.DASH)));
        addAbilityRow(rightCol, t("guide.ability.focus"), t("guide.ability.focus.desc", KeyBindings.keyName(KeyBindings.FOCUS)));
        addAbilityRow(rightCol, t("guide.ability.soul"), t("guide.ability.soul.desc"));
        addAbilityRow(rightCol, t("guide.ability.masks"), t("guide.ability.masks.desc"));
        addAbilityRow(rightCol, t("guide.ability.pogo"), t("guide.ability.pogo.desc"));
        addAbilityRow(rightCol, t("guide.ability.wall"), t("guide.ability.wall.desc"));

        contentTable.add(leftCol).width(380).padRight(100).top();
        contentTable.add(rightCol).width(480).top();

        ScrollPane mainScroll = new ScrollPane(contentTable, skin);
        mainScroll.setScrollingDisabled(true, false);
        mainScroll.setFadeScrollBars(false);
        mainScroll.setVariableSizeKnobs(false);

        mainTable.add(mainScroll).expand().fill().pad(20).row();

        Table backBtn = createHoverButton(t("common.backToMenu"), new ClickListener() {
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
        table.add(descLabel).growX().left().padBottom(25).padLeft(15).row();
    }
}
