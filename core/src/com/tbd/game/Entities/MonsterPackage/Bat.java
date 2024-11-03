package com.tbd.game.Entities.MonsterPackage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.tbd.game.Weapons.BodyWeapon;
import com.tbd.game.Entities.Healthbar;
import com.tbd.game.MyGame;
import com.tbd.game.Weapons.Weapon;

import static com.tbd.game.Constants.*;

public class Bat extends Monster {
    Weapon weapon;
    Healthbar healthbar;
    float currentVerticalVelocityAdder;
    double lastVerticalChange;
    Animation<Texture> batAnimation;
    float timePassed;
    boolean asleep;
    public Bat(MyGame myGame) {
        super(myGame, BAT_HEALTH);
        healthbar = new Healthbar(myGame, this, BAT_HEALTH);

        createBody(BAT_INITIAL_X_POSITION, BAT_INITIAL_Y_POSITION);

        CircleShape shape = new CircleShape();
        shape.setRadius(BAT_WEAPON_RADIUS);
        weapon = new BodyWeapon(myGame, this, BAT_ATTACK_DAMAGE, shape);
        weapon.attack(body.getPosition());
        currentVerticalVelocityAdder = BAT_VERTICAL_VELOCITY;
        lastVerticalChange = myGame.timePassed;
        asleep = false;
        createAnimations();
    }
    public Bat(MyGame myGame, float initialX, float initialY) {
        super(myGame, BAT_HEALTH);
        healthbar = new Healthbar(myGame, this, BAT_HEALTH);

        createBody(initialX, initialY);

        CircleShape shape = new CircleShape();
        shape.setRadius(BAT_WEAPON_RADIUS);
        weapon = new BodyWeapon(myGame, this, BAT_ATTACK_DAMAGE, shape);
        weapon.attack(body.getPosition());
        currentVerticalVelocityAdder = BAT_VERTICAL_VELOCITY;
        lastVerticalChange = myGame.timePassed;
        asleep = false;
        createAnimations();
    }
    private void createBody(float initialX, float initialY) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        body = myGame.world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(BAT_RADIUS);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.restitution = 1f;
        body.setGravityScale(0);

        body.createFixture(fixtureDef).setUserData(this);

        shape.dispose();
        body.setFixedRotation(true);

        body.setTransform(initialX, initialY, 0);
    }
    public void createAnimations() {
        Texture[] batFrames = {myGame.bat1, myGame.bat2, myGame.bat3};
        batAnimation = new Animation<>(BAT_VERTICAL_DELAY / batFrames.length, batFrames);
        timePassed = 0;
    }

    @Override
    public Vector2 getBodyCenter() {
        return body.getPosition();
    }

    @Override
    public void update() {
        weapon.render();
        if (getDistance(getBodyCenter(), myGame.player.getBodyCenter()) > BAT_ACTIVATION_RANGE) {
            body.setLinearVelocity(0, 0);
            asleep = true;
            return;
        }
        if ((myGame.timePassed - lastVerticalChange) >= BAT_VERTICAL_DELAY) {
            currentVerticalVelocityAdder *= -1;
            lastVerticalChange = myGame.timePassed;
        }
        Vector2 target = myGame.player.getBodyCenter();
        float diffX = target.x - body.getPosition().x;
        float diffY = target.y - body.getPosition().y;
        float hypotenuse = (float) Math.sqrt(diffX * diffX + diffY * diffY);

        body.setLinearVelocity(BAT_VELOCITY / hypotenuse * diffX,currentVerticalVelocityAdder + BAT_VELOCITY / hypotenuse * diffY);
        weapon.attack(body.getPosition());
        asleep = false;
    }

    @Override
    public void render() {
        if (!asleep) timePassed += Gdx.graphics.getDeltaTime();
        //myGame.batch.draw(myGame.bat, body.getPosition().x - BAT_RADIUS, body.getPosition().y - BAT_RADIUS, 2 * BAT_RADIUS, 2 * BAT_RADIUS);
        myGame.batch.draw(batAnimation.getKeyFrame(timePassed, true), body.getPosition().x - BAT_RADIUS, body.getPosition().y - BAT_RADIUS, 2 * BAT_RADIUS, 2 * BAT_RADIUS);
        myGame.batch.draw(healthbar.getHealthBar(), body.getPosition().x - BAT_RADIUS, body.getPosition().y + BAT_RADIUS + HEALTHBAR_OFFSET, 2 * BAT_RADIUS, HEALTHBAR_HEIGHT);
    }
    @Override
    public void death() {
        super.death();
        myGame.world.destroyBody(((BodyWeapon) weapon).body);
        myGame.activeMonsters.remove(this);
    }
}
