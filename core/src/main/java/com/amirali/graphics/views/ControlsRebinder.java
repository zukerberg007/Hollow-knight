package com.amirali.graphics.views;

import com.amirali.graphics.KeyBindings;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.EnumMap;
import java.util.Map;

import static com.amirali.graphics.LanguageManager.t;

/**
 * Builds a self-contained controls panel: one "Action: [key]" row per rebindable action plus a
 * "Reset Controls" button. Click a key button, then press any key to rebind it (Esc cancels).
 *
 * The capture works by briefly swapping the global input processor for a one-shot adapter and then
 * restoring it, so the exact same panel works on a full Screen and inside an in-game Modal.
 */
public class ControlsRebinder {

    public static Table buildControls(final Skin skin) {
        Table panel = new Table();
        panel.defaults().pad(4);

        final Map<KeyBindings.Action, TextButton> buttons =
            new EnumMap<>(KeyBindings.Action.class);

        for (KeyBindings.Action action : KeyBindings.Action.values()) {
            final KeyBindings.Action a = action;

            Label name = new Label(KeyBindings.displayName(a) + ":", skin);
            final TextButton keyBtn = new TextButton(KeyBindings.keyName(KeyBindings.getKey(a)), skin);

            keyBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    final InputProcessor previous = Gdx.input.getInputProcessor();
                    keyBtn.setText(t("controls.pressKey"));

                    Gdx.input.setInputProcessor(new InputAdapter() {
                        @Override
                        public boolean keyDown(int keycode) {
                            if (keycode != Input.Keys.ESCAPE) {
                                KeyBindings.setKey(a, keycode); // also persists
                            }
                            keyBtn.setText(KeyBindings.keyName(KeyBindings.getKey(a)));
                            Gdx.input.setInputProcessor(previous); // hand control back
                            return true;
                        }
                    });
                }
            });

            buttons.put(a, keyBtn);

            Table row = new Table();
            row.add(name).left().width(140).padRight(15);
            row.add(keyBtn).width(170).left();
            panel.add(row).row();
        }

        TextButton resetBtn = new TextButton(t("controls.reset"), skin);
        resetBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                KeyBindings.resetToDefault();
                for (Map.Entry<KeyBindings.Action, TextButton> e : buttons.entrySet()) {
                    e.getValue().setText(KeyBindings.keyName(KeyBindings.getKey(e.getKey())));
                }
            }
        });
        panel.add(resetBtn).padTop(12).row();

        return panel;
    }
}
