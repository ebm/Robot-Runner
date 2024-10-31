package com.tbd.game.Entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.tbd.game.MyGame;

public abstract class Entity {
    public MyGame myGame;
    public float health;
    public Body body;
    public Class<?> friendly;
    public Class<?> enemy;
    public boolean death;
    public void takeDamage(float damage) {
        //System.out.println("Health: " + health);
        health -= damage;
        if (health <= 0) {
            death();
        }
    }
    public void death() {
        //System.out.println("Death: " + this.getClass());
        myGame.world.destroyBody(body);
        death = true;
    }
    public abstract Vector2 getBodyCenter();
    public abstract void update();
    public abstract void render();
}
