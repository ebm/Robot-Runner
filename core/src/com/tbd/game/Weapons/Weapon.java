package com.tbd.game.Weapons;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.tbd.game.Entities.Entity;
import com.tbd.game.Listener;
import com.tbd.game.MyGame;

public abstract class Weapon {
    MyGame myGame;
    public Entity user;
    public float attackDamage;
    public float attacksPerSecond;
    public Weapon(MyGame myGame, Entity user) {
        this.myGame = myGame;
        this.user = user;
    }
    public abstract void attack(Vector2 target);
    public abstract void render();
    public static void handleContact(Fixture fixtureA, Fixture fixtureB, boolean beginContact, MyGame myGame) {
        RangedWeapon.handleContact(fixtureA, fixtureB, beginContact, myGame);
        BulletClass.handleContact(fixtureA, fixtureB, beginContact, myGame);
        Laser.handleContact(fixtureA, fixtureB, beginContact, myGame);
        BodyWeapon.handleContact(fixtureA, fixtureB, beginContact, myGame);
    }
}
