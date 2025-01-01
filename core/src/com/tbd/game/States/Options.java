package com.tbd.game.States;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tbd.game.World.KeyType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Options extends InputAdapter implements Screen, InputProcessor {
    GameStateManager gsm;
    Stage stage;
    Table mainTable;
    Table controlTable;
    Table videoSettingsTable;
    boolean canEscape;
    enum ControlState {
        Main, Controls, VideoSettings
    }
    ControlState currentState;
    boolean changed;
    int currKeyCode;
    int currButtonCode;
    boolean receivingInput = false;
    InputMultiplexer multiplexer;
    String currString;
    HashMap<String, TextButton> keybindMap;
    ScrollPane sp;
    Table outsideTable;
    void updateVisual() {
        keybindMap.get(currString).setText(gsm.myGame.keybinds.keybinds.get(currString).getKeycodeString());
    }
    void handleKeybind() {
        canEscape = false;
        if (currKeyCode == Input.Keys.ESCAPE) {
            gsm.myGame.keybinds.keybinds.put(currString, new KeyType('k', -99));
            updateVisual();
        } else if (currKeyCode != -99){
            gsm.myGame.keybinds.keybinds.put(currString, new KeyType('k', currKeyCode));
            updateVisual();
        } else if (currButtonCode != -99) {
            gsm.myGame.keybinds.keybinds.put(currString, new KeyType('b', currButtonCode));
            updateVisual();
        } else {
            receivingInput = true;
        }
    }
    public Options(GameStateManager gsm) {
        this.gsm = gsm;
        changed = false;
        multiplexer = new InputMultiplexer();

        stage = new Stage(new ScreenViewport());
        //stage = new Stage(new FitViewport(1920,1080));
        mainTable = new Table();
        mainTable.setFillParent(true);
        //table.setDebug(true);

        stage.addActor(mainTable);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        TextureRegion buttonUp = new TextureRegion(new Texture(Gdx.files.internal("buttonUp.png")));
        TextureRegion buttonDown = new TextureRegion(new Texture(Gdx.files.internal("buttonDown.png")));
        textButtonStyle.up = new TextureRegionDrawable(buttonUp);
        textButtonStyle.down = new TextureRegionDrawable(buttonDown);
        textButtonStyle.pressedOffsetX = -1;
        textButtonStyle.pressedOffsetY = -1;
        textButtonStyle.font = gsm.font;

        TextButton backButton = new TextButton("Back", textButtonStyle);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                gsm.setScreen(gsm.pause);
                changed = true;
            }
        });

        TextButton videoSettingsButton = new TextButton("Video Settings", textButtonStyle);
        videoSettingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                currentState = ControlState.VideoSettings;
                changed = true;
            }
        });

        TextButton controlsButton = new TextButton("Controls", textButtonStyle);
        controlsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                currentState = ControlState.Controls;
                changed = true;
            }
        });

        mainTable.add(backButton);
        mainTable.row();
        mainTable.add(videoSettingsButton).pad(30);
        mainTable.row();
        mainTable.add(controlsButton);

        controlTable = new Table();
        controlTable.setFillParent(true);
        //stage.addActor(controlTable);
        //controlTable.setVisible(false);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = gsm.font;
        keybindMap = new HashMap<>();
        for (String s : gsm.myGame.keybinds.keybinds.keySet()) {
            Label currLabel = new Label(s + ": ", labelStyle);
            TextButton currKeybindButton = new TextButton(gsm.myGame.keybinds.keybinds.get(s).getKeycodeString(), textButtonStyle);
            keybindMap.put(s, currKeybindButton);
            currKeybindButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {
                    System.out.println("button: " + s);
                    currString = s;
                    receivingInput = true;
                }
            });
            controlTable.add(currLabel).pad(30);
            controlTable.add(currKeybindButton);
            controlTable.row();
        }
        canEscape = false;
        currKeyCode = -99;
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown (int keycode) {
                if (receivingInput) {
                    currKeyCode = keycode;
                    receivingInput = false;
                    handleKeybind();
                }
                else {
                    currKeyCode = -99;
                }
                System.out.println("down: currKeyCode = " + currKeyCode);
                return true;
            }
            @Override
            public boolean keyUp (int keycode) {
                currKeyCode = -99;
                System.out.println("up: currKeyCode = " + currKeyCode);
                return true;
            }
            @Override
            public boolean touchDown (int x, int y, int pointer, int button) {
                if (receivingInput) {
                    currButtonCode = button;
                    receivingInput = false;
                    handleKeybind();
                }
                else {
                    currButtonCode = -99;
                }
                System.out.println("down: currButtonCode = " + currButtonCode);
                return true;
            }
            @Override
            public boolean touchUp (int x, int y, int pointer, int button) {
                currButtonCode = -99;
                System.out.println("up: currButtonCode = " + currKeyCode);
                return true;
            }
        });
        sp = new ScrollPane(controlTable);
        sp.setBounds(0, 0, 500, 800);
        outsideTable = new Table();
        outsideTable.add(sp);//.height(1000);
        outsideTable.setFillParent(true);
        stage.addActor(outsideTable);
        outsideTable.setVisible(false);
        outsideTable.left();
        outsideTable.top();
        outsideTable.setDebug(true);
        //sp.setVisible(false);
    }

    @Override
    public void show() {
        //Gdx.input.setInputProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        gsm.batch.setProjectionMatrix(gsm.camera.combined);
        gsm.batch.begin();
        stage.act();
        stage.draw();
        gsm.batch.end();
        if (changed) {
            changed = false;
            if (currentState == ControlState.Controls) {
                //controlTable.setVisible(true);
                mainTable.setVisible(false);
                //sp.setVisible(true);
                outsideTable.setVisible(true);
            } else if (currentState == ControlState.VideoSettings) {

            } else if (currentState == ControlState.Main) {
                //controlTable.setVisible(false);
                mainTable.setVisible(true);
                //sp.setVisible(false);
                outsideTable.setVisible(false);
            }
        }
        if (!Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            canEscape = true;
        }
        if (canEscape && Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            gsm.myGame.keybinds.saveKeybinds();
            if (currentState == ControlState.Main) gsm.setScreen(gsm.pause);
            else {
                canEscape = false;
                currentState = ControlState.Main;
                changed = true;
            }

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
