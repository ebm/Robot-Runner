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

/**
 * There can only be one player at a time. All time measurements in this class are measured in seconds.
 */
public class Player extends Entity {
    Healthbar healthbar;
    int remainingJumps;
    boolean canJump;
    double wallClimbTime;
    boolean wallClimbFinished;
    double lastDash;
    double combatTimer;
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

    /**
     * Initializes Player at location initialX, initialY
     * @param myGame game class (manages game panel, resizing, fps)
     * @param initialX x coordinate
     * @param initialY y coordinate
     */
    public Player(MyGame myGame, float initialX, float initialY) {
        super(myGame, PLAYER_HEALTH, Player.class, Monster.class, CATEGORY_BITS_PLAYER);
        this.myGame = myGame;
        canJump = false;
        wallClimbFinished = false;
        wallClimbTime = 0;
        lastDash = 0;
        combatTimer = 0;
        lastJump = 0;

        // Default state
        currentState = PlayerState.StillRight;
        // Weapon creation
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

    /**
     * Initializes player statistics. This cannot be done in the Player() initializer because the initializer is called
     * in a separate thread. OpenGL needs graphics changes to be done in the same thread as the main render thread. This
     * function is called when the myGame panel is set visible for the first time.
     */
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

        // Creates player inventory. Initializer calls the graphics library, so it needs to be done in the main render thread
        inventory = new Inventory(myGame);
    }

    /**
     * Creates player body at (initialX, initialY) map coordinates.
     * @param initialX x coordinate
     * @param initialY y coordinate
     */
    private void createPlayer(float initialX, float initialY) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;

        // Filters set collisions. Lower number = more handling
        Filter filter = new Filter();
        //filter.categoryBits = 0x0004;
        filter.categoryBits = CATEGORY_BITS_PLAYER;

