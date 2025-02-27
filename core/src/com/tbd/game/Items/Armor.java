package com.tbd.game.Items;

import com.badlogic.gdx.graphics.Texture;
import com.tbd.game.States.MyGame;

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

    @Override
    public String toString() {
        return "Reduces damage taken by " + (int) Math.ceil(((1 - dmgTakenMultiplier) * 100)) + "%.";
    }
}
