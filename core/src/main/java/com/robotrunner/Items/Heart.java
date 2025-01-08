package com.robotrunner.Items;

import com.badlogic.gdx.graphics.Texture;
import com.robotrunner.States.MyGame;

public class Heart extends Item {
    public float additionalHealth;
    public Heart(float additionalHealth, int id, float x, float y, Texture itemTexture, MyGame myGame) {
        super(id, ItemType.Attribute, x, y, itemTexture, myGame);
        this.additionalHealth = additionalHealth;
    }

    @Override
    public void apply() {
        myGame.player.additionalHealth = additionalHealth;
    }
    @Override
    public String toString() {
        return "Increases maximum health by " + (int) additionalHealth + ".";
    }
}

