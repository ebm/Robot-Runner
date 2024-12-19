package com.tbd.game.Entities.MonsterPackage;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.tbd.game.Entities.Healthbar;
import com.tbd.game.Items.Armor;
import com.tbd.game.States.MyGame;
import com.tbd.game.Weapons.BodyWeapon;
import com.tbd.game.Weapons.RangedWeapon;
import com.tbd.game.Weapons.Weapon;

import static com.tbd.game.World.Constants.*;

public class Spaceship extends Monster {
    Weapon weapon;
    double directionTime;
    double lastSwitch;
    int multiplier;
    boolean activated;
    boolean follow;
    Healthbar healthbar;
    TextureRegion spaceshipTextureRegion;
    public Spaceship(MyGame myGame, float initialX, float initialY) {
        super(myGame, SPACESHIP_HEALTH);
        healthbar = new Healthbar(myGame, this, SPACESHIP_HEALTH);

        weapon = new RangedWeapon(myGame, this, 15, 80, 0.5f, 1, null);
        createBody(initialX, initialY);
        lastSwitch = 0;
        multiplier = 1;
        activated = false;
        follow = true;
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
        if (myGame.timePassed - lastSwitch > 1) {
            body.setLinearVelocity(body.getLinearVelocity().x, SPACESHIP_VERTICAL_VELOCITY * multiplier);
            multiplier *= -1;
            lastSwitch = myGame.timePassed;
        }
        if (activated) {
            if (follow || Math.abs(myGame.player.getBodyCenter().x - getBodyCenter().x) > 10) {
                if (myGame.player.getBodyCenter().x > getBodyCenter().x) {
                    body.setLinearVelocity(SPACESHIP_VELOCITY, body.getLinearVelocity().y);
                    float angle = -10 * (float) Math.PI / 180;
                    if (body.getAngle() > angle) body.setAngularVelocity(-1);
                    else body.setAngularVelocity(0);
                } else {
                    body.setLinearVelocity(-SPACESHIP_VELOCITY, body.getLinearVelocity().y);
                    float angle = 10 * (float) Math.PI / 180;
                    if (body.getAngle() < angle) body.setAngularVelocity(1);
                    else body.setAngularVelocity(0);
                }
                if (Math.abs(myGame.player.getBodyCenter().x - getBodyCenter().x) < 2) {
                    follow = false;
                    body.setLinearVelocity(0, body.getLinearVelocity().y);
                    if (body.getAngle() > 0) {
                        body.setAngularVelocity(-2);
                    } else {
                        body.setAngularVelocity(2);
                    }
                } else follow = true;
            }
            if (!follow && ((body.getAngle() > 0 && body.getAngularVelocity() > 0) || (body.getAngle() < 0 && body.getAngularVelocity() < 0))) {
                body.setAngularVelocity(0);
            }
            //if (body.getAngle() == 0 && !follow) body.setAngularVelocity(0);
            if (myGame.rand.nextInt() % 2 == 0) ((RangedWeapon) weapon).attackWithAimbot(myGame.player);
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
