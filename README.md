# Robot Runner

A 2D parkour shooter where a player controls a robot that jumps around and fights monsters. On their journey, a player kill monsters and collect items which grant the player abilities and stat increases.

# Features
A player can move around, jump, fires lasers, dash, wall climb, and more. The player has an inventory where they can store collected items, and equip the desired abilities.
There are lasers, monsters with various weapons, and cool parkour puzzles where the player attempts to avoid lasers and get to the top.

# Installation
The game was made to be a desktop application, but it can be ran from the browser from this link: https://ebm3.itch.io/robot-runner

# How to Play
The controls are adjustable. There is an options and controls menu accessible when pausing the game.
Default controls:
  A             -> LEFT
  D             -> RIGHT
  SPACE         -> JUMP
  MOUSE_LEFT    -> FIRE
  LEFT_SHIFT    -> ABILITY
  WALL_CLIMB    -> A/D (must be moving into a wall)
  INVENTORY     -> E
  PAUSE         -> ESCAPE

# Development
Robot Runner was coded in Java and developed with LibGDX (a game development framework that handles cross-platform game development and graphics calculations), and Box2D (a physics engine that handles collisions).
Code Outline:
In core/src/main/java/com.robotrunner, there will be 5 important folders with functions vital to the game.
States: Holds the classes that manage any currently displaying screen. For example, GameStateManager renders the current screen and houses the necessary logic for switching screens.
World: Holds the classes that manage the game world. For example, Listener handles any collisions, and passes that information to Entity or Map to handle.
Entities: Holds the classes for any entity in the game. For example, Player handles user input which controls the player's movement, renders the player, and adjusts the player depending on collisions (on the ground, or touching a wall, etc).
Weapons: Holds the classes for the weapons entities will use. For example, RangedWeapon spawns bullets which have a speed, damage, fire rate, and more.
Items: Holds classes for managing items on the map. For example, Dash is an item that appears on the map, and when a player picks it up and equips it, it gives the player a dash ability.


