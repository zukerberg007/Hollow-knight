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

public class CrystalGuardian extends Entity {
    private enum State {
        IDLE, EVADE, SHOOT, ENRAGE, TURN, DEATH_AIR, DEATH_LAND, DEAD
    }

    private State state = State.IDLE;
    private float stateTimer = 0f;
    private float runTimer = 0f;
    private float direction = 1f;


    private float enrageSpeed = 450f;
    private float evadeSpeed = 100f;


    private static final float EVADE_DURATION = 0.5f;
    private static final float SHOOT_DURATION = 0.5f;
    private static final float ENRAGE_DURATION = 2.5f;
    private static final float TURN_DURATION = 0.2f;
    private static final float DEATH_AIR_DURATION = 0.1f;
    private static final float DEATH_LAND_DURATION = 0.8f;

    private float visionRange = 700f;
    private float visionHeight = 50f;
    private Rectangle laserBox = new Rectangle();
    private boolean isLaserActive = false;
    private boolean laserHitConsumed = false;

    protected float knockbackTimer = 0f;
    protected float knockbackSpeed = 0f;
    private float hitFlash = 0f;
    private final float defaultHeight;
    private final float maxHealth;
    private final float respawnX, respawnY;
    private boolean playerHasLeft = false;
    private static final float RESPAWN_FAR_DIST = 1500f;
    private static final float RESPAWN_NEAR_DIST = 1200f;
    private Player player;

    private float drawOffsetX = 0f;
    private float drawOffsetY = 0f;

