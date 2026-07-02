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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Mosquito extends Entity {
    private enum State {
        IDLE,
        TURN,
        ANTICIPATE,
        CHARGE,
        DEATH_AIR,
        DEATH_LAND,
        DEAD
    }

    private State state = State.IDLE;
    private float stateTimer = 0f;
    private float idleTimer = 0f;
    private float direction = 1f;
    private float targetDirection = 1f;
    private Vector2 targetPosition = new Vector2();


    private float aggroRange = 350f;
    private float chargeSpeed = 450f;
    private float chargeDuration = 1.5f;
    private float turnDuration = 0.2f;


    private float attackCooldownTimer = 0f;
    private float attackCooldown = 1.5f;

    protected float knockbackTimer = 0f;
    protected float knockbackSpeed = 0f;

    private Player player;

    private static final float ANTICIPATE_DURATION = 0.3f;
    private static final float DEATH_AIR_DURATION = 0.3f;
    private static final float DEATH_LAND_DURATION = 0.3f;

    private float hitFlash = 0f;
    private final float maxHealth;
    private final float respawnX, respawnY;
    private boolean playerHasLeft = false;
    private static final float RESPAWN_FAR_DIST = 1500f;
    private static final float RESPAWN_NEAR_DIST = 1200f;

    private float drawOffsetX = 0f;
    private float drawOffsetY = 0f;


    public Mosquito(float x, float y, float w, float h, float health) {
        super(x, y, w, h, health);
        this.maxHealth = health;
        this.respawnX = x;
        this.respawnY = y;
        bounds.x = x;
        bounds.y = y;
    }

    public void setPlayer(Player player) {this.player = player;}

    @Override
    public boolean takeDamage(float dmg, float sourceX) {
        boolean hit = super.takeDamage(dmg);
        if (hit && state != State.DEAD && state != State.DEATH_AIR && state != State.DEATH_LAND) {
            knockbackTimer = 0.15f;
            float baseKnockback = 400f;
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

        idleTimer += delta;
        if (attackCooldownTimer > 0f) attackCooldownTimer -= delta;

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
            case TURN:
                stateTimer += delta;
                if (stateTimer >= turnDuration) {
                    state = State.IDLE;
                    stateTimer = 0f;
                    direction = targetDirection;
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
                bounds.y -= 400f * delta;
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
        if (player == null) return;

        float dx = player.position.x - bounds.x;
        float dy = player.position.y - bounds.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist < aggroRange && attackCooldownTimer <= 0f) {
            int playerDirection = (dx > 0) ? 1 : -1;

            if (playerDirection != direction) {
                targetDirection = playerDirection;
                state = State.TURN;
                stateTimer = 0f;
            } else {
                targetPosition.set(player.position);
                state = State.ANTICIPATE;
                stateTimer = 0f;
            }
        }
    }

    private void updateCharge(float delta, Array<SolidBlock> blocks) {
        float dx = targetPosition.x - bounds.x;
        float dy = targetPosition.y - bounds.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist < 2f) {
            state = State.IDLE;
            stateTimer = 0f;
            attackCooldownTimer = attackCooldown;
            return;
        }

        float moveX = dx / dist * chargeSpeed * delta;
        float moveY = dy / dist * chargeSpeed * delta;
        bounds.x += moveX;
        bounds.y += moveY;

        for (SolidBlock b : blocks) {
            if (b.isDeadly) continue;
            if (bounds.overlaps(b.bounds)) {
                if (moveX > 0) bounds.x = b.bounds.x - bounds.width;
                else if (moveX < 0) bounds.x = b.bounds.x + b.bounds.width;
                if (moveY > 0) bounds.y = b.bounds.y - bounds.height;
                else if (moveY < 0) bounds.y = b.bounds.y + b.bounds.height;

                state = State.IDLE;
                stateTimer = 0f;
                attackCooldownTimer = attackCooldown;
                return;
            }
        }

        stateTimer += delta;
        if (stateTimer >= chargeDuration) {
            state = State.IDLE;
            stateTimer = 0f;
            attackCooldownTimer = attackCooldown;
        }
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
        health = maxHealth;
        alive = true;
        invincibleTimer = 0f;
        state = State.IDLE;
        stateTimer = 0f;
        idleTimer = 0f;
        direction = 1f;
        targetDirection = 1f;
        attackCooldownTimer = 0f;
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
                anim = GameAssetManager.animationMap.get(AnimationType.MOSQUITO_IDLE);
                animTime = idleTimer;
                loop = true;
                break;
            case TURN:
                if (targetDirection > 0) {
                    anim = GameAssetManager.animationMap.get(AnimationType.MOSQUITO_TURN_L_TO_R);
                } else {
                    anim = GameAssetManager.animationMap.get(AnimationType.MOSQUITO_TURN_R_TO_L);
                }
                animTime = stateTimer;
                loop = false;
                break;
            case ANTICIPATE:
                anim = GameAssetManager.animationMap.get(AnimationType.MOSQUITO_ANTICIPATE);
                animTime = stateTimer;
                loop = false;
                break;
            case CHARGE:
                anim = GameAssetManager.animationMap.get(AnimationType.MOSQUITO_ATTACK);
                animTime = stateTimer;
                loop = false;
                break;
            case DEATH_AIR:
                anim = GameAssetManager.animationMap.get(AnimationType.MOSQUITO_DEATH_AIR);
                animTime = stateTimer;
                loop = false;
                break;
            case DEATH_LAND:
            case DEAD:
                anim = GameAssetManager.animationMap.get(AnimationType.MOSQUITO_DEATH_LAND);
                animTime = (state == State.DEAD) ? 100f : stateTimer;
                loop = false;
                break;
            default:
                return;
        }

        if (anim == null) return;
        TextureRegion frame = loop ? anim.getKeyFrame(animTime, true) : anim.getKeyFrame(animTime, false);
        if (frame == null) return;

        float scale = 1f;
        float spriteWidth = frame.getRegionWidth() * scale;
        float spriteHeight = frame.getRegionHeight() * scale;

        float cx = bounds.x + bounds.width / 2f + drawOffsetX;
        float cy = bounds.y + bounds.height / 2f + drawOffsetY;

        if (hitFlash > 0f) batch.setColor(1f, 0.4f, 0.4f, 1f);

        float scaleX;
        if (state == State.TURN) {
            scaleX = 1f;
        } else {
            scaleX = (direction > 0) ? -1f : 1f;
        }

        float rotation = 0f;
        if (state == State.ANTICIPATE || state == State.CHARGE) {
            float dx = targetPosition.x - bounds.x;
            float dy = targetPosition.y - bounds.y;
            float angle = (float) Math.toDegrees(Math.atan2(dy, dx));

            if (direction < 0) {
                rotation = angle - 180f;
            } else {
                rotation = angle;
            }
        }

        batch.draw(frame,
            cx - spriteWidth / 2,
            cy - spriteHeight / 2,
            spriteWidth / 2,
            spriteHeight / 2,
            spriteWidth + 15,
            spriteHeight + 15,
            scaleX, 1f,
            rotation);

        batch.setColor(Color.WHITE);
    }


    @Override
    protected float invincibleTime() { return 0f; }
}
