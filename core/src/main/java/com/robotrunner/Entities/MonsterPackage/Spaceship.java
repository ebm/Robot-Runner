package com.robotrunner.Entities.MonsterPackage;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.robotrunner.Entities.Healthbar;
import com.robotrunner.Items.Armor;
import com.robotrunner.States.MyGame;
import com.robotrunner.Weapons.BodyWeapon;
import com.robotrunner.Weapons.RangedWeapon;
import com.robotrunner.Weapons.Weapon;

import static com.robotrunner.World.Constants.*;

public class Spaceship extends Monster {
    Weapon weapon;
    double directionTime;
    double lastSwitch;
    int multiplier;
    boolean activated;
    boolean follow;
    boolean dodge;
    double lastDodge;
    double dodgeDelay;
    int dodgeDirection;
    TextureRegion spaceshipTextureRegion;
    public Spaceship(MyGame myGame, float initialX, float initialY) {
        super(myGame, SPACESHIP_HEALTH);
        healthbar = new Healthbar(myGame, this, SPACESHIP_HEALTH);

        weapon = new RangedWeapon(myGame, this, 15, 80, 0.5f, 1, null);
        createBody(initialX, initialY);
        lastSwitch = 0;
        multiplier = 1;
        lastDodge = 0;
        dodgeDelay = 0;
        dodgeDirection = 1;
        activated = false;
        follow = true;
        dodge = false;
        spaceshipTextureRegion = new TextureRegion((Texture) myGame.assetManager.get("spaceship.png"));
    }
    public void createBody(float initialX, float initialY) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        body = myGame.world.createBody(bodyDef);
        //body.setUserData(this);
        PolygonShape polygon = new PolygonShape();
        polygon.set(new Vector2[] {new Vector2(-SPACESHIP_WIDTH / 2, -SPACESHIP_HEIGHT / 2), new Vector2(SPACESHIP_WIDTH / 2, -SPACESHIP_HEIGHT / 2),
                new Vector2(-SPACESHIP_WIDTH / 2, SPACESHIP_HEIGHT / 2), new Vector2(SPACESHIP_WIDTH / 2, SPACESHIP_HEIGHT / 2)});

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygon;
        fixtureDef.density = 1000f;
        fixtureDef.friction = 100f;
        fixtureDef.restitution = 0f;

        body.createFixture(fixtureDef).setUserData(this);

        body.setFixedRotation(true);
        body.setGravityScale(0);

        body.setTransform(initialX, initialY, 0);
    }

    @Override
    public Vector2 getBodyCenter() {
        //return new Vector2(body.getPosition().x + SPACESHIP_WIDTH / 2, body.getPosition().y + SPACESHIP_HEIGHT / 2);
        return body.getPosition();
    }

    @Override
    public void update() {
        super.update();
        if (myGame.timePassed - lastSwitch > 1) {
            body.setLinearVelocity(body.getLinearVelocity().x, SPACESHIP_VERTICAL_VELOCITY * multiplier);
            multiplier *= -1;
            lastSwitch = myGame.timePassed;
        }
        if (activated) {
            if (follow || Math.abs(myGame.player.getBodyCenter().x - getBodyCenter().x) > 10) {
                if (myGame.player.getBodyCenter().x > getBodyCenter().x) {
                    body.setLinearVelocity(SPACESHIP_VELOCITY, body.getLinearVelocity().y);
                    float angle = -15 * (float) Math.PI / 180;
                    if (body.getAngle() > angle) body.setAngularVelocity(-2);
                    else body.setAngularVelocity(0);
                } else {
                    body.setLinearVelocity(-SPACESHIP_VELOCITY, body.getLinearVelocity().y);
                    float angle = 15 * (float) Math.PI / 180;
                    if (body.getAngle() < angle) body.setAngularVelocity(2);
                    else body.setAngularVelocity(0);
                }
                if (Math.abs(myGame.player.getBodyCenter().x - getBodyCenter().x) < 2) {
                    follow = false;
                    body.setLinearVelocity(0, body.getLinearVelocity().y);
                    if (body.getAngle() > 0) {
                        body.setAngularVelocity(-4);
                    } else {
                        body.setAngularVelocity(4);
                    }
                } else {
                    follow = true;
                    dodge = false;
                }
            }
            if (!follow) {
                if (myGame.timePassed - lastDodge > dodgeDelay) {
                    dodgeDelay = myGame.rand.nextDouble() % 1;
                    if (myGame.rand.nextInt() % 2 == 0) dodgeDirection *= -1;
                    body.setLinearVelocity(SPACESHIP_VELOCITY * dodgeDirection, body.getLinearVelocity().y);
                    lastDodge = myGame.timePassed;
                }
                float angle = -dodgeDirection * 15 * (float) Math.PI / 180;
                //System.out.println("Body angle: " + Math.toDegrees(body.getAngle()) + ", Desired angle: " + Math.toDegrees(angle));
                //body.setAngularVelocity(angle);
                if (angle < 0) {
                    if (body.getAngle() > angle) body.setAngularVelocity(-4);
                    else body.setAngularVelocity(0);
                } else {
                    if (body.getAngle() < angle) body.setAngularVelocity(4);
                    else body.setAngularVelocity(0);
                }
                dodge = true;
            }
            //if (!dodge && !follow && ((body.getAngle() > 0 && body.getAngularVelocity() > 0) || (body.getAngle() < 0 && body.getAngularVelocity() < 0))) {
            //    body.setAngularVelocity(0);
            //}
            //if (body.getAngle() == 0 && !follow) body.setAngularVelocity(0);
            if (myGame.rand.nextInt() % 3 != 0) ((RangedWeapon) weapon).attackWithAimbot(myGame.player);
            else ((RangedWeapon) weapon).attack(myGame.player.getBodyCenter());
        } else if (getDistance(getBodyCenter(), myGame.player.getBodyCenter()) < SPACESHIP_ACTIVATION_RANGE) {
            activated = true;
        }

    }
    @Override
    public void death() {
        super.death();
        myGame.activeMonsters.remove(this);
    }

    @Override
    public void render() {
        weapon.render();
        //myGame.batch.draw(myGame.spaceship, body.getPosition().x - SPACESHIP_WIDTH / 2, body.getPosition().y - SPACESHIP_HEIGHT / 2, SPACESHIP_WIDTH, SPACESHIP_HEIGHT);
        myGame.batch.draw(spaceshipTextureRegion, body.getPosition().x - SPACESHIP_WIDTH / 2, body.getPosition().y - SPACESHIP_HEIGHT / 2, SPACESHIP_WIDTH / 2, SPACESHIP_HEIGHT / 2, SPACESHIP_WIDTH, SPACESHIP_HEIGHT, 1, 1, body.getAngle() * (float) (180 / Math.PI));
        myGame.batch.draw(healthbar.getHealthBar(), body.getPosition().x - SPACESHIP_WIDTH / 2, body.getPosition().y - SPACESHIP_HEIGHT / 2 + SPACESHIP_HEIGHT + HEALTHBAR_OFFSET, SPACESHIP_WIDTH, HEALTHBAR_HEIGHT);

    }
}
