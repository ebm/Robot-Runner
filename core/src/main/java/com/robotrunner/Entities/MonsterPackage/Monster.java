package com.robotrunner.Entities.MonsterPackage;

import com.robotrunner.Entities.Entity;
import com.robotrunner.Entities.PlayerPackage.Player;
import com.robotrunner.States.MyGame;

import static com.robotrunner.World.Constants.*;

public abstract class Monster extends Entity {
    Range range;
    public Monster(MyGame myGame, float health) {
        super(myGame, health, Monster.class, Player.class, CATEGORY_BITS_MONSTER);
    }
}
