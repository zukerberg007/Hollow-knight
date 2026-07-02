package com.amirali.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

public class LanguageManager {

    public enum Language {
        // EN maps to ROOT so the base file is always used, regardless of the system locale.
        EN(Locale.ROOT),
        FR(Locale.forLanguageTag("fr"));

        public final Locale locale;

        Language(Locale locale) {
            this.locale = locale;
        }
    }

    private static final String PREFS_NAME = "HollowKnightSettings";
    private static final String KEY = "language";

    private static Language current = Language.EN;
    private static I18NBundle bundle;

    public static void init() {
        I18NBundle.setSimpleFormatter(true);

        String saved = Gdx.app.getPreferences(PREFS_NAME).getString(KEY, Language.EN.name());
        try {
            current = Language.valueOf(saved);
        } catch (IllegalArgumentException e) {
            current = Language.EN;
        }
        loadBundle();
    }

    private static void loadBundle() {
        bundle = I18NBundle.createBundle(Gdx.files.internal("i18n/strings"), current.locale, "UTF-8");
    }

    public static Language current() {
        return current;
    }

    public static void set(Language language) {
        current = language;
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putString(KEY, language.name());
        prefs.flush();
        loadBundle();
    }

    public static Language toggle() {
        set(current == Language.EN ? Language.FR : Language.EN);
        return current;
    }
    public static String t(String key) {
        if (bundle == null) init();
        return bundle.get(key);
    }
    public static String t(String key, Object... args) {
        if (bundle == null) init();
        return bundle.format(key, args);
    }
}
