package com.amirali.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class BackgroundManager {

    public enum Theme {
        VOIDHEART("Void Heart", "Voidheart.webm"),
        GODHOME("Godhome", "Godmaster.webm"),
        ETERNAL_ORDEAL("Eternal Ordeal", "Eternal Ordeal.webm");

        public final String label;
        public final String file;

        Theme(String label, String file) {
            this.label = label;
            this.file = file;
        }
    }
    private static final String PREFS_NAME = "HollowKnightSettings";
    private static final String KEY = "bgTheme";

    private static Theme current = Theme.VOIDHEART;

    public static void init() {
        String saved = Gdx.app.getPreferences(PREFS_NAME).getString(KEY, Theme.VOIDHEART.name());
        try {
            current = Theme.valueOf(saved);
        } catch (IllegalArgumentException e) {
            current = Theme.VOIDHEART;
        }
    }

    public static Theme current() {
        return current;
    }

    public static String currentFile() {
        return current.file;
    }

    public static void set(Theme theme) {
        current = theme;
        Preferences p = Gdx.app.getPreferences(PREFS_NAME);
        p.putString(KEY, theme.name());
        p.flush();
    }

    public static Theme cycleNext() {
        Theme[] all = Theme.values();
        set(all[(current.ordinal() + 1) % all.length]);
        return current;
    }
}
