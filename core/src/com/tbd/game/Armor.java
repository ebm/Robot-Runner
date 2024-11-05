package com.tbd.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;

public class Armor extends Item {
    public float dmgTakenMultiplier;
    public Armor(float dmgTakenMultiplier, int id, float x, float y, Texture itemTexture, MyGame myGame) {
        super(id, ItemType.Armor, x, y, itemTexture, myGame);
        this.dmgTakenMultiplier = dmgTakenMultiplier;
    }

    @Override
    public void apply() {
        myGame.player.dmgTakenMultiplier = dmgTakenMultiplier;
    }
}
