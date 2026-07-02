package com.amirali.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;

public class KeyBindings {
    private static final String PREFS_NAME = "keybindings";
    private static Preferences prefs;

    public static int MOVE_LEFT = Input.Keys.LEFT;
    public static int MOVE_RIGHT = Input.Keys.RIGHT;
    public static int JUMP = Input.Keys.Z;
    public static int ATTACK = Input.Keys.X;
    public static int DASH = Input.Keys.C;
    public static int FOCUS = Input.Keys.A;
    public static int INVENTORY = Input.Keys.I;

    public static void load() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        MOVE_LEFT = prefs.getInteger("move_left", Input.Keys.LEFT);
        MOVE_RIGHT = prefs.getInteger("move_right", Input.Keys.RIGHT);
        JUMP = prefs.getInteger("jump", Input.Keys.Z);
        ATTACK = prefs.getInteger("attack", Input.Keys.X);
        DASH = prefs.getInteger("dash", Input.Keys.C);
        FOCUS = prefs.getInteger("focus", Input.Keys.A);
        INVENTORY = prefs.getInteger("inventory", Input.Keys.I);
    }

    public static void save() {
        prefs.putInteger("move_left", MOVE_LEFT);
        prefs.putInteger("move_right", MOVE_RIGHT);
        prefs.putInteger("jump", JUMP);
        prefs.putInteger("attack", ATTACK);
        prefs.putInteger("dash", DASH);
        prefs.putInteger("focus", FOCUS);
        prefs.putInteger("inventory", INVENTORY);
        prefs.flush();
    }

    public static void resetToDefault() {
        MOVE_LEFT = Input.Keys.LEFT;
        MOVE_RIGHT = Input.Keys.RIGHT;
        JUMP = Input.Keys.Z;
        ATTACK = Input.Keys.X;
        DASH = Input.Keys.C;
        FOCUS = Input.Keys.A;
        INVENTORY = Input.Keys.I;
        save();
    }

    public static String keyName(int keycode) {
        return Input.Keys.toString(keycode);
    }

    public enum Action { MOVE_LEFT, MOVE_RIGHT, JUMP, ATTACK, DASH, FOCUS, INVENTORY }

    public static int getKey(Action a) {
        switch (a) {
            case MOVE_LEFT:  return MOVE_LEFT;
            case MOVE_RIGHT: return MOVE_RIGHT;
            case JUMP:       return JUMP;
            case ATTACK:     return ATTACK;
            case DASH:       return DASH;
            case FOCUS:      return FOCUS;
            case INVENTORY:  return INVENTORY;
            default:         return Input.Keys.UNKNOWN;
        }
    }

    public static void setKey(Action a, int keycode) {
        switch (a) {
            case MOVE_LEFT:  MOVE_LEFT = keycode;  break;
            case MOVE_RIGHT: MOVE_RIGHT = keycode; break;
            case JUMP:       JUMP = keycode;       break;
            case ATTACK:     ATTACK = keycode;     break;
            case DASH:       DASH = keycode;       break;
            case FOCUS:      FOCUS = keycode;      break;
            case INVENTORY:  INVENTORY = keycode;  break;
        }
        save();
    }

    public static String displayName(Action a) {
        return LanguageManager.t("action." + a.name().toLowerCase());
    }
}
