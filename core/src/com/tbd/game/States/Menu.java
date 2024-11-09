package com.tbd.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Menu implements Screen {
    GameStateManager gsm;
    Stage stage;
    Table table;
    public Menu(GameStateManager gsm) {
        this.gsm = gsm;

        stage = new Stage(new ScreenViewport());
        table = new Table();
        table.setFillParent(true);
        //table.setDebug(true);

        stage.addActor(table);
        TextButtonStyle textButtonStyle = new TextButtonStyle();
        TextureRegion buttonUp = new TextureRegion(new Texture(Gdx.files.internal("buttonUp.png")));
        TextureRegion buttonDown = new TextureRegion(new Texture(Gdx.files.internal("buttonDown.png")));
        textButtonStyle.up = new TextureRegionDrawable(buttonUp);
        textButtonStyle.down = new TextureRegionDrawable(buttonDown);
        textButtonStyle.pressedOffsetX = -1;
        textButtonStyle.pressedOffsetY = -1;
        textButtonStyle.font = gsm.font;
        TextButton textButton = new TextButton("Play", textButtonStyle);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                gsm.setScreen(gsm.myGame);
            }
        });
        LabelStyle labelStyle = new LabelStyle();
        labelStyle.font = gsm.font;
        Label label = new Label("Game", labelStyle);
        table.add(label).padBottom(30);
        table.row();
        table.add(textButton);
    }
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float v) {
        ScreenUtils.clear(0, 0, 0, 1);
        gsm.batch.setProjectionMatrix(gsm.camera.combined);
        //gsm.batch.begin();
        //gsm.batch.end();
        stage.act();
        stage.draw();
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            gsm.setScreen(gsm.myGame);
        }
    }

    @Override
    public void resize(int width, int height) {
        //gsm.vp.update(width, height, true);
        stage.getViewport().update(width, height, true);
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
