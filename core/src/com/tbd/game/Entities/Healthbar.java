package com.tbd.game.Entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tbd.game.States.MyGame;

public class Healthbar {
    MyGame myGame;
    Entity entity;
    public float maxHealth;
    TextureRegion[] healthbarArray;
    public Healthbar(MyGame myGame, Entity entity, float maxHealth) {
        this.myGame = myGame;
        this.entity = entity;
        this.maxHealth = maxHealth;

        healthbarArray = new TextureRegion[] {myGame.healthbarAtlas.findRegion("healthbar1"), myGame.healthbarAtlas.findRegion("healthbar2"),
                myGame.healthbarAtlas.findRegion("healthbar3"), myGame.healthbarAtlas.findRegion("healthbar4"), myGame.healthbarAtlas.findRegion("healthbar5"),
                myGame.healthbarAtlas.findRegion("healthbar6"), myGame.healthbarAtlas.findRegion("healthbar7")};
    }
    public void changeHealth(float maxHealth) {
        this.maxHealth = maxHealth;
    }
    public TextureRegion getHealthBar() {
        int index = (int) (entity.health * 7 / maxHealth);
        if (index >= 7) index = 6;
        if (index < 0) index = 0;
        return healthbarArray[index];
    }
}
