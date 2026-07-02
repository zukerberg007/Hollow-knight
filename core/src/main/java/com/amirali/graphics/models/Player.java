package com.amirali.graphics.models;

import com.amirali.graphics.GameAssetManager;
import com.amirali.graphics.views.AnimationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Player extends Entity{
    private static final float GRAVITY = -920f;
    public static final float GROUND_Y = -200f;
    public static final float BODY_W = 70f;
    public static final float BODY_H = 100f;
    public static final float BODY_OFF_X = 20f;
    public Vector2 position = new Vector2(0, GROUND_Y);
    public Vector2 velocity = new Vector2(0, 0);
    public boolean isOnGround = true;
    public boolean movingLeft = false , movingRight = false;
    public boolean facingRight = true;

    public AnimationType currentAnimation = AnimationType.HOLLOW_KNIGHT_IDLE;

    public float stateTime = 0f;
    public float spriteDrawnW = 0f;
    public float spriteDrawnH = 0f;
    private static final float BODY_W_FRAC = 0.2f;
    private static final float BODY_H_FRAC = 0.6f;
    private float spriteW = 0f, spriteH = 0f;
    public  float drawScale = 0.9f;
    public static final float DRAW_OFFSET_X = -170f;

    private float knockbackTimer = 0f;
    private float knockbackVelocityX = 0f;

    public Vector2 lastSafeSpot = new Vector2(0, GROUND_Y);
    private float safeSpotTimer = 0f;

    public int maxMasks = 5;
    public int masks = 5;

    public int soul = 0;
    public static final int MAX_SOUL = 99;
    public static final int SOUL_PER_HIT = 11;
    public static final int SOUL_PER_HEAL = 33;

    public boolean focusing = false;
    public float focusTimer = 0f;
    public static final float FOCUS_TIME = 1.5f;

    private final Rectangle attackBox = new Rectangle();
    private float attackTimer = 0f;
    private float slashTimer  = 0f;
    private int slashFrame  = 0;

    private static final float ATTACK_DURATION = 0.20f;
    private static final float SLASH_FRAME_TIME = 0.04f;
    private static final int   SLASH_FRAMES = 4;
    public static final float NAIL_DAMAGE = 1f;
    private float timeSinceLastPress = 0f;
    public int attackComboCounter = 0;
    private static final float COMBO_RESET_TIME = 0.4f;

    public boolean isDashing = false;
    private float dashTimer = 0f;
    private float dashCooldown = 0f;
    private int dashDir = 1;

    public static final float DASH_SPEED    = 1400f;
    public static final float DASH_DURATION = 0.25f;
    public static final float DASH_COOLDOWN = 1.2f;

    public boolean isLookingDown = false, isLookingUp = false;

    public boolean isLanding = false;
    private float landTimer = 0f;
    public static final float LAND_DURATION = 0.4f;

    private boolean jumpHeld = false;
    private float jumpHoldTime = 0f;
    private static final float JUMP_VELOCITY = 350f;
    private static final float MAX_JUMP_HOLD_TIME = 0.28f;
    private static final float EXTRA_JUMP_ACC = 1100f;
    private boolean doubleJumpUsed = false;
    private boolean isDoubleJumping = false;
    private float doubleJumpAnimTimer = 0f;
    private static final float DOUBLE_JUMP_ANIM_DURATION = 0.4f;

    private boolean isFalling = false;
    public boolean isDownSlashing = false;
    public float downSlashTimer = 0f;
    public static final float DOWN_SLASH_DURATION = 0.25f;
    public boolean pogoHit = false;
    private boolean canPogoReset = false;

    public boolean isUpSlashing = false;
    public float upSlashTimer = 0f;
    public static final float UP_SLASH_DURATION = 0.25f;
    public boolean upHitConsumed = false;

    private float airTime = 0f;
    private static final float FALL_ANIM_THRESHOLD = 0.45f;
    private boolean jumpJustPressed = false;

    private boolean isWallSliding = false;
    private int wallSide = 0;
    private static final float WALL_SLIDE_SPEED = 50f;
    private static final float WALL_CLING_DIST = 5f;
    private static final float WALL_JUMP_HORIZONTAL = 500f;
    private static final float WALL_JUMP_VERTICAL = 550f;

    private boolean isWallJumping = false;
    private float wallJumpTimer = 0f;
    private static final float WALL_JUMP_ANIM_DURATION = 0.25f;

    private float wallGraceTimer = 0f;
    private int lastWallSide = 0;
    private static final float WALL_GRACE_DURATION = 0.15f;

    private Vector2 respawnPosition = new Vector2(0, GROUND_Y);
    public boolean isDying = false;
    private float deathTimer = 0f;
    private static final float DEATH_ANIM_DURATION = 1.8f;

    private boolean isCastingSpell = false;
    private float spellCastTimer = 0f;
    private AnimationType currentSpellType;
    private float spellDuration = 0.9f;

    private static final float VENGEFUL_CAST_DURATION = 0.45f;
    private static final float HOWLING_CAST_DURATION = 0.35f;

    public boolean isHowlingWraithsActive = false;
    private float howlingWraithsTimer = 0f;
    private float howlingWraithsHitTimer = 0f;
    private int howlingWraithsHits = 0;
    private static final float HOWLING_WRAITHS_EFFECT_DURATION = 0.35f;
    private static final float HOWLING_WRAITHS_HIT_INTERVAL = 0.11f;
    private static final int HOWLING_WRAITHS_MAX_HITS = 3;

    public boolean isVengefulSpiritActive = false;
    public float vengefulSpiritBlastTimer = 0f;
    public float vengefulSpiritProjectileTimer = 0f;
    public Vector2 spiritPosition = new Vector2();
    public Vector2 spiritVelocity = new Vector2();
    public static final float BLAST_DURATION = 0.3f;
    private static final float SPIRIT_SPEED = 900f;
    private Array<Entity> hitEnemies = new Array<>();
    private static final float PROJECTILE_WIDTH = 60f;
    private static final float PROJECTILE_HEIGHT = 60f;

    public static final int MAX_NOTCHES = 3;
    public int currentNotchesUsed = 0;

    public boolean hasSoulCatcher = false;
    public boolean hasDashmaster = false;
    public boolean hasUnbreakableStrength = false;
    public boolean hasQuickSlash = false;
    public boolean hasQuickFocus = false;
    public boolean hasHeavyBlow = false;
    public boolean hasSharpShadow = false;
    public boolean hasVoidHeart = false;

    public boolean isGodMode = false;
    public boolean isNoclip = false;

    private float pendingShakeMag = 0f;
    private float pendingShakeTime = 0f;

    public Player() {
        super(0, GROUND_Y, 100, 100, 5);
        position.set(0, GROUND_Y);
        bounds.setPosition(position.x, position.y);
    }

    public void requestShake(float mag, float time) {
        if (mag > pendingShakeMag) {
            pendingShakeMag = mag;
            pendingShakeTime = time;
        }
    }
    public float consumeShakeMag() { float m = pendingShakeMag; pendingShakeMag = 0f; return m; }
    public float consumeShakeTime() { float t = pendingShakeTime; pendingShakeTime = 0f; return t; }

    @Override
    public void update(float delta, Array<SolidBlock> blocks) {
        super.update(delta, blocks);
        AnimationType previousAnimation = currentAnimation;

        if (isNoclip) {
            velocity.set(0, 0);
            float noclipSpeed = 950f;

            if (movingLeft) {
                position.x -= noclipSpeed * delta;
                facingRight = false;
            }
            if (movingRight) {
                position.x += noclipSpeed * delta;
                facingRight = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
                position.y += noclipSpeed * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
                position.y -= noclipSpeed * delta;
            }

            syncBounds();
            stateTime += delta;
            currentAnimation = AnimationType.HOLLOW_KNIGHT_IDLE;
            if (currentAnimation != previousAnimation) stateTime = 0;
            return;
        }

        if (isDying){
            deathTimer += delta;
            currentAnimation = AnimationType.HOLLOW_KNIGHT_DEATH;
            stateTime += delta;
            if (deathTimer >= DEATH_ANIM_DURATION) {
                respawn();
            }
            return;
        }
        if (isCastingSpell) {
            spellCastTimer += delta;
            currentAnimation = currentSpellType;
            stateTime += delta;
            if (isVengefulSpiritActive) {
                updateVengefulSpirit(delta, blocks);
            }

            if (isHowlingWraithsActive) {
                updateHowlingWraiths(delta);
            }
            if (spellCastTimer >= spellDuration) {
                isCastingSpell = false;
                stateTime = 0;
                velocity.set(0, 0);
                if (!isOnGround) {
                    currentAnimation = AnimationType.HOLLOW_KNIGHT_JUMP;
                } else {
                    if(masks == 1){
                        currentAnimation = AnimationType.HOLLOW_KNIGHT_IDLE_HURT;
                    }
                    currentAnimation = AnimationType.HOLLOW_KNIGHT_IDLE;
                }
            }
            return;
        }
        if(dashCooldown > 0f){
            float cooldownModifier = hasDashmaster ? 1.75f : 1f;
            if (dashCooldown > 0f) dashCooldown -= delta * cooldownModifier;
        }
        if (isDashing) {
            dashTimer -= delta;

            float currentDashSpeed = hasSharpShadow ? DASH_SPEED * 1.2f : DASH_SPEED;
            velocity.x = currentDashSpeed * dashDir;
            velocity.y = 0;
            currentAnimation = AnimationType.HOLLOW_KNIGHT_DASH_FRAMES;

            if (dashTimer <= 0) {
                isDashing = false;
                dashCooldown = DASH_COOLDOWN;
                velocity.x = 0;
            }
            if (currentAnimation != previousAnimation) stateTime = 0;
            moveAndCollide(delta, blocks);
            stateTime += delta;
            return;
        }
        boolean prevOnGround = isOnGround;

        velocity.y += GRAVITY * delta;

        if(!isCastingSpell) {
            if (knockbackTimer > 0f) {
                knockbackTimer -= delta;
                velocity.x = knockbackVelocityX;
            }else if (movingLeft) {
                velocity.x = -630;
                facingRight = false;
            } else if (movingRight) {
                velocity.x = 630;
                facingRight = true;
            } else {
                velocity.x = 0;
            }
        }
        if (jumpHeld && velocity.y > 0 && jumpHoldTime < MAX_JUMP_HOLD_TIME) {
            velocity.y += EXTRA_JUMP_ACC * delta;
            jumpHoldTime += delta;
        }
        moveAndCollide(delta, blocks);
        if (!isOnGround && !isDashing && !isDownSlashing && !isDoubleJumping && !isAttacking() && !focusing) {
            boolean foundWall = false;
            int foundSide = 0;
            for (SolidBlock block : blocks) {
                if (block.isDeadly) continue;
                Rectangle b = block.bounds;
                float overlapY = Math.min(bounds.y + bounds.height, b.y + b.height) - Math.max(bounds.y, b.y);
                if (overlapY > 20) {
                    if (Math.abs(bounds.x - (b.x + b.width)) < WALL_CLING_DIST && movingLeft) {
                        foundWall = true;
                        foundSide = -1;
                        break;
                    }
                    if (Math.abs((bounds.x + bounds.width) - b.x) < WALL_CLING_DIST && movingRight) {
                        foundWall = true;
                        foundSide = 1;
                        break;
                    }
                }
            }

            if (foundWall) {
                if (!isWallSliding) {
                    loopSound(GameAssetManager.wallSlideSound, 0.4f);
                }

                isWallSliding = true;
                wallSide = foundSide;

                lastWallSide = foundSide;
                wallGraceTimer = WALL_GRACE_DURATION;

                facingRight = (wallSide == 1);
                if (velocity.y < -WALL_SLIDE_SPEED) {
                    velocity.y = -WALL_SLIDE_SPEED;
                }

                if((wallSide == -1 && movingRight) || (wallSide == 1 && movingLeft)) {
                    isWallSliding = false;
                    if (GameAssetManager.wallSlideSound != null) GameAssetManager.wallSlideSound.stop();
                } else {
                    velocity.x = 0;
                }
            } else {
                if (isWallSliding) {
                    if ((wallSide == -1 && !movingLeft) || (wallSide == 1 && !movingRight)) {
                        isWallSliding = false;
                        if (GameAssetManager.wallSlideSound != null) GameAssetManager.wallSlideSound.stop();
                    }
                }
            }
        } else {
            if (isWallSliding && GameAssetManager.wallSlideSound != null) {
                GameAssetManager.wallSlideSound.stop();
            }
            isWallSliding = false;
        }

        if (!isWallSliding && wallGraceTimer > 0f) {
            wallGraceTimer -= delta;
        }

        if (isOnGround) {
            doubleJumpUsed = false;
            isDoubleJumping = false;
            wallGraceTimer = 0f;
        }
        if(isOnGround && !prevOnGround){
            isLanding = true;
            landTimer = LAND_DURATION;

            playSound(GameAssetManager.landingSound, 1.0f);
        }
        if (!isOnGround) {
            airTime += delta;
        } else {
            airTime = 0f;
        }
        if(isLanding){
            landTimer -= delta;
            if(landTimer <= 0f) isLanding = false;
        }
        if (!isOnGround && velocity.y < 0 && !isDashing && !isDownSlashing) {
            isFalling = true;
        } else {
            isFalling = false;
        }
        if(currentAnimation != previousAnimation){
            stateTime = 0;
        }

        if (isCastingSpell) {
        }else if (attackTimer > 0f) {
            attackTimer -= delta;
            slashTimer += delta;
            slashFrame = Math.min((int) (slashTimer / SLASH_FRAME_TIME), SLASH_FRAMES - 1);
            currentAnimation = AnimationType.HOLLOW_KNIGHT_NORMAL_SLASH;
        } else if (isDoubleJumping) {
        } else if (isDownSlashing) {
        } else if(isUpSlashing){
        }
        else if(isWallJumping){
        }else if (isWallSliding) {
            currentAnimation = AnimationType.HOLLOW_KNIGHT_WALL_SLIDE;
        }
        else if (!isOnGround) {
            if (velocity.y < -100 && airTime > 0.45f) {
                currentAnimation = AnimationType.HOLLOW_KNIGHT_FALL;
            } else {
                if (currentAnimation != AnimationType.HOLLOW_KNIGHT_JUMP &&
                    currentAnimation != AnimationType.HOLLOW_KNIGHT_DOUBLE_JUMP_FRAMES &&
                    currentAnimation != AnimationType.HOLLOW_KNIGHT_FALL) {
                    currentAnimation = AnimationType.HOLLOW_KNIGHT_JUMP;
                }
            }
        } else {
            if (jumpJustPressed) {
            }else if (focusing) {
                currentAnimation = AnimationType.HOLLOW_KNIGHT_FOCUS;
            }
            else if (isLanding) {
                currentAnimation = AnimationType.HOLLOW_KNIGHT_LANDING_FRAMES;
            } else if (movingLeft || movingRight) {
                currentAnimation = AnimationType.HOLLOW_KNIGHT_RUN;
            } else {
                if (isLookingDown) {
                    currentAnimation = AnimationType.HOLLOW_KNIGHT_LOOKING_DOWN_FRAMES;
                } else if (isLookingUp) {
                    currentAnimation = AnimationType.HOLLOW_KNIGHT_LOOKING_UP_FRAMES;
                } else {
                    if(masks == 1){
                        currentAnimation =AnimationType.HOLLOW_KNIGHT_IDLE_HURT;
                    }else {
                        currentAnimation = AnimationType.HOLLOW_KNIGHT_IDLE;
                    }
                }
            }
            jumpJustPressed = false;
        }
        if(focusing){
            float focusSpeedModifier = hasQuickFocus ? 2.0f : 1.0f;
            focusTimer += delta * focusSpeedModifier;
            if (focusTimer >= FOCUS_TIME) {
                soul -= SOUL_PER_HEAL;
                masks = Math.min(maxMasks, masks + 1);

                playSound(GameAssetManager.healSound, 1.0f);

                cancelFocus();
            }
        }
        if (isDoubleJumping) {
            doubleJumpAnimTimer += delta;
            if (doubleJumpAnimTimer >= DOUBLE_JUMP_ANIM_DURATION) {
                isDoubleJumping = false;
                if (!isOnGround) {
                    currentAnimation = AnimationType.HOLLOW_KNIGHT_JUMP;
                    stateTime = 0;
                }
            }
        }
        if (isDownSlashing) {
            downSlashTimer += delta;
            currentAnimation = AnimationType.HOLLOW_KNIGHT_DOWN_SLASH;
            if (downSlashTimer >= DOWN_SLASH_DURATION) {
                isDownSlashing = false;
                downSlashTimer = 0f;
            }
        }
        if (isUpSlashing) {
            upSlashTimer += delta;
            currentAnimation = AnimationType.HOLLOW_KNIGHT_UP_SLASH;
            if (upSlashTimer >= UP_SLASH_DURATION) {
                isUpSlashing = false;
                upSlashTimer = 0f;
            }
        }
        if (isWallJumping) {
            wallJumpTimer += delta;
            if (wallJumpTimer >= WALL_JUMP_ANIM_DURATION) {
                isWallJumping = false;
                if (!isOnGround) {
                    currentAnimation = AnimationType.HOLLOW_KNIGHT_JUMP;
                    stateTime = 0;
                }
            }
        }
        if (isHowlingWraithsActive) {
            howlingWraithsTimer += delta;
            howlingWraithsHitTimer += delta;
            if (howlingWraithsTimer >= HOWLING_WRAITHS_EFFECT_DURATION) {
                isHowlingWraithsActive = false;
                howlingWraithsHits = 0;
                howlingWraithsHitTimer = 0f;
            }
        }
        if (isVengefulSpiritActive) {
            updateVengefulSpirit(delta, blocks);
        }
        timeSinceLastPress += delta;
        stateTime += delta;

        if (isOnGround && !isWallSliding) {
            safeSpotTimer += delta;
            if (safeSpotTimer >= 0.2f) {
                lastSafeSpot.set(position);
            }
        } else {
            safeSpotTimer = 0f;
        }
    }

    public void lookingDown(){
        isLookingDown = true;
    }

    public void jump() {
        if (isCastingSpell) return;

        if (isWallSliding || (!isOnGround && wallGraceTimer > 0f)) {
            if (GameAssetManager.wallSlideSound != null) GameAssetManager.wallSlideSound.stop();

            int activeWall = isWallSliding ? wallSide : lastWallSide;

            playSound(GameAssetManager.jumpSound, 0.8f);

            velocity.x = activeWall * -WALL_JUMP_HORIZONTAL;
            velocity.y = WALL_JUMP_VERTICAL;
            isWallSliding = false;
            wallGraceTimer = 0f;
            doubleJumpUsed = false;
            currentAnimation = AnimationType.HOLLOW_KNIGHT_WALL_JUMP;
            stateTime = 0;
            isWallJumping = true;
            wallJumpTimer = 0f;
            jumpJustPressed = true;
            return;
        }

        isFalling = false;
        jumpJustPressed = true;
        if (isOnGround) {
            playSound(GameAssetManager.jumpSound, 0.8f);

            velocity.y = JUMP_VELOCITY;
            currentAnimation = AnimationType.HOLLOW_KNIGHT_JUMP;
            stateTime = 0;
            jumpHeld = true;
            jumpHoldTime = 0f;
            doubleJumpUsed = false;
            isDoubleJumping = false;
        } else if (!doubleJumpUsed) {
            playSound(GameAssetManager.wingsSound, 1.0f);

            velocity.y = JUMP_VELOCITY;
            currentAnimation = AnimationType.HOLLOW_KNIGHT_DOUBLE_JUMP_FRAMES;
            stateTime = 0;
            doubleJumpUsed = true;
            jumpHeld = true;
            jumpHoldTime = 0f;
            isDoubleJumping = true;
            doubleJumpAnimTimer = 0f;
        }
    }

    public void dash(){
        if (isCastingSpell) return;
        if(isDashing || dashCooldown > 0f) return;

        if (GameAssetManager.wallSlideSound != null) GameAssetManager.wallSlideSound.stop();
        playSound(GameAssetManager.dashSound, 0.9f);

        isDashing = true;
        dashTimer = DASH_DURATION;
        dashDir = facingRight ? 1: -1;
        stateTime = 0;
        invincibleTimer = DASH_DURATION;

        hitEnemies.clear();

        requestShake(5f, 0.2f);

        cancelFocus();
        currentAnimation = AnimationType.HOLLOW_KNIGHT_DASH_FRAMES;
    }

    public void cancelFocus(){
        focusing = false;
        focusTimer = 0f;

        if (GameAssetManager.focusSound != null) GameAssetManager.focusSound.stop();
    }

    @Override
    public boolean takeDamage(float dmg){
        return takeDamage(dmg, position.x);
    }

    @Override
    public boolean takeDamage(float dmg, float sourceX) {
        if (isGodMode || isNoclip) return false;

        if (invincibleTimer > 0) return false;
        if (isCastingSpell) {
            isCastingSpell = false;
            stateTime = 0;
            currentAnimation = isOnGround ? AnimationType.HOLLOW_KNIGHT_IDLE : AnimationType.HOLLOW_KNIGHT_JUMP;
        }
        int amount = (int) dmg;
        masks = Math.max(0, masks - amount);

        playSound(GameAssetManager.damageSound, 1.0f);
        if (GameAssetManager.wallSlideSound != null) GameAssetManager.wallSlideSound.stop();

        invincibleTimer = INVINCIBLE_TIME;
        cancelFocus();

        requestShake(25f, 0.4f);

        knockbackTimer = 0.25f;
        knockbackVelocityX = (position.x > sourceX) ? 350f : -350f;

        return true;
    }

    public void hazardRespawn() {
        if (isGodMode || isNoclip) return;

        if (invincibleTimer > 0) return;

        boolean hit = takeDamage(1f, position.x);

        if (hit && !isDying) {
            position.set(lastSafeSpot);
            velocity.set(0, 0);
            knockbackTimer = 0f;

            isDashing = false;
            isDownSlashing = false;
            isUpSlashing = false;
            isWallJumping = false;
            isWallSliding = false;
            isDoubleJumping = false;
            isCastingSpell = false;

            if (GameAssetManager.wallSlideSound != null) GameAssetManager.wallSlideSound.stop();

            syncBounds();
        }
    }

    public void addSoul(int amount){
        if (GameAssetManager.parrySound != null) {
            GameAssetManager.parrySound.stop();
        }
        playSound(GameAssetManager.enemyDamageSound, 0.8f);

        int finalAmount = amount;
        if(hasSoulCatcher) {
            finalAmount = amount * 2;
        }
        int oldSpellCharges = soul / SOUL_PER_HEAL;
        soul = Math.min(MAX_SOUL, soul + finalAmount);
        int newSpellCharges = soul / SOUL_PER_HEAL;
        if (newSpellCharges > oldSpellCharges) {
            playSound(GameAssetManager.gainSoulSound, 0.6f);
        }
    }

    public void startFocus(){
        if (isCastingSpell) return;
        if(soul >= SOUL_PER_HEAL && masks < maxMasks && isOnGround){
            focusing = true;
            focusTimer = 0f;

            loopSound(GameAssetManager.focusSound, 1.0f);
        }
    }

    public void attack() {
        if (isCastingSpell) return;
        if (attackTimer > 0f) return;

        if (timeSinceLastPress < COMBO_RESET_TIME) {
            attackComboCounter++;
        } else {
            attackComboCounter = 0;
        }
        timeSinceLastPress = 0f;

        if (attackComboCounter % 2 == 0) {
            currentAnimation = AnimationType.HOLLOW_KNIGHT_NORMAL_SLASH;
        } else {
            currentAnimation = AnimationType.HOLLOW_KNIGHT_SLASH_ALT;
        }
        float duration = hasQuickSlash ? (ATTACK_DURATION / 2f) : ATTACK_DURATION;
        attackTimer = duration;

        playSound(GameAssetManager.slashSound, 0.6f);

        slashTimer = 0f;
        slashFrame = 0;
        hitConsumed = false;
        stateTime = 0;
    }

    private boolean hitConsumed = false;
    public boolean tryConsumeHit() {
        if (hitConsumed || attackTimer <= 0f) return false;
        hitConsumed = true;
        return true;
    }

    public Rectangle getAttackBox() {
        float reach = 100f;
        float cx = bounds.x + bounds.width / 2f;
        float ax = facingRight ? cx : cx - reach;
        attackBox.set(ax, bounds.y, reach, bounds.height);
        return attackBox;
    }

    public void syncBounds() {
        float hitboxW = spriteDrawnW * BODY_W_FRAC;
        float hitboxH = spriteDrawnH * BODY_H_FRAC;
        bounds.set(position.x - hitboxW / 2, position.y, hitboxW, hitboxH);
    }

    private void moveAndCollide(float delta, Array<SolidBlock> blocks) {
        if (isNoclip) return;

        isOnGround = false;

        position.x += velocity.x * delta;
        syncBounds();

        for (SolidBlock b : blocks) {
            if (b.isDeadly) {
                if (bounds.overlaps(b.bounds)) {
                    hazardRespawn();
                    return;
                }
                continue;
            }
            if (bounds.overlaps(b.bounds)) {
                float overlapY = Math.min(bounds.y + bounds.height, b.bounds.y + b.bounds.height) - Math.max(bounds.y, b.bounds.y);
                if (overlapY < 0.1f) continue;

                if (velocity.x > 0) {
                    position.x = b.bounds.x - bounds.width / 2;
                } else if (velocity.x < 0) {
                    position.x = b.bounds.x + b.bounds.width + bounds.width / 2;
                }
                velocity.x = 0;
                syncBounds();
            }
        }

        position.y += velocity.y * delta;
        syncBounds();

        for (SolidBlock b : blocks) {
            if (b.isDeadly) {
                if (bounds.overlaps(b.bounds)) {
                    hazardRespawn();
                    return;
                }
                continue;
            }
            if (bounds.overlaps(b.bounds)) {
                float overlapX = Math.min(bounds.x + bounds.width, b.bounds.x + b.bounds.width) - Math.max(bounds.x, b.bounds.x);
                if (overlapX < 0.1f) continue;

                if (velocity.y <= 0) {
                    position.y = b.bounds.y + b.bounds.height;
                    isOnGround = true;
                } else {
                    position.y = b.bounds.y - bounds.height;
                }
                velocity.y = 0;
                syncBounds();
            }
        }
    }

    public void downSlash() {
        if (isCastingSpell) return;
        if (isOnGround || isDashing || isDownSlashing) return;

        playSound(GameAssetManager.slashSound, 0.6f);

        isDownSlashing = true;
        downSlashTimer = 0f;
        pogoHit = false;
        stateTime = 0;
        cancelFocus();
    }

    public void upSlash() {
        if (isCastingSpell) return;
        if (isUpSlashing || isDashing || attackTimer > 0f) return;

        playSound(GameAssetManager.slashSound, 0.6f);

        isUpSlashing = true;
        upSlashTimer = 0f;
        upHitConsumed = false;
        stateTime = 0;
        cancelFocus();
    }

    public void pogoBounce() {
        isDashing = false;
        doubleJumpUsed = false;
        velocity.y = 350f;
        pogoHit = true;
        dashCooldown = 0;

        playSound(GameAssetManager.parrySound, 1.0f);
    }

    private void triggerDeath() {
        if (isGodMode || isNoclip) return;
        if (isDying) return;

        playSound(GameAssetManager.deathSound, 1.0f);
        if (GameAssetManager.wallSlideSound != null) GameAssetManager.wallSlideSound.stop();

        isDying = true;
        deathTimer = 0f;
        velocity.set(0, 0);
        stateTime = 0;
        focusing = false;
        isDashing = false;
        isDownSlashing = false;
        isUpSlashing = false;
        isDoubleJumping = false;
        isWallSliding = false;
        isWallJumping = false;
        attackTimer = 0f;
        currentAnimation = AnimationType.HOLLOW_KNIGHT_DEATH;
    }

    private void respawn() {
        position.set(respawnPosition);
        lastSafeSpot.set(respawnPosition);
        velocity.set(0, 0);
        isOnGround = true;
        masks = maxMasks;
        isDying = false;
        deathTimer = 0f;
        stateTime = 0;
        currentAnimation = AnimationType.HOLLOW_KNIGHT_IDLE;
        focusing = false;
        isDashing = false;
        isDownSlashing = false;
        isUpSlashing = false;
        isDoubleJumping = false;
        isWallSliding = false;
        isWallJumping = false;
        attackTimer = 0f;
        syncBounds();
    }

    public void checkDeath() {
        if (masks <= 0 && !isDying) {
            triggerDeath();
        }
    }

    public void castVengefulSpirit() {
        if (isCastingSpell || isAttacking() || isDashing || focusing || isDownSlashing || isUpSlashing
            || isDoubleJumping || isWallSliding || isWallJumping || isDying) return;
        if (soul < SOUL_PER_HEAL) return;
        soul -= SOUL_PER_HEAL;

        playSound(GameAssetManager.fireballSound, 1.0f);

        isCastingSpell = true;
        spellCastTimer = 0f;
        stateTime = 0;
        spellDuration = VENGEFUL_CAST_DURATION;
        currentAnimation = AnimationType.HOLLOW_KNIGHT_VENGEFUL_SPIRIT;
        currentSpellType = AnimationType.HOLLOW_KNIGHT_VENGEFUL_SPIRIT;
        startVengefulSpiritEffect();
        cancelFocus();

        requestShake(15f, 0.3f);
    }

    public void castHowlingWraiths() {
        if (isCastingSpell || isAttacking() || isDashing || focusing || isDownSlashing || isUpSlashing
            || isDoubleJumping || isWallSliding || isWallJumping || isDying) return;
        if (soul < SOUL_PER_HEAL) return;
        soul -= SOUL_PER_HEAL;

        playSound(GameAssetManager.howlingWraithsSound, 1.0f);

        isCastingSpell = true;
        spellCastTimer = 0f;
        stateTime = 0;
        spellDuration = HOWLING_CAST_DURATION;
        currentAnimation = AnimationType.HOLLOW_KNIGHT_HOWLING_WRAITHS;
        currentSpellType = AnimationType.HOLLOW_KNIGHT_HOWLING_WRAITHS;
        startHowlingWraithsEffect();
        cancelFocus();

        requestShake(20f, 0.4f);
    }

    private void startHowlingWraithsEffect() {
        isHowlingWraithsActive = true;
        howlingWraithsTimer = 0f;
        howlingWraithsHitTimer = 0f;
        howlingWraithsHits = 0;
    }

    private void updateHowlingWraiths(float delta){
        howlingWraithsTimer += delta;
        howlingWraithsHitTimer += delta;
        if (howlingWraithsTimer >= HOWLING_WRAITHS_EFFECT_DURATION) {
            isHowlingWraithsActive = false;
            howlingWraithsHits = 0;
            howlingWraithsHitTimer = 0f;
        }
    }

    public boolean tryHowlingWraithsHit() {
        if (!isHowlingWraithsActive) return false;
        if (howlingWraithsHits >= HOWLING_WRAITHS_MAX_HITS) return false;
        if (howlingWraithsHitTimer < HOWLING_WRAITHS_HIT_INTERVAL) return false;
        howlingWraithsHitTimer = 0f;
        howlingWraithsHits++;
        return true;
    }

    public Rectangle getDownSlashBox() {
        float slashW = 50f;
        float slashH = 60f;
        float cx = position.x + DRAW_OFFSET_X + spriteDrawnW / (2 * drawScale);
        float x = cx - slashW / 2f;
        float y = position.y - slashH;
        return new Rectangle(x - 20, y, slashW + 30, slashH + 10);
    }

    public Rectangle getUpSlashBox() {
        float slashW = 50f;
        float slashH = 60f;
        float cx = position.x + DRAW_OFFSET_X + spriteDrawnW / (2 * drawScale);
        float x = cx - slashW / 2f;
        float y = position.y + spriteDrawnH - 50;
        return new Rectangle(x - 20, y, slashW + 30, slashH + 10);
    }

    public Rectangle getHowlingWraithsBox() {
        float w = 150f;
        float h = 140f;
        float cx = position.x + DRAW_OFFSET_X + spriteDrawnW / (2 * drawScale);
        float x = cx - w / 2f;
        float y = position.y + spriteDrawnH - 40;
        return new Rectangle(x, y, w, h);
    }

    private void startVengefulSpiritEffect() {
        isVengefulSpiritActive = true;
        vengefulSpiritBlastTimer = 0f;
        vengefulSpiritProjectileTimer = 0f;
        hitEnemies.clear();

        float cx = position.x + DRAW_OFFSET_X + spriteDrawnW / (2 * drawScale);
        float offset = facingRight ? 50f : -50f;
        spiritPosition.set(cx + offset, position.y + spriteDrawnH * 0.5f);
        spiritVelocity.set(facingRight ? SPIRIT_SPEED : -SPIRIT_SPEED, 0);
    }

    private void updateVengefulSpirit(float delta, Array<SolidBlock> blocks) {
        if (!isVengefulSpiritActive) return;

        vengefulSpiritBlastTimer += delta;
        vengefulSpiritProjectileTimer += delta;

        spiritPosition.x += spiritVelocity.x * delta;

        Rectangle projBox = getVengefulSpiritBox();
        for (SolidBlock b : blocks) {
            if (!b.isDeadly && projBox.overlaps(b.bounds)) {
                isVengefulSpiritActive = false;
                break;
            }
        }
    }

    public Rectangle getVengefulSpiritBox() {
        float w = PROJECTILE_WIDTH;
        float h = PROJECTILE_HEIGHT;
        return new Rectangle(spiritPosition.x - w/2, spiritPosition.y - h/2, w, h);
    }

    public boolean isEnemyHit(Entity e) {
        return hitEnemies.contains(e, true);
    }

    public void markEnemyHit(Entity e) {
        hitEnemies.add(e);
    }
    public float getKnockbackModifier() {
        return hasHeavyBlow ? 2.0f : 1.0f;
    }

    public float getAttackElapsed() { return ATTACK_DURATION - attackTimer; }
    public float getDashElapsed() { return DASH_DURATION - dashTimer; }
    public boolean isAttacking() { return attackTimer > 0f; }
    public int getSlashFrame() { return slashFrame; }
    public void setJumpHeld(boolean held) {
        jumpHeld = held;
    }

    public void setRespawnPosition(Vector2 pos) {
        respawnPosition.set(pos);
        lastSafeSpot.set(pos);
    }

    public float getHowlingWraithsTimer() {
        return howlingWraithsTimer;
    }

    public void resetInputs() {
        this.velocity.x = 0;
        this.movingLeft = false;
        this.movingRight = false;
        this.isDashing = false;
        this.jumpHeld = false;
    }
    private void playSound(com.badlogic.gdx.audio.Sound sound, float volume) {
        if (sound != null && Gdx.app.getPreferences("HollowKnightSettings").getBoolean("sfxOn", true)) {
            sound.play(volume);
        }
    }

    private void loopSound(com.badlogic.gdx.audio.Sound sound, float volume) {
        if (sound != null && Gdx.app.getPreferences("HollowKnightSettings").getBoolean("sfxOn", true)) {
            sound.loop(volume);
        }
    }
}
