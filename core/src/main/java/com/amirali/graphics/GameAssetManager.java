package com.amirali.graphics;

import com.amirali.graphics.views.AnimationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.HashMap;

public class GameAssetManager {
    public static Skin skin;
    public static final HashMap<AnimationType, Animation<TextureRegion>> animationMap = new HashMap<>();

    public static Music menuMusic;
    public static Music winMusic;

    public static com.badlogic.gdx.audio.Sound wallBreakSound;
    public static com.badlogic.gdx.audio.Sound[] zoteTalkSounds;
    public static com.badlogic.gdx.audio.Sound zoteAttackSound;

    public static Texture hoverPointer;
    public static Texture titleImage;

    public static Texture soulOrb;
    public static Texture filledMask;
    public static Texture emptyMask;
    public static Texture soulOrbEmpty;
    public static Texture FULL_ORB;
    public static Texture forgottenCrossroadsBg;
    public static Texture greenPathBg;
    public static Texture crystalPeakBg;

    public static com.badlogic.gdx.audio.Sound slashSound;
    public static com.badlogic.gdx.audio.Sound jumpSound;
    public static com.badlogic.gdx.audio.Sound dashSound;
    public static com.badlogic.gdx.audio.Sound damageSound;
    public static com.badlogic.gdx.audio.Sound focusSound;
    public static com.badlogic.gdx.audio.Sound healSound;
    public static com.badlogic.gdx.audio.Sound deathSound;
    public static com.badlogic.gdx.audio.Sound fireballSound;
    public static com.badlogic.gdx.audio.Sound howlingWraithsSound;
    public static com.badlogic.gdx.audio.Sound landingSound;
    public static com.badlogic.gdx.audio.Sound runSound;
    public static com.badlogic.gdx.audio.Sound wallSlideSound;
    public static com.badlogic.gdx.audio.Sound wingsSound;
    public static com.badlogic.gdx.audio.Sound gainSoulSound;
    public static com.badlogic.gdx.audio.Sound enemyDamageSound;
    public static com.badlogic.gdx.audio.Sound parrySound;

    public static void init() {

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        for (TextButton.TextButtonStyle style : skin.getAll(TextButton.TextButtonStyle.class).values()) {
            style.up = null;
            style.down = null;
            style.over = null;
        }

        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("HollowKnight.mp3"));
        menuMusic.setLooping(true);
        menuMusic.setVolume(0.5f);

        winMusic = Gdx.audio.newMusic(Gdx.files.internal("winMusic.mp3"));

