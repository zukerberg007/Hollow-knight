package com.amirali.graphics.models;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public abstract class Entity {
    protected final Rectangle bounds = new Rectangle();
    protected float health;
    protected boolean alive = true;

    protected float invincibleTimer = 0f;
    protected static final float INVINCIBLE_TIME = 1f;

    public Entity(float x, float y, float w, float h, float health){
        bounds.set(x,y,w,h);
        this.health = health;
    }

    public Rectangle getBounds() { return bounds; }
    public boolean isAlive() { return alive; }
    public boolean isHarmful() { return alive && health > 0f; }

    protected float invincibleTime() { return INVINCIBLE_TIME; }

    public boolean takeDamage(float dmg){
        if(!alive || invincibleTimer > 0f) return false;
        health -= dmg;
        invincibleTimer = invincibleTime();
        if(health <= 0f) { health = 0f; alive = false; onDeath(); }
        return true;
    }

    public boolean takeDamage(float dmg, float sourceX) {
        return takeDamage(dmg);
    }

    protected void onDeath() {}

    public void update(float delta, Array<SolidBlock> blocks){
        if(invincibleTimer > 0f) invincibleTimer -= delta;
    }

    public void render(SpriteBatch batch){

    }
    public boolean takeDamagePogo(float dmg) {
        return takeDamage(dmg);
    }
}
