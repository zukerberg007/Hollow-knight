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

public class HuskHornhead extends Entity {
    private enum State {
        WALK, IDLE, TURN, ANTICIPATE, CHARGE, DEATH_AIR, DEATH_LAND, DEAD
    }

    private State state = State.WALK;
    private float stateTimer = 0f;
    private float walkTimer = 0f;
    private float direction = 1f;
    private float speed = 60f;
    private float chargeSpeed = 300f;
    private float moveTimer = 0f;
    private static final float MOVE_DURATION = 3.0f;
    private static final float REST_DURATION = 1.5f;

    private float visionRange = 400f;
    private float visionHeight = 60f;
    private final float defaultHeight;

    protected float knockbackTimer = 0f;
    protected float knockbackSpeed = 0f;

    private Player player;


    private static final float TURN_DURATION = 0.2f;
    private static final float ANTICIPATE_DURATION = 0.5f;
    private static final float DEATH_AIR_DURATION = 0.1f;
    private static final float DEATH_LAND_DURATION = 0.8f;

    private float hitFlash = 0f;
    private final float maxHealth;
    private final float respawnX, respawnY;
    private boolean playerHasLeft = false;
    private static final float RESPAWN_FAR_DIST = 1500f;
    private static final float RESPAWN_NEAR_DIST = 1200f;

    private float drawOffsetX = 0f;
    private float drawOffsetY = 0f;

