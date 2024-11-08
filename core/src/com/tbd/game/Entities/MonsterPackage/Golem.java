package com.tbd.game.Entities.MonsterPackage;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.tbd.game.Items.Armor;
import com.tbd.game.Entities.Healthbar;
import com.tbd.game.States.MyGame;
import com.tbd.game.Weapons.BodyWeapon;
import com.tbd.game.Weapons.RangedWeapon;
import com.tbd.game.Weapons.Weapon;

import static com.tbd.game.World.Constants.*;

public class Golem extends Monster{
    Weapon weapon;
    double lastJump;
    double directionTime;
    Healthbar healthbar;
    public Golem(MyGame myGame, float initialX, float initialY, String range) {
        super(myGame, GOLEM_HEALTH);
        healthbar = new Healthbar(myGame, this, GOLEM_HEALTH);

        Shape shape = createShape();
        createBody(initialX, initialY, shape);
        weapon = new BodyWeapon(myGame, this, GOLEM_ATTACK_DAMAGE, shape, GOLEM_ATTACK_COOLDOWN);

        if (range != null) this.range = new Range(range, this);

        lastJump = 0;
        directionTime = 0;
    }
    public Shape createShape() {
        PolygonShape polygon = new PolygonShape();
        polygon.set(new Vector2[] {new Vector2(0, 0), new Vector2(0, GOLEM_HITBOX_HEIGHT), new Vector2(GOLEM_HITBOX_WIDTH, GOLEM_HITBOX_HEIGHT), new Vector2(GOLEM_HITBOX_WIDTH, 0)});
        return polygon;
    }
    public void createBody(float initialX, float initialY, Shape shape) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        body = myGame.world.createBody(bodyDef);
        //body.setUserData(this);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1000f;
        fixtureDef.friction = 100f;
        fixtureDef.restitution = 0f;

        body.createFixture(fixtureDef).setUserData(this);

        body.setFixedRotation(true);

        body.setTransform(initialX, initialY, 0);
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
            double timeFromLastJump = (myGame.timePassed - lastJump);
            if (timeFromLastJump > 1) {
                weapon.attack(body.getPosition());
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
                if (timeFromLastJump > GOLEM_JUMP_COOLDOWN) {
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
        myGame.batch.draw(myGame.golem, body.getPosition().x, body.getPosition().y, GOLEM_HITBOX_WIDTH, GOLEM_HITBOX_HEIGHT);
        myGame.batch.draw(healthbar.getHealthBar(), body.getPosition().x, body.getPosition().y + GOLEM_HITBOX_HEIGHT + HEALTHBAR_OFFSET, GOLEM_HITBOX_WIDTH, HEALTHBAR_HEIGHT);
    }

    @Override
    public Vector2 getBodyCenter() {
        return new Vector2(body.getPosition().x + GOLEM_HITBOX_WIDTH / 2, body.getPosition().y + GOLEM_HITBOX_HEIGHT / 2);
    }
    @Override
    public void death() {
        super.death();
        myGame.itemMapManager.addItem(new Armor(0.8f, myGame.itemMapManager.getID(), getBodyCenter().x, getBodyCenter().y, myGame.rockArmor, myGame));
        myGame.activeMonsters.remove(this);
    }
}
