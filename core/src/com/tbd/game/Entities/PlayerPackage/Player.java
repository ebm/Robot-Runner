package com.tbd.game.Entities.PlayerPackage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.tbd.game.Entities.Entity;
import com.tbd.game.Entities.Healthbar;
import com.tbd.game.Entities.MonsterPackage.Monster;
import com.tbd.game.Listener;
import com.tbd.game.MyGame;
import com.tbd.game.Weapons.RangedWeapon;
import com.tbd.game.Weapons.Weapon;

import static com.tbd.game.Constants.*;

public class Player extends Entity {
    int contactFeet;
    int contactLeftArm;
    int contactRightArm;
    Healthbar healthbar;
    public boolean touchingFloor;
    public boolean touchingRightWall;
    public boolean touchingLeftWall;
    int remainingJumps;
    boolean canJump;
    double wallClimbTime;
    boolean wallClimbFinished;
    double dashTime;
    boolean dashFinished;
    double lastDash;
    double combatTimer;
    //float dashXVelocity;
    //float dashYVelocity;
    PlayerState currentState;
    Animation<TextureRegion> walkingLeft;
    Animation<TextureRegion> walkingRight;
    Animation<TextureRegion> still;
    float timePassed;
    Weapon weapon;

    public Player(MyGame myGame) {
        this.myGame = myGame;
        touchingFloor = false;
        touchingRightWall = false;
        touchingLeftWall = false;
        canJump = false;
        wallClimbFinished = false;
        wallClimbTime = 0;
        dashFinished = false;
        dashTime = 0;
        lastDash = 0;
        combatTimer = 0;

        currentState = PlayerState.Still;
        weapon = new RangedWeapon(myGame, this, 20, 3, 8, 0.2f * METERS_PER_PIXEL, myGame.playerFireNoise);

        // Create Body
        createBody(PLAYER_INITIAL_X_POSITION, PLAYER_INITIAL_Y_POSITION);
        // Create Sensors
        createFeet();
        createLeftArm();
        createRightArm();

        createAnimations();

        health = PLAYER_HEALTH;
        friendly = Player.class;
        enemy = Monster.class;
        healthbar = new Healthbar(myGame, this, health);
    }

    public Player(MyGame myGame, float initialX, float initialY) {
        this.myGame = myGame;
        touchingFloor = false;
        touchingRightWall = false;
        touchingLeftWall = false;
        canJump = false;
        wallClimbFinished = false;
        wallClimbTime = 0;
        dashFinished = false;
        dashTime = 0;
        lastDash = 0;
        combatTimer = 0;

        currentState = PlayerState.Still;
        weapon = new RangedWeapon(myGame, this, 20, 3, 8, 0.2f * METERS_PER_PIXEL, myGame.playerFireNoise);

        // Create Body
        createBody(initialX, initialY);
        // Create Sensors
        createFeet();
        createLeftArm();
        createRightArm();

        createAnimations();

        health = PLAYER_HEALTH;
        friendly = Player.class;
        enemy = Monster.class;
        healthbar = new Healthbar(myGame, this, health);
    }

    private void createBody(float initialX, float initialY) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        //bodyDef.position.set(INITIAL_X_POSITION, INITIAL_Y_POSITION);

        body = myGame.world.createBody(bodyDef);

        float bottomHeight = PLAYER_HITBOX_HEIGHT / 20;
        float bottomHorizontalHeight = PLAYER_HITBOX_WIDTH / 3;

