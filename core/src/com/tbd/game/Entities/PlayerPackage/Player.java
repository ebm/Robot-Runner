package com.tbd.game.Entities.PlayerPackage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.tbd.game.Entities.Entity;
import com.tbd.game.Entities.Healthbar;
import com.tbd.game.Entities.MonsterPackage.Monster;
import com.tbd.game.Items.Ability;
import com.tbd.game.World.Listener;
import com.tbd.game.States.MyGame;
import com.tbd.game.Weapons.RangedWeapon;
import com.tbd.game.Weapons.Weapon;

import static com.tbd.game.World.Constants.*;

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
    double lastDash;
    double combatTimer;
    //float dashXVelocity;
    //float dashYVelocity;
    public PlayerState currentState;
    Animation<TextureRegion> walkingLeft;
    Animation<TextureRegion> walkingRight;
    Animation<TextureRegion> still;
    float timePassed;
    Weapon weapon;
    public Label healthLabel;
    public Label abilityCooldownLabel;
    public Label combatLabel;
    public Inventory inventory;
    public boolean canOpenInventory;
    public float dmgTakenMultiplier;
    public float speedMultiplier;
    public float additionalHealth;
    TextureRegion gunTextureRegion;
    public float angle;
    boolean flip;
    public Player(MyGame myGame, float initialX, float initialY) {
        this.myGame = myGame;
        touchingFloor = false;
        touchingRightWall = false;
        touchingLeftWall = false;
        canJump = false;
        wallClimbFinished = false;
        wallClimbTime = 0;
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

        healthLabel = new Label("Health: " + (int) health + " / " + (int) PLAYER_HEALTH, myGame.labelStyle);
        abilityCooldownLabel = new Label("Cooldown: 0", myGame.labelStyle);
        combatLabel = new Label("Combat Timer: " + Math.max((int) Math.ceil((PLAYER_COMBAT_TIMER - (myGame.timePassed - combatTimer))), 0), myGame.labelStyle);

        myGame.table.add(combatLabel);
        myGame.table.add(abilityCooldownLabel).pad(5);
        myGame.table.pad(5);
        myGame.table.add(healthLabel);

        inventory = new Inventory(myGame);
        canOpenInventory = false;

        speedMultiplier = 1;
        dmgTakenMultiplier = 1;
        additionalHealth  = 0;
        gunTextureRegion = new TextureRegion(myGame.gun);
        flip = false;
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
    public void resetMultipliers() {
        speedMultiplier = 1;
        dmgTakenMultiplier = 1;
        additionalHealth  = 0;
    }
    public void update() {
        if (getBodyCenter().y < 0) death();
        currentState = PlayerState.Still;
        // ON GROUND
        if (touchingFloor) {
            body.setLinearVelocity(0, body.getLinearVelocity().y);
            remainingJumps = PLAYER_MAXIMUM_JUMPS;
            wallClimbFinished = false;
            wallClimbTime = 0;
            //canJump = true; // enable this to allow user to hold spacebar to continuously jump
        }
        if (inventory.open) {
            return;
        } else if (canOpenInventory && Gdx.input.isKeyPressed(Input.Keys.E)) {
            inventory.setOpen(true);
            canOpenInventory = false;
            return;
        }
        if (!Gdx.input.isKeyPressed(Input.Keys.E)) canOpenInventory = true;
        // LEFT
        if (Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (touchingFloor) {
                body.setLinearVelocity(-PLAYER_HORIZONTAL_VELOCITY * speedMultiplier, body.getLinearVelocity().y);
            } else {
                if (body.getLinearVelocity().x - PLAYER_HORIZONTAL_AIR_ACCELERATION > -PLAYER_MAXIMUM_HORIZONTAL_AIR_VELOCITY * speedMultiplier) {
                    body.setLinearVelocity(body.getLinearVelocity().x - PLAYER_HORIZONTAL_AIR_ACCELERATION, body.getLinearVelocity().y);
                }
            }
            currentState = PlayerState.WalkingLeft;
        }
        // RIGHT
        if (Gdx.input.isKeyPressed(Input.Keys.D) && !Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (touchingFloor) {
                body.setLinearVelocity(PLAYER_HORIZONTAL_VELOCITY * speedMultiplier, body.getLinearVelocity().y);
            } else {
                if (body.getLinearVelocity().x + PLAYER_HORIZONTAL_AIR_ACCELERATION < PLAYER_MAXIMUM_HORIZONTAL_AIR_VELOCITY * speedMultiplier) {
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
                body.setLinearVelocity(-PLAYER_HORIZONTAL_VELOCITY * speedMultiplier, body.getLinearVelocity().y);
            } else if (currentState == PlayerState.WalkingRight) {
                body.setLinearVelocity(PLAYER_HORIZONTAL_VELOCITY * speedMultiplier, body.getLinearVelocity().y);
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
        inventory.applyMultipliers();
        // Fire
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            weapon.attack(new Vector2(myGame.getMousePosition().x, myGame.getMousePosition().y));
            combatTimer = myGame.timePassed;
            if (myGame.getMousePosition().x > getBodyCenter().x) {
                currentState = PlayerState.ShootingRight;
                if (flip) gunTextureRegion.flip(true, false);
                flip = false;
            } else {
                currentState = PlayerState.ShootingLeft;
                if (!flip) gunTextureRegion.flip(true, false);
                flip = true;
            }
            angle = (float) Math.atan((getBodyCenter().y - myGame.getMousePosition().y) / (getBodyCenter().x - myGame.getMousePosition().x));
        }
    }
    @Override
    public void render() {
        weapon.render();
        if (inventory.open) inventory.render();
        if (health < PLAYER_HEALTH + additionalHealth && (myGame.timePassed - combatTimer) > PLAYER_COMBAT_TIMER) {
            health += Gdx.graphics.getDeltaTime() * PLAYER_HEALTH_REGEN_PER_SEC;
            if (health >= PLAYER_HEALTH + additionalHealth) health = PLAYER_HEALTH + additionalHealth;
        }
        healthLabel.setText("Health: " + (int) health + " / " + (int) (PLAYER_HEALTH + additionalHealth));
        if (inventory.getAbility() != null) abilityCooldownLabel.setText("Cooldown: " + Math.max((int) Math.ceil((((Ability) inventory.getAbility()).cooldown - (myGame.timePassed - ((Ability) inventory.getAbility()).lastUse))), 0));
        combatLabel.setText("Combat Timer: " + Math.max((int) Math.ceil((PLAYER_COMBAT_TIMER - (myGame.timePassed - combatTimer))), 0));
        timePassed += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame;
        if (currentState == PlayerState.WalkingLeft) {
            walkingRight.setPlayMode(Animation.PlayMode.NORMAL);
            currentFrame = walkingLeft.getKeyFrame(timePassed, true);
        } else if (currentState == PlayerState.WalkingRight) {
            walkingLeft.setPlayMode(Animation.PlayMode.NORMAL);
            currentFrame = walkingRight.getKeyFrame(timePassed, true);
        } else if (currentState == PlayerState.ShootingLeft) {
            if (body.getLinearVelocity().x < 0) walkingLeft.setPlayMode(Animation.PlayMode.NORMAL);
            else walkingLeft.setPlayMode(Animation.PlayMode.REVERSED);
            currentFrame = walkingLeft.getKeyFrame(timePassed, true);
        } else if (currentState == PlayerState.ShootingRight) {
            if (body.getLinearVelocity().x > 0) walkingRight.setPlayMode(Animation.PlayMode.NORMAL);
            else walkingRight.setPlayMode(Animation.PlayMode.REVERSED);
            currentFrame = walkingRight.getKeyFrame(timePassed, true);
        } else {
            currentFrame = still.getKeyFrame(timePassed, true);
        }
        //if (touchingFloor) myGame.batch.draw(myGame.shadow, body.getPosition().x - HORIZONTAL_OFFSET, body.getPosition().y - VERTICAL_OFFSET - 5 * UNIT_SCALE, 32 * UNIT_SCALE, 12 * UNIT_SCALE);
        myGame.batch.draw(currentFrame, body.getPosition().x - PLAYER_HORIZONTAL_OFFSET, body.getPosition().y - PLAYER_VERTICAL_OFFSET, PLAYER_SPRITE_WIDTH, PLAYER_SPRITE_HEIGHT);
        healthbar.maxHealth = PLAYER_HEALTH + additionalHealth;
        if (PLAYER_HEALTH + additionalHealth < health) health = PLAYER_HEALTH + additionalHealth;
        myGame.batch.draw(healthbar.getHealthBar(), body.getPosition().x - PLAYER_HORIZONTAL_OFFSET, body.getPosition().y + PLAYER_HITBOX_HEIGHT + HEALTHBAR_OFFSET, PLAYER_SPRITE_WIDTH, HEALTHBAR_HEIGHT);
        if (currentState == PlayerState.ShootingRight) {
            myGame.batch.draw(gunTextureRegion, getBodyCenter().x - 0.5f + 0.25f, getBodyCenter().y - 0.25f, 0.5f, 0.25f, 1, 0.5f, 1, 1, (float) Math.toDegrees(angle));
        } else if (currentState == PlayerState.ShootingLeft) {
            myGame.batch.draw(gunTextureRegion, getBodyCenter().x - 0.5f - 0.25f, getBodyCenter().y - 0.25f, 0.5f, 0.25f, 1, 0.5f, 1, 1, (float) Math.toDegrees(angle));
        }
    }
    @Override
    public void takeDamage(float damage) {
        super.takeDamage(damage * dmgTakenMultiplier);
        combatTimer = myGame.timePassed;
    }
    @Override
    public void death() {
        super.death();
        myGame.listener.resetContacts();
        myGame.table.clear();
        myGame.player = new Player(myGame, PLAYER_INITIAL_X_POSITION, PLAYER_INITIAL_Y_POSITION);
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
