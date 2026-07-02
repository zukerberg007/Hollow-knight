package com.amirali.graphics.views;

import com.amirali.graphics.GameAssetManager;
import com.amirali.graphics.models.Player;
import com.amirali.graphics.views.AnimationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class GameHud {
    private final OrthographicCamera uiCamera;
    private final SpriteBatch batch;

    private final Animation<TextureRegion> maskShine;
    private final Animation<TextureRegion> maskBreak;
    private final Animation<TextureRegion> maskRefill;

    private float stateTime = 0f;
    private final float[] breakTimer;
    private int lastMasks;

    private static final int MAX_MASKS = 5;
    private static final float MASK_SIZE = 120f;
    private static final float MASK_GAP  = -30f;
    private static final float ORB_SIZE  = 200f;
    private static final float MARGIN    = 30f;

    private float displayedSoulRatio = 0f;

    public GameHud(Player player) {
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();

        maskShine  = GameAssetManager.animationMap.get(AnimationType.HOLLOW_KNIGHT_MASK_SHINE_FRAMES);
        maskBreak  = GameAssetManager.animationMap.get(AnimationType.HOLLOW_KNIGHT_MASK_BREAK_FRAMES);
        maskRefill = GameAssetManager.animationMap.get(AnimationType.HOLLOW_KNIGHT_MASK_REFILL_FRAMES);

        breakTimer = new float[MAX_MASKS];
        lastMasks  = player.masks;
        displayedSoulRatio = (float) player.soul / Player.MAX_SOUL;
    }

    public void render(Player player, float delta) {
        stateTime += delta;

        int masks = player.masks;
        float targetRatio = (float) player.soul / Player.MAX_SOUL;

        displayedSoulRatio += (targetRatio - displayedSoulRatio) * Math.min(1f, delta * 8f);
        displayedSoulRatio = MathUtils.clamp(displayedSoulRatio, 0f, 1f);

        if (masks < lastMasks) {
            for (int i = masks; i < lastMasks && i < MAX_MASKS; i++) {
                breakTimer[i] = maskBreak.getAnimationDuration();
            }
        }
        lastMasks = masks;

        uiCamera.update();
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        drawSoulOrb(displayedSoulRatio);
        drawMasks(player, masks, delta);

        batch.end();
    }

    private void drawSoulOrb(float ratio) {
        float x = MARGIN;
        float y = Gdx.graphics.getHeight() - ORB_SIZE - MARGIN;
        ratio = MathUtils.clamp(ratio, 0f, 1f);

        batch.draw(GameAssetManager.soulOrbEmpty, x, y, ORB_SIZE, ORB_SIZE);

        if (ratio > 0.001f) {
            int texHeight = GameAssetManager.soulOrb.getHeight();
            int srcH = Math.round(texHeight * ratio);
            int srcY = texHeight - srcH;   // start from bottom

            TextureRegion fill = new TextureRegion(
                GameAssetManager.soulOrb,
                0, srcY,
                GameAssetManager.soulOrb.getWidth(),
                srcH
            );

            float drawH = ORB_SIZE * ratio;
            batch.draw(fill, x, y, ORB_SIZE , drawH);
        }
    }

    private void drawMasks(Player player, int masks, float delta) {
        float startX = MARGIN + ORB_SIZE - 125;
        float orbY   = Gdx.graphics.getHeight() - ORB_SIZE - MARGIN;
        float y      = orbY + (ORB_SIZE - MASK_SIZE) / 2f + 10;

        for (int i = 0; i < MAX_MASKS; i++) {
            float x = startX + i * (MASK_SIZE + MASK_GAP);

            if (breakTimer[i] > 0f) {
                breakTimer[i] -= delta;
                float t = maskBreak.getAnimationDuration() - breakTimer[i];
                batch.draw(maskBreak.getKeyFrame(t, false), x, y, MASK_SIZE, MASK_SIZE);
            } else if (i < masks) {
                batch.draw(maskShine.getKeyFrame(stateTime, true), x, y, MASK_SIZE, MASK_SIZE);
            } else if (i == masks && player.focusing) {
                batch.draw(maskRefill.getKeyFrame(stateTime, true), x, y, MASK_SIZE, MASK_SIZE);
            } else {
                batch.draw(GameAssetManager.emptyMask, x, y, MASK_SIZE, MASK_SIZE);
            }
        }
    }

    public void resize(int width, int height) {
        uiCamera.setToOrtho(false, width, height);
    }

    public void dispose() {
        batch.dispose();
    }
}
