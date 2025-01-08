package com.robotrunner.Weapons;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.robotrunner.World.Direction;
import com.robotrunner.World.Listener;
import com.robotrunner.States.MyGame;

import static com.robotrunner.World.Constants.*;


public class Laser {
    MyGame myGame;
    float angle;
    float laserDistance;
    double lastSwitch;
    Body body;
    Body testBody;
    float initialX;
    float initialY;
    Weapon weapon;
    boolean active;
    TextureRegion laserBeamTexture;
    TextureRegion laserTexture;
    float width;
    float height;
    float laserTexturePositionX;
    float laserTexturePositionY;
    public Laser(MyGame myGame, float angle, float initialX, float initialY, float laserDistance) {
        this.myGame = myGame;
        this.angle = angle;

        createTestProjectile(initialX, initialY);
        //createBody(initialX, initialY);
        this.laserDistance = laserDistance;
        laserBeamTexture = new TextureRegion((Texture) myGame.assetManager.get("laserBeam.png"));
        laserTexture = new TextureRegion((Texture) myGame.assetManager.get("laser.png"));
        lastSwitch = 0;
        active = true;
    }
    public void createTestProjectile(float initialX, float initialY) {
        laserTexturePositionX = initialX;
        laserTexturePositionY = initialY;
        this.initialX = 0.5f + initialX + 0.3f * (float) Math.cos(Math.toRadians(angle));
        this.initialY = 0.5f + initialY + 0.3f * (float) Math.sin(Math.toRadians(angle));

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.gravityScale = 0;

        testBody = myGame.world.createBody(bodyDef);
        testBody.setUserData(this);

        CircleShape shape = new CircleShape();
        shape.setRadius(LASER_RADIUS);

        Fixture normalBody = testBody.createFixture(shape, 0.0f);
        normalBody.setSensor(false);
        normalBody.setUserData(this);
        Filter filter = new Filter();
        filter.categoryBits = CATEGORY_BITS_LASER_PROJECTILE;
        filter.maskBits = CATEGORY_BITS_MAP;
        normalBody.setFilterData(filter);

        testBody.setBullet(true);
        testBody.setGravityScale(0);

        shape.dispose();

        testBody.setTransform(this.initialX + 1.5f * (float) Math.cos(Math.toRadians(angle)), this.initialY + 1.5f * (float) Math.sin(Math.toRadians(angle)), 0);
        testBody.setLinearVelocity(LASER_TEST_PROJECTILE_VELOCITY * (float) Math.cos(Math.toRadians(angle)), LASER_TEST_PROJECTILE_VELOCITY * (float) Math.sin(Math.toRadians(angle)));
    }
    public void createBody() {
        width = LASER_RADIUS + (float) Math.sqrt(Math.pow(initialX - testBody.getPosition().x, 2) + Math.pow(initialY - testBody.getPosition().y, 2));
        height = LASER_RADIUS * 2;
        if (width < LASER_MINIMUM_DISTANCE) return;
        System.out.println(width + ", " + height);
        if (Math.abs(width) <= 0 || Math.abs(height) <= 0) return;
        PolygonShape polygon = new PolygonShape();
        polygon.set(new Vector2[] {new Vector2(0, -height / 2), new Vector2(0, height / 2), new Vector2(width, height / 2), new Vector2(width, -height / 2)});
        weapon = new BodyWeapon(myGame, myGame.mapEntity, LASER_ATTACK_DAMAGE_PER_SECOND, polygon, 0);

        body = ((BodyWeapon) weapon).body;

        body.setTransform(initialX, initialY, (float) Math.toRadians(angle));

        myGame.world.destroyBody(testBody);
    }

    public void render() {
        if (body == null) {
            if (getDistance(new Vector2(initialX, initialY), testBody.getPosition()) > laserDistance) createBody();
            return;
        }
        weapon.render();
        if (active) {
            if (myGame.timePassed - lastSwitch > LASER_THREAT_SECONDS) {
                active = false;
                lastSwitch = myGame.timePassed;
                body.setActive(false);
                body.setAwake(true);
            }
            myGame.batch.draw(laserBeamTexture, body.getPosition().x, body.getPosition().y - height / 2, 0, height / 2, width, height, 1, 1, angle);
        } else {
            if (myGame.timePassed - lastSwitch > LASER_SAFE_SECONDS) {
                active = true;
                lastSwitch = myGame.timePassed;
                body.setActive(true);
                body.setAwake(true);
            }
        }
        myGame.batch.draw(laserTexture, laserTexturePositionX, laserTexturePositionY, 0.5f, 0.5f, 1, 1, 1, 1, angle);
    }
    public static void handleContact(Fixture fixtureA, Fixture fixtureB, boolean beginContact, MyGame myGame) {
        if (!beginContact) return;
        Fixture laserProjectileFixture = null;
        Fixture contactFixture = null;
        Fixture[] collision = Listener.checkContact(fixtureA, fixtureB, Laser.class);
        if (collision[0] != null) {
            laserProjectileFixture = collision[0];
            contactFixture = collision[1];
        }
        if (laserProjectileFixture != null) {
            Laser l  = (Laser) laserProjectileFixture.getUserData();
            if (l.body == null && !(contactFixture.getUserData() instanceof Laser || contactFixture.getUserData() instanceof BodyWeapon)) {
                System.out.println(contactFixture.getUserData());
                l.createBody();
            }
        }
    }
}
