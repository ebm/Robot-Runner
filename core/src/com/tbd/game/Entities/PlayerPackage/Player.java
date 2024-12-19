package com.tbd.game.Entities.PlayerPackage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.tbd.game.Entities.BodyPart;
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
    Healthbar healthbar;
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
    Animation<TextureRegion> jumpRight;
    Animation<TextureRegion> jumpLeft;
    Animation<TextureRegion> climbLeft;
    Animation<TextureRegion> climbRight;
    Animation<TextureRegion> dashLeft;
    Animation<TextureRegion> dashRight;
    Animation<TextureRegion> stillRight;
    Animation<TextureRegion> stillLeft;
    float timePassed;
    Weapon weapon;
    public Label healthLabel;
    public Label abilityCooldownLabel;
    public Label combatLabel;
    public Label stateLabel;
    public Inventory inventory;
    public boolean canOpenInventory;
    public float dmgTakenMultiplier;
    public float speedMultiplier;
    public float additionalHealth;
    TextureRegion gunTextureRegion;
    public float angle;
    public double lastJump;
    public PlayerState jumpState;
    boolean flip;
    public Player(MyGame myGame, float initialX, float initialY) {
        super(myGame, PLAYER_HEALTH, Player.class, Monster.class, CATEGORY_BITS_PLAYER);
        this.myGame = myGame;
        canJump = false;
        wallClimbFinished = false;
        wallClimbTime = 0;
        lastDash = 0;
        combatTimer = 0;
        lastJump = 0;

        currentState = PlayerState.StillRight;
//        weapon = new RangedWeapon(myGame, this, 20, 3, 8, 0.2f * METERS_PER_PIXEL, myGame.playerFireNoise);
        weapon = new RangedWeapon(myGame, this, 50, 3, 8, 0.2f * METERS_PER_PIXEL, myGame.assetManager.get("fire.mp3"));

        // Create Body
        createPlayer(initialX, initialY);

        createAnimations();

        healthbar = new Healthbar(myGame, this, health);

        canOpenInventory = false;

        speedMultiplier = 1;
        dmgTakenMultiplier = 1;
        additionalHealth  = 0;
        gunTextureRegion = new TextureRegion((Texture) myGame.assetManager.get("gun.png"));
        flip = false;

    }
    public void initializeOnScreenPlayerStats() {
        healthLabel = new Label("Health: " + (int) health + " / " + (int) PLAYER_HEALTH, myGame.labelStyle);
        abilityCooldownLabel = new Label("Cooldown: 0", myGame.labelStyle);
        combatLabel = new Label("Combat Timer: " + Math.max((int) Math.ceil((PLAYER_COMBAT_TIMER - (myGame.timePassed - combatTimer))), 0), myGame.labelStyle);
        stateLabel = new Label("Current State: " + currentState, myGame.labelStyle);

        myGame.table.add(stateLabel).pad(5);
        myGame.table.add(combatLabel);
        myGame.table.add(abilityCooldownLabel).pad(5);
        myGame.table.add(healthLabel);
        myGame.table.pad(5);

        inventory = new Inventory(myGame);
    }
    private void createPlayer(float initialX, float initialY) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;
        Filter filter = new Filter();
        filter.categoryBits = 0x0004;

        createBody(PLAYER_HITBOX_WIDTH, PLAYER_HITBOX_HEIGHT, fixtureDef);
        body.getFixtureList().get(0).setFilterData(filter);
        body.setTransform(initialX, initialY, 0);
    }

    private void createAnimations() {
        // walking
        Array<TextureAtlas.AtlasRegion> normalWalkTextures = ((TextureAtlas) myGame.assetManager.get("robot_player/robot_character.atlas")).findRegions(("ebmarantz-walk"));
        walkingRight = new Animation<>(0.02f, normalWalkTextures);
        Array<TextureRegion> flippedWalkTextures = new Array<>();
        for (TextureRegion tr : normalWalkTextures) {
            TextureRegion curr = new TextureRegion(tr);
            curr.flip(true, false);
            flippedWalkTextures.add(curr);
        }
        walkingLeft = new Animation<>(0.02f, flippedWalkTextures);

        // jumping
        jumpRight = walkingRight;
        jumpLeft = walkingLeft;
//        Array<TextureAtlas.AtlasRegion> normalJumpTextures = myGame.atlas.findRegions(("ebmarantz-jump-double"));
//        jumpRight = new Animation<>(0.02f, normalJumpTextures);
//        Array<TextureRegion> flippedJumpTextures = new Array<>();
//        for (TextureRegion tr : normalJumpTextures) {
//            TextureRegion curr = new TextureRegion(tr);
//            curr.flip(true, false);
//            flippedJumpTextures.add(curr);
//        }
//        jumpLeft = new Animation<>(0.02f, flippedJumpTextures);
//        jumpRight.setPlayMode(Animation.PlayMode.NORMAL);
//        jumpLeft.setPlayMode(Animation.PlayMode.NORMAL);

        // climbing
        Array<TextureAtlas.AtlasRegion> normalClimbTextures = ((TextureAtlas) myGame.assetManager.get("robot_player/robot_character.atlas")).findRegions(("ebmarantz-climb"));
        climbRight = new Animation<>(0.02f, normalClimbTextures);
        Array<TextureRegion> flippedClimbTextures = new Array<>();
        for (TextureRegion tr : normalClimbTextures) {
            TextureRegion curr = new TextureRegion(tr);
            curr.flip(true, false);
            flippedClimbTextures.add(curr);
        }
        climbLeft = new Animation<>(0.02f, flippedClimbTextures);

        // dash
        Array<TextureAtlas.AtlasRegion> normalDashTextures = ((TextureAtlas) myGame.assetManager.get("robot_player/robot_character.atlas")).findRegions(("ebmarantz-dash"));
        dashRight = new Animation<>(0.05f, normalDashTextures);
        Array<TextureRegion> flippedDashTextures = new Array<>();
        for (TextureRegion tr : normalDashTextures) {
            TextureRegion curr = new TextureRegion(tr);
            curr.flip(true, false);
            flippedDashTextures.add(curr);
        }
        dashLeft = new Animation<>(0.05f, flippedDashTextures);

        // still
        Array<TextureAtlas.AtlasRegion> normalStillTextures = ((TextureAtlas) myGame.assetManager.get("robot_player/robot_character.atlas")).findRegions(("ebmarantz-idle"));
        stillRight = new Animation<>(0.05f, normalStillTextures);
        Array<TextureRegion> flippedStillTextures = new Array<>();
        for (TextureRegion tr : normalStillTextures) {
            TextureRegion curr = new TextureRegion(tr);
            curr.flip(true, false);
            flippedStillTextures.add(curr);
        }
        stillLeft = new Animation<>(0.05f, flippedStillTextures);


        timePassed = 0;
    }
    @Override
    public Vector2 getBodyCenter() {
        return new Vector2(body.getPosition().x + PLAYER_HITBOX_WIDTH / 2, body.getPosition().y + PLAYER_HITBOX_HEIGHT / 2);
    }
    public void resetMultipliers() {
        speedMultiplier = 1;
        dmgTakenMultiplier = 1;
        additionalHealth  = 0;
    }
    public void update() {
        if (getBodyCenter().y < 0) death();
        if (currentState == PlayerState.ClimbingRight || currentState == PlayerState.JumpingRight || currentState == PlayerState.WalkingRight || currentState == PlayerState.StillRight) {
            currentState = PlayerState.StillRight;
        } else {
            currentState = PlayerState.StillLeft;
        }
        // ON GROUND
        if (contactFeet >= 1) {
            body.setLinearVelocity(0, body.getLinearVelocity().y);
            remainingJumps = PLAYER_MAXIMUM_JUMPS;
            wallClimbFinished = false;
            wallClimbTime = 0;
            //canJump = true; // enable this to allow user to hold spacebar to continuously jump
        }
        if (inventory.open) {
            return;
        } else if (canOpenInventory && myGame.checkKeybind("Inventory")) {
            inventory.setOpen(true);
            canOpenInventory = false;
            return;
        }
        if (!myGame.checkKeybind("Inventory")) canOpenInventory = true;
        // LEFT
        if (myGame.checkKeybind("Move Left") && !myGame.checkKeybind("Move Right")) {
            if (contactFeet >= 1) {
                body.setLinearVelocity(-PLAYER_HORIZONTAL_VELOCITY * speedMultiplier, body.getLinearVelocity().y);
            } else {
                if (body.getLinearVelocity().x - PLAYER_HORIZONTAL_AIR_ACCELERATION > -PLAYER_MAXIMUM_HORIZONTAL_AIR_VELOCITY * speedMultiplier) {
                    body.setLinearVelocity(body.getLinearVelocity().x - PLAYER_HORIZONTAL_AIR_ACCELERATION, body.getLinearVelocity().y);
                }
            }
            currentState = PlayerState.WalkingLeft;
        }
        // RIGHT
        if (myGame.checkKeybind("Move Right") && !myGame.checkKeybind("Move Left")) {
            if (contactFeet >= 1) {
                body.setLinearVelocity(PLAYER_HORIZONTAL_VELOCITY * speedMultiplier, body.getLinearVelocity().y);
            } else {
                if (body.getLinearVelocity().x + PLAYER_HORIZONTAL_AIR_ACCELERATION < PLAYER_MAXIMUM_HORIZONTAL_AIR_VELOCITY * speedMultiplier) {
                    body.setLinearVelocity(body.getLinearVelocity().x + PLAYER_HORIZONTAL_AIR_ACCELERATION, body.getLinearVelocity().y);
                }
            }
            currentState = PlayerState.WalkingRight;
        }
        // JUMP
        if (myGame.checkKeybind("Jump") && remainingJumps >= 1 && canJump && (wallClimbTime == 0 || wallClimbFinished)) {
            body.setTransform(body.getPosition().x, body.getPosition().y + 2 * UNIT_SCALE, 0);
            body.setLinearVelocity(body.getLinearVelocity().x, PLAYER_JUMP_VELOCITY);
            if (currentState == PlayerState.WalkingLeft) {
                body.setLinearVelocity(-PLAYER_HORIZONTAL_VELOCITY * speedMultiplier, body.getLinearVelocity().y);
                jumpState = PlayerState.JumpingLeft;
            } else if (currentState == PlayerState.WalkingRight) {
                body.setLinearVelocity(PLAYER_HORIZONTAL_VELOCITY * speedMultiplier, body.getLinearVelocity().y);
                jumpState = PlayerState.JumpingRight;
            } else {
                jumpState = currentState;
            }
            lastJump = myGame.timePassed;
            remainingJumps--;
            canJump = false;
        } else if (!myGame.checkKeybind("Jump")){
            canJump = true;
        }
        // WALLCLIMB
        if (/*Gdx.input.isKeyPressed(Input.Keys.W) && */!wallClimbFinished) {
            // Ensure player is walking into wall
            if ((currentState == PlayerState.WalkingLeft && contactLeftArm >= 1 || currentState == PlayerState.WalkingRight && contactRightArm >= 1) &&
                    // Ensure player has not exceeded climbing wall time
                    (wallClimbTime == 0 || (myGame.timePassed - wallClimbTime) < PLAYER_WALLCLIMB_LENGTH_SECONDS)) {
                if (wallClimbTime == 0) wallClimbTime = myGame.timePassed;
                if (currentState == PlayerState.WalkingLeft) {
                    currentState = PlayerState.ClimbingLeft;
                } else {
                    currentState = PlayerState.ClimbingRight;
                }
                body.setLinearVelocity(body.getLinearVelocity().x, PLAYER_WALLCLIMB_VELOCITY);
            } else {
                if (wallClimbTime != 0) wallClimbFinished = true;
            }
        } else if (!wallClimbFinished) {
            if (wallClimbTime != 0) wallClimbFinished = true;
        }
        //if (myGame.timePassed - lastJump < 0.5f) {
        //    currentState = jumpState;
        //}
        inventory.applyMultipliers();
        // Fire
        if (myGame.checkKeybind("Fire")) {
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
        //System.out.println(currentState);
        weapon.render();
        if (inventory.open) inventory.render();
        if (health < PLAYER_HEALTH + additionalHealth && (myGame.timePassed - combatTimer) > PLAYER_COMBAT_TIMER) {
            health += Gdx.graphics.getDeltaTime() * PLAYER_HEALTH_REGEN_PER_SEC;
            if (health >= PLAYER_HEALTH + additionalHealth) health = PLAYER_HEALTH + additionalHealth;
        }
        healthLabel.setText("Health: " + (int) health + " / " + (int) (PLAYER_HEALTH + additionalHealth));
        if (inventory.getAbility() != null) abilityCooldownLabel.setText("Cooldown: " + Math.max((int) Math.ceil((((Ability) inventory.getAbility()).cooldown - (myGame.timePassed - ((Ability) inventory.getAbility()).lastUse))), 0));
        combatLabel.setText("Combat Timer: " + Math.max((int) Math.ceil((PLAYER_COMBAT_TIMER - (myGame.timePassed - combatTimer))), 0));
        stateLabel.setText("Current State: " + currentState);
        timePassed += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame;
        if (currentState == PlayerState.WalkingLeft) {
            currentFrame = walkingLeft.getKeyFrame(timePassed, true);
        } else if (currentState == PlayerState.WalkingRight) {
            currentFrame = walkingRight.getKeyFrame(timePassed, true);
        } else if (currentState == PlayerState.JumpingLeft) {
            currentFrame = jumpLeft.getKeyFrame(timePassed, true);
        } else if (currentState == PlayerState.JumpingRight) {
            currentFrame = jumpRight.getKeyFrame(timePassed, true);
        } else if (currentState == PlayerState.ClimbingLeft) {
            currentFrame = climbLeft.getKeyFrame(timePassed, true);
        } else if (currentState == PlayerState.ClimbingRight) {
            currentFrame = climbRight.getKeyFrame(timePassed, true);
        } else if (currentState == PlayerState.DashingLeft) {
            currentFrame = dashLeft.getKeyFrame(timePassed, true);
        } else if (currentState == PlayerState.DashingRight) {
            currentFrame = dashRight.getKeyFrame(timePassed, true);
        } else if (currentState == PlayerState.StillLeft){
            currentFrame = stillLeft.getKeyFrame(timePassed, true);
        } else {
            currentFrame = stillRight.getKeyFrame(timePassed, true);
        }
        //if (touchingFloor) myGame.batch.draw(myGame.shadow, body.getPosition().x - HORIZONTAL_OFFSET, body.getPosition().y - VERTICAL_OFFSET - 5 * UNIT_SCALE, 32 * UNIT_SCALE, 12 * UNIT_SCALE);
        myGame.batch.draw(currentFrame, body.getPosition().x - PLAYER_HORIZONTAL_OFFSET, body.getPosition().y - PLAYER_VERTICAL_OFFSET, PLAYER_SPRITE_WIDTH, PLAYER_SPRITE_HEIGHT);
        healthbar.maxHealth = PLAYER_HEALTH + additionalHealth;
        if (PLAYER_HEALTH + additionalHealth < health) health = PLAYER_HEALTH + additionalHealth;
        myGame.batch.draw(healthbar.getHealthBar(), body.getPosition().x, body.getPosition().y + PLAYER_HITBOX_HEIGHT + HEALTHBAR_OFFSET, PLAYER_HITBOX_WIDTH, HEALTHBAR_HEIGHT);
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
        weapon.destroy();
        myGame.player = new Player(myGame, PLAYER_INITIAL_X_POSITION, PLAYER_INITIAL_Y_POSITION);
    }
}
