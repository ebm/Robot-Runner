package com.tbd.game.Entities.MonsterPackage;

import com.tbd.game.Entities.Entity;
import com.tbd.game.Entities.PlayerPackage.Player;
import com.tbd.game.States.MyGame;

import static com.tbd.game.World.Constants.*;

public abstract class Monster extends Entity {
    Range range;
    public Monster(MyGame myGame, float health) {
        super(myGame, health, Monster.class, Player.class, CATEGORY_BITS_MONSTER);
    }
}
