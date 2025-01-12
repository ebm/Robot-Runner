package com.robotrunner.World;

import com.badlogic.gdx.math.Vector2;

public class Constants {
    // Game-Wide Constants:
    public static float METERS_PER_PIXEL = 1f;
    public static float UNIT_SCALE = METERS_PER_PIXEL * 1/32f;
    public static float VISIBLE_HORIZONTAL_TILES = 30 * METERS_PER_PIXEL;
    public static float VISIBLE_VERTICAL_TILES = 18 * METERS_PER_PIXEL;
    public static float TIME_STEP = 1/240f;
    public static float GRAVITY = -20 * METERS_PER_PIXEL;
    public static float CAMERA_Y_OFFSET = 0;//3 * METERS_PER_PIXEL;
    public static float HEALTHBAR_OFFSET = 0.25f * METERS_PER_PIXEL;
    public static float HEALTHBAR_HEIGHT = 0.25f * METERS_PER_PIXEL;

    // Entity Constants
    public static float ENTITY_APPENDAGE_DISTANCE_FROM_EDGE = UNIT_SCALE;
    public static float ENTITY_APPENDAGE_THICKNESS = UNIT_SCALE / 3f;
    public static float ENTITY_APPENDAGE_OFFSET = UNIT_SCALE * 3;
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
    public static float PLAYER_SPRITE_WIDTH = 1f;
    public static float PLAYER_SPRITE_HEIGHT = 1f;
    public static float PLAYER_HITBOX_WIDTH = PLAYER_SPRITE_WIDTH / 2f;
    public static float PLAYER_HITBOX_HEIGHT = PLAYER_SPRITE_HEIGHT / 1.25f;
    public static int PLAYER_MAXIMUM_JUMPS = 2;
    public static float PLAYER_HORIZONTAL_OFFSET = (PLAYER_SPRITE_WIDTH - PLAYER_HITBOX_WIDTH) / 2;
    public static float PLAYER_VERTICAL_OFFSET = (PLAYER_SPRITE_HEIGHT - PLAYER_HITBOX_HEIGHT) / 2 - 0.03f;
    public static float PLAYER_COMBAT_TIMER = 5;
    public static float PLAYER_HEALTH_REGEN_PER_SEC = 20;
    public static float PLAYER_DASH_COOLDOWN = 3;
    public static int PLAYER_INVENTORY_ROWS = 3;
    public static int PLAYER_INVENTORY_COLS = 4;
    public static int PLAYER_INVENTORY_SPACE = PLAYER_INVENTORY_ROWS * PLAYER_INVENTORY_COLS;
    public static int PLAYER_ATTRIBUTE_SPACE = 4;
    public static float SPRITE_DIMENSIONS_PIXELS = 855;
    public static float SPRITE_SCALE_UNITS =  PLAYER_SPRITE_WIDTH / SPRITE_DIMENSIONS_PIXELS;

    // Stats {position.x, position.y, origin.x, origin.y, change.x, change.y, width_image, height_image}
    // SL/SR = ShootingLeft/ShootingRight
    // ML/MR = MovingLeft/MovingRight
    public static float[] STATS_SLML = {437 * SPRITE_SCALE_UNITS, 390 * SPRITE_SCALE_UNITS, 1262 * SPRITE_SCALE_UNITS, 163 * SPRITE_SCALE_UNITS,
        729 * SPRITE_SCALE_UNITS, 184 * SPRITE_SCALE_UNITS, 1281 * SPRITE_SCALE_UNITS, 296 * SPRITE_SCALE_UNITS};
    public static float[] STATS_SLMR = {230 * SPRITE_SCALE_UNITS, 411 * SPRITE_SCALE_UNITS, 1269 * SPRITE_SCALE_UNITS, 71 * SPRITE_SCALE_UNITS,
        727 * SPRITE_SCALE_UNITS, 171 * SPRITE_SCALE_UNITS, 1292 * SPRITE_SCALE_UNITS, 286 * SPRITE_SCALE_UNITS};
    public static float[] STATS_SRML = {296 * SPRITE_SCALE_UNITS, 446 * SPRITE_SCALE_UNITS, 20 * SPRITE_SCALE_UNITS, 97 * SPRITE_SCALE_UNITS,
        524 * SPRITE_SCALE_UNITS, 191 * SPRITE_SCALE_UNITS, 1254 * SPRITE_SCALE_UNITS, 301 * SPRITE_SCALE_UNITS};
    public static float[] STATS_SRMR = {339 * SPRITE_SCALE_UNITS, 391 * SPRITE_SCALE_UNITS, 20 * SPRITE_SCALE_UNITS, 162 * SPRITE_SCALE_UNITS,
        503 * SPRITE_SCALE_UNITS, 186 * SPRITE_SCALE_UNITS, 1233 * SPRITE_SCALE_UNITS, 295 * SPRITE_SCALE_UNITS};
    public static float[][] PLAYER_GUN_STATS = {STATS_SLML, STATS_SLMR, STATS_SRML, STATS_SRMR};

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
