package com.robotrunner.Entities.MonsterPackage;

import com.badlogic.gdx.Gdx;
import com.robotrunner.Entities.Entity;
import com.robotrunner.Entities.Healthbar;
import com.robotrunner.Entities.PlayerPackage.Player;
import com.robotrunner.States.MyGame;

import static com.robotrunner.World.Constants.*;

public abstract class Monster extends Entity {
    Healthbar healthbar;
    Range range;
    double combatTimer;
    public Monster(MyGame myGame, float health) {
        super(myGame, health, Monster.class, Player.class, CATEGORY_BITS_MONSTER);
        combatTimer = 0;
    }
    public void update() {
        if (health < healthbar.maxHealth && (myGame.timePassed - combatTimer) > MONSTER_COMBAT_TIMER) {
            health += Gdx.graphics.getDeltaTime() * PLAYER_HEALTH_REGEN_PER_SEC;
            if (health >= healthbar.maxHealth) health = healthbar.maxHealth;
        }
    }
    public void takeDamage(float damage) {
        super.takeDamage(damage);
        combatTimer = myGame.timePassed;
    }
}
