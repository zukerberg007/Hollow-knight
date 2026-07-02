package com.amirali.graphics.models.enemies;

import com.amirali.graphics.GameAssetManager;
import com.amirali.graphics.models.Entity;
import com.amirali.graphics.models.Player;
import com.amirali.graphics.models.SolidBlock;
import com.amirali.graphics.views.AnimationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Zote extends Entity {
    public enum State { IDLE, TALKING, ANGRY }
    public State state = State.IDLE;

    public float stateTimer = 0f;
    public float angryTimer = 0f;

    public float direction = -1f;
    private Player player;

    public int mainDialogueIndex = 0;
    public boolean hasFinishedMainDialogue = false;

    public final String[] mainDialogues = {
        "zote.main.0",
        "zote.main.1",
        "zote.main.2"
    };

    public final String[] precepts = {
        "zote.precept.0",
        "zote.precept.1",
        "zote.precept.2",
        "zote.precept.3"
    };

    public Zote(float x, float y) {
        super(x, y, 70, 90, 9999);
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
    public Rectangle getInteractBounds() {
        return new Rectangle(bounds.x - 50, bounds.y, bounds.width + 100, bounds.height);
    }

    @Override
    public boolean takeDamage(float dmg, float sourceX) {
        if (state != State.ANGRY) {
            state = State.ANGRY;
            angryTimer = 3f;
            stateTimer = 0f;
            direction = (sourceX > bounds.x) ? 1f : -1f;

            if (GameAssetManager.zoteAttackSound != null) {
                GameAssetManager.zoteAttackSound.play(1.0f);
            }
        }
        return true;
    }

    @Override
    public void update(float delta, Array<SolidBlock> blocks) {
        super.update(delta, blocks);

        if (state == State.ANGRY) {
            angryTimer -= delta;

            direction = (player.position.x > bounds.x) ? 1f : -1f;
            bounds.x += 180f * direction * delta;

            if (angryTimer <= 0) {
                state = State.IDLE;
                stateTimer = 0f;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        stateTimer += Gdx.graphics.getDeltaTime();
        Animation<TextureRegion> anim;

        if (state == State.ANGRY) {
            anim = GameAssetManager.animationMap.get(AnimationType.ZOTE_ATTACK);
        } else if (state == State.TALKING) {
            anim = GameAssetManager.animationMap.get(AnimationType.ZOTE_TALK);
            if (player != null) {
                direction = (player.position.x > bounds.x) ? 1f : -1f;
            }
        } else {
            anim = GameAssetManager.animationMap.get(AnimationType.ZOTE_IDLE);
        }

        if (anim == null) return;
        TextureRegion frame = anim.getKeyFrame(stateTimer, true);

        float scale = 0.9f;
        float spriteWidth = frame.getRegionWidth() * scale;
        float spriteHeight = frame.getRegionHeight() * scale;

        float scaleX = (direction > 0) ? -scale : scale;

        if (state == State.ANGRY) batch.setColor(1f, 0.5f, 0.5f, 1f);

        batch.draw(frame, bounds.x - (spriteWidth - bounds.width)/2f, bounds.y - 20, spriteWidth / 2f, spriteHeight / 2f, spriteWidth, spriteHeight, scaleX, scale, 0f);

        batch.setColor(Color.WHITE);
    }

    @Override
    protected void onDeath() {}
    @Override
    protected float invincibleTime() { return 0.2f; }
}