    public HuskHornhead(float x, float y, float w, float h, float health) {
        super(x, y, w, h, health);
        this.maxHealth = health;
        this.respawnX = x;
        this.respawnY = y;
        this.defaultHeight = h;
        bounds.x = x;
        bounds.y = y;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public boolean takeDamage(float dmg, float sourceX) {
        boolean hit = super.takeDamage(dmg);
        if (hit && state != State.DEAD && state != State.DEATH_AIR && state != State.DEATH_LAND) {
            knockbackTimer = 0.2f;
            float baseKnockback = 550f;
            float multiplier = (player != null) ? player.getKnockbackModifier() : 1.0f;
            knockbackSpeed = (bounds.x > sourceX) ? (baseKnockback * multiplier) : -(baseKnockback * multiplier);

            float center = bounds.x + bounds.width / 2f;
            float attackerDirection = (sourceX > center) ? 1f : -1f;

            if (direction != attackerDirection) {
                direction = attackerDirection;
                state = State.TURN;
                stateTimer = 0f;
                walkTimer = 0f;
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
    }
    @Override
    public void update(float delta, Array<SolidBlock> blocks) {
        super.update(delta, blocks);
        if (hitFlash > 0f) hitFlash -= delta;
        handleRespawn();

        if (state == State.WALK || state == State.TURN) {
            walkTimer += delta;
        }

        if (state == State.ANTICIPATE || state == State.CHARGE) {
            bounds.height = defaultHeight + 10;
        } else {
            bounds.height = defaultHeight + 15;
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
            case WALK:
                updateWalk(delta, blocks);
                break;
            case IDLE:
                updateIdle(delta, blocks);
                break;
            case TURN:
                stateTimer += delta;
                if (stateTimer >= TURN_DURATION) {
                    state = State.WALK;
                    stateTimer = 0f;
                }
                break;
            case ANTICIPATE:
                stateTimer += delta;
                if (stateTimer >= ANTICIPATE_DURATION) {
                    state = State.CHARGE;
                    stateTimer = 0f;
                }
                break;
            case CHARGE:
                updateCharge(delta, blocks);
                break;
            case DEATH_AIR:
                stateTimer += delta;
                if (stateTimer >= DEATH_AIR_DURATION) {
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
    private void updateWalk(float delta, Array<SolidBlock> blocks) {
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

        moveTimer += delta;
        if (moveTimer >= MOVE_DURATION) {
            state = State.IDLE;
            stateTimer = 0f;
            moveTimer = 0f;
            return;
        }

        if (player != null && isPlayerInVision()) {
            if (player.position.x < bounds.x) direction = -1;
            else direction = 1;
            state = State.ANTICIPATE;
            stateTimer = 0f;
            return;
        }


        float frontX = (direction > 0) ? bounds.x + bounds.width : bounds.x - 4;
        Rectangle wallCheck = new Rectangle(frontX, bounds.y + 2, 4, bounds.height - 4);
        if (isOverlappingBlock(wallCheck, blocks)) {
            changeDirection();
            return;
        }
        Rectangle groundCheck = new Rectangle(frontX, bounds.y - 2, 4, 3);
        if (!isOverlappingBlock(groundCheck, blocks)) {
            changeDirection();
            return;
        }

        bounds.x += speed * direction * delta;

        // Resolve collision
        for (SolidBlock b : blocks) {
            if (b.isDeadly) continue;
            if (bounds.overlaps(b.bounds)) {
                if (direction > 0) bounds.x = b.bounds.x - bounds.width;
                else bounds.x = b.bounds.x + b.bounds.width;
                changeDirection();
                break;
            }
        }
    }
    private void updateIdle(float delta, Array<SolidBlock> blocks) {
        stateTimer += delta;
        if (stateTimer >= REST_DURATION) {
            state = State.WALK;
            stateTimer = 0f;
            moveTimer = 0f;
        }
    }
    private void updateCharge(float delta, Array<SolidBlock> blocks) {
        stateTimer += delta;
        bounds.x += chargeSpeed * direction * delta;

        // Stop charging if hitting a wall
        for (SolidBlock b : blocks) {
            if (b.isDeadly) continue;
            if (bounds.overlaps(b.bounds)) {
                if (direction > 0) bounds.x = b.bounds.x - bounds.width;
                else bounds.x = b.bounds.x + b.bounds.width;
                state = State.WALK;
                stateTimer = 0f;
                moveTimer = 0f;
                return;
            }
        }

        float frontX = (direction > 0) ? bounds.x + bounds.width : bounds.x - 4;
        Rectangle groundCheck = new Rectangle(frontX, bounds.y - 2, 4, 3);
        if (!isOverlappingBlock(groundCheck, blocks)) {
            state = State.WALK;
            stateTimer = 0f;
            moveTimer = 0f;
            return;
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
    private void changeDirection() {
        direction *= -1;
        state = State.TURN;
        stateTimer = 0f;
        walkTimer = 0f;
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
            if (!alive) {
                respawn(); // only dead enemies come back; living ones must not teleport
            } else {
                playerHasLeft = false; // re-arm: a later death needs a fresh leave-and-return
            }
        }
    }

    public void respawn() {
        bounds.x = respawnX;
        bounds.y = respawnY;
        bounds.height = defaultHeight;
        health = maxHealth;
        alive = true;
        invincibleTimer = 0f;
        state = State.WALK;
        stateTimer = 0f;
        walkTimer = 0f;
        moveTimer = 0f;
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
                anim = GameAssetManager.animationMap.get(AnimationType.HUSK_HORNHEAD_WALK);
                animTime = walkTimer;
                loop = true;
                break;
            case IDLE:
                anim = GameAssetManager.animationMap.get(AnimationType.HUSK_HORNHEAD_IDLE);
                animTime = stateTimer;
                loop = true;
                break;
            case TURN:
                anim = GameAssetManager.animationMap.get(AnimationType.HUSK_HORNHEAD_TURN);
                animTime = stateTimer;
                loop = false;
                break;
            case ANTICIPATE:
                anim = GameAssetManager.animationMap.get(AnimationType.HUSK_HORNHEAD_ANTICIPATE);
                animTime = stateTimer;
                loop = false;
                break;
            case CHARGE:
                anim = GameAssetManager.animationMap.get(AnimationType.HUSK_HORNHEAD_LUNGE);
                animTime = stateTimer;
                loop = true;
                break;
            case DEATH_AIR:
                anim = GameAssetManager.animationMap.get(AnimationType.HUSK_HORNHEAD_DEATH_AIR);
                animTime = stateTimer;
                loop = false;
                break;
            case DEATH_LAND:
            case DEAD:
                anim = GameAssetManager.animationMap.get(AnimationType.HUSK_HORNHEAD_DEATH_LAND);
                animTime = (state == State.DEAD) ? 100f : stateTimer;
                loop = false;
                break;
            default:
                return;
        }

        if (anim == null) {
            return;
        }
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

        batch.setColor(Color.WHITE);
    }

    @Override
    protected float invincibleTime() { return 0f; }

}
