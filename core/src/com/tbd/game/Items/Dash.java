package com.tbd.game.Items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.tbd.game.Entities.PlayerPackage.PlayerState;
import com.tbd.game.States.MyGame;

import static com.tbd.game.World.Constants.*;

public class Dash extends Ability{
    public Dash(int id, float x, float y, Texture itemTexture, MyGame myGame) {
        super(id, ItemType.Ability, x, y, itemTexture, myGame);
        lastUse = 0;
        cooldown = PLAYER_DASH_COOLDOWN;
    }

    @Override
    public void apply() {
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && (myGame.timePassed - lastUse) > cooldown) {
            float dashXVelocity = myGame.player.body.getLinearVelocity().x;
            float dashYVelocity = PLAYER_DASH_VERTICAL_VELOCITY;
            if (myGame.player.currentState == PlayerState.WalkingLeft) {
                dashXVelocity = -PLAYER_DASH_HORIZONTAL_VELOCITY * myGame.player.speedMultiplier;
            } else if (myGame.player.currentState == PlayerState.WalkingRight) {
                dashXVelocity = PLAYER_DASH_HORIZONTAL_VELOCITY * myGame.player.speedMultiplier;
            }
            myGame.player.body.setTransform(myGame.player.body.getPosition().x, myGame.player.body.getPosition().y + UNIT_SCALE, 0);
            myGame.player.body.setLinearVelocity(dashXVelocity, dashYVelocity);
            lastUse = myGame.timePassed;
        }
    }

    @Override
    public String toString() {
        return "Dash ability. Cooldown: " + (int) cooldown + " seconds.";
    }
}
