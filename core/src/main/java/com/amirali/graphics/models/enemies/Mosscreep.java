package com.amirali.graphics.models.enemies;

import com.amirali.graphics.GameAssetManager;
import com.amirali.graphics.models.Entity;
import com.amirali.graphics.models.Player;
import com.amirali.graphics.models.SolidBlock;
import com.amirali.graphics.views.AnimationType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Mosscreep extends Entity {
    private enum State {
        WALK, TURN, DEATH_AIR, DEATH_LANDING, DEAD
    }

    public static final float GROUND_Y = -200f;

    private State state = State.WALK;
    private float stateTimer = 0f;
    private float walkTimer = 0f;
    private float direction = 1f;
    private float speed = 60f;

    protected float knockbackTimer = 0f;
    protected float knockbackSpeed = 0f;

    private static final float TURN_DURATION = 0.2f;
    private static final float DEATH_AIR_DURATION = 0.3f;
    private static final float DEATH_LANDING_DURATION = 0.2f;

    private float hitFlash = 0f;
    private final float maxHealth;
    private final float respawnX, respawnY;
    private boolean playerHasLeft = false;
    private static final float RESPAWN_FAR_DIST = 1500f;
    private static final float RESPAWN_NEAR_DIST = 1200f;

    private final float drawW = 140f, drawH = 120f;
    private final float drawOffsetX = -30f;
    private final float drawOffsetY = 0f;

    private Player player;

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Mosscreep(float x, float y, float w, float h, float health) {
        super(x, y, w, h, health);
        this.maxHealth = health;
        this.respawnX = x;
        this.respawnY = y;
        bounds.x = x;
        bounds.y = y;
    }

    @Override
    public boolean takeDamage(float dmg, float sourceX) {
        boolean hit = super.takeDamage(dmg);
        if (hit && state != State.DEAD && state != State.DEATH_AIR && state != State.DEATH_LANDING) {
            knockbackTimer = 0.15f;
            float baseKnockback = 300f;
            float multiplier = (player != null) ? player.getKnockbackModifier() : 1.0f;
            knockbackSpeed = (bounds.x > sourceX) ? (baseKnockback * multiplier) : -(baseKnockback * multiplier);
        }
        if (hit) hitFlash = 0.12f;
        return hit;
    }
    @Override
    public boolean takeDamagePogo(float dmg) {
        boolean hit = super.takeDamage(dmg);
        if (hit) hitFlash = 0.12f;
        return hit;
    }
    @Override
    public boolean takeDamage(float dmg) {
        return takeDamage(dmg, bounds.x);
    }

    @Override
    protected void onDeath() {
        state = State.DEATH_AIR;
        stateTimer = 0f;
        alive = true;
    }

    @Override
    public void update(float delta, Array<SolidBlock> blocks) {
        super.update(delta, blocks);
        if (hitFlash > 0f) hitFlash -= delta;
        handleRespawn();

        if (knockbackTimer > 0f) {
            knockbackTimer -= delta;
            bounds.x += knockbackSpeed * delta;
            for (SolidBlock b : blocks) {
                if (b.isDeadly) continue;
                if (bounds.overlaps(b.bounds)) {
                    if (knockbackSpeed > 0) bounds.x = b.bounds.x - bounds.width;
                    else bounds.x = b.bounds.x + b.bounds.width;
                    knockbackTimer = 0f;
                    break;
                }
            }
        }

        switch (state) {
            case WALK:
                updateWalk(delta, blocks);
                break;
            case TURN:
                stateTimer += delta;
                if (stateTimer >= TURN_DURATION) {
                    state = State.WALK;
                    stateTimer = 0f;
                }
                break;
            case DEATH_AIR:
                stateTimer += delta;
                if (stateTimer >= DEATH_AIR_DURATION) {
                    state = State.DEATH_LANDING;
                    stateTimer = 0f;
                }
                break;
            case DEATH_LANDING:
                stateTimer += delta;
                if (stateTimer >= DEATH_LANDING_DURATION) {
                    state = State.DEAD;
                    stateTimer = 0f;
                    alive = false;
                }
                break;
            case DEAD:
                break;
        }
    }

    private void updateWalk(float delta, Array<SolidBlock> blocks) {
        walkTimer += delta;

        boolean onGround = false;
        Rectangle feet = new Rectangle(bounds.x + 5, bounds.y - 2, bounds.width - 10, 4);
        for (SolidBlock b : blocks) {
            if (b.isDeadly) continue;
            if (feet.overlaps(b.bounds)) {
                onGround = true;
                bounds.y = b.bounds.y + b.bounds.height;
                break;
            }
        }

        if (!onGround) {
            bounds.y -= 200f * delta;
            return;
        }

        float frontX = (direction > 0) ? bounds.x + bounds.width : bounds.x - 4;
        float frontY = bounds.y;

        Rectangle wallCheck = new Rectangle(frontX, frontY + 2, 4, bounds.height - 4);
        if (isOverlappingBlock(wallCheck, blocks)) {
            changeDirection();
            return;
        }

        Rectangle groundCheck = new Rectangle(frontX, frontY - 2, 4, 3);
        if (!isOverlappingBlock(groundCheck, blocks)) {
            changeDirection();
            return;
        }

        if (knockbackTimer <= 0f) {
            bounds.x += speed * direction * delta;
        }

        for (SolidBlock b : blocks) {
            if (b.isDeadly) continue;
            if (bounds.overlaps(b.bounds)) {
                if (direction > 0) {
                    bounds.x = b.bounds.x - bounds.width;
                } else {
                    bounds.x = b.bounds.x + b.bounds.width;
                }
                changeDirection();
                break;
            }
        }
    }

    private void changeDirection() {
        direction *= -1;
        state = State.TURN;
        stateTimer = 0f;
    }

    private void handleRespawn() {
        if (player == null) return;
        float dx = player.position.x - bounds.x;
        float dy = player.position.y - bounds.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist > RESPAWN_FAR_DIST) {
            playerHasLeft = true;
        } else if (playerHasLeft && dist < RESPAWN_NEAR_DIST) {
            if (!alive) {
                respawn();
            } else {
                playerHasLeft = false;
            }
        }
    }

    public void respawn() {
        bounds.x = respawnX;
        bounds.y = respawnY;
        health = maxHealth;
        alive = true;
        invincibleTimer = 0f;
        state = State.WALK;
        stateTimer = 0f;
        walkTimer = 0f;
        direction = 1f;
        knockbackTimer = 0f;
        hitFlash = 0f;
        playerHasLeft = false;
    }

    @Override
    public void render(SpriteBatch batch) {

        Animation<TextureRegion> anim = null;
        float animTime = 0f;
        boolean loop = false;

        switch (state) {
            case WALK:
                anim = GameAssetManager.animationMap.get(AnimationType.MOSSCREEP_WALK_FRAMES);
                animTime = walkTimer;
                loop = true;
                break;
            case TURN:
                anim = GameAssetManager.animationMap.get(AnimationType.MOSSCREEP_TURN_FRAMES);
                animTime = stateTimer;
                loop = false;
                break;
            case DEATH_AIR:
                anim = GameAssetManager.animationMap.get(AnimationType.MOSSCREEP_DEATH_AIR_FRAMES);
                animTime = stateTimer;
                loop = false;
                break;
            case DEATH_LANDING:
            case DEAD:
                anim = GameAssetManager.animationMap.get(AnimationType.MOSSCREEP_DEATH_LANDING_FRAMES);
                animTime = (state == State.DEAD) ? 100f : stateTimer;
                loop = false;
                break;
            default:
                return;
        }

        if (anim == null) return;
        TextureRegion frame = loop ? anim.getKeyFrame(animTime, true) : anim.getKeyFrame(animTime, false);
        if (frame == null) return;

        float scale = 0.85f;
        float spriteWidth = frame.getRegionWidth() * scale;
        float spriteHeight = frame.getRegionHeight() * scale;

        float cx = bounds.x + bounds.width / 2f;
        float cy = bounds.y + bounds.height / 2f;

        if (hitFlash > 0f) batch.setColor(1f, 0.4f, 0.4f, 1f);

        float scaleX = (direction > 0) ? -1f : 1f;
        batch.draw(frame,
            cx - spriteWidth / 2,
            cy - spriteHeight / 2,
            spriteWidth / 2,
            spriteHeight / 2,
            spriteWidth,
            spriteHeight,
            scaleX,
            1f,
            0f);

        batch.setColor(Color.WHITE);
    }

    private boolean isOverlappingBlock(Rectangle rect, Array<SolidBlock> blocks) {
        for (SolidBlock b : blocks) {
            if (b.isDeadly) continue;
            if (rect.overlaps(b.bounds)) return true;
        }
        return false;
    }

    @Override
    protected float invincibleTime() { return 0f; }
}
