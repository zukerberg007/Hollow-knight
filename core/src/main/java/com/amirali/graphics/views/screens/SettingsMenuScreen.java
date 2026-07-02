package com.amirali.graphics.views.screens;

import com.amirali.graphics.GameAssetManager;
import com.amirali.graphics.BrightnessManager;
import com.amirali.graphics.BackgroundManager;
import com.amirali.graphics.uiManager;
import com.amirali.graphics.views.ControlsRebinder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SettingsMenuScreen extends AbstractScreen {

    private boolean musicOn;
    private boolean sfxOn;
    private boolean english;
    private Preferences prefs;

    @Override
    public void show() {
        super.show();

        prefs = Gdx.app.getPreferences("HollowKnightSettings");
        musicOn = prefs.getBoolean("musicOn", true);
        sfxOn = prefs.getBoolean("sfxOn", true);
        english = prefs.getBoolean("english", true);
        float currentVol = prefs.getFloat("musicVol", 0.5f);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();

        Label title = new Label("SETTINGS", skin, "title");
        mainTable.add(title).padTop(40).padBottom(20).row();

        Table centerTable = new Table();
        centerTable.defaults().space(15).center();

        Label audioLabel = new Label("AUDIO", skin, "subtitle");
        centerTable.add(audioLabel).padTop(20).row();

        Table volTable = new Table();
        Label volText = new Label("Music Volume: ", skin);
        final Slider volSlider = new Slider(0f, 1f, 0.1f, false, skin);
        volSlider.setValue(currentVol);

        volTable.add(volText).padRight(10);
        volTable.add(volSlider).width(150);
        centerTable.add(volTable).row();

        final TextButton musicToggleBtn = new TextButton("Music: " + (musicOn ? "ON" : "OFF"), skin);
        centerTable.add(musicToggleBtn).width(200).row();

        final TextButton sfxToggleBtn = new TextButton("SFX: " + (sfxOn ? "ON" : "OFF"), skin);
        centerTable.add(sfxToggleBtn).width(200).row();

        Table resetSoundsBtn = createHoverButton("Reset Audio Settings", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                volSlider.setValue(0.5f);
                musicOn = true;
                sfxOn = true;
                musicToggleBtn.setText("Music: ON");
                sfxToggleBtn.setText("SFX: ON");

                prefs.putFloat("musicVol", 0.5f);
                prefs.putBoolean("musicOn", true);
                prefs.putBoolean("sfxOn", true);
                prefs.flush();

                if (GameAssetManager.menuMusic != null) {
                    GameAssetManager.menuMusic.setVolume(0.5f);
                }
            }
        });
        centerTable.add(resetSoundsBtn).padBottom(10).row();

        Label videoLabel = new Label("VIDEO", skin, "subtitle");
        centerTable.add(videoLabel).padTop(20).row();

        Table brightTable = new Table();
        Label brightText = new Label("Brightness: ", skin);
        final Slider brightSlider = new Slider(0.1f, 1f, 0.1f, false, skin);
        brightSlider.setValue(BrightnessManager.get());
        brightSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                BrightnessManager.set(brightSlider.getValue());
            }
        });
        brightTable.add(brightText).padRight(10);
        brightTable.add(brightSlider).width(150);
        centerTable.add(brightTable).padBottom(10).row();

        final TextButton bgThemeBtn = new TextButton("Background: " + BackgroundManager.current().label, skin);
        bgThemeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BackgroundManager.Theme t = BackgroundManager.cycleNext();
                bgThemeBtn.setText("Background: " + t.label);
                reloadBackgroundVideo(); // swap the menu video immediately
            }
        });
        centerTable.add(bgThemeBtn).width(260).padBottom(10).row();

        Label controlsLabel = new Label("CONTROLS", skin, "subtitle");
        centerTable.add(controlsLabel).padTop(20).row();
        centerTable.add(ControlsRebinder.buildControls(skin)).padBottom(10).row();


        Label langLabel = new Label("LANGUAGE", skin, "subtitle");
        centerTable.add(langLabel).padTop(20).row();

        final TextButton languageBtn = new TextButton("Language: " + (english ? "EN" : "FA"), skin);
        centerTable.add(languageBtn).width(200).padBottom(20).row();

        ScrollPane scrollPane = new ScrollPane(centerTable, skin);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setVariableSizeKnobs(false);
        mainTable.add(scrollPane).expand().fill().pad(10).row();

        Table backBtn = createHoverButton("Back to Main Menu", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                uiManager.setScreen(new MainMenuScreen());
            }
        });
        mainTable.add(backBtn).padTop(10).padBottom(30);

        rootTable.add(mainTable).grow();

        volSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float val = volSlider.getValue();
                prefs.putFloat("musicVol", val);
                prefs.flush();
                if (musicOn && GameAssetManager.menuMusic != null) {
                    GameAssetManager.menuMusic.setVolume(val);
                }
            }
        });

        musicToggleBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                musicOn = !musicOn;
                prefs.putBoolean("musicOn", musicOn);
                prefs.flush();
                musicToggleBtn.setText("Music: " + (musicOn ? "ON" : "OFF"));

                if (GameAssetManager.menuMusic != null) {
                    if (musicOn) {
                        GameAssetManager.menuMusic.setVolume(prefs.getFloat("musicVol", 0.5f));
                    } else {
                        GameAssetManager.menuMusic.setVolume(0f);
                    }
                }
            }
        });

        sfxToggleBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sfxOn = !sfxOn;
                prefs.putBoolean("sfxOn", sfxOn);
                prefs.flush();
                sfxToggleBtn.setText("SFX: " + (sfxOn ? "ON" : "OFF"));
            }
        });

        languageBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                english = !english;
                prefs.putBoolean("english", english);
                prefs.flush();
                languageBtn.setText("Language: " + (english ? "EN" : "FA"));
            }
        });
    }
}
