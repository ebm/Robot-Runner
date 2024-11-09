package com.tbd.game.World;

import com.badlogic.gdx.math.Vector2;

public class Constants {
    // Game-Wide Constants:
    public static float METERS_PER_PIXEL = 1f;
    public static float UNIT_SCALE = METERS_PER_PIXEL * 1/32f;
    public static float VISIBLE_HORIZONTAL_TILES = 30 * METERS_PER_PIXEL;
    public static float VISIBLE_VERTICAL_TILES = 18 * METERS_PER_PIXEL;
    public static float TIME_STEP = 1/240f;
    public static float GRAVITY = -20 * METERS_PER_PIXEL;
    public static float CAMERA_Y_OFFSET = 3 * METERS_PER_PIXEL;
    public static float HEALTHBAR_OFFSET = 0.25f * METERS_PER_PIXEL;
    public static float HEALTHBAR_HEIGHT = 0.25f * METERS_PER_PIXEL;

    // Entity Constants
    public static float ENTITY_APPENDAGE_DISTANCE_FROM_EDGE = UNIT_SCALE;
    public static float ENTITY_APPENDAGE_THICKNESS = UNIT_SCALE / 3f;
    public static short CATEGORY_BITS_PLAYER = 0x0002;
    public static short CATEGORY_BITS_MONSTER = 0x0004;
    public static short CATEGORY_BITS_LASER_PROJECTILE = 0x0008;
    public static short CATEGORY_BITS_MAP = 0x0008;

    // Player Constants
    public static float PLAYER_INITIAL_X_POSITION = 4 * METERS_PER_PIXEL;
    public static float PLAYER_INITIAL_Y_POSITION = 8 * METERS_PER_PIXEL;
    public static float PLAYER_HEALTH = 100;
    public static float PLAYER_HORIZONTAL_VELOCITY = 3.5f * METERS_PER_PIXEL;
    public static float PLAYER_HORIZONTAL_AIR_ACCELERATION = 0.08f * METERS_PER_PIXEL;
    public static float PLAYER_MAXIMUM_HORIZONTAL_AIR_VELOCITY = PLAYER_HORIZONTAL_VELOCITY * 1.5f;
    public static float PLAYER_JUMP_VELOCITY = 8.5f * METERS_PER_PIXEL;
    public static float PLAYER_WALLCLIMB_VELOCITY = 2.5f * METERS_PER_PIXEL;
    public static float PLAYER_WALLCLIMB_LENGTH_SECONDS = 1f;
    public static float PLAYER_DASH_HORIZONTAL_VELOCITY = 10f * METERS_PER_PIXEL;
    public static float PLAYER_DASH_VERTICAL_VELOCITY = 5f * METERS_PER_PIXEL;
    public static float PLAYER_SPRITE_WIDTH = 32f * UNIT_SCALE;
    public static float PLAYER_SPRITE_HEIGHT = 32f * UNIT_SCALE;
    public static float PLAYER_HITBOX_WIDTH = 22f * UNIT_SCALE;
    public static float PLAYER_HITBOX_HEIGHT = 28f * UNIT_SCALE;
    public static int PLAYER_MAXIMUM_JUMPS = 2;
    public static float PLAYER_HORIZONTAL_OFFSET = 5 * UNIT_SCALE;
    public static float PLAYER_VERTICAL_OFFSET = 2 * UNIT_SCALE;
    public static float PLAYER_COMBAT_TIMER = 5;
    public static float PLAYER_HEALTH_REGEN_PER_SEC = 20;
    public static float PLAYER_DASH_COOLDOWN = 3;
    public static int PLAYER_INVENTORY_ROWS = 3;
    public static int PLAYER_INVENTORY_COLS = 4;
    public static int PLAYER_INVENTORY_SPACE = PLAYER_INVENTORY_ROWS * PLAYER_INVENTORY_COLS;
    public static int PLAYER_ATTRIBUTE_SPACE = 4;

    // Bullet
    public static float BULLET_DESPAWN_HITBOX = 1/5f;
    public static float BULLET_EXPIRY_SECONDS = 10;

    // Laser
    public static float LASER_ATTACK_DAMAGE_PER_SECOND = 300;
    public static float LASER_MAXIMUM_DISTANCE = 15;
    public static float LASER_MINIMUM_DISTANCE = 1.1f;
    public static float LASER_THREAT_SECONDS = 3;
    public static float LASER_SAFE_SECONDS = 1;
    public static float LASER_RADIUS = 0.1f * METERS_PER_PIXEL;
    public static float LASER_TEST_PROJECTILE_VELOCITY = 100/*30f * METERS_PER_PIXEL*/;

    // Golem
    public static float GOLEM_INITIAL_X_POSITION = 50 * METERS_PER_PIXEL;
    public static float GOLEM_INITIAL_Y_POSITION = 72 * METERS_PER_PIXEL;
    public static float GOLEM_HEALTH = 300;
    public static float GOLEM_HORIZONTAL_VELOCITY = 2 * METERS_PER_PIXEL;
    public static float GOLEM_JUMP_VELOCITY = 10 * METERS_PER_PIXEL;
    public static float GOLEM_ATTACK_DAMAGE = 55;
    public static float GOLEM_ATTACK_COOLDOWN = 0.5f;
    public static float GOLEM_MAXIMUM_HORIZONTAL_JUMP_VELOCITY = 40 * METERS_PER_PIXEL;
    public static float GOLEM_ACTIVATION_RANGE = 20 * METERS_PER_PIXEL;
    public static float GOLEM_JUMP_COOLDOWN = 3;
    public static float GOLEM_DIRECTION_DELAY = 0.5f;
    public static float GOLEM_HITBOX_WIDTH = 1.5f;//3 * METERS_PER_PIXEL;
    public static float GOLEM_HITBOX_HEIGHT = 1.5f;//3 * METERS_PER_PIXEL;

    // Bat
    public static float BAT_HEALTH = 50;
    public static float BAT_VELOCITY = 3 * METERS_PER_PIXEL;
    public static float BAT_VERTICAL_VELOCITY = 1 * METERS_PER_PIXEL;
    public static float BAT_VERTICAL_DELAY = 0.2f;
    public static float BAT_ATTACK_DAMAGE = 20;
    public static float BAT_ACTIVATION_RANGE = 20 * METERS_PER_PIXEL;
    public static float BAT_RADIUS = 0.5f * METERS_PER_PIXEL;
    public static float BAT_WEAPON_RADIUS = BAT_RADIUS + 0.1f * METERS_PER_PIXEL;

    // Spaceship
    public static float SPACESHIP_HEALTH = 600;
    public static float SPACESHIP_WIDTH = 3;
    public static float SPACESHIP_HEIGHT = 1;
    public static float SPACESHIP_VERTICAL_VELOCITY = 0.15f;
    public static float SPACESHIP_ACTIVATION_RANGE = 20;
    public static float SPACESHIP_VELOCITY = 8;


    public static float getDistance(Vector2 a, Vector2 b) {
        return (float) Math.sqrt((a.y - b.y) * (a.y - b.y) + (a.x - b.x) * (a.x - b.x));
    }
}
