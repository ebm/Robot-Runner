package com.tbd.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;

public class Menu extends State{
    TextButton playButton;
    public Menu(GameStateManager gsm) {
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
