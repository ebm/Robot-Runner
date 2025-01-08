package com.robotrunner.Items;

import com.badlogic.gdx.graphics.Texture;
import com.robotrunner.States.MyGame;

public class Boots extends Item {
    public float speedMultiplier;
    public Boots(float speedMultiplier, int id, float x, float y, Texture itemTexture, MyGame myGame) {
        super(id, ItemType.Boots, x, y, itemTexture, myGame);
        this.speedMultiplier = speedMultiplier;
    }

    @Override
    public void apply() {
        myGame.player.speedMultiplier = speedMultiplier;
    }
    @Override
    public String toString() {
        return "Increases movement speed by " + (int) ((speedMultiplier - 1) * 100) + "%.";
    }
}
