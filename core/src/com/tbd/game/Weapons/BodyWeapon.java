package com.tbd.game.Weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.tbd.game.Entities.Entity;
import com.tbd.game.World.Listener;
import com.tbd.game.States.MyGame;

public class BodyWeapon extends Weapon {
    public Body body;
    Entity contactEntity;
    float cooldown;
    double lastUse;
    public BodyWeapon(MyGame myGame, Entity user, float attackDamage, Shape shape, float cooldown) {
        super(myGame, user);
        this.attackDamage = attackDamage;
        contactEntity = null;
        this.cooldown = cooldown;
        lastUse = 0;

        createBody(shape);
    }
    private void createBody(Shape shape) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        body = myGame.world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        body.setGravityScale(0);

        Fixture bodyFixture = body.createFixture(fixtureDef);
        bodyFixture.setUserData(this);
        bodyFixture.setSensor(true);

        shape.dispose();
        body.setFixedRotation(true);
    }
    public void contactStarted(Entity e) {
        contactEntity = e;
    }
    public void contactEnded() {
        contactEntity = null;
    }

    @Override
    public void attack(Vector2 target) {
        body.setTransform(target.x, target.y, 0);
    }

    @Override
    public void render() {
        if (contactEntity != null && myGame.timePassed - lastUse > cooldown) {
            lastUse = myGame.timePassed;
            float damage;
            if (cooldown == 0) damage = Gdx.graphics.getDeltaTime() * attackDamage;
            else damage = attackDamage;
            if (contactEntity.death) {
                contactEntity = null;
                return;
            }
            contactEntity.takeDamage(damage);
        }
    }
    public static void handleContact(Fixture fixtureA, Fixture fixtureB, boolean beginContact, MyGame myGame) {
        if (beginContact) return;
        Fixture weaponFixture = null;
        Fixture contactFixture = null;
        Fixture[] collision = Listener.checkContact(fixtureA, fixtureB, BodyWeapon.class);
        if (collision[0] != null) {
            weaponFixture = collision[0];
            contactFixture = collision[1];
        }
        if (weaponFixture != null) {
            BodyWeapon weapon = (BodyWeapon) weaponFixture.getUserData();
            if (contactFixture.getUserData() == null || weapon.user.getClass() != contactFixture.getUserData().getClass()) {
                if (weapon.user.enemy.isInstance(contactFixture.getUserData())) {
                    weapon.contactEnded();
                }
            }
        }
    }
}