        slashSound = Gdx.audio.newSound(Gdx.files.internal("sfx/slash.wav"));
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("sfx/hero_jump.wav"));
        dashSound = Gdx.audio.newSound(Gdx.files.internal("sfx/hero_dash.wav"));
        damageSound = Gdx.audio.newSound(Gdx.files.internal("sfx/hero_damage.wav"));
        focusSound = Gdx.audio.newSound(Gdx.files.internal("sfx/focus_health_charging.wav"));
        healSound = Gdx.audio.newSound(Gdx.files.internal("sfx/focus_health_heal.wav"));
        deathSound = Gdx.audio.newSound(Gdx.files.internal("sfx/hero_death_extra_details.wav"));
        fireballSound = Gdx.audio.newSound(Gdx.files.internal("sfx/hero_fireball.wav"));
        howlingWraithsSound = Gdx.audio.newSound(Gdx.files.internal("sfx/hero_scream_spell.wav"));
        landingSound = Gdx.audio.newSound(Gdx.files.internal("sfx/hero_land_soft.wav"));
        runSound = Gdx.audio.newSound(Gdx.files.internal("sfx/hero_run_footsteps_stone.wav"));
        wallSlideSound = Gdx.audio.newSound(Gdx.files.internal("sfx/hero_wall_slide.wav"));
        wingsSound = Gdx.audio.newSound(Gdx.files.internal("sfx/hero_wings.wav"));
        gainSoulSound = Gdx.audio.newSound(Gdx.files.internal("sfx/soulGain.wav"));
        enemyDamageSound = Gdx.audio.newSound(Gdx.files.internal("sfx/enemy_damage.wav"));
        parrySound = Gdx.audio.newSound(Gdx.files.internal("sfx/hero_parry.wav"));

        wallBreakSound = Gdx.audio.newSound(Gdx.files.internal("breakable_wall_hit_1.wav"));

        zoteTalkSounds = new com.badlogic.gdx.audio.Sound[3];
        zoteTalkSounds[0] = Gdx.audio.newSound(Gdx.files.internal("zote_talk.wav"));
        zoteTalkSounds[1] = Gdx.audio.newSound(Gdx.files.internal("zote_02.wav"));
        zoteTalkSounds[2] = Gdx.audio.newSound(Gdx.files.internal("zote_03.wav"));
        zoteAttackSound = Gdx.audio.newSound(Gdx.files.internal("Zote_battle_roar.wav"));

        ScrollPane.ScrollPaneStyle scrollStyle = skin.get(ScrollPane.ScrollPaneStyle.class);
        scrollStyle.background = null;

        Texture trackTex = new Texture(Gdx.files.internal("ui/background.png"));
        trackTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        scrollStyle.vScroll = new TextureRegionDrawable(new TextureRegion(trackTex));

        Texture knobTex = new Texture(Gdx.files.internal("ui/Scroller.png"));
        knobTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        scrollStyle.vScrollKnob = new TextureRegionDrawable(new TextureRegion(knobTex));

        hoverPointer = new Texture(Gdx.files.internal("ui/pointer.png"));
        hoverPointer.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        Window.WindowStyle winStyle = skin.get(Window.WindowStyle.class);
        if (winStyle != null) winStyle.background = null;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("TrajanPro-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 14;
        param.color = Color.WHITE;
        param.borderWidth = 1;
        param.borderColor = Color.BLACK;

        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;

        FreeTypeFontGenerator.FreeTypeFontParameter titleParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParam.size = 64;
        titleParam.color = Color.WHITE;
        titleParam.borderWidth = 2;
        titleParam.borderColor = Color.BLACK;
        titleParam.minFilter = Texture.TextureFilter.Linear;
        titleParam.magFilter = Texture.TextureFilter.Linear;
        BitmapFont titleFont = generator.generateFont(titleParam);

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = titleFont;
        skin.add("title", titleStyle);

        FreeTypeFontGenerator.FreeTypeFontParameter subtitleParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        subtitleParam.size = 20;
        subtitleParam.color = Color.WHITE;
        subtitleParam.borderWidth = 1;
        subtitleParam.borderColor = Color.BLACK;
        subtitleParam.minFilter = Texture.TextureFilter.Linear;
        subtitleParam.magFilter = Texture.TextureFilter.Linear;
        BitmapFont subtitleFont = generator.generateFont(subtitleParam);

        Label.LabelStyle subtitleStyle = new Label.LabelStyle();
        subtitleStyle.font = subtitleFont;
        skin.add("subtitle", subtitleStyle);

        BitmapFont customFont = generator.generateFont(param);
        generator.dispose();

        Pixmap cursorPixmap = new Pixmap(Gdx.files.internal("ui/cursor.png"));
        Cursor customCursor = Gdx.graphics.newCursor(cursorPixmap, 0, 0);
        Gdx.graphics.setCursor(customCursor);
        cursorPixmap.dispose();

        for (Label.LabelStyle style : skin.getAll(Label.LabelStyle.class).values()) {
            style.font = customFont;
        }
        for (TextButton.TextButtonStyle style : skin.getAll(TextButton.TextButtonStyle.class).values()) {
            style.font = customFont;
        }
        for (TextField.TextFieldStyle style : skin.getAll(TextField.TextFieldStyle.class).values()) {
            style.font = customFont;
        }
        for (Window.WindowStyle style : skin.getAll(Window.WindowStyle.class).values()) {
            style.titleFont = customFont;
        }

        for (CheckBox.CheckBoxStyle style : skin.getAll(CheckBox.CheckBoxStyle.class).values()) {
            style.font = customFont;
        }

        skin.add("default-font", customFont, BitmapFont.class);

        for (AnimationType type : AnimationType.values()) {
            loadAnimation(type);
        }
        soulOrb = new Texture(Gdx.files.internal("animations/HUD/SoulOrb_Full.png"));
        filledMask = new Texture(Gdx.files.internal("animations/HUD/FilledHealth.png"));
        emptyMask = new Texture(Gdx.files.internal("animations/HUD/EmptyHealth.png"));
        soulOrbEmpty = new Texture(Gdx.files.internal("animations/HUD/HealthBar_005.png"));
        titleImage = new Texture(Gdx.files.internal("vheart_title.png"));
        FULL_ORB = new Texture(Gdx.files.internal("animations/HUD/FULL_ORB.png"));
        try {
            forgottenCrossroadsBg = new Texture(Gdx.files.internal("Area save art/Area_Forgotten Crossroads.png"));
        } catch (Exception e) {
            Gdx.app.error("Texture", "Missing Area_Forgotten Crossroads.png! Using fallback.");
        }
        try {
            greenPathBg = new Texture(Gdx.files.internal("Area save art/Area_Green_Path.png"));
        } catch (Exception e) {
            Gdx.app.error("Texture", "Missing Area_Green_Path.png! Using fallback.");
        }
        try {
            crystalPeakBg = new Texture(Gdx.files.internal("Area save art/Area_Crystal_Mines.png"));
        } catch (Exception e) {
            Gdx.app.error("Texture", "Missing Area_Crystal_Mines.png! Using fallback.");
        }

        Window.WindowStyle style = skin.get(Window.WindowStyle.class);
        style.background = null;
    }

    public static void dispose() {
        soulOrb.dispose();
        filledMask.dispose();
        emptyMask.dispose();
        soulOrbEmpty.dispose();
        titleImage.dispose();

        if (menuMusic != null) menuMusic.dispose();
        if (winMusic != null) winMusic.dispose();
        if (hoverPointer != null) hoverPointer.dispose();
        if (forgottenCrossroadsBg != null) forgottenCrossroadsBg.dispose();
        if (greenPathBg != null) greenPathBg.dispose();
        if (crystalPeakBg != null) crystalPeakBg.dispose();
        if (wallBreakSound != null) wallBreakSound.dispose();
        if (zoteTalkSounds != null) {
            for (com.badlogic.gdx.audio.Sound sound : zoteTalkSounds) {
                if (sound != null) sound.dispose();
            }
        }
        if (zoteAttackSound != null) zoteAttackSound.dispose();

        if (slashSound != null) slashSound.dispose();
        if (jumpSound != null) jumpSound.dispose();
        if (dashSound != null) dashSound.dispose();
        if (damageSound != null) damageSound.dispose();
        if (focusSound != null) focusSound.dispose();
        if (healSound != null) healSound.dispose();
        if (deathSound != null) deathSound.dispose();
        if (fireballSound != null) fireballSound.dispose();
        if (howlingWraithsSound != null) howlingWraithsSound.dispose();
        if (landingSound != null) landingSound.dispose();
        if (runSound != null) runSound.dispose();
        if (wallSlideSound != null) wallSlideSound.dispose();
        if (wingsSound != null) wingsSound.dispose();
        if (gainSoulSound != null) gainSoulSound.dispose();
        if (enemyDamageSound != null) enemyDamageSound.dispose();
        if (parrySound != null) parrySound.dispose();
    }

    public static void loadAnimation(AnimationType type) {
        Texture texture = new Texture(type.path);

        TextureRegion[][] split = TextureRegion.split(
            texture,
            texture.getWidth() / type.colCount,
            texture.getHeight() / type.rowCount
        );

        int frameCount = type.frameCount;
        TextureRegion[] frames = new TextureRegion[frameCount];
        int cols = split[0].length;

        for (int i = 0; i < frameCount; i++) {
            if (type.activeCols != null) {
                frames[i] = split[0][type.activeCols[i]];
            } else {
                int row = i / cols;
                int col = i % cols;
                frames[i] = split[row][col];
            }
            if (type == AnimationType.HOLLOW_KNIGHT_NORMAL_SLASH_EFFECT) {
                TextureRegion original = frames[i];
                frames[i] = new TextureRegion(
                    original.getTexture(),
                    original.getRegionX() + 18,
                    original.getRegionY() + 3,
                    original.getRegionWidth() - 18,
                    original.getRegionHeight() - 3
                );
            }
            if (type == AnimationType.HOLLOW_KNIGHT_SLASH_ALT_EFFECT) {
                TextureRegion original = frames[i];
                frames[i] = new TextureRegion(
                    original.getTexture(),
                    original.getRegionX() + 58,
                    original.getRegionY() + 3,
                    original.getRegionWidth() - 58,
                    original.getRegionHeight() - 3
                );
            }
        }
        if (type == AnimationType.HOLLOW_KNIGHT_DOUBLE_JUMP_FRAMES || type == AnimationType.HOLLOW_KNIGHT_NORMAL_SLASH_EFFECT ||type == AnimationType.HOLLOW_KNIGHT_SLASH_ALT_EFFECT || type == AnimationType.HOLLOW_KNIGHT_UP_SLASH_EFFECT || type == AnimationType.HOLLOW_KNIGHT_DOWN_SLASH_EFFECT) {
            for (int i = 0; i < frameCount / 2; i++) {
                TextureRegion temp = frames[i];
                frames[i] = frames[frameCount - 1 - i];
                frames[frameCount - 1 - i] = temp;
            }
        }

        float frameDuration = 1 / 10f;
        Animation<TextureRegion> animation;

        if (type == AnimationType.HOLLOW_KNIGHT_NORMAL_SLASH_EFFECT) {
            animation = new Animation<>(frameDuration, frames);
            animation.setPlayMode(Animation.PlayMode.NORMAL);
        }else if (type == AnimationType.HOLLOW_KNIGHT_DOUBLE_JUMP_FRAMES) {
            frameDuration = 0.05f;
            animation = new Animation<>(frameDuration, frames);
            animation.setPlayMode(Animation.PlayMode.LOOP);
        }
        else if (type == AnimationType.HOLLOW_KNIGHT_MASK_SHINE_FRAMES) {
            frameDuration = 0.5f;
            animation = new Animation<>(frameDuration, frames);
            animation.setPlayMode(Animation.PlayMode.LOOP);
        }
        else if (
            type.equals(AnimationType.HOLLOW_KNIGHT_NORMAL_SLASH) || type.equals(AnimationType.HOLLOW_KNIGHT_DOWN_SLASH_EFFECT) || type.equals(AnimationType.HOLLOW_KNIGHT_SLASH_ALT)  ||
                type.equals(AnimationType.HOLLOW_KNIGHT_SLASH_ALT_EFFECT) || type.equals(AnimationType.HOLLOW_KNIGHT_UP_SLASH_EFFECT) || type.equals(AnimationType.HOLLOW_KNIGHT_DASH_EFFECT) || type.equals(AnimationType.HOLLOW_KNIGHT_LOOKING_DOWN_FRAMES)
                || type.equals(AnimationType.HOLLOW_KNIGHT_LOOKING_UP_FRAMES) || type.equals(AnimationType.HOLLOW_KNIGHT_UP_SLASH) ||
                type.equals(AnimationType.HOLLOW_KNIGHT_DOWN_SLASH) || type.equals(AnimationType.HOLLOW_KNIGHT_WALL_JUMP) || type.equals(AnimationType.HOLLOW_KNIGHT_JUMP)
                || type.equals(AnimationType.HOLLOW_KNIGHT_DEATH) || type.equals(AnimationType.HOLLOW_KNIGHT_HOWLING_WRAITHS) || type.equals(AnimationType.HOLLOW_KNIGHT_VENGEFUL_SPIRIT) || type.equals(AnimationType.HOLLOW_KNIGHT_VENGEFUL_SPIRIT_BLAST)
                || type.equals(AnimationType.HOLLOW_KNIGHT_VENGEFUL_SPIRIT_PROJECTILE) || type == AnimationType.MOSSCREEP_TURN_FRAMES ||
                type == AnimationType.MOSSCREEP_DEATH_AIR_FRAMES || type == AnimationType.MOSSCREEP_DEATH_LANDING_FRAMES || type == AnimationType.MOSQUITO_ANTICIPATE || type == AnimationType.MOSQUITO_ATTACK || type == AnimationType.MOSQUITO_DEATH_AIR || type == AnimationType.MOSQUITO_DEATH_LAND || type == AnimationType.MOSQUITO_TURN_L_TO_R || type == AnimationType.MOSQUITO_TURN_R_TO_L ||
                type == AnimationType.HUSK_HORNHEAD_TURN ||
                type == AnimationType.HUSK_HORNHEAD_ANTICIPATE ||
                type == AnimationType.HUSK_HORNHEAD_DEATH_AIR ||
                type == AnimationType.HUSK_HORNHEAD_DEATH_LAND) {
            animation = new Animation<>(frameDuration, frames);
            animation.setPlayMode(Animation.PlayMode.NORMAL);
        }
        else {
            animation = new Animation<>(frameDuration, frames);
            animation.setPlayMode(Animation.PlayMode.LOOP);
        }
        animationMap.put(type, animation);
    }

    public static void playRandomZoteSound() {
        if (zoteTalkSounds != null && zoteTalkSounds.length > 0) {
            int index = com.badlogic.gdx.math.MathUtils.random(0, zoteTalkSounds.length - 1);
            if (zoteTalkSounds[index] != null) {
                zoteTalkSounds[index].play(1.0f);
            }
        }
    }
}
