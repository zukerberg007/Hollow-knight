package com.amirali.graphics.models;

import com.badlogic.gdx.utils.Array;

public class Game {
    public Player player = new Player();

    public void update(float delta, Array<SolidBlock> blocks){
        player.update(delta, blocks);
    }
}
