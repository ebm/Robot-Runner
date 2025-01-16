package com.robotrunner.Entities.MonsterPackage;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.robotrunner.Items.Armor;
import com.robotrunner.Entities.Healthbar;
import com.robotrunner.States.MyGame;
import com.robotrunner.Weapons.BodyWeapon;
import com.robotrunner.Weapons.RangedWeapon;
import com.robotrunner.Weapons.Weapon;

import static com.robotrunner.World.Constants.*;

public class Golem extends Monster{
    Weapon weapon;
    double lastJump;
    double directionTime;
    boolean activated;
    double time = 2*GOLEM_JUMP_VELOCITY/(-GRAVITY);
    public Golem(MyGame myGame, float initialX, float initialY, String range) {
        super(myGame, GOLEM_HEALTH);
        healthbar = new Healthbar(myGame, this, GOLEM_HEALTH);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1000f;
        fixtureDef.friction = 100f;
        fixtureDef.restitution = 0f;
        createBody(GOLEM_HITBOX_WIDTH, GOLEM_HITBOX_HEIGHT, fixtureDef);
        body.setTransform(initialX, initialY, 0);

        PolygonShape bodyWeaponShape = new PolygonShape();
        float offset = 0.1f;
        bodyWeaponShape.set(new Vector2[] {new Vector2(-offset, -offset), new Vector2(-offset, GOLEM_HITBOX_HEIGHT + offset),
                new Vector2(GOLEM_HITBOX_WIDTH + offset, GOLEM_HITBOX_HEIGHT + offset), new Vector2(GOLEM_HITBOX_WIDTH + offset, -offset)});
        weapon = new BodyWeapon(myGame, this, GOLEM_ATTACK_DAMAGE, bodyWeaponShape, GOLEM_ATTACK_COOLDOWN);

        if (range != null) this.range = new Range(range, this);

        lastJump = 0;
        directionTime = 0;
        weapon.attack(body.getPosition());
    }
    public Vector2 calculateTrajectory(Vector2 objectPosition, Vector2 objectVelocity, Vector2 objectAcceleration, Vector2 projectilePosition, double verticalVelocity, Vector2 projectileAcceleration) {
        double n = verticalVelocity;
        double p = objectAcceleration.y - projectileAcceleration.y;
        double x = objectPosition.x - projectilePosition.x;
        double y = objectPosition.y - projectilePosition.y;
        double a = objectVelocity.x;
        double b = objectVelocity.y;

        double t = quadratic(p/2, b-n, y, false);
        double m = x/t+a;
        if (m > 0) {
            double maxVelocity = Math.min((range.xMax-projectilePosition.x)/t, GOLEM_MAXIMUM_HORIZONTAL_JUMP_VELOCITY);
            m = Math.min(m, maxVelocity);
        } else {
            double minVelocity = Math.max((range.xMin-projectilePosition.x)/t, -GOLEM_MAXIMUM_HORIZONTAL_JUMP_VELOCITY);
            m = Math.max(m, minVelocity);
        }
        return new Vector2(new Vector2((float) m, (float) n));
    }
    public Vector2 calculateTrajectoryLite(Vector2 objectPosition, Vector2 objectVelocity, Vector2 objectAcceleration, Vector2 projectilePosition, double verticalVelocity, Vector2 projectileAcceleration) {
        double n = verticalVelocity;
        double x = objectPosition.x - projectilePosition.x;
        double a = objectVelocity.x;

        double t = time;
        double m = x/t+a;
        if (m > 0) {
            double maxVelocity = Math.min((range.xMax-projectilePosition.x)/t, GOLEM_MAXIMUM_HORIZONTAL_JUMP_VELOCITY);
            System.out.println(m + ", " + (range.xMax-projectilePosition.x)/t + ", " + GOLEM_MAXIMUM_HORIZONTAL_JUMP_VELOCITY);
            m = Math.min(m, maxVelocity);
        } else {
            double minVelocity = Math.max((range.xMin-projectilePosition.x)/t, -GOLEM_MAXIMUM_HORIZONTAL_JUMP_VELOCITY);
            m = Math.max(m, minVelocity);
        }
        System.out.println("VELOCITY: " + m + ", TIME: " + time);

        return new Vector2(new Vector2((float) m, (float) n));
    }
    public void update() {
        super.update();
        if (activated || (Math.abs(body.getPosition().x - myGame.player.body.getPosition().x) < GOLEM_ACTIVATION_RANGE_X &&
            Math.abs(body.getPosition().y - myGame.player.body.getPosition().y) < GOLEM_ACTIVATION_RANGE_Y) || health < GOLEM_HEALTH) {
            body.setActive(true);
            weapon.attack(body.getPosition());
            if (contactFeet >= 1) {
                if (getBodyCenter().x < myGame.player.getBodyCenter().x && body.getLinearVelocity().x <= 0 || getBodyCenter().x > myGame.player.getBodyCenter().x && body.getLinearVelocity().x >= 0) {
                    if (directionTime == 0) directionTime = myGame.timePassed;
                }
                if ((myGame.timePassed - directionTime) > GOLEM_DIRECTION_DELAY) {
                    directionTime = 0;
                    if (getBodyCenter().x < myGame.player.getBodyCenter().x) {
                        if (getBodyCenter().x < range.xMax) body.setLinearVelocity(GOLEM_HORIZONTAL_VELOCITY, body.getLinearVelocity().y);
                        else body.setLinearVelocity(0, body.getLinearVelocity().y);
                    } else {
                        if (getBodyCenter().x > range.xMin) body.setLinearVelocity(-GOLEM_HORIZONTAL_VELOCITY, body.getLinearVelocity().y);
                        else body.setLinearVelocity(0, body.getLinearVelocity().y);
                    }
                }
                if (myGame.timePassed - lastJump > GOLEM_JUMP_COOLDOWN) {
                    body.setTransform(body.getPosition().x, body.getPosition().y + UNIT_SCALE, 0);
                    Vector2 playerPos = new Vector2(myGame.player.getBodyCenter().x, body.getPosition().y);
                    Vector2 monsterPos = new Vector2(body.getPosition().x + GOLEM_HITBOX_WIDTH / 2, body.getPosition().y);
                    Vector2 playerAcceleration = new Vector2(0, 0);
                    Vector2 playerVelocity = myGame.player.body.getLinearVelocity();

                    Vector2 velocityVector = calculateTrajectoryLite(playerPos, playerVelocity, playerAcceleration, monsterPos, GOLEM_JUMP_VELOCITY, new Vector2(0, GRAVITY));
                    body.setLinearVelocity(velocityVector);
                    lastJump = myGame.timePassed;
                }
            }
            //weapon.attack(myGame.player.getBodyCenter());
        } else {
            body.setActive(false);
        }
    }

    @Override
    public void render() {
        weapon.render();
        myGame.batch.draw((Texture) myGame.assetManager.get("golem.png"), body.getPosition().x, body.getPosition().y, GOLEM_HITBOX_WIDTH, GOLEM_HITBOX_HEIGHT);
        myGame.batch.draw(healthbar.getHealthBar(), body.getPosition().x, body.getPosition().y + GOLEM_HITBOX_HEIGHT + HEALTHBAR_OFFSET, GOLEM_HITBOX_WIDTH, HEALTHBAR_HEIGHT);
    }

    @Override
    public Vector2 getBodyCenter() {
        return new Vector2(body.getPosition().x + GOLEM_HITBOX_WIDTH / 2, body.getPosition().y + GOLEM_HITBOX_HEIGHT / 2);
    }
    @Override
    public void death() {
        super.death();
        myGame.itemMapManager.addItem(new Armor(0.8f, myGame.itemMapManager.getID(), getBodyCenter().x, getBodyCenter().y, myGame.assetManager.get("rock_armor.png"), myGame));
        myGame.activeMonsters.remove(this);
    }
}
