package com.tbd.game.Items;

import com.badlogic.gdx.graphics.Texture;
import com.tbd.game.States.MyGame;

import static com.tbd.game.World.Constants.PLAYER_DASH_COOLDOWN;

public abstract class Ability extends Item {
    public double lastUse;
    public float cooldown;
    public Ability(int id, ItemType itemType, float x, float y, Texture itemTexture, MyGame myGame) {
        super(id, itemType, x, y, itemTexture, myGame);
    }

    @Override
    public abstract void apply();

    @Override
    public abstract String toString();
}
