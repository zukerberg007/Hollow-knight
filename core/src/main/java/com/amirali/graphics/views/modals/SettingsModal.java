package com.amirali.graphics.views.modals;

import com.amirali.graphics.BrightnessManager;
import com.amirali.graphics.views.ControlsRebinder;
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

/**
 * In-game settings overlay, opened from the Pause menu. Because it's a Modal (not a Screen), the
 * live GameScreen stays paused underneath, so "Back" returns to the pause menu and then the game.
 *
 * Brightness and controls apply globally, so they take effect immediately while playing.
 */
public class SettingsModal extends Modal {

    public SettingsModal() {
        super();

        final Preferences prefs = Gdx.app.getPreferences("HollowKnightSettings");

        defaults().space(12).pad(6);

        Label title = new Label("SETTINGS", skin);
        title.setFontScale(1.4f);
        add(title).padBottom(15).row();

        Table content = new Table();
        content.defaults().space(10).center();

        // --- VIDEO / BRIGHTNESS ---
        content.add(new Label("VIDEO", skin, "subtitle")).padTop(5).row();
        Table brightTable = new Table();
        final Slider brightSlider = new Slider(0.1f, 1f, 0.1f, false, skin);
        brightSlider.setValue(BrightnessManager.get());
        brightSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                BrightnessManager.set(brightSlider.getValue());
            }
        });
        brightTable.add(new Label("Brightness: ", skin)).padRight(10);
        brightTable.add(brightSlider).width(150);
        content.add(brightTable).row();

        // --- CONTROLS ---
        content.add(new Label("CONTROLS", skin, "subtitle")).padTop(15).row();
        content.add(ControlsRebinder.buildControls(skin)).row();

        // --- LANGUAGE ---
        content.add(new Label("LANGUAGE", skin, "subtitle")).padTop(15).row();
        final boolean[] english = { prefs.getBoolean("english", true) };
        final TextButton languageBtn = new TextButton("Language: " + (english[0] ? "EN" : "FA"), skin);
        languageBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                english[0] = !english[0];
                prefs.putBoolean("english", english[0]);
                prefs.flush();
                languageBtn.setText("Language: " + (english[0] ? "EN" : "FA"));
            }
        });
        content.add(languageBtn).width(200).row();

        ScrollPane scroll = new ScrollPane(content, skin);
        scroll.setScrollingDisabled(true, false);
        scroll.setFadeScrollBars(false);
        scroll.setVariableSizeKnobs(false);
        add(scroll).width(520).height(420).row();

        TextButton backBtn = new TextButton("Back", skin);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                hide();
            }
        });
        add(backBtn).width(200).padTop(10);
    }
}
