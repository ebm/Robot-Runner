package com.tbd.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Pause extends State {
    public Pause(GameStateManager gsm) {
        super(gsm);
    }
    @Override
    public void render() {
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            gsm.switchState(GameState.Play);
        }
    }

    @Override
    public void dispose() {

    }
}
