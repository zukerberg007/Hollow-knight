package com.amirali.graphics.models.enemies;

import com.amirali.graphics.GameAssetManager;
import com.amirali.graphics.models.Entity;
import com.amirali.graphics.models.Player;
import com.amirali.graphics.models.SolidBlock;
import com.amirali.graphics.views.AnimationType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class FalseKnight extends Entity {

    public enum State {
        DORMANT, INTRO, IDLE, TURN,
        SLAM_ANTIC, SLAM_HIT, SLAM_RECOVER,
        CHARGE_ANTIC, CHARGE, CHARGE_RECOVER,
        LEAP_ANTIC, LEAP_AIR, LEAP_LAND,
        DEF_LEAP_AIR, DEF_LEAP_LAND,
        POWER_ANTIC, POWER_AIR, POWER_LAND,
        STUN_FALL, STUNNED, STUN_RECOVER,
        DEATH_HIT, DEATH_FALL, DEATH_LAND, DEAD
    }

    private static final float BODY_W = 200f;
    private static final float BODY_H = 200f;

    private static final float GRAVITY = -1800f;

    private static final float DRAW_SCALE    = 0.95f;
    private static final float DRAW_OFFSET_Y = -60f;
    private static final float FACING_SIGN   = -1f;

    private static final float CLOSE_RANGE = 240f;
    private static final float MID_RANGE   = 600f;

    private static final float INTRO_T         = 0.6f;
    private static final float TURN_T          = 0.22f;
    private static final float SLAM_ANTIC_T    = 0.45f;
    private static final float SLAM_HIT_T      = 0.18f;
    private static final float SLAM_RECOVER_T  = 0.45f;
    private static final float CHARGE_ANTIC_T  = 0.40f;
    private static final float CHARGE_MAX_T    = 1.40f;
    private static final float CHARGE_RECOVER_T= 0.55f;
    private static final float LEAP_ANTIC_T    = 0.35f;
    private static final float LAND_RECOVER_T  = 0.40f;
    private static final float POWER_ANTIC_T   = 0.55f;
    private static final float POWER_LAND_T    = 0.55f;
    private static final float STUN_DURATION   = 3.0f;
    private static final float STUN_RECOVER_T  = 0.6f;

    private static final float DEATH_HIT_T     = 0.3f;
    private static final float DEATH_LAND_T    = 1.1f;

    private float runSpeed = 320f;
    private static final float LEAP_VX   = 360f;
    private static final float LEAP_VY   = 950f;
    private static final float DEF_LEAP_VX = 300f;
    private static final float DEF_LEAP_VY = 820f;
    private static final float POWER_VX  = 220f;
    private static final float POWER_VY  = 1150f;

    private static final float BODY_TOUCH_DMG = 1f;
    private static final float SLAM_DMG       = 1f;
    private static final float CHARGE_DMG     = 1f;
    private static final float SHOCKWAVE_DMG  = 2f;
    private static final float STUN_DMG_MULT  = 2f;

    private static final float HEAVY_WINDOW    = 1.4f;
    private static final float HEAVY_THRESHOLD = 3f;

    private final Vector2 position = new Vector2();
    private final Vector2 velocity = new Vector2();
    private boolean onGround   = false;
    private boolean facingRight = false;
    private boolean wallBonked  = false;
    private int chargeDir = 1;

    private Player player;
    private final float maxHealth;

    private int phase = 1;
    private boolean enraged = false;
    private float animSpeed = 1f;

    private State state = State.DORMANT;
    private float stateTimer = 0f;
    private float animClock  = 0f;

    private float thinkTimer = 0f;
    private float thinkDelay = 0.45f;
    private State lastMove = null;
    private int sameMoveStreak = 0;

    private float recentDamage = 0f;
    private float damageWindow = 0f;

    private float stunTimer = 0f;

    private boolean maceHitConsumed = false;
    private final Rectangle maceBox = new Rectangle();

    private final Rectangle headBox = new Rectangle();
    private static final boolean DEBUG_HITBOX = false;
    private static final float STUN_BOX_W     = 300f;
    private static final float STUN_BOX_H     = 100f;
    private static final float STUN_BOX_OFF_X = 0f;
    private static final float STUN_BOX_OFF_Y = 0f;

    private float stunFlinchTimer = 0f;
    private float stunFlinchClock = 0f;

    private float pendingShake = 0f;
    private float hitFlash = 0f;

    private final Array<Shockwave> shockwaves = new Array<>();
    private static Texture pixel;

    public FalseKnight(float x, float y, float health) {
        super(x, y, BODY_W, BODY_H, health);
        this.maxHealth = health;
        position.set(x, y);
        syncBounds();
    }

    public void setPlayer(Player p) { this.player = p; }

    public void activate() {
        if (state == State.DORMANT) {
            setState(State.INTRO);
            requestShake(15f);
        }
    }

    public boolean isActive()   { return state != State.DORMANT; }
    public boolean isDefeated() { return state == State.DEAD; }
    public State getState()     { return state; }

    public Rectangle getActiveHitbox() {
        if (state == State.STUN_FALL || state == State.STUNNED || state == State.STUN_RECOVER) {
            float dir = facingRight ? 1f : -1f;
            float cx  = bounds.x + bounds.width / 2f + dir * STUN_BOX_OFF_X;
            float hx  = cx - STUN_BOX_W / 2f;
            float hy  = bounds.y + STUN_BOX_OFF_Y;
            headBox.set(hx, hy, STUN_BOX_W, STUN_BOX_H);
            return headBox;
        }
        return bounds;
    }

    public float consumeShake() {
        float s = pendingShake;
        pendingShake = 0f;
        return s;
    }

    public void reset(float x, float y) {
        position.set(x, y);
        velocity.set(0f, 0f);
        health = maxHealth;
        alive = true;
        phase = 1; enraged = false; animSpeed = 1f; thinkDelay = 0.45f;
        lastMove = null; sameMoveStreak = 0;
        recentDamage = 0f; damageWindow = 0f;
        pendingShake = 0f; hitFlash = 0f;
        shockwaves.clear();
        onGround = false; wallBonked = false;
        setState(State.DORMANT);
        syncBounds();
    }

    @Override
    public void update(float delta, Array<SolidBlock> blocks) {
        super.update(delta, blocks);

        animClock += delta;
        if (hitFlash > 0f) hitFlash -= delta;
        if (stunFlinchTimer > 0f) { stunFlinchTimer -= delta; stunFlinchClock += delta; }
        if (damageWindow > 0f) { damageWindow -= delta; if (damageWindow <= 0f) recentDamage = 0f; }

        updateShockwaves(delta, blocks);

        if (state == State.DORMANT) { applyGravity(delta, blocks); return; }

        switch (state) {
            case INTRO:           updateIntro(delta, blocks); break;
            case IDLE:            updateIdle(delta, blocks); break;
            case TURN:            updateTurn(delta, blocks); break;
            case SLAM_ANTIC:      updateSlamAntic(delta, blocks); break;
            case SLAM_HIT:        updateSlamHit(delta, blocks); break;
            case SLAM_RECOVER:    updateGroundRecover(delta, blocks, SLAM_RECOVER_T); break;
            case CHARGE_ANTIC:    updateChargeAntic(delta, blocks); break;
            case CHARGE:          updateCharge(delta, blocks); break;
            case CHARGE_RECOVER:  updateGroundRecover(delta, blocks, CHARGE_RECOVER_T); break;
            case LEAP_ANTIC:      updateLeapAntic(delta, blocks); break;
            case LEAP_AIR:        updateAirToLand(delta, blocks, 25f, State.LEAP_LAND); break;
            case LEAP_LAND:       updateGroundRecover(delta, blocks, LAND_RECOVER_T); break;
            case DEF_LEAP_AIR:    updateAirToLand(delta, blocks, 15f, State.DEF_LEAP_LAND); break;
            case DEF_LEAP_LAND:   updateGroundRecover(delta, blocks, LAND_RECOVER_T); break;
            case POWER_ANTIC:     updatePowerAntic(delta, blocks); break;
            case POWER_AIR:       updatePowerAir(delta, blocks); break;
            case POWER_LAND:      updateGroundRecover(delta, blocks, POWER_LAND_T); break;
            case STUN_FALL:       updateStunFall(delta, blocks); break;
            case STUNNED:         updateStunned(delta, blocks); break;
            case STUN_RECOVER:    updateStunRecover(delta, blocks); break;
            case DEATH_HIT:
            case DEATH_FALL:
            case DEATH_LAND:      updateDeath(delta, blocks); break;
            case DEAD:            break;
            default:              break;
        }
    }

    private void updateIntro(float delta, Array<SolidBlock> blocks) {
        velocity.x = 0f; applyGravity(delta, blocks);
        facePlayer();
        stateTimer += delta;
        if (stateTimer >= INTRO_T) returnToIdle();
    }

    private void updateIdle(float delta, Array<SolidBlock> blocks) {
        velocity.x = 0f; applyGravity(delta, blocks);
        bossContactDamage();
        think(delta);
    }

    private void think(float delta) {
        thinkTimer += delta;
        if (thinkTimer >= thinkDelay) {
            thinkTimer = 0f;
            if (needsTurn()) {
                setState(State.TURN);
            } else {
                beginMove(decideNextMove());
            }
        }
    }

    private boolean needsTurn() {
        if (player == null) return false;
        float dx = player.position.x - position.x;
        if (Math.abs(dx) < 24f) return false;
        boolean playerOnRight = dx > 0f;
        return playerOnRight != facingRight;
    }

    private void updateTurn(float delta, Array<SolidBlock> blocks) {
        velocity.x = 0f; applyGravity(delta, blocks);
        bossContactDamage();
        stateTimer += delta * animSpeed;
        if (stateTimer >= TURN_T) {
            facingRight = !facingRight;
            returnToIdle();
        }
    }

    private State decideNextMove() {
        float dist = distToPlayer();

        float wSlam, wCharge, wLeap;
        if (dist < CLOSE_RANGE)      { wSlam = 6f;   wCharge = 0.4f; wLeap = 1.5f; }
        else if (dist < MID_RANGE)   { wSlam = 1.5f; wCharge = 2.5f; wLeap = 4f;   }
        else                         { wSlam = 0.3f; wCharge = 5f;   wLeap = 3.5f; }
        float wPower = (phase == 2 && dist > CLOSE_RANGE) ? 3f : 0f;

        wSlam   = penalize(wSlam,   State.SLAM_ANTIC);
        wCharge = penalize(wCharge, State.CHARGE_ANTIC);
        wLeap   = penalize(wLeap,   State.LEAP_ANTIC);
        wPower  = penalize(wPower,  State.POWER_ANTIC);

        float total = wSlam + wCharge + wLeap + wPower;
        if (total <= 0f) return State.SLAM_ANTIC;
        float r = MathUtils.random(0f, total);
        if ((r -= wSlam)   < 0) return State.SLAM_ANTIC;
        if ((r -= wCharge) < 0) return State.CHARGE_ANTIC;
        if ((r -= wLeap)   < 0) return State.LEAP_ANTIC;
        return State.POWER_ANTIC;
    }

    private float penalize(float weight, State moveAntic) {
        if (moveAntic != lastMove) return weight;
        return (sameMoveStreak >= 2) ? 0f : weight * 0.15f;
    }

    private void beginMove(State moveAntic) {
        sameMoveStreak = (moveAntic == lastMove) ? sameMoveStreak + 1 : 1;
        lastMove = moveAntic;
        facePlayer();
        setState(moveAntic);
    }

    private void updateSlamAntic(float delta, Array<SolidBlock> blocks) {
        velocity.x = 0f; applyGravity(delta, blocks);
        if (stateTimer < SLAM_ANTIC_T * 0.5f) facePlayer();
        bossContactDamage();
        stateTimer += delta * animSpeed;
        if (stateTimer >= SLAM_ANTIC_T) {
            setState(State.SLAM_HIT);
            maceHitConsumed = false;
            requestShake(25f);
            playImpact();
        }
    }

    private void updateSlamHit(float delta, Array<SolidBlock> blocks) {
        velocity.x = 0f; applyGravity(delta, blocks);
        if (!maceHitConsumed && player != null
            && getMaceHitbox().overlaps(player.getBounds())
            && player.takeDamage(SLAM_DMG, position.x)) {
            maceHitConsumed = true;
        }
        bossContactDamage();
        stateTimer += delta * animSpeed;
        if (stateTimer >= SLAM_HIT_T) setState(State.SLAM_RECOVER);
    }

    private void updateChargeAntic(float delta, Array<SolidBlock> blocks) {
        velocity.x = 0f; applyGravity(delta, blocks);
        facePlayer();
        bossContactDamage();
        stateTimer += delta * animSpeed;
        if (stateTimer >= CHARGE_ANTIC_T) {
            chargeDir = facingRight ? 1 : -1;
            wallBonked = false;
            requestShake(10f);
            setState(State.CHARGE);
        }
    }

    private void updateCharge(float delta, Array<SolidBlock> blocks) {
        velocity.x = chargeDir * runSpeed * (enraged ? 1.5f : 1f);
        moveAndCollide(delta, blocks);
        bossContactDamage(CHARGE_DMG);
        stateTimer += delta;
        if (wallBonked || stateTimer >= CHARGE_MAX_T) {
            if (wallBonked) { requestShake(30f); playImpact(); }
            wallBonked = false;
            velocity.x = 0f;
            setState(State.CHARGE_RECOVER);
        }
    }

    private void updateLeapAntic(float delta, Array<SolidBlock> blocks) {
        velocity.x = 0f; applyGravity(delta, blocks);
        facePlayer();
        bossContactDamage();
        stateTimer += delta * animSpeed;
        if (stateTimer >= LEAP_ANTIC_T) {
            int dir = facingRight ? 1 : -1;
            velocity.x = dir * LEAP_VX * (enraged ? 1.25f : 1f);
            velocity.y = LEAP_VY;
            onGround = false;
            requestShake(10f);
            setState(State.LEAP_AIR);
        }
    }

    private void triggerDefensiveLeap() {
        int dir = (player != null && player.position.x > position.x) ? -1 : 1;
        velocity.x = dir * DEF_LEAP_VX;
        velocity.y = DEF_LEAP_VY;
        onGround = false;
        lastMove = State.DEF_LEAP_AIR;
        sameMoveStreak = 1;
        requestShake(10f);
        setState(State.DEF_LEAP_AIR);
    }

    private void updateAirToLand(float delta, Array<SolidBlock> blocks, float landShake, State landState) {
        moveAndCollide(delta, blocks);
        bossContactDamage();
        if (onGround && velocity.y <= 0f) {
            requestShake(landShake);
            velocity.x = 0f;
            setState(landState);
        }
    }

    private void updatePowerAntic(float delta, Array<SolidBlock> blocks) {
        velocity.x = 0f; applyGravity(delta, blocks);
        facePlayer();
        bossContactDamage();
        stateTimer += delta * animSpeed;
        if (stateTimer >= POWER_ANTIC_T) {
            int dir = facingRight ? 1 : -1;
            velocity.x = dir * POWER_VX;
            velocity.y = POWER_VY;
            onGround = false;
            requestShake(12f);
            setState(State.POWER_AIR);
        }
    }

    private void updatePowerAir(float delta, Array<SolidBlock> blocks) {
        moveAndCollide(delta, blocks);
        bossContactDamage();
        if (onGround && velocity.y <= 0f) {
            requestShake(45f);
            playImpact();
            velocity.x = 0f;
            spawnShockwaves();
            setState(State.POWER_LAND);
        }
    }

    private void updateGroundRecover(float delta, Array<SolidBlock> blocks, float duration) {
        velocity.x = 0f; applyGravity(delta, blocks);
        bossContactDamage();
        stateTimer += delta * animSpeed;
        if (stateTimer >= duration) returnToIdle();
    }

    private void enterStun() {
        velocity.set(0f, 0f);
        shockwaves.clear();
        requestShake(25f);
        if (GameAssetManager.wallBreakSound != null) GameAssetManager.wallBreakSound.play(1f);
        setState(State.STUN_FALL);
    }

    private void updateStunFall(float delta, Array<SolidBlock> blocks) {
        applyGravity(delta, blocks);
        stateTimer += delta;
        if (onGround && stateTimer >= 0.2f) { setState(State.STUNNED); stunTimer = STUN_DURATION; }
    }

    private void updateStunned(float delta, Array<SolidBlock> blocks) {
        velocity.x = 0f; applyGravity(delta, blocks);
        stunTimer -= delta;
        if (stunTimer <= 0f) setState(State.STUN_RECOVER);
    }

    private void updateStunRecover(float delta, Array<SolidBlock> blocks) {
        velocity.x = 0f; applyGravity(delta, blocks);
        stateTimer += delta;
        if (stateTimer >= STUN_RECOVER_T) {
            if (phase == 1) {
                phase = 2;
                enraged = true;
                animSpeed = 1.5f;
                thinkDelay = 0.28f;
            }
            returnToIdle();
        }
    }

    private void updateDeath(float delta, Array<SolidBlock> blocks) {
        applyGravity(delta, blocks);
        stateTimer += delta;
        switch (state) {
            case DEATH_HIT:  if (stateTimer >= DEATH_HIT_T)      setState(State.DEATH_FALL); break;
            case DEATH_FALL: if (onGround && stateTimer >= 0.2f) setState(State.DEATH_LAND); break;
            case DEATH_LAND: if (stateTimer >= deathLandDuration()) { state = State.DEAD; alive = false; } break;
            default: break;
        }
    }

    private float deathLandDuration() {
        Animation<TextureRegion> a = A(AnimationType.FALSE_KNIGHT_DEATH_LAND);
        return (a != null) ? a.getAnimationDuration() : DEATH_LAND_T;
    }

    private float flinchDuration() {
        Animation<TextureRegion> a = A(AnimationType.FALSE_KNIGHT_DEATH_HIT);
        return (a != null) ? a.getAnimationDuration() : 0.3f;
    }

    @Override
    public boolean takeDamage(float dmg) { return takeDamage(dmg, bounds.x); }

    @Override
    public boolean takeDamagePogo(float dmg) { return takeDamage(dmg, bounds.x); }

    @Override
    public boolean takeDamage(float dmg, float sourceX) {
        if (!alive) return false;
        switch (state) {
            case DORMANT: case INTRO:
            case DEATH_HIT: case DEATH_FALL: case DEATH_LAND: case DEAD:
                return false;
            default: break;
        }

        boolean stunned = (state == State.STUNNED || state == State.STUN_FALL);
        float applied = stunned ? dmg * STUN_DMG_MULT : dmg;

        boolean ok = super.takeDamage(applied);
        if (!ok) return false;

        hitFlash = 0.1f;
        if (state == State.STUNNED || state == State.STUN_FALL) {
            stunFlinchTimer = flinchDuration();
            stunFlinchClock = 0f;
        }

        recentDamage += dmg;
        damageWindow = HEAVY_WINDOW;

        if (phase == 1 && health > 0f && health <= maxHealth * 0.5f
            && !stunned && state != State.STUN_RECOVER) {
            enterStun();
            return true;
        }

        if (!stunned && canDefLeap() && recentDamage >= HEAVY_THRESHOLD
            && MathUtils.randomBoolean(0.6f)) {
            recentDamage = 0f;
            triggerDefensiveLeap();
        }
        return true;
    }

    private boolean canDefLeap() {
        if (!onGround) return false;
        switch (state) {
            case IDLE: case SLAM_RECOVER: case CHARGE_RECOVER:
            case LEAP_LAND: case DEF_LEAP_LAND:
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onDeath() {
        state = State.DEATH_FALL;
        stateTimer = 0f;
        alive = true;
        velocity.x = 0f;
        shockwaves.clear();
        stunFlinchTimer = 0f;
    }

    @Override
    protected float invincibleTime() { return 0f; }

    private void bossContactDamage() { bossContactDamage(BODY_TOUCH_DMG); }

    private void bossContactDamage(float dmg) {
        if (player == null || isHarmlessState()) return;
        if (bounds.overlaps(player.getBounds())) {
            player.takeDamage(dmg, bounds.x + bounds.width / 2f);
        }
    }

    private boolean isHarmlessState() {
        switch (state) {
            case STUNNED: case STUN_FALL: case STUN_RECOVER:
            case DEATH_HIT: case DEATH_FALL: case DEATH_LAND: case DEAD:
            case DORMANT: case INTRO:
                return true;
            default:
                return false;
        }
    }

    public Rectangle getMaceHitbox() {
        float w = 200f, h = 130f;
        float x = facingRight ? (bounds.x + bounds.width - 30f) : (bounds.x - w + 30f);
        maceBox.set(x, bounds.y - 10f, w , h);
        return maceBox;
    }

    private void spawnShockwaves() {
        float groundY = bounds.y;
        float originX = bounds.x + bounds.width / 2f;
        shockwaves.add(new Shockwave(originX, groundY, 1));
        shockwaves.add(new Shockwave(originX, groundY, -1));
    }

    private void updateShockwaves(float delta, Array<SolidBlock> blocks) {
        for (int i = shockwaves.size - 1; i >= 0; i--) {
            Shockwave w = shockwaves.get(i);
            w.update(delta);

            if (player != null && w.bounds.overlaps(player.getBounds())) {
                player.takeDamage(SHOCKWAVE_DMG, w.bounds.x);
            }

            boolean blocked = false;
            for (SolidBlock b : blocks) {
                if (b.isDeadly) continue;
                if (!w.bounds.overlaps(b.bounds)) continue;
                float oy = Math.min(w.bounds.y + w.bounds.height, b.bounds.y + b.bounds.height)
                    - Math.max(w.bounds.y, b.bounds.y);
                float ox = Math.min(w.bounds.x + w.bounds.width, b.bounds.x + b.bounds.width)
                    - Math.max(w.bounds.x, b.bounds.x);
                if (ox < oy && oy > 8f) { blocked = true; break; }
            }
            if (blocked || w.life <= 0f) shockwaves.removeIndex(i);
        }
    }

    private static class Shockwave {
        final Rectangle bounds = new Rectangle();
        float x, speed, accel, life;
        final int dir;

        float stateTime;

        Shockwave(float startX, float groundY, int dir) {
            this.dir = dir;
            this.x = startX;
            this.speed = 260f;
            this.accel = 520f;
            this.life = 3.0f;
            this.stateTime = 0f;
            bounds.set(startX - 30f, groundY, 60f, 70f);
        }
        void update(float delta) {
            speed += accel * delta;
            x += dir * speed * delta;
            bounds.x = x - bounds.width / 2f;
            life -= delta;
            stateTime += delta;
        }
    }

    private void applyGravity(float delta, Array<SolidBlock> blocks) {
        moveAndCollide(delta, blocks);
    }

    private void moveAndCollide(float delta, Array<SolidBlock> blocks) {
        position.x += velocity.x * delta;
        syncBounds();
        for (SolidBlock b : blocks) {
            if (b.isDeadly) continue;
            if (!bounds.overlaps(b.bounds)) continue;
            float overlapY = Math.min(bounds.y + bounds.height, b.bounds.y + b.bounds.height)
                - Math.max(bounds.y, b.bounds.y);
            if (overlapY < 4f) continue;
            if (velocity.x > 0)      position.x = b.bounds.x - BODY_W / 2f;
            else if (velocity.x < 0) position.x = b.bounds.x + b.bounds.width + BODY_W / 2f;
            onHitWall();
            velocity.x = 0f;
            syncBounds();
        }

        velocity.y += GRAVITY * delta;
        position.y += velocity.y * delta;
        syncBounds();
        onGround = false;
        for (SolidBlock b : blocks) {
            if (b.isDeadly) continue;
            if (!bounds.overlaps(b.bounds)) continue;
            float overlapX = Math.min(bounds.x + bounds.width, b.bounds.x + b.bounds.width)
                - Math.max(bounds.x, b.bounds.x);
            if (overlapX < 4f) continue;
            if (velocity.y <= 0) { position.y = b.bounds.y + b.bounds.height; onGround = true; }
            else                 { position.y = b.bounds.y - BODY_H; }
            velocity.y = 0f;
            syncBounds();
        }
    }

    private void onHitWall() { if (state == State.CHARGE) wallBonked = true; }

    private void syncBounds() {
        bounds.set(position.x - BODY_W / 2f, position.y, BODY_W, BODY_H);
    }

    private void setState(State s) { state = s; stateTimer = 0f; }
    private void returnToIdle()    { setState(State.IDLE); thinkTimer = 0f; }

    private void facePlayer() { if (player != null) facingRight = player.position.x > position.x; }
    private float distToPlayer() { return player == null ? 0f : Math.abs(player.position.x - position.x); }

    private void requestShake(float mag) { pendingShake = Math.max(pendingShake, mag); }

    private void playImpact() {
        if (GameAssetManager.landingSound != null) GameAssetManager.landingSound.play(1f);
    }

    private static Animation<TextureRegion> A(AnimationType t) {
        return GameAssetManager.animationMap.get(t);
    }

    private static Texture pixel() {
        if (pixel == null) {
            Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pm.setColor(Color.WHITE);
            pm.fill();
            pixel = new Texture(pm);
            pm.dispose();
        }
        return pixel;
    }

    private float mapTime(Animation<TextureRegion> a, float elapsed, float duration) {
        if (duration <= 0f) return elapsed;
        float p = elapsed / duration;
        if (p > 1f) p = 1f;
        return p * a.getAnimationDuration();
    }

    @Override
    public void render(SpriteBatch batch) {
        drawShockwaves(batch);

        Animation<TextureRegion> anim;
        boolean loop = false;
        float dur = 0f;
        float fixedTime = -1f;

        boolean forceLastFrame = false;

        switch (state) {
            case DORMANT: case INTRO: case IDLE:
                anim = A(AnimationType.FALSE_KNIGHT_IDLE); loop = true; break;
            case TURN:
                anim = A(AnimationType.FALSE_KNIGHT_TURN); dur = TURN_T; break;
            case SLAM_ANTIC:
                anim = A(AnimationType.FALSE_KNIGHT_ATTACK_ANTIC); dur = SLAM_ANTIC_T; break;
            case SLAM_HIT:
                anim = A(AnimationType.FALSE_KNIGHT_ATTACK); dur = SLAM_HIT_T; break;
            case SLAM_RECOVER:
                anim = A(AnimationType.FALSE_KNIGHT_ATTACK_RECOVER); dur = SLAM_RECOVER_T; break;
            case CHARGE_ANTIC:
                anim = A(AnimationType.FALSE_KNIGHT_RUN_ANTIC); dur = CHARGE_ANTIC_T; break;
            case CHARGE:
                anim = A(AnimationType.FALSE_KNIGHT_RUN); loop = true; break;
            case CHARGE_RECOVER:
                anim = A(AnimationType.FALSE_KNIGHT_LAND); dur = CHARGE_RECOVER_T; break;
            case LEAP_ANTIC:
                anim = A(AnimationType.FALSE_KNIGHT_JUMP_ANTIC); dur = LEAP_ANTIC_T; break;
            case POWER_ANTIC:
                anim = A(AnimationType.FALSE_KNIGHT_JUMP_ANTIC); dur = POWER_ANTIC_T; break;
            case LEAP_AIR: case DEF_LEAP_AIR:
                anim = A(AnimationType.FALSE_KNIGHT_JUMP); loop = true; break;
            case POWER_AIR:
                anim = A(AnimationType.FALSE_KNIGHT_JUMP_ATTACK);

                float jumpProgress = (POWER_VY - velocity.y) / (POWER_VY * 2f);
                jumpProgress = MathUtils.clamp(jumpProgress, 0f, 1f);

                fixedTime = jumpProgress * (anim.getAnimationDuration() - 0.001f);
                break;
            case LEAP_LAND: case DEF_LEAP_LAND:
                anim = A(AnimationType.FALSE_KNIGHT_LAND); dur = LAND_RECOVER_T; break;
            case POWER_LAND:
                anim = A(AnimationType.FALSE_KNIGHT_LAND); dur = POWER_LAND_T; break;
            case STUN_FALL: case STUNNED:
                if (stunFlinchTimer > 0f) {
                    anim = A(AnimationType.FALSE_KNIGHT_DEATH_HIT);
                    fixedTime = stunFlinchClock;
                } else {
                    anim = A(AnimationType.FALSE_KNIGHT_BODY); loop = true;
                }
                break;
            case STUN_RECOVER:
                anim = A(AnimationType.FALSE_KNIGHT_STUN_RECOVER); dur = 0f; break;
            case DEATH_HIT:
                anim = A(AnimationType.FALSE_KNIGHT_DEATH_HIT); dur = 0f; break;
            case DEATH_FALL:
                anim = A(AnimationType.FALSE_KNIGHT_DEATH_FALL); loop = true; break;
            case DEATH_LAND:
                anim = A(AnimationType.FALSE_KNIGHT_DEATH_LAND); dur = 0f; break;
            case DEAD:
                anim = A(AnimationType.FALSE_KNIGHT_DEATH_LAND);
                forceLastFrame = true;
                break;
            default:
                anim = A(AnimationType.FALSE_KNIGHT_IDLE); loop = true; break;
        }

        if (anim == null) return;

        TextureRegion frame;

        if (forceLastFrame) {
            Object[] frames = anim.getKeyFrames();
            frame = (TextureRegion) frames[frames.length - 1];
        } else {
            float time = (fixedTime >= 0f) ? fixedTime : (loop ? animClock : mapTime(anim, stateTimer, dur));
            frame = anim.getKeyFrame(time, loop);
        }

        if (frame == null) return;

        float fw = frame.getRegionWidth()  * DRAW_SCALE;
        float fh = frame.getRegionHeight() * DRAW_SCALE;
        float cx = bounds.x + bounds.width  / 2f;
        float scaleX = facingRight ? FACING_SIGN : -FACING_SIGN;

        if (hitFlash > 0f) batch.setColor(1f, 0.5f, 0.5f, 1f);

        batch.draw(frame,
            cx - fw / 2f,
            bounds.y + DRAW_OFFSET_Y,
            fw / 2f, 0f,
            fw, fh,
            scaleX, 1f, 0f);

        batch.setColor(Color.WHITE);

        if (DEBUG_HITBOX) {
            Rectangle hb = getActiveHitbox();
            batch.setColor(1f, 0f, 0f, 0.30f);
            batch.draw(pixel(), hb.x, hb.y, hb.width, hb.height);
            batch.setColor(Color.WHITE);
        }
    }

    private void drawShockwaves(SpriteBatch batch) {
        if (shockwaves.size == 0) return;

        Animation<TextureRegion> shockwaveAnim = GameAssetManager.animationMap.get(AnimationType.FALSE_KNIGHT_SHOCKWAVE);
        if (shockwaveAnim == null) return;

        for (Shockwave w : shockwaves) {
            TextureRegion frame = shockwaveAnim.getKeyFrame(w.stateTime, false);

            if (frame != null) {
                float fw = frame.getRegionWidth() * DRAW_SCALE;
                float fh = frame.getRegionHeight() * DRAW_SCALE;

                float cx = w.bounds.x + w.bounds.width / 2f;

                float scaleX = (w.dir == 1) ? 1f : -1f;
                batch.draw(frame,
                    cx - fw / 2f,
                    w.bounds.y,
                    fw / 2f, 0f,
                    fw, fh,
                    scaleX, 1f, 0f);
            }
        }
    }
}
