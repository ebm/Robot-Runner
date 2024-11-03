package com.tbd.game.Weapons;


import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.tbd.game.Direction;
import com.tbd.game.Listener;
import com.tbd.game.MyGame;

import static com.tbd.game.Constants.*;


public class Laser {
    MyGame myGame;
    public Direction direction;
    public Body body;
    Body testBody;
    float initialPositionX;
    float initialPositionY;
    float width;
    float height;
    Sprite sprite;
    float laserDistance;
    double timeOfCreation;
    double lastSwitch;
    boolean active = false;
    public BodyWeapon weapon;
    public Laser(MyGame myGame, Direction direction, float initialX, float initialY) {
        this.myGame = myGame;
        this.direction = direction;
        body = null;

        createTestProjectile(initialX, initialY);
        //createBody(initialX, initialY);
        laserDistance = LASER_MAXIMUM_DISTANCE;
        timeOfCreation = myGame.timePassed;
        lastSwitch = 0;
    }
    public Laser(MyGame myGame, Direction direction, float initialX, float initialY, float laserDistance) {
        this.myGame = myGame;
        this.direction = direction;
        body = null;

        createTestProjectile(initialX, initialY);
        //createBody(initialX, initialY);
        this.laserDistance = laserDistance;
        timeOfCreation = myGame.timePassed;
        lastSwitch = 0;
    }
    public void createTestProjectile(float initialX, float initialY) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.gravityScale = 0;

        testBody = myGame.world.createBody(bodyDef);
        testBody.setUserData(this);

        CircleShape shape = new CircleShape();
        shape.setRadius(LASER_RADIUS);

        Fixture normalBody = testBody.createFixture(shape, 0.0f);
        //normalBody.setSensor(true);
        normalBody.setUserData(this);

        testBody.setBullet(true);

        shape.dispose();

        float xVelocity = 0;
        float yVelocity = 0;
        if (direction == Direction.Up) {
            initialPositionX = initialX + 0.5f * METERS_PER_PIXEL;
            initialPositionY = initialY + 1 * METERS_PER_PIXEL;
            initialX += 0.5f * METERS_PER_PIXEL;
            initialY += (LASER_RADIUS + (1 + 0.1f) * METERS_PER_PIXEL);
            yVelocity = LASER_TEST_PROJECTILE_VELOCITY;
        } else if (direction == Direction.Down) {
            initialPositionX = initialX + 0.5f * METERS_PER_PIXEL;
            initialPositionY = initialY;
            initialX += 0.5f * METERS_PER_PIXEL;
            initialY -= (LASER_RADIUS + (0.1f) * METERS_PER_PIXEL);
            yVelocity = -LASER_TEST_PROJECTILE_VELOCITY;
        } else if (direction == Direction.Left) {
            initialPositionX = initialX;
            initialPositionY = initialY + 0.5f * METERS_PER_PIXEL;
            initialX -= (LASER_RADIUS + (0.1f) * METERS_PER_PIXEL);
            initialY += 0.5f * METERS_PER_PIXEL;
            xVelocity = -LASER_TEST_PROJECTILE_VELOCITY;
        } else if (direction == Direction.Right) {
            initialPositionX = initialX + 1 * METERS_PER_PIXEL;
            initialPositionY = initialY + 0.5f * METERS_PER_PIXEL;
            initialX += (LASER_RADIUS + (0.1f + 1) * METERS_PER_PIXEL);
            initialY += 0.5f * METERS_PER_PIXEL;
            xVelocity = LASER_TEST_PROJECTILE_VELOCITY;
        }
        testBody.setTransform(initialX, initialY, 0);
        testBody.setLinearVelocity(xVelocity, yVelocity);
    }
    public void createBody() {
        width = 0;
        height = 0;
        if (direction == Direction.Up) {
            height = testBody.getPosition().y - initialPositionY + LASER_RADIUS;
            width = LASER_RADIUS * 2;
            initialPositionX -= LASER_RADIUS;

            sprite = new Sprite(myGame.laserVertical);
            sprite.setOrigin(width / 2, height / 2);
            sprite.setSize(width, height);
            sprite.setRotation(180);
        } else if (direction == Direction.Down) {
            height = initialPositionY - testBody.getPosition().y + LASER_RADIUS;
            width = LASER_RADIUS * 2;
            initialPositionX -= LASER_RADIUS;
            initialPositionY -= height;

            sprite = new Sprite(myGame.laserVertical);
            sprite.setOrigin(width / 2, height / 2);
            sprite.setSize(width, height);
            sprite.setRotation(0);
        } else if (direction == Direction.Left) {
            height = LASER_RADIUS * 2;
            width = initialPositionX - testBody.getPosition().x + LASER_RADIUS;
            initialPositionY -= LASER_RADIUS;
            initialPositionX -= width;

            sprite = new Sprite(myGame.laserHorizontal);
            sprite.setOrigin(width / 2, height / 2);
            sprite.setSize(width, height);
            sprite.setRotation(180);

            //sprite.setRotation(90);
        } else if (direction == Direction.Right) {
            height = LASER_RADIUS * 2;
            width = testBody.getPosition().x - initialPositionX + LASER_RADIUS;
            initialPositionY -= LASER_RADIUS;

            sprite = new Sprite(myGame.laserHorizontal);
            sprite.setOrigin(width / 2, height / 2);
            sprite.setSize(width, height);
            sprite.setRotation(0);
        }
        /*BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        body = myGame.world.createBody(bodyDef);
        //body.setUserData(this);

        if (width == 0 || height == 0) return;
        PolygonShape polygon = new PolygonShape();
        polygon.set(new Vector2[] {new Vector2(0, 0), new Vector2(0, height), new Vector2(width, height), new Vector2(width, 0)});
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygon;

        Fixture bodyFixture = body.createFixture(fixtureDef);
        bodyFixture.setUserData(this);
        bodyFixture.setSensor(true);

        polygon.dispose();
        body.setFixedRotation(true);

        body.setTransform(initialPositionX, initialPositionY, 0);

        myGame.world.destroyBody(testBody);*/

        if (width == 0 || height == 0) return;
        PolygonShape polygon = new PolygonShape();
        polygon.set(new Vector2[] {new Vector2(0, 0), new Vector2(0, height), new Vector2(width, height), new Vector2(width, 0)});
        weapon = new BodyWeapon(myGame, myGame.mapEntity, LASER_ATTACK_DAMAGE_PER_SECOND, polygon);

        body = weapon.body;

        body.setTransform(initialPositionX, initialPositionY, 0);

        myGame.world.destroyBody(testBody);
    }

    public void render() {
        if (body == null) {
            if (getDistance(new Vector2(initialPositionX, initialPositionY), testBody.getPosition()) > laserDistance) createBody();
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
            sprite.setPosition(body.getPosition().x, body.getPosition().y);
            sprite.draw(myGame.batch);
        } else {
            if (myGame.timePassed - lastSwitch > LASER_SAFE_SECONDS) {
                active = true;
                lastSwitch = myGame.timePassed;
                body.setActive(true);
                body.setAwake(true);
            }
        }
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
            if (l.body == null) {
                l.createBody();
            }
        }
    }
}
