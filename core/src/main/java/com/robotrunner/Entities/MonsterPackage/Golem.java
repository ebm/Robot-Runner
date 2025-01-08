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
    Healthbar healthbar;
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
    public float calculateTrajectory(float initialYVelocity, Vector2 monsterPosition, Vector2 playerPosition) {
        float initialYPosition = monsterPosition.y;
        float finalYPosition = playerPosition.y;

        float maximumPlayerPosition = -(initialYVelocity * initialYVelocity - 2 * GRAVITY * monsterPosition.y) / (2 * GRAVITY);
        //System.out.println("Maximum player pos: " + maximumPlayerPosition);

        //System.out.println("Monster position: " + monsterPosition + ", Player position: " + playerPosition + ", Gravity: " + gravity);
        if (maximumPlayerPosition < playerPosition.y) finalYPosition = maximumPlayerPosition;
        float time = (float) (-initialYVelocity - Math.sqrt(initialYVelocity * initialYVelocity + 2 * GRAVITY * finalYPosition - 2 * GRAVITY * initialYPosition)) / (GRAVITY);
        //System.out.println("Velocity: " + (playerPosition.x - monsterPosition.x) / time);

        float finalX = playerPosition.x;
        if (finalX < range.xMin) {
            finalX = range.xMin;
        } else if (finalX > range.xMax) {
            finalX = range.xMax;
        }
        return Math.min((finalX - monsterPosition.x) / time, GOLEM_MAXIMUM_HORIZONTAL_JUMP_VELOCITY);
    }
    public void update() {
        if (getDistance(body.getPosition(), myGame.player.body.getPosition()) < GOLEM_ACTIVATION_RANGE) {
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
                    Vector2 playerPos = new Vector2(myGame.player.getBodyCenter());
                    Vector2 monsterPos = new Vector2(body.getPosition().x + GOLEM_HITBOX_WIDTH / 2, body.getPosition().y);
                    body.setLinearVelocity(calculateTrajectory(GOLEM_JUMP_VELOCITY, monsterPos, playerPos), GOLEM_JUMP_VELOCITY);
                    lastJump = myGame.timePassed;
                }
            }
            //weapon.attack(myGame.player.getBodyCenter());
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
