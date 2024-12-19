package com.tbd.game.Weapons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.tbd.game.World.Listener;
import com.tbd.game.States.MyGame;
import static com.tbd.game.World.Constants.*;

public class BulletClass extends Weapon{
    float bulletSpeed;
    float bulletRadius;
    Body body;
    double timeOfCreation;
    RangedWeapon rangedWeapon;
    public BulletClass(MyGame myGame, RangedWeapon rangedWeapon) {
        super(myGame, rangedWeapon.user);

        this.bulletSpeed = rangedWeapon.bulletSpeed;
        this.attackDamage = rangedWeapon.attackDamage;
        this.bulletRadius = rangedWeapon.bulletRadius;
        this.attacksPerSecond = rangedWeapon.attacksPerSecond;
        this.rangedWeapon = rangedWeapon;

        createBody();
        createDespawnBody();
    }
    private void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.gravityScale = 0;
        bodyDef.position.set(user.getBodyCenter());

        body = myGame.world.createBody(bodyDef);
        body.setUserData(this);

        CircleShape shape = new CircleShape();
        shape.setRadius(bulletRadius);

        Fixture normalBody = body.createFixture(shape, 0.0f);
        normalBody.setSensor(true);
        normalBody.setUserData(this);

        body.setBullet(true);

        shape.dispose();

        timeOfCreation = myGame.timePassed;
    }
    private void createDespawnBody() {
        CircleShape shape = new CircleShape();
        shape.setRadius(bulletRadius * BULLET_DESPAWN_HITBOX);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        //fixtureDef.isSensor = true;

        Filter filter = new Filter();
        filter.maskBits = CATEGORY_BITS_MAP;
        Fixture despawnBody = body.createFixture(fixtureDef);
        despawnBody.setFilterData(filter);
        despawnBody.setUserData(BulletType.DespawnHitbox);

        shape.dispose();
    }
    public void destroy() {
        rangedWeapon.totalBullets.remove(this);
        myGame.world.destroyBody(body);
    }

    @Override
    public void attack(Vector2 target) {

    }

    @Override
    public void render() {
        myGame.batch.draw((Texture) myGame.assetManager.get("bullet.png"), body.getPosition().x - bulletRadius, body.getPosition().y - bulletRadius, 2 * bulletRadius, 2 * bulletRadius);
    }
    public static void handleContact(Fixture fixtureA, Fixture fixtureB, boolean beginContact, MyGame myGame) {
        if (!beginContact) return;
        Fixture weaponFixture = null;
        Fixture contactFixture = null;
        Fixture[] collision = Listener.checkContact(fixtureA, fixtureB, BulletType.class);
        if (collision[0] != null) {
            weaponFixture = collision[0];
            contactFixture = collision[1];
        }
        if (weaponFixture != null && weaponFixture.getBody().getUserData().getClass() == BulletClass.class) {
            if (contactFixture.getUserData() == null || contactFixture.getUserData().getClass() == weaponFixture.getBody().getUserData().getClass()) ((BulletClass) weaponFixture.getBody().getUserData()).destroy();
        }
    }
}
