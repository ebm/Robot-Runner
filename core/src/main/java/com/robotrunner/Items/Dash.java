package com.robotrunner.Items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.robotrunner.Entities.PlayerPackage.Player;
import com.robotrunner.Entities.PlayerPackage.PlayerState;
import com.robotrunner.States.MyGame;

import static com.robotrunner.World.Constants.*;

public class Dash extends Ability{
    PlayerState state;
    public Dash(int id, float x, float y, Texture itemTexture, MyGame myGame) {
        super(id, ItemType.Ability, x, y, itemTexture, myGame);
        lastUse = 0;
        state = myGame.player.currentState;
        cooldown = PLAYER_DASH_COOLDOWN;
    }

    @Override
    public void apply() {
        if (myGame.checkKeybind("Ability") && (myGame.timePassed - lastUse) > cooldown) {
            System.out.println("ability");
            float dashXVelocity = myGame.player.body.getLinearVelocity().x;
            float dashYVelocity = PLAYER_DASH_VERTICAL_VELOCITY;
            if (myGame.checkKeybind("Move Left") && !myGame.checkKeybind("Move Right")) {
                dashXVelocity = -PLAYER_DASH_HORIZONTAL_VELOCITY * myGame.player.speedMultiplier;
                state = PlayerState.DashingLeft;
            } else if (myGame.checkKeybind("Move Right") && !myGame.checkKeybind("Move Left")) {
                dashXVelocity = PLAYER_DASH_HORIZONTAL_VELOCITY * myGame.player.speedMultiplier;
                state = PlayerState.DashingRight;
            }
            myGame.player.body.setTransform(myGame.player.body.getPosition().x, myGame.player.body.getPosition().y + UNIT_SCALE, 0);
            myGame.player.body.setLinearVelocity(dashXVelocity, dashYVelocity);
            lastUse = myGame.timePassed;
        }
        if (myGame.timePassed - lastUse < 0.5f && myGame.player.contactFeet == 0) {
            myGame.player.currentState = state;
        }
    }

    @Override
    public String toString() {
        return "Dash ability. Cooldown: " + (int) cooldown + " seconds.";
    }
}
