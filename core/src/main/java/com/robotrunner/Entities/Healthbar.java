package com.robotrunner.Entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.robotrunner.States.MyGame;

public class Healthbar {
    MyGame myGame;
    Entity entity;
    public float maxHealth;
    TextureRegion[] healthbarArray;
    TextureAtlas healthbarAtlas;
    public Healthbar(MyGame myGame, Entity entity, float maxHealth) {
        this.myGame = myGame;
        this.entity = entity;
        this.maxHealth = maxHealth;

        healthbarAtlas = myGame.assetManager.get("healthbar/healthbar.atlas");

        healthbarArray = new TextureRegion[] {healthbarAtlas.findRegion("healthbar1"), healthbarAtlas.findRegion("healthbar2"),
                healthbarAtlas.findRegion("healthbar3"), healthbarAtlas.findRegion("healthbar4"), healthbarAtlas.findRegion("healthbar5"),
                healthbarAtlas.findRegion("healthbar6"), healthbarAtlas.findRegion("healthbar7")};
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
