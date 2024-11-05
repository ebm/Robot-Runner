package com.tbd.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;

public class Armor extends Item {
    public float dmgMultiplier;
    public Armor(float dmgMultiplier, int id, float x, float y, Texture itemTexture, MyGame myGame) {
        super(id, ItemType.Armor, x, y, itemTexture, myGame);
        this.dmgMultiplier = dmgMultiplier;
    }
}
