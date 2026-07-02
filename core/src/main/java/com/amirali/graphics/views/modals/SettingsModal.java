package com.amirali.graphics.views.modals;

import com.amirali.graphics.BrightnessManager;
import com.amirali.graphics.LanguageManager;
import com.amirali.graphics.uiManager;
import com.amirali.graphics.views.ControlsRebinder;

import static com.amirali.graphics.LanguageManager.t;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SettingsModal extends Modal {

    public SettingsModal() {
        super();

        defaults().space(12).pad(6);

        Label title = new Label(t("settings.title"), skin);
        title.setFontScale(1.4f);
        add(title).padBottom(15).row();

        Table content = new Table();
        content.defaults().space(10).center();

        content.add(new Label(t("settings.video"), skin, "subtitle")).padTop(5).row();
        Table brightTable = new Table();
        final Slider brightSlider = new Slider(0.1f, 1f, 0.1f, false, skin);
        brightSlider.setValue(BrightnessManager.get());
        brightSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                BrightnessManager.set(brightSlider.getValue());
            }
        });
        brightTable.add(new Label(t("settings.brightness"), skin)).padRight(10);
        brightTable.add(brightSlider).width(150);
        content.add(brightTable).row();

        content.add(new Label(t("settings.controls"), skin, "subtitle")).padTop(15).row();
        content.add(ControlsRebinder.buildControls(skin)).row();

        content.add(new Label(t("settings.language"), skin, "subtitle")).padTop(15).row();
        final TextButton languageBtn = new TextButton(t("settings.languageBtn") + ": " + LanguageManager.current().name(), skin);
        languageBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                LanguageManager.toggle();
                uiManager.getScreen().getModalStack().clearChildren();
                new PauseModal().show();
                new SettingsModal().show();
            }
        });
        content.add(languageBtn).width(200).row();

        ScrollPane scroll = new ScrollPane(content, skin);
        scroll.setScrollingDisabled(true, false);
        scroll.setFadeScrollBars(false);
        scroll.setVariableSizeKnobs(false);
        add(scroll).width(520).height(420).row();

        TextButton backBtn = new TextButton(t("common.back"), skin);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                hide();
            }
        });
        add(backBtn).width(200).padTop(10);
    }
}