    public CrystalGuardian(float x, float y, float w, float h, float health) {
        super(x, y, w, h, health);
        this.defaultHeight = h;
        this.maxHealth = health;
        this.respawnX = x;
        this.respawnY = y;
        bounds.x = x;
        bounds.y = y;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Rectangle getLaserBox() {
        return laserBox;
    }

    public boolean isShootingLaser() {
        return isLaserActive && state == State.SHOOT;
    }

    @Override
    public boolean takeDamage(float dmg, float sourceX) {
        boolean hit = super.takeDamage(dmg);
        if (hit && state != State.DEAD && state != State.DEATH_AIR && state != State.DEATH_LAND) {
            knockbackTimer = 0.15f;
            float baseKnockback = 400f;
            float multiplier = (player != null) ? player.getKnockbackModifier() : 1.0f;
            knockbackSpeed = (bounds.x > sourceX) ? (baseKnockback * multiplier) : -(baseKnockback * multiplier);

            if (state == State.IDLE) {
                float center = bounds.x + bounds.width / 2f;
                float attackerDirection = (sourceX > center) ? 1f : -1f;
                if (direction != attackerDirection) {
                    direction = attackerDirection;
                    state = State.TURN;
                    stateTimer = 0f;
                }
            }
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
        isLaserActive = false;
    }

    @Override
    public void update(float delta, Array<SolidBlock> blocks) {
        super.update(delta, blocks);
        if (hitFlash > 0f) hitFlash -= delta;
        handleRespawn();

        if (state == State.ENRAGE || state == State.TURN) {
            runTimer += delta;
        }

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
            case IDLE:
                updateIdle(delta, blocks);
                break;
            case EVADE:
                updateEvade(delta, blocks);
                break;
            case SHOOT:
                updateShoot(delta, blocks);
                break;
            case ENRAGE:
                updateEnrage(delta, blocks);
                break;
            case TURN:
                stateTimer += delta;
                if (stateTimer >= TURN_DURATION) {
                    state = State.IDLE;
                    stateTimer = 0f;
                }
                break;
            case DEATH_AIR:
                bounds.y -= 400f * delta; // Fall to the ground
                boolean hitGround = false;
                for (SolidBlock b : blocks) {
                    if (b.isDeadly) continue;
                    if (bounds.overlaps(b.bounds)) {
                        bounds.y = b.bounds.y + b.bounds.height;
                        hitGround = true;
                        break;
                    }
                }
                if (hitGround) {
                    state = State.DEATH_LAND;
                    stateTimer = 0f;
                }
                break;
            case DEATH_LAND:
                stateTimer += delta;
                if (stateTimer >= DEATH_LAND_DURATION) {
                    state = State.DEAD;
                    stateTimer = 0f;
                    alive = false;
                }
                break;
            case DEAD:
                break;
        }
    }

    private void updateIdle(float delta, Array<SolidBlock> blocks) {
        applyGravity(delta, blocks);

        stateTimer += delta;

        if (player != null && isPlayerInVision()) {
            state = State.EVADE;
            stateTimer = 0f;
            isLaserActive = false;
            laserHitConsumed = false;
        }
    }

    private void updateEvade(float delta, Array<SolidBlock> blocks) {
        applyGravity(delta, blocks);
        stateTimer += delta;
        if (knockbackTimer <= 0f) {
            bounds.x += evadeSpeed * -direction * delta;
            resolveHorizontalCollision(blocks, -direction);
        }
        if (stateTimer >= EVADE_DURATION) {
            state = State.SHOOT;
            stateTimer = 0f;
            isLaserActive = true;
        }
    }

    private void updateShoot(float delta, Array<SolidBlock> blocks) {
        applyGravity(delta, blocks);
        stateTimer += delta;

        float lx = (direction > 0) ? bounds.x + bounds.width : bounds.x - visionRange;
        float ly = bounds.y + bounds.height * 0.4f;

        laserBox.set(lx, ly, visionRange, 40f);

        if (stateTimer >= 0.25f) {
            if (player != null && !player.isDying && isLaserActive && !laserHitConsumed) {
                if (laserBox.overlaps(player.getBounds())) {
                    player.takeDamage(1, bounds.x);
                    laserHitConsumed = true;
                }
            }
        }

        if (stateTimer >= SHOOT_DURATION) {
            isLaserActive = false;
            state = State.ENRAGE;
            stateTimer = 0f;
            runTimer = 0f;
        }
    }

    private void updateEnrage(float delta, Array<SolidBlock> blocks) {
        applyGravity(delta, blocks);
        stateTimer += delta;

        if (stateTimer >= ENRAGE_DURATION) {
            state = State.IDLE;
            stateTimer = 0f;
            return;
        }

        float frontX = (direction > 0) ? bounds.x + bounds.width : bounds.x - 4;

        Rectangle wallCheck = new Rectangle(frontX, bounds.y + 2, 4, bounds.height - 4);
        Rectangle groundCheck = new Rectangle(frontX, bounds.y - 2, 4, 3);

        if (isOverlappingBlock(wallCheck, blocks) || !isOverlappingBlock(groundCheck, blocks)) {
            direction *= -1;
            return;
        }

        if (knockbackTimer <= 0f) {
            bounds.x += enrageSpeed * direction * delta;
        }
        resolveHorizontalCollision(blocks, direction);
    }

    private void applyGravity(float delta, Array<SolidBlock> blocks) {
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
            bounds.y -= 400f * delta;
        }
    }

    private void resolveHorizontalCollision(Array<SolidBlock> blocks, float checkDir) {
        for (SolidBlock b : blocks) {
            if (b.isDeadly) continue;
            if (bounds.overlaps(b.bounds)) {
                if (checkDir > 0) bounds.x = b.bounds.x - bounds.width;
                else bounds.x = b.bounds.x + b.bounds.width;
                break;
            }
        }
    }

    private boolean isPlayerInVision() {
        if (player == null) return false;
        float visionX;
        if (direction > 0) {
            visionX = bounds.x + bounds.width;
        } else {
            visionX = bounds.x - visionRange;
        }
        float visionY = bounds.y + (bounds.height - visionHeight) / 2f;
        Rectangle visionRect = new Rectangle(visionX, visionY, visionRange, visionHeight);

        return visionRect.overlaps(player.getBounds());
    }

