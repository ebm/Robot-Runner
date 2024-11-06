package com.tbd.game.Entities.MonsterPackage;

import com.tbd.game.Entities.Entity;
import com.tbd.game.Entities.PlayerPackage.Player;
import com.tbd.game.States.MyGame;

public abstract class Monster extends Entity {
    Range range;
    public Monster(MyGame myGame, float health) {
        this.myGame = myGame;
        this.friendly = Monster.class;
        this.enemy = Player.class;
        this.health = health;
    }
}