        float topHeight = PLAYER_HITBOX_HEIGHT / 5;
        float verticalWidth = PLAYER_HITBOX_WIDTH / 20;
        PolygonShape polygon = new PolygonShape();
        System.out.println(topHeight + "," + verticalWidth + "," + bottomHorizontalHeight + "," + bottomHeight);
        polygon.set(new Vector2[] {new Vector2(0, bottomHeight), new Vector2(0, PLAYER_HITBOX_HEIGHT - topHeight), new Vector2(verticalWidth, PLAYER_HITBOX_HEIGHT), new Vector2(PLAYER_HITBOX_WIDTH - verticalWidth, PLAYER_HITBOX_HEIGHT), new Vector2(PLAYER_HITBOX_WIDTH, PLAYER_HITBOX_HEIGHT - topHeight), new Vector2(PLAYER_HITBOX_WIDTH, bottomHeight), new Vector2(PLAYER_HITBOX_WIDTH - bottomHorizontalHeight,0), new Vector2(0 + bottomHorizontalHeight,0)});

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygon;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;

        body.createFixture(fixtureDef).setUserData(this);

        polygon.dispose();
        body.setFixedRotation(true);
        //body.setSleepingAllowed(false);

        body.setTransform(initialX, initialY, 0);
    }

    private void createFeet() {
        PolygonShape shape = new PolygonShape();
        shape.set(new float[] {PLAYER_APPENDAGE_DISTANCE, 0, PLAYER_APPENDAGE_DISTANCE, -PLAYER_APPENDAGE_DISTANCE, PLAYER_HITBOX_WIDTH - PLAYER_APPENDAGE_DISTANCE, 0, PLAYER_HITBOX_WIDTH - PLAYER_APPENDAGE_DISTANCE, -PLAYER_APPENDAGE_DISTANCE});

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef).setUserData(BodyPart.Feet);

        shape.dispose();
    }
    private void createLeftArm() {
        PolygonShape shape = new PolygonShape();
        shape.set(new float[] {-PLAYER_APPENDAGE_DISTANCE, PLAYER_APPENDAGE_DISTANCE, -PLAYER_APPENDAGE_DISTANCE, PLAYER_HITBOX_HEIGHT - PLAYER_APPENDAGE_DISTANCE, 0, PLAYER_HITBOX_HEIGHT - PLAYER_APPENDAGE_DISTANCE, 0, PLAYER_APPENDAGE_DISTANCE});

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef).setUserData(BodyPart.LeftArm);

        shape.dispose();
    }
    private void createRightArm() {
        PolygonShape shape = new PolygonShape();
        shape.set(new float[] {PLAYER_HITBOX_WIDTH, PLAYER_APPENDAGE_DISTANCE, PLAYER_HITBOX_WIDTH, PLAYER_HITBOX_HEIGHT - PLAYER_APPENDAGE_DISTANCE, PLAYER_HITBOX_WIDTH + PLAYER_APPENDAGE_DISTANCE, PLAYER_HITBOX_HEIGHT - PLAYER_APPENDAGE_DISTANCE, PLAYER_HITBOX_WIDTH + PLAYER_APPENDAGE_DISTANCE, PLAYER_APPENDAGE_DISTANCE});

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef).setUserData(BodyPart.RightArm);

        shape.dispose();
    }
    private void createAnimations() {
        TextureRegion[] walkingLeftFrames = {myGame.atlas.findRegion("left1"), myGame.atlas.findRegion("left2"),
                myGame.atlas.findRegion("left3"), myGame.atlas.findRegion("left4")};
        walkingLeft = new Animation<>(0.08f, walkingLeftFrames);

        TextureRegion[] walkingRightFrames = {myGame.atlas.findRegion("right1"), myGame.atlas.findRegion("right2"),
                myGame.atlas.findRegion("right3"), myGame.atlas.findRegion("right4")};
        walkingRight = new Animation<>(0.08f, walkingRightFrames);

        still = new Animation<>(0.08f, myGame.atlas.findRegion("still"));

        timePassed = 0;
    }
    @Override
    public Vector2 getBodyCenter() {
        return new Vector2(body.getPosition().x - PLAYER_HORIZONTAL_OFFSET + PLAYER_SPRITE_WIDTH / 2, body.getPosition().y - PLAYER_VERTICAL_OFFSET + PLAYER_SPRITE_HEIGHT / 2);
    }

    public void update() {
        currentState = PlayerState.Still;
        // ON GROUND
        if (touchingFloor) {
            body.setLinearVelocity(0, body.getLinearVelocity().y);
            remainingJumps = PLAYER_MAXIMUM_JUMPS;
            wallClimbFinished = false;
            wallClimbTime = 0;
            //canJump = true; // enable this to allow user to hold spacebar to continuously jump
        }
        // LEFT
        if (Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (touchingFloor) {
                body.setLinearVelocity(-PLAYER_HORIZONTAL_VELOCITY, body.getLinearVelocity().y);
            } else {
                if (body.getLinearVelocity().x - PLAYER_HORIZONTAL_AIR_ACCELERATION > -PLAYER_MAXIMUM_HORIZONTAL_AIR_VELOCITY) {
                    body.setLinearVelocity(body.getLinearVelocity().x - PLAYER_HORIZONTAL_AIR_ACCELERATION, body.getLinearVelocity().y);
                }
            }
            currentState = PlayerState.WalkingLeft;
        }
        // RIGHT
        if (Gdx.input.isKeyPressed(Input.Keys.D) && !Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (touchingFloor) {
                body.setLinearVelocity(PLAYER_HORIZONTAL_VELOCITY, body.getLinearVelocity().y);
            } else {
                if (body.getLinearVelocity().x + PLAYER_HORIZONTAL_AIR_ACCELERATION < PLAYER_MAXIMUM_HORIZONTAL_AIR_VELOCITY) {
                    body.setLinearVelocity(body.getLinearVelocity().x + PLAYER_HORIZONTAL_AIR_ACCELERATION, body.getLinearVelocity().y);
                }
            }
            currentState = PlayerState.WalkingRight;
        }
        // JUMP
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && remainingJumps >= 1 && canJump && (wallClimbTime == 0 || wallClimbFinished)) {
            body.setTransform(body.getPosition().x, body.getPosition().y + UNIT_SCALE, 0);
            body.setLinearVelocity(body.getLinearVelocity().x, PLAYER_JUMP_VELOCITY);
            if (currentState == PlayerState.WalkingLeft) {
                body.setLinearVelocity(-PLAYER_HORIZONTAL_VELOCITY, body.getLinearVelocity().y);
            } else if (currentState == PlayerState.WalkingRight) {
                body.setLinearVelocity(PLAYER_HORIZONTAL_VELOCITY, body.getLinearVelocity().y);
            }
            remainingJumps--;
            canJump = false;
        } else if (!Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            canJump = true;
        }
        // WALLCLIMB
        if (Gdx.input.isKeyPressed(Input.Keys.W) && !wallClimbFinished) {
            // Ensure player is walking into wall
            if ((currentState == PlayerState.WalkingLeft && touchingLeftWall || currentState == PlayerState.WalkingRight && touchingRightWall) &&
                    // Ensure player has not exceeded climbing wall time
                    (wallClimbTime == 0 || (myGame.timePassed - wallClimbTime) < PLAYER_WALLCLIMB_LENGTH_SECONDS)) {
                if (wallClimbTime == 0) wallClimbTime = myGame.timePassed;

                body.setLinearVelocity(body.getLinearVelocity().x, PLAYER_WALLCLIMB_VELOCITY);
            } else {
                if (wallClimbTime != 0) wallClimbFinished = true;
            }
        } else if (!wallClimbFinished) {
            if (wallClimbTime != 0) wallClimbFinished = true;
        }
        // Dash
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && (myGame.timePassed - dashTime) > 3) {
            float dashXVelocity = body.getLinearVelocity().x;
            float dashYVelocity = PLAYER_DASH_VERTICAL_VELOCITY;
            if (currentState == PlayerState.WalkingLeft) {
                dashXVelocity = -PLAYER_DASH_HORIZONTAL_VELOCITY;
            } else if (currentState == PlayerState.WalkingRight) {
                dashXVelocity = PLAYER_DASH_HORIZONTAL_VELOCITY;
            }
            body.setTransform(body.getPosition().x, body.getPosition().y + UNIT_SCALE, 0);
            body.setLinearVelocity(dashXVelocity, dashYVelocity);
            dashTime = myGame.timePassed;
        }

        // Fire
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            weapon.attack(new Vector2(myGame.getMousePosition().x, myGame.getMousePosition().y));
            combatTimer = myGame.timePassed;
        }
    }
    @Override
    public void render() {
        weapon.render();
        if (health < PLAYER_HEALTH && (myGame.timePassed - combatTimer) > PLAYER_SECONDS_TILL_REGEN) {
            health += Gdx.graphics.getDeltaTime() * PLAYER_HEALTH_REGEN_PER_SEC;
            if (health >= 100) health = 100;
        }
        timePassed += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame;
        if (currentState == PlayerState.WalkingLeft) {
            currentFrame = walkingLeft.getKeyFrame(timePassed, true);
        } else if (currentState == PlayerState.WalkingRight) {
            currentFrame = walkingRight.getKeyFrame(timePassed, true);
        } else {
            currentFrame = still.getKeyFrame(timePassed, true);
        }
        //if (touchingFloor) myGame.batch.draw(myGame.shadow, body.getPosition().x - HORIZONTAL_OFFSET, body.getPosition().y - VERTICAL_OFFSET - 5 * UNIT_SCALE, 32 * UNIT_SCALE, 12 * UNIT_SCALE);
        myGame.batch.draw(currentFrame, body.getPosition().x - PLAYER_HORIZONTAL_OFFSET, body.getPosition().y - PLAYER_VERTICAL_OFFSET, PLAYER_SPRITE_WIDTH, PLAYER_SPRITE_HEIGHT);
        myGame.batch.draw(healthbar.getHealthBar(), body.getPosition().x - PLAYER_HORIZONTAL_OFFSET, body.getPosition().y + PLAYER_HITBOX_HEIGHT + HEALTHBAR_OFFSET, PLAYER_SPRITE_WIDTH, HEALTHBAR_HEIGHT);
    }
    @Override
    public void takeDamage(float damage) {
        super.takeDamage(damage);
        combatTimer = myGame.timePassed;
    }
    @Override
    public void death() {
        super.death();
        myGame.listener.resetContacts();
        myGame.player = new Player(myGame);
    }
    public static void handleContact(Fixture fixtureA, Fixture fixtureB, boolean beginContact, MyGame myGame) {
        BodyPart bp = null;
        Fixture[] collision = Listener.checkContact(fixtureA, fixtureB, BodyPart.class);
        if (collision[0] != null) {
            bp = (BodyPart) collision[0].getUserData();
        }
        if (fixtureA.isSensor() && fixtureB.isSensor()) return;
        if (bp != null) {
            if (bp == BodyPart.Feet) {
                if (beginContact) {
                    myGame.player.touchingFloor = true;
                    myGame.player.contactFeet++;
                } else {
                    myGame.player.contactFeet--;
                    if (myGame.player.contactFeet == 0) myGame.player.touchingFloor = false;
                }
            }
            if (bp == BodyPart.LeftArm) {
                if (beginContact) {
                    myGame.player.touchingLeftWall = true;
                    myGame.player.contactLeftArm++;
                } else {
                    myGame.player.contactLeftArm--;
                    if (myGame.player.contactLeftArm == 0) myGame.player.touchingLeftWall = false;
                }
            }
            if (bp == BodyPart.RightArm) {
                if (beginContact) {
                    myGame.player.touchingRightWall = true;
                    myGame.player.contactRightArm++;
                } else {
                    myGame.player.contactRightArm--;
                    if (myGame.player.contactRightArm == 0) myGame.player.touchingRightWall = false;
                }
            }
        }
    }
}