    private boolean isOverlappingBlock(Rectangle rect, Array<SolidBlock> blocks) {
        for (SolidBlock b : blocks) {
            if (b.isDeadly) continue;
            if (rect.overlaps(b.bounds)) return true;
        }
        return false;
    }

    private void handleRespawn() {
        if (player == null) return;
        float dx = player.position.x - bounds.x;
        float dy = player.position.y - bounds.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist > RESPAWN_FAR_DIST) {
            playerHasLeft = true;
        } else if (playerHasLeft && dist < RESPAWN_NEAR_DIST) {
            respawn();
        }
    }

    public void respawn() {
        bounds.x = respawnX;
        bounds.y = respawnY;
        health = maxHealth;
        alive = true;
        invincibleTimer = 0f;
        state = State.IDLE;
        stateTimer = 0f;
        runTimer = 0f;
        direction = 1f;
        isLaserActive = false;
        laserHitConsumed = false;
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
            case IDLE:
                anim = GameAssetManager.animationMap.get(AnimationType.CRYSTAL_GUARDIAN_IDLE);
                animTime = stateTimer;
                loop = true;
                break;
            case EVADE:
                anim = GameAssetManager.animationMap.get(AnimationType.CRYSTAL_GUARDIAN_EVADE);
                animTime = stateTimer;
                loop = false;
                break;
            case SHOOT:
                anim = GameAssetManager.animationMap.get(AnimationType.CRYSTAL_GUARDIAN_SHOOT);
                animTime = stateTimer;
                loop = false;
                break;
            case ENRAGE:
                anim = GameAssetManager.animationMap.get(AnimationType.CRYSTAL_GUARDIAN_RUN);
                animTime = runTimer;
                loop = true;
                break;
            case TURN:
                anim = GameAssetManager.animationMap.get(AnimationType.CRYSTAL_GUARDIAN_TURN);
                animTime = stateTimer;
                loop = false;
                break;
            case DEATH_AIR:
                anim = GameAssetManager.animationMap.get(AnimationType.CRYSTAL_GUARDIAN_DEATH_AIR);
                animTime = stateTimer;
                loop = false;
                break;
            case DEATH_LAND:
            case DEAD:
                anim = GameAssetManager.animationMap.get(AnimationType.CRYSTAL_GUARDIAN_DEATH_LAND);
                animTime = (state == State.DEAD) ? 100f : stateTimer;
                loop = false;
                break;
        }

        if (anim == null) return;
        TextureRegion frame = loop ? anim.getKeyFrame(animTime, true) : anim.getKeyFrame(animTime, false);
        if (frame == null) return;

        float scale = 1f;
        float spriteWidth = frame.getRegionWidth() * scale;
        float spriteHeight = frame.getRegionHeight() * scale;

        float cx = bounds.x + bounds.width / 2f + drawOffsetX;

        if (hitFlash > 0f) batch.setColor(1f, 0.4f, 0.4f, 1f);

        float scaleX = (direction > 0) ? -1f : 1f;
        batch.draw(frame,
            cx - spriteWidth / 2,
            bounds.y + drawOffsetY,
            spriteWidth / 2,
            0,
            spriteWidth,
            spriteHeight,
            scaleX, 1f, 0f);

        if (isShootingLaser()) {
            Animation<TextureRegion> laserAnim = GameAssetManager.animationMap.get(AnimationType.CRYSTAL_GUARDIAN_LASER);
            if (laserAnim != null) {
                TextureRegion laserTex = laserAnim.getKeyFrame(0);

                float laserDrawHeight = 120f;
                float laserDrawY = laserBox.y - (laserDrawHeight - laserBox.height) / 2f;
                batch.draw(laserTex, laserBox.x, laserDrawY, laserBox.width, laserDrawHeight);
            }
        }
        batch.setColor(Color.WHITE);
    }
}
