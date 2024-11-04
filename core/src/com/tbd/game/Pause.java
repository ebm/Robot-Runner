package com.tbd.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

public class Pause implements Screen {
    GameStateManager gsm;
    public Pause(GameStateManager gsm) {
        this.gsm = gsm;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        ScreenUtils.clear(0, 0, 0, 1);
        gsm.batch.setProjectionMatrix(gsm.camera.combined);
        gsm.batch.begin();

        gsm.batch.end();
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            gsm.setScreen(gsm.myGame);
        }
    }

    @Override
    public void resize(int width, int height) {
        //gsm.vp.update(width, height, true);
        gsm.vp.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
