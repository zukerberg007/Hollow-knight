package com.amirali.graphics.models;

public class BreakableWall extends SolidBlock {
    public int hp = 3;
    public boolean isBroken = false;

    public String breakSide;
    public boolean revealsSecret;

    public BreakableWall(float x, float y, float width, float height, String breakSide, boolean revealsSecret) {
        super(x, y, width, height, false);
        this.breakSide = breakSide;
        this.revealsSecret = revealsSecret;
    }

    public void takeDamage() {
        if (!isBroken) {
            hp--;
            if (hp <= 0) {
                isBroken = true;
                this.bounds.set(-1000, -1000, 0, 0);
            }
        }
    }
}
