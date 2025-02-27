package com.robotrunner.Weapons;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.robotrunner.Entities.Entity;
import com.robotrunner.States.MyGame;

public abstract class Weapon {
    MyGame myGame;
    public Entity user;
    public float attackDamage;
    public float attacksPerSecond;
    public Sound attackSound;
    public double lastUse;
    public Weapon(MyGame myGame, Entity user) {
        this.myGame = myGame;
        this.user = user;
        this.lastUse = 0;
    }
    public abstract void attack(Vector2 target);
    public abstract void render();
    public void destroy() {
    }
    public static void handleContact(Fixture fixtureA, Fixture fixtureB, boolean beginContact, MyGame myGame) {
        RangedWeapon.handleContact(fixtureA, fixtureB, beginContact, myGame);
        BulletClass.handleContact(fixtureA, fixtureB, beginContact, myGame);
        Laser.handleContact(fixtureA, fixtureB, beginContact, myGame);
        BodyWeapon.handleContact(fixtureA, fixtureB, beginContact, myGame);
    }
}
