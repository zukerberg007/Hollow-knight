package com.amirali.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

/**
 * Global screen brightness. The value (0.1 .. 1.0) is persisted in the shared settings prefs.
 *
 * Brightness is applied by drawing a translucent black quad over the ENTIRE window once per frame,
 * after every screen has finished rendering (see Main.render). 1.0 = no overlay, 0.1 = 90% dim.
 */
public class BrightnessManager {
    private static final String PREFS_NAME = "HollowKnightSettings";
    private static final String KEY = "brightness";

    private static float brightness = 1f;
    private static SpriteBatch batch;
    private static Texture pixel;

    /** Load the saved brightness. Call once at startup (Main.create). */
    public static void init() {
        brightness = Gdx.app.getPreferences(PREFS_NAME).getFloat(KEY, 1f);
    }

    public static float get() {
        return brightness;
    }

    public static void set(float value) {
        brightness = MathUtils.clamp(value, 0.1f, 1f);
        Preferences p = Gdx.app.getPreferences(PREFS_NAME);
        p.putFloat(KEY, brightness);
        p.flush();
    }

    /** Draw the dim overlay across the whole window. Call last, after super.render() in Main. */
    public static void render() {
        if (brightness >= 0.999f) return; // fully bright: nothing to draw

        if (batch == null) {
            batch = new SpriteBatch();
            Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pm.setColor(Color.WHITE);
            pm.fill();
            pixel = new Texture(pm);
            pm.dispose();
        }

        float alpha = 1f - brightness; // up to 0.9 at the darkest setting
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.begin();
        batch.setColor(0f, 0f, 0f, alpha);
        batch.draw(pixel, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(Color.WHITE);
        batch.end();
    }

    public static void dispose() {
        if (batch != null) { batch.dispose(); batch = null; }
        if (pixel != null) { pixel.dispose(); pixel = null; }
    }
}
