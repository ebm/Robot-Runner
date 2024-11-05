package com.tbd.game;

import com.badlogic.gdx.graphics.Texture;

public enum ItemType {
    GolemArmor, DashAbility;
    public static Texture getTexture(ItemType itemType, MyGame myGame) {
        if (itemType == GolemArmor) {
            return myGame.rockArmor;
        }
        return null;
    }
}
