package com.tbd.game.Weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.tbd.game.Entities.Entity;
import com.tbd.game.Listener;
import com.tbd.game.MyGame;

import static com.tbd.game.Constants.BAT_INITIAL_X_POSITION;
import static com.tbd.game.Constants.BAT_INITIAL_Y_POSITION;

public class BodyWeapon extends Weapon {
    public Body body;
    Entity contactEntity;
    double timeOfContact;
    public BodyWeapon(MyGame myGame, Entity user, float attackDamage, Shape shape) {
        super(myGame, user);
        this.attackDamage = attackDamage;
        timeOfContact = 0;
        contactEntity = null;

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
        timeOfContact = System.nanoTime();
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
        if (contactEntity != null) {
            float damage = (float) ((System.nanoTime() - timeOfContact) / 1000000000) * attackDamage;
            if (contactEntity.death) {
                contactEntity = null;
                return;
            }
            contactEntity.takeDamage(damage);
            timeOfContact = System.nanoTime();
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