        createBody(PLAYER_HITBOX_WIDTH, PLAYER_HITBOX_HEIGHT, fixtureDef);
        body.getFixtureList().get(0).setFilterData(filter);
        body.setTransform(initialX, initialY, 0);
    }

    /**
     * Creates two animations. A right and left version of the same animation.
     * @param name of animation
     * @param frameDuration speed of animation
     * @return an array of size 2 with left and right versions of the animation
     */
    private Animation<TextureRegion>[] createIndividualAnimation(String name, float frameDuration) {
        Animation<TextureRegion>[] res = new Animation[2];
        Array<TextureAtlas.AtlasRegion> normalTextures = ((TextureAtlas) myGame.assetManager.get("robot_player/robot_character.atlas")).findRegions((name));
        res[0] = new Animation<>(frameDuration, normalTextures);
        Array<TextureRegion> flippedTextures = new Array<>();
        for (TextureRegion tr : normalTextures) {
            TextureRegion curr = new TextureRegion(tr);
            curr.flip(true, false);
            flippedTextures.add(curr);
        }
        res[1] = new Animation<>(frameDuration, flippedTextures);
        return res;
    }
    /**
     * Creates player animations from the texture atlas
     */
    private void createAnimations() {
        // walking
        Animation<TextureRegion>[] res = createIndividualAnimation("ebmarantz-walk", 0.02f);
        walkingRight = res[0];
        walkingLeft = res[1];

        // jumping
        //jumpRight = walkingRight;
        //jumpLeft = walkingLeft;
        res = createIndividualAnimation("ebmarantz-jump", 0.02f);
        jumpRight = res[0];
        jumpLeft = res[1];
        jumpRight.setPlayMode(Animation.PlayMode.NORMAL);
        jumpLeft.setPlayMode(Animation.PlayMode.NORMAL);

        // climbing
        res = createIndividualAnimation("ebmarantz-climb", 0.02f);
        climbRight = res[0];
        climbLeft = res[1];

        // dashing
        res = createIndividualAnimation("ebmarantz-dash", 0.05f);
        dashRight = res[0];
        dashLeft = res[1];

        // still
        res = createIndividualAnimation("ebmarantz-idle", 0.05f);
        stillRight = res[0];
        stillLeft = res[1];

        timePassed = 0;
    }

    /**
     * Coordinates are set in the bottom left of rectangular shapes.
     * @return the coordinates for the center of the body
     */
    @Override
    public Vector2 getBodyCenter() {
        return new Vector2(body.getPosition().x + PLAYER_HITBOX_WIDTH / 2, body.getPosition().y + PLAYER_HITBOX_HEIGHT / 2);
    }

    /**
     * Multipliers are changed through items. This function sets the player multipliers to the defaults (no items)
     */
    public void resetMultipliers() {
        speedMultiplier = 1;
        dmgTakenMultiplier = 1;
        additionalHealth  = 0;
    }

    /**
     * Resets the player state to default.
     */
    public void resetPlayerState() {
        if (currentState == PlayerState.ClimbingRight || currentState == PlayerState.JumpingRight || currentState == PlayerState.WalkingRight || currentState == PlayerState.StillRight) {
            currentState = PlayerState.StillRight;
        } else {
            currentState = PlayerState.StillLeft;
        }
    }

    /**
     * Checks if the player is on the ground. Sets the x velocity of the player to 0 if true.
     */
    public void onGround() {
        if (contactFeet >= 1) {
            body.setLinearVelocity(0, body.getLinearVelocity().y);
            remainingJumps = PLAYER_MAXIMUM_JUMPS;
            wallClimbFinished = false;
            wallClimbTime = 0;
            //canJump = true; // enable this to allow user to hold spacebar to continuously jump
        }
    }

    /**
     * Check if the inventory can be opened, and handles when the keybind is pressed. Function is necessary so that
     * the inventory can use the same key to open and close it.
     */
    public void inventoryCheck() {
        if (inventory.open) {
            return;
        } else if (canOpenInventory && myGame.checkKeybind("Inventory")) {
            inventory.setOpen(true);
            canOpenInventory = false;
            return;
        }
        if (!myGame.checkKeybind("Inventory")) canOpenInventory = true;
    }

    /**
     * Checks if the character should move left.
     */
    public void leftCheck() {
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
    }
    /**
     * Checks if the character should move right.
     */
    public void rightCheck() {
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
    }

    /**
     * Checks if the player has jumps left, and is pressing the jump keybind.
     */
    public void jumpCheck() {
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
            timePassed = 2.5f;
            remainingJumps--;
            canJump = false;
        } else if (!myGame.checkKeybind("Jump")){
            canJump = true;
        }
    }

    /**
     * Checks if the player can wall climb. Must be moving into a wall to wallclimb. Also ensures player can only wallclimb
     * once.
     */
    public void wallClimbCheck() {
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
    }

    /**
     * Checks if the player is holding down the fire button. Also determines where the mouse is to show where to aim
     * the shot.
     */
    public void fireCheck() {
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
    /**
     * Handles user input. Adjusts player velocity accordingly. Also checks if the player is out of bounds. Called
     * 240 times per second, regardless of computer speed.
     */
    public void update() {
        if (getBodyCenter().y < 0) death();
        resetPlayerState();
        onGround();
        inventoryCheck();
        leftCheck();
        rightCheck();
        jumpCheck();
        wallClimbCheck();

        // Jump animation needs to be fixed for this code to be uncommented
        //if (myGame.timePassed - lastJump < 0.5f) {
        //    currentState = jumpState;
        //}
        // multipliers should be applied after so player state is correct
        inventory.applyMultipliers();
        fireCheck();
    }

    /**
     * Renders all active player statistics.
     */
    public void renderStatistics() {
        healthLabel.setText("Health: " + (int) health + " / " + (int) (PLAYER_HEALTH + additionalHealth));
        if (inventory.getAbility() != null) abilityCooldownLabel.setText("Cooldown: " + Math.max((int) Math.ceil((((Ability) inventory.getAbility()).cooldown - (myGame.timePassed - ((Ability) inventory.getAbility()).lastUse))), 0));
        combatLabel.setText("Combat Timer: " + Math.max((int) Math.ceil((PLAYER_COMBAT_TIMER - (myGame.timePassed - combatTimer))), 0));
        stateLabel.setText("Current State: " + currentState);
        timePassed += Gdx.graphics.getDeltaTime();
    }
    /**
     * Gets the current player animation.
     * @return a textureRegion with the current frame.
     */
    public TextureRegion getCurrentFrame() {
        TextureRegion currentFrame;
        if (currentState == PlayerState.WalkingLeft) {
            currentFrame = walkingLeft.getKeyFrame(timePassed, true);
        } else if (currentState == PlayerState.WalkingRight) {
            currentFrame = walkingRight.getKeyFrame(timePassed, true);
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
        return currentFrame;
    }

    /**
     * Render the gun animation.
     */
    public void renderGun() {
        if (currentState == PlayerState.ShootingRight) {
            myGame.batch.draw(gunTextureRegion, getBodyCenter().x - 0.5f + 0.25f, getBodyCenter().y - 0.25f, 0.5f, 0.25f, 1, 0.5f, 1, 1, (float) Math.toDegrees(angle));
        } else if (currentState == PlayerState.ShootingLeft) {
            myGame.batch.draw(gunTextureRegion, getBodyCenter().x - 0.5f - 0.25f, getBodyCenter().y - 0.25f, 0.5f, 0.25f, 1, 0.5f, 1, 1, (float) Math.toDegrees(angle));
        }
    }
    /**
     * Gets called in myGame. Renders player animation frame, weapon, healthbar, and statistics. Checks playerState to
     * ensure the right animation is played.
     */
    @Override
    public void render() {
        weapon.render();
        if (inventory.open) inventory.render();
        if (health < PLAYER_HEALTH + additionalHealth && (myGame.timePassed - combatTimer) > PLAYER_COMBAT_TIMER) {
            health += Gdx.graphics.getDeltaTime() * PLAYER_HEALTH_REGEN_PER_SEC;
            if (health >= PLAYER_HEALTH + additionalHealth) health = PLAYER_HEALTH + additionalHealth;
        }
        renderStatistics();
        // Uncomment this to draw a shadow beneath player.
        //if (touchingFloor) myGame.batch.draw(myGame.shadow, body.getPosition().x - HORIZONTAL_OFFSET, body.getPosition().y - VERTICAL_OFFSET - 5 * UNIT_SCALE, 32 * UNIT_SCALE, 12 * UNIT_SCALE);
        myGame.batch.draw(getCurrentFrame(), body.getPosition().x - PLAYER_HORIZONTAL_OFFSET, body.getPosition().y - PLAYER_VERTICAL_OFFSET, PLAYER_SPRITE_WIDTH, PLAYER_SPRITE_HEIGHT);
        healthbar.maxHealth = PLAYER_HEALTH + additionalHealth;
        if (PLAYER_HEALTH + additionalHealth < health) health = PLAYER_HEALTH + additionalHealth;
        myGame.batch.draw(healthbar.getHealthBar(), body.getPosition().x - PLAYER_HORIZONTAL_OFFSET, body.getPosition().y + PLAYER_HITBOX_HEIGHT + HEALTHBAR_OFFSET, PLAYER_SPRITE_WIDTH, HEALTHBAR_HEIGHT);
        renderGun();
    }

    /**
     * Adjusts health and combatTimer when damage is taken.
     * TODO: add sound effect for when player gets damaged.
     * @param damage amount to subtract from health
     */
    @Override
    public void takeDamage(float damage) {
        super.takeDamage(damage * dmgTakenMultiplier);
        combatTimer = myGame.timePassed;
    }

    /**
     * Handles the death and respawn of player.
     */
    @Override
    public void death() {
        super.death();
        myGame.listener.resetContacts();
        myGame.table.clear();
        weapon.destroy();
        myGame.player = new Player(myGame, PLAYER_INITIAL_X_POSITION, PLAYER_INITIAL_Y_POSITION);
        myGame.player.initializeOnScreenPlayerStats();
    }
}
