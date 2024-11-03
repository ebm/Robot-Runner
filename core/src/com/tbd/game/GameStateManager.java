package com.tbd.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.tbd.game.Constants.VISIBLE_HORIZONTAL_TILES;
import static com.tbd.game.Constants.VISIBLE_VERTICAL_TILES;

public class GameStateManager extends ApplicationAdapter {
    public SpriteBatch batch;
    public OrthographicCamera camera;
    public Viewport vp;
    public GameState currentState;
    private State referenceState;
    public MyGame myGame;
    public Menu menu;
    public Pause pause;
    @Override
    public void create() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        vp = new ExtendViewport(VISIBLE_HORIZONTAL_TILES, VISIBLE_VERTICAL_TILES, camera);

        switchState(GameState.Menu);
    }
    public void switchState(GameState newState) {
        currentState = newState;
        if (currentState == GameState.Menu) {
            if (menu == null) {
                menu = new Menu(this);
            }
            referenceState = menu;
        } else if (currentState == GameState.Play) {
            if (myGame == null) {
                myGame = new MyGame(this);
            }
            referenceState = myGame;
        } else if (currentState == GameState.Pause) {
            if (pause == null) {
                pause = new Pause(this);
            }
            referenceState = pause;
        }
    }
    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if (referenceState != null) referenceState.render();
        batch.end();
    }
    @Override
    public void resize(int width, int height) {
        vp.update(width, height);
    }
    public void dispose() {
        myGame.dispose();
    }
}
