package com.robotrunner.Entities.PlayerPackage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.robotrunner.Entities.BodyPart;
import com.robotrunner.Entities.Entity;
import com.robotrunner.Entities.Healthbar;
import com.robotrunner.Entities.MonsterPackage.Monster;
import com.robotrunner.Items.Ability;
import com.robotrunner.World.Listener;
import com.robotrunner.States.MyGame;
import com.robotrunner.Weapons.RangedWeapon;
import com.robotrunner.Weapons.Weapon;

import java.util.ArrayList;

import static com.robotrunner.World.Constants.*;

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
    public PlayerState shootingState;
    public PlayerState movingState; // has to be either MovingLeft or MovingRight
    public PlayerState jumpState;
    Animation<TextureRegion> walkingLeft;
    Animation<TextureRegion> walkingRight;
    Animation<TextureRegion> jumpRight;
    Animation<TextureRegion> jumpLeft;
    Animation<TextureRegion> climbLeft;
    Animation<TextureRegion> climbRight;
    Animation<TextureRegion> dashLeft;
    Animation<TextureRegion> dashRight;
    Animation<TextureRegion> shootingLeftWalkingRight;
    Animation<TextureRegion> shootingLeftWalkingLeft;
    Animation<TextureRegion> shootingRightWalkingRight;
    Animation<TextureRegion> shootingRightWalkingLeft;

    Animation<TextureRegion> shootingRightJumpRight;
    Animation<TextureRegion> shootingLeftJumpLeft;
    Animation<TextureRegion> shootingLeftJumpRight;
    Animation<TextureRegion> shootingRightJumpLeft;

    Animation<TextureRegion> shootingRightDashRight;
    Animation<TextureRegion> shootingLeftDashLeft;
    Animation<TextureRegion> shootingLeftDashRight;
    Animation<TextureRegion> shootingRightDashLeft;

    Animation<TextureRegion> stillRight;
    Animation<TextureRegion> stillLeft;
    float timePassed;
    Weapon weapon;
    public Label healthLabel;
    public Label abilityCooldownLabel;
    public Label combatLabel;
    public Label stateLabel;
    public Label speedLabel;
    public Inventory inventory;
    public boolean canOpenInventory;
    public float dmgTakenMultiplier;
    public float speedMultiplier;
    public float additionalHealth;
    //TextureRegion gunTextureRegion;
    Animation<TextureRegion> gunAnimationSLMR;
    Animation<TextureRegion> gunAnimationSRML;
    Animation<TextureRegion> gunAnimationSRMR;
    Animation<TextureRegion> gunAnimationSLML;
    Animation<TextureRegion>[] gunAnimations;
    public float angle;
    public double lastJump;
    ShapeRenderer shapeRenderer = new ShapeRenderer();
    Color[] shapeRendererColors = {Color.BLUE, Color.BROWN, Color.GREEN, Color.GRAY, Color.GRAY};
    ArrayList<Vector2> pointsToRender = new ArrayList<>();

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
        jumpState = PlayerState.JumpingRight;
        movingState = PlayerState.MovingRight;
        // Weapon creation
        weapon = new RangedWeapon(myGame, this, 20, 3, 8, 0.1f * METERS_PER_PIXEL, myGame.assetManager.get("fire.mp3"));

        // Create Body
        createPlayer(initialX, initialY);
        createAnimations();
        resetMultipliers();
        healthbar = new Healthbar(myGame, this, health);
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
        speedLabel = new Label("Speed: " + (int) Math.abs(body.getLinearVelocity().x), myGame.labelStyle);

        myGame.table.add(speedLabel);
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
    private Animation<TextureRegion>[] createMirroredAnimation(String name, float frameDuration) {
        Animation<TextureRegion>[] res = new Animation[2];
        Array<TextureAtlas.AtlasRegion> normalTextures = ((TextureAtlas) myGame.assetManager.get("robot_player_scaled/robot_character.atlas")).findRegions((name));
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
     * Creates one animation.
     * @param name of animation
     * @param frameDuration speed of animation
     * @return an animation of type TextureRegion
     */
    private Animation<TextureRegion> createIndividualAnimation(String name, float frameDuration) {
        return new Animation<>(frameDuration, ((TextureAtlas) myGame.assetManager.get("robot_player_scaled/robot_character.atlas")).findRegions((name)));
    }
    public void createIdleAnimations() {
        Animation<TextureRegion>[] res = createMirroredAnimation("ebmarantz-idle", 0.05f);
        stillRight = res[0];
        stillLeft = res[1];
    }
    public void createGunAnimations() {
        gunAnimationSRMR = createIndividualAnimation("ebmarantz-shoot-L2", 0.01f);
        gunAnimationSLML = createIndividualAnimation("ebmarantz-shoot-R2", 0.01f);

        gunAnimationSLMR = createIndividualAnimation("ebmarantz-shoot-R1", 0.01f);
        gunAnimationSRML = createIndividualAnimation("ebmarantz-shoot-L1", 0.01f);

        gunAnimations = new Animation[]{gunAnimationSLML, gunAnimationSLMR, gunAnimationSRML, gunAnimationSRMR};
    }
    public void createDashingAnimations() {
        Animation<TextureRegion>[] res = createMirroredAnimation("ebmarantz-dash", 0.05f);
        dashRight = res[0];
        dashLeft = res[1];

        shootingRightDashRight = createIndividualAnimation("ebmarantz-dash-shoot-L1", 0.02f);
        shootingLeftDashLeft = createIndividualAnimation("ebmarantz-dash-shoot-R2", 0.02f);

        shootingLeftDashRight = createIndividualAnimation("ebmarantz-dash-shoot-R1", 0.02f);
        shootingRightDashLeft = createIndividualAnimation("ebmarantz-dash-shoot-L2", 0.02f);
    }
    public void createClimbingAnimations() {
        Animation<TextureRegion>[] res = createMirroredAnimation("ebmarantz-climb", 0.02f);
        climbRight = res[0];
        climbLeft = res[1];
    }
    public void createJumpingAnimations() {
        Animation<TextureRegion>[] res = createMirroredAnimation("ebmarantz-jump", 0.02f);
        jumpRight = res[0];
        jumpLeft = res[1];

        shootingRightJumpRight = createIndividualAnimation("ebmarantz-jump-shoot-L1", 0.02f);
        shootingLeftJumpLeft = createIndividualAnimation("ebmarantz-jump-shoot-R2", 0.02f);

        shootingLeftJumpRight = createIndividualAnimation("ebmarantz-jump-shoot-R1", 0.02f);
        shootingRightJumpLeft = createIndividualAnimation("ebmarantz-jump-shoot-L2", 0.02f);
    }
    public void createWalkingAnimations() {
        Animation<TextureRegion>[] res = createMirroredAnimation("ebmarantz-walk", 0.02f);
        walkingRight = res[0];
        walkingLeft = res[1];

        shootingRightWalkingRight = createIndividualAnimation("ebmarantz-walk-shoot-L1", 0.02f);
        shootingLeftWalkingLeft = createIndividualAnimation("ebmarantz-walk-shoot-R2", 0.02f);

        shootingLeftWalkingRight = createIndividualAnimation("ebmarantz-walk-shoot-R1", 0.02f);
        shootingRightWalkingLeft =createIndividualAnimation("ebmarantz-walk-shoot-L2", 0.02f);
    }
    /**
     * Creates player animations from the texture atlas
     */
    private void createAnimations() {
        createIdleAnimations();
        createWalkingAnimations();
        createJumpingAnimations();
        createClimbingAnimations();
        createDashingAnimations();
        createGunAnimations();

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
            movingState = PlayerState.MovingRight;
        } else {
            currentState = PlayerState.StillLeft;
            movingState = PlayerState.MovingLeft;
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
    public boolean inventoryCheck() {
        if (inventory.open) {
            return true;
        } else if (canOpenInventory && myGame.checkKeybind("Inventory")) {
            inventory.setOpen(true);
            canOpenInventory = false;
            return true;
        }
        if (!myGame.checkKeybind("Inventory")) canOpenInventory = true;
        return false;
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
            movingState = PlayerState.MovingLeft;
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
            movingState = PlayerState.MovingRight;
        }
    }

    /**
     * Checks if the player has jumps left, and is pressing the jump keybind.
     */
    public void jumpCheck() {
        if (myGame.checkKeybind("Jump") && remainingJumps >= 1 && canJump && (wallClimbTime == 0 || wallClimbFinished)) {
            body.setTransform(body.getPosition().x, body.getPosition().y + 2 * UNIT_SCALE, 0);
            body.setLinearVelocity(body.getLinearVelocity().x, PLAYER_JUMP_VELOCITY);
            if (movingState == PlayerState.MovingLeft) {
                body.setLinearVelocity(-PLAYER_HORIZONTAL_VELOCITY * speedMultiplier, body.getLinearVelocity().y);
                jumpState = PlayerState.JumpingLeft;
            } else if (movingState == PlayerState.MovingRight) {
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
                    movingState = PlayerState.MovingLeft;
                } else {
                    currentState = PlayerState.ClimbingRight;
                    movingState = PlayerState.MovingRight;
                }
                body.setLinearVelocity(body.getLinearVelocity().x, PLAYER_WALLCLIMB_VELOCITY);
            } else {
                if (wallClimbTime != 0) wallClimbFinished = true;
            }
        } else if (!wallClimbFinished) {
            if (wallClimbTime != 0) wallClimbFinished = true;
        }
    }
    public Vector2 rotateAroundPoint(Vector2 rotationPoint, Vector2 pointToRotate, double angle) {
        double x = rotationPoint.x + (pointToRotate.x - rotationPoint.x) * Math.cos(angle) - (pointToRotate.y - rotationPoint.y) * Math.sin(angle);
        double y = rotationPoint.y + (pointToRotate.x - rotationPoint.x) * Math.sin(angle) + (pointToRotate.y - rotationPoint.y) * Math.cos(angle);
        return new Vector2((float) x, (float) y);
    }
    public double slope(Vector2 a, Vector2 b) {
        return (b.y - a.y) / (b.x - a.x);
    }
    public double equation(Vector2 center, Vector2 startingPoint, Vector2 spawnPoint, Vector2 mousePos, double angle) {
        Vector2 rotatedStart = rotateAroundPoint(center, startingPoint, angle);
        Vector2 rotatedSpawn = rotateAroundPoint(center, spawnPoint, angle);
        return rotatedStart.x * (rotatedSpawn.y - mousePos.y) + rotatedSpawn.x * (mousePos.y - rotatedStart.y) + mousePos.x * (rotatedStart.y - rotatedSpawn.y);
    }
    public double equation2(Vector2 start, float changeY, Vector2 mousePos, double angle) {
        return Math.pow((mousePos.x - start.x), 2) + Math.pow((mousePos.y - start.y), 2) * Math.pow(Math.cos(angle), 2) - 2 * changeY * (mousePos.x - start.x) * Math.cos(angle) + Math.pow(changeY, 2) - Math.pow((mousePos.y - start.y), 2);
        //return Math.pow(Math.sin(angle), 2) / Math.cos(angle) * changeY - (mousePos.y - start.y) * Math.tan(angle) - ((mousePos.x - start.x) - changeY * Math.cos(angle));
    }
    public double secantMethod(double x0, double x1, Vector2 center, Vector2 startingPoint, Vector2 spawnPoint, Vector2 mousePos) {
        double prevGuess = Math.toRadians(x0);
        double currGuess = Math.toRadians(x1);
        double nextGuess = 0;

        double resultPrevGuess = equation(center, startingPoint, spawnPoint, mousePos, prevGuess);
        double resultCurrGuess;
        int i;
        for (i = 0; i < 1000; i++) {
            resultCurrGuess = equation(center, startingPoint, spawnPoint, mousePos, currGuess);
            if ((resultPrevGuess - resultCurrGuess) == 0) {
                double accuracy = equation(center, startingPoint, spawnPoint, mousePos, currGuess);
                System.out.println("Iterations: " + i + ", accuracy: " + accuracy + ", Initial Guesses: (" + x0 + ", " + x1 + ")");
                return currGuess;
            }
            nextGuess = currGuess - (resultCurrGuess * (prevGuess - currGuess)) / (resultPrevGuess - resultCurrGuess);
            if (Math.abs(nextGuess - currGuess) < Math.pow(10, -3)) {
                double accuracy = equation(center, startingPoint, spawnPoint, mousePos, nextGuess);
                System.out.println("Iterations: " + i + ", accuracy: " + accuracy + ", Initial Guesses: (" + x0 + ", " + x1 + ")");
                return nextGuess;
            }
            prevGuess = currGuess;
            currGuess = nextGuess;
            resultPrevGuess = resultCurrGuess;
        }
        double accuracy = equation(center, startingPoint, spawnPoint, mousePos, nextGuess);
        System.out.println("Iterations: " + i + ", accuracy: " + accuracy + ", Initial Guesses: (" + x0 + ", " + x1 + ")");
        return nextGuess;
    }
    public double bisectionMethod(double x0, double x1, Vector2 center, Vector2 startingPoint, Vector2 spawnPoint, Vector2 mousePos) {
        double lowerGuess = Math.toRadians(x0);
        double upperGuess = Math.toRadians(x1);
        double mid = 0;
        int iterations = 0;
        while (Math.abs(upperGuess - lowerGuess) >= Math.pow(10, -3) && iterations < 1000) {
            mid = (upperGuess + lowerGuess) / 2;
            double midResult = equation(center, startingPoint, spawnPoint, mousePos, mid);
            if (midResult == 0) {
                double accuracy = equation(center, startingPoint, spawnPoint, mousePos, mid);
                //System.out.println("Iterations: " + iterations + ", accuracy: " + accuracy + ", Initial Guesses: (" + x0 + ", " + x1 + "). Angle: " + Math.toDegrees(mid));
                return mid;
            } else if (equation(center, startingPoint, spawnPoint, mousePos, lowerGuess) * midResult < 0) {
                upperGuess = mid;
            } else if (equation(center, startingPoint, spawnPoint, mousePos, upperGuess) * midResult < 0) {
                lowerGuess = mid;
            }
            iterations++;
        }
        double accuracy = equation(center, startingPoint, spawnPoint, mousePos, mid);
        //System.out.println("Iterations: " + iterations + ", accuracy: " + accuracy + ", Initial Guesses: (" + x0 + ", " + x1 + "). Angle: " + Math.toDegrees(mid));
        return mid;
    }
    public double bisectionMethod2(double x0, double x1, Vector2 start, float changeY, Vector2 mousePos) {
        double lowerGuess = Math.toRadians(x0);
        double upperGuess = Math.toRadians(x1);
        double mid = 0;
        int iterations = 0;
        while (Math.abs(upperGuess - lowerGuess) >= Math.pow(10, -3) && iterations < 1000) {
            mid = (upperGuess + lowerGuess) / 2;
            double midResult = equation2(start, changeY, mousePos, mid);
            if (midResult == 0) {
                double accuracy = equation2(start, changeY, mousePos, mid);
                System.out.println("Iterations2: " + iterations + ", accuracy: " + accuracy + ", Initial Guesses: (" + x0 + ", " + x1 + "). Angle: " + Math.toDegrees(mid));
                return mid;
            } else if (equation2(start, changeY, mousePos, lowerGuess) * midResult < 0) {
                upperGuess = mid;
            } else if (equation2(start, changeY, mousePos, upperGuess) * midResult < 0) {
                lowerGuess = mid;
            }
            iterations++;
        }
        double accuracy = equation2(start, changeY, mousePos, mid);
        System.out.println("Iterations2: " + iterations + ", accuracy: " + accuracy + ", Initial Guesses: (" + x0 + ", " + x1 + "). Angle: " + Math.toDegrees(mid));
        return mid;
    }
    public int getStatIndex() {
        if (shootingState == PlayerState.ShootingLeft) {
            if (movingState == PlayerState.MovingLeft) {
                return 0;
            } else {
                return 1;
            }
        } else if (shootingState == PlayerState.ShootingRight) {
            if (movingState == PlayerState.MovingLeft) {
                return 2;
            } else {
                return 3;
            }
        }
        return -1;
    }
    /**
     * Gets the location of the joint, and spawn point relative to the joint if the angle is 0
     * @return a Vector2 array with {joint, change}
     */
    public Vector2[] getFireLocation() {
        int statIndex = getStatIndex();
        // Gun animation: (float) (myGame.timePassed - weapon.lastUse)
        Vector2 joint = new Vector2(body.getPosition().x - PLAYER_HORIZONTAL_OFFSET + PLAYER_GUN_STATS[statIndex][0] * PLAYER_SPRITE_WIDTH,
            body.getPosition().y - PLAYER_VERTICAL_OFFSET + PLAYER_GUN_STATS[statIndex][1] * PLAYER_SPRITE_HEIGHT);
        Vector2 change = new Vector2(PLAYER_GUN_STATS[statIndex][4] - PLAYER_GUN_STATS[statIndex][2], PLAYER_GUN_STATS[statIndex][5] - PLAYER_GUN_STATS[statIndex][3]);
        return new Vector2[] {joint, change};
    }
    /**
     * Calculate angle with formula
     * @param fireLoc a Vector2 array with {joint, change}
     * @return angle of rotation
     */
    public double getAngle(Vector2[] fireLoc) {
        double x = myGame.getMousePosition().x - fireLoc[0].x;
        double y = myGame.getMousePosition().y - fireLoc[0].y;
        double r = fireLoc[1].y;
        double angle1 = Math.acos((y * Math.sqrt(y * y + x * x - r * r) + r * x) / (y * y + x * x)) - Math.toRadians(90);
        double angle2 = Math.acos((-y * Math.sqrt(y * y + x * x - r * r) + r * x) / (y * y + x * x)) - Math.toRadians(90);
        double angle;
        Vector2 startingPoint;
        if (shootingState == PlayerState.ShootingLeft) {
            startingPoint = rotateAroundPoint(fireLoc[0], new Vector2(fireLoc[0].x, fireLoc[0].y + fireLoc[1].y), angle1);
            if (startingPoint.x <= myGame.getMousePosition().x) {
                angle1 = (angle1 + Math.toRadians(180)) * -1;
            }
            angle = (float) angle1;
        } else {
            startingPoint = rotateAroundPoint(fireLoc[0], new Vector2(fireLoc[0].x, fireLoc[0].y + fireLoc[1].y), angle2);
            if (startingPoint.x >= myGame.getMousePosition().x) {
                angle2 = (Math.toRadians(180) - angle2);
            }
            angle = (float) angle2;
        }
        if (Double.isNaN(angle)) {
            return 0;
        }
        //System.out.println("Angle getter3 test: (" + Math.toDegrees(angle1) + ", " + Math.toDegrees(angle2));
        return angle;
    }

    /**
     * Calculate angle with different formula
     * @param fireLoc a Vector2 array with {joint, change}
     * @return angle of rotation
     */
    public double getAngle2(Vector2[] fireLoc) {
        double a = myGame.getMousePosition().x - fireLoc[0].x;
        double b = myGame.getMousePosition().y - fireLoc[0].y;
        double r = fireLoc[1].y;

        float angle1 = (float) (Math.acos((2*r*a + Math.sqrt(4*r*r*a*a-4*(a*a+b*b)*(r*r-b*b)))/(2*(a*a+b*b))) - Math.toRadians(90));
        float angle2 = (float) (Math.acos((2*r*a - Math.sqrt(4*r*r*a*a-4*(a*a+b*b)*(r*r-b*b)))/(2*(a*a+b*b))) - Math.toRadians(90));
        if (myGame.getMousePosition().y < fireLoc[0].y && shootingState == PlayerState.ShootingRight || myGame.getMousePosition().y > fireLoc[0].y && shootingState == PlayerState.ShootingLeft) {
            angle = angle1;
            //System.out.println(1);
        } else {
            angle = angle2;
            //System.out.println(2);
        }
        //System.out.println("Angle getter test: (" + Math.toDegrees(angle1) + ", " + Math.toDegrees(angle2));
        //System.out.println("Accuracy og: " + equation2(fireLoc[0], fireLoc[1].y, new Vector2(myGame.getMousePosition().x, myGame.getMousePosition().y), angle));
        return angle;
    }
    /**
     * Calculate angle with approximation method
     * @param fireLoc a Vector2 array with {joint, change}
     * @return angle of rotation
     */
    public double getAngle3(Vector2[] fireLoc) {
        double estimation = (float) Math.atan((fireLoc[0].y + fireLoc[1].y - myGame.getMousePosition().y) / (fireLoc[0].x - myGame.getMousePosition().x));
        Vector2 center = fireLoc[0];
        Vector2 startingPoint = new Vector2(fireLoc[0].x, fireLoc[0].y + fireLoc[1].y);
        Vector2 spawnPoint = new Vector2(fireLoc[0].x + fireLoc[1].x, fireLoc[0].y + fireLoc[1].y);
        Vector2 mousePos = new Vector2( myGame.getMousePosition().x,  myGame.getMousePosition().y);
        return bisectionMethod(Math.toDegrees(estimation) - 20, Math.toDegrees(estimation) + 20, center, startingPoint, spawnPoint, mousePos);
    }

    /**
     * Calculate the accuracy of the angle
     * @param fireLoc a Vector2 array with {joint, change}
     * @param angle of rotation
     * @return accuracy (number closer to 0 means more accurate)
     */
    public double calculateAccuracy(Vector2[] fireLoc, double angle) {
        Vector2 center = fireLoc[0];
        Vector2 startingPoint = new Vector2(fireLoc[0].x, fireLoc[0].y + fireLoc[1].y);
        Vector2 spawnPoint = new Vector2(fireLoc[0].x + fireLoc[1].x, fireLoc[0].y + fireLoc[1].y);
        Vector2 mousePos = new Vector2( myGame.getMousePosition().x,  myGame.getMousePosition().y);
        return equation(center, startingPoint, spawnPoint, mousePos, angle);
    }
    /**
     * Checks if the player is holding down the fire button. Also determines where the mouse is to show where to aim
     * the shot.
     */
    public void fireCheck() {
        if (myGame.checkKeybind("Fire")) {

            if (myGame.getMousePosition().x < getBodyCenter().x) {
                shootingState = PlayerState.ShootingLeft;
            } else {
                shootingState = PlayerState.ShootingRight;
            }
            Vector2[] fireLoc = getFireLocation();
            angle = (float) getAngle(fireLoc);

            //System.out.println("difference: " + Math.abs(Math.toDegrees(getAngle(fireLoc)) - Math.toDegrees(getAngle2(fireLoc))));

            Vector2 startingPoint = rotateAroundPoint(fireLoc[0], new Vector2(fireLoc[0].x, fireLoc[0].y + fireLoc[1].y), angle);
            Vector2 spawnPoint = rotateAroundPoint(fireLoc[0], new Vector2(fireLoc[0].x + fireLoc[1].x, fireLoc[0].y + fireLoc[1].y), angle);
            //System.out.println("angle: " + Math.toDegrees(angle) + ", accuracy: " + calculateAccuracy(fireLoc, angle));
            ((RangedWeapon) weapon).attack(spawnPoint, startingPoint, new Vector2(myGame.getMousePosition().x, myGame.getMousePosition().y));
            // debug
            pointsToRender.add(startingPoint);
            pointsToRender.add(spawnPoint);
            pointsToRender.add(fireLoc[0]);

            combatTimer = myGame.timePassed;
        } else {
            shootingState = PlayerState.NotShooting;
        }
    }
    public void debug() {
        shapeRenderer.setProjectionMatrix(myGame.gsm.camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < pointsToRender.size(); i++) {
            shapeRenderer.setColor(shapeRendererColors[i % shapeRendererColors.length]);
            float length = 0.05f;
            shapeRenderer.rect(pointsToRender.get(0).x - length / 2, pointsToRender.get(0).y - length / 2, length, length);
            pointsToRender.remove(0);
        }
        shapeRenderer.end();
    }
    /**
     * Handles user input. Adjusts player velocity accordingly. Also checks if the player is out of bounds. Called
     * 240 times per second, regardless of computer speed.
     */
    public void update() {
        if (getBodyCenter().y < 0) death();
        resetPlayerState();
        onGround();
        if (inventoryCheck()) return;
        leftCheck();
        rightCheck();
        jumpCheck();
        wallClimbCheck();

        // Jump animation needs to be fixed for this code to be uncommented
        if (myGame.timePassed - lastJump < 0.4f) {
            currentState = jumpState;
        }
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
        speedLabel.setText("Speed: " + (int) Math.abs(body.getLinearVelocity().x));
        timePassed += Gdx.graphics.getDeltaTime();
    }
    /**
     * Gets the current player animation.
     * @return a textureRegion with the current frame.
     */
    public TextureRegion getCurrentFrame() {
        TextureRegion currentFrame;
        if (currentState == null) return stillRight.getKeyFrame(timePassed, true);
        switch(currentState) {
            case WalkingLeft:
                if (shootingState == PlayerState.ShootingLeft) {
                    currentFrame = shootingLeftWalkingLeft.getKeyFrame(timePassed, true);
                } else if (shootingState == PlayerState.ShootingRight) {
                    currentFrame = shootingRightWalkingLeft.getKeyFrame(timePassed, true);
                } else {
                    currentFrame = walkingLeft.getKeyFrame(timePassed, true);
                }
                break;
            case WalkingRight:
                if (shootingState == PlayerState.ShootingLeft) {
                    currentFrame = shootingLeftWalkingRight.getKeyFrame(timePassed, true);
                } else if (shootingState == PlayerState.ShootingRight) {
                    currentFrame = shootingRightWalkingRight.getKeyFrame(timePassed, true);
                } else {
                    currentFrame = walkingRight.getKeyFrame(timePassed, true);
                }
                break;
            case JumpingRight:
                if (shootingState == PlayerState.ShootingLeft) {
                    currentFrame = shootingLeftJumpRight.getKeyFrame(timePassed, true);
                } else if (shootingState == PlayerState.ShootingRight) {
                    currentFrame = shootingRightJumpRight.getKeyFrame(timePassed, true);
                } else {
                    currentFrame = jumpRight.getKeyFrame(timePassed, true);
                }
                break;
            case JumpingLeft:
                if (shootingState == PlayerState.ShootingLeft) {
                    currentFrame = shootingLeftJumpLeft.getKeyFrame(timePassed, true);
                } else if (shootingState == PlayerState.ShootingRight) {
                    currentFrame = shootingRightJumpLeft.getKeyFrame(timePassed, true);
                } else {
                    currentFrame = jumpLeft.getKeyFrame(timePassed, true);
                }
                break;
            case ClimbingRight:
                currentFrame = climbRight.getKeyFrame(timePassed, true);
                break;
            case ClimbingLeft:
                currentFrame = climbLeft.getKeyFrame(timePassed, true);
                break;
            case DashingLeft:
                if (shootingState == PlayerState.ShootingLeft) {
                    currentFrame = shootingLeftDashLeft.getKeyFrame(timePassed, true);
                } else if (shootingState == PlayerState.ShootingRight) {
                    currentFrame = shootingRightDashLeft.getKeyFrame(timePassed, true);
                } else {
                    currentFrame = dashLeft.getKeyFrame(timePassed, true);
                }
                break;
            case DashingRight:
                if (shootingState == PlayerState.ShootingLeft) {
                    currentFrame = shootingLeftDashRight.getKeyFrame(timePassed, true);
                } else if (shootingState == PlayerState.ShootingRight) {
                    currentFrame = shootingRightDashRight.getKeyFrame(timePassed, true);
                } else {
                    currentFrame = dashRight.getKeyFrame(timePassed, true);
                }
                break;
            case StillLeft:
                if (shootingState == PlayerState.ShootingLeft) {
                    currentFrame = shootingLeftWalkingLeft.getKeyFrame(0, true);
                } else if (shootingState == PlayerState.ShootingRight) {
                    currentFrame = shootingRightWalkingLeft.getKeyFrame(0, true);
                } else {
                    currentFrame = stillLeft.getKeyFrame(timePassed, true);
                }
                break;
            default:
                if (shootingState == PlayerState.ShootingLeft) {
                    currentFrame = shootingLeftWalkingRight.getKeyFrame(0, true);
                } else if (shootingState == PlayerState.ShootingRight) {
                    currentFrame = shootingRightWalkingRight.getKeyFrame(0, true);
                } else {
                    currentFrame = stillRight.getKeyFrame(timePassed, true);
                }
                break;

        }
        return currentFrame;
    }
    /**
     * Render the gun animation.
     */
    public void renderGun() {
        if (currentState == null) return;
        TextureRegion gunKeyFrame = null;
        int statIndex = getStatIndex();
        if (statIndex == -1) return;
        // Gun animation: (float) (myGame.timePassed - weapon.lastUse)
        myGame.batch.draw(gunAnimations[statIndex].getKeyFrame(0), body.getPosition().x - PLAYER_HORIZONTAL_OFFSET + PLAYER_GUN_STATS[statIndex][0] * PLAYER_SPRITE_WIDTH - PLAYER_GUN_STATS[statIndex][2],
            body.getPosition().y - PLAYER_VERTICAL_OFFSET + PLAYER_GUN_STATS[statIndex][1] * PLAYER_SPRITE_HEIGHT - PLAYER_GUN_STATS[statIndex][3], PLAYER_GUN_STATS[statIndex][2],
            PLAYER_GUN_STATS[statIndex][3], PLAYER_GUN_STATS[statIndex][6], PLAYER_GUN_STATS[statIndex][7], 1, 1, (float) Math.toDegrees(angle));
    }

    /**
     * Render the character animation
     */
    public void renderCharacter() {
        if (shootingState == PlayerState.ShootingLeft && (currentState == PlayerState.StillLeft || currentState == PlayerState.DashingLeft
            || currentState == PlayerState.JumpingLeft || currentState == PlayerState.WalkingLeft)) {
            renderGun();
            myGame.batch.draw(getCurrentFrame(), body.getPosition().x - PLAYER_HORIZONTAL_OFFSET, body.getPosition().y - PLAYER_VERTICAL_OFFSET, PLAYER_SPRITE_WIDTH, PLAYER_SPRITE_HEIGHT);
        } else {
            myGame.batch.draw(getCurrentFrame(), body.getPosition().x - PLAYER_HORIZONTAL_OFFSET, body.getPosition().y - PLAYER_VERTICAL_OFFSET, PLAYER_SPRITE_WIDTH, PLAYER_SPRITE_HEIGHT);
            renderGun();
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
        renderCharacter();
        // Uncomment this to draw a shadow beneath player.
        //if (touchingFloor) myGame.batch.draw(myGame.shadow, body.getPosition().x - HORIZONTAL_OFFSET, body.getPosition().y - VERTICAL_OFFSET - 5 * UNIT_SCALE, 32 * UNIT_SCALE, 12 * UNIT_SCALE);
        healthbar.maxHealth = PLAYER_HEALTH + additionalHealth;
        if (PLAYER_HEALTH + additionalHealth < health) health = PLAYER_HEALTH + additionalHealth;
        myGame.batch.draw(healthbar.getHealthBar(), body.getPosition().x - PLAYER_HORIZONTAL_OFFSET, body.getPosition().y + PLAYER_HITBOX_HEIGHT + HEALTHBAR_OFFSET, PLAYER_SPRITE_WIDTH, HEALTHBAR_HEIGHT);
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
        //super.death();
        myGame.listener.resetContacts();
        //myGame.table.clear();
        health = healthbar.maxHealth; //
        body.setTransform(PLAYER_INITIAL_X_POSITION, PLAYER_INITIAL_Y_POSITION, 0);
        body.setLinearVelocity(0, 0);
        //weapon.destroy();
        //myGame.player = new Player(myGame, PLAYER_INITIAL_X_POSITION, PLAYER_INITIAL_Y_POSITION);
        //myGame.player.initializeOnScreenPlayerStats();
    }
}
