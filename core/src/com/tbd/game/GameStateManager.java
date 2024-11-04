package com.tbd.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.tbd.game.Constants.VISIBLE_HORIZONTAL_TILES;
import static com.tbd.game.Constants.VISIBLE_VERTICAL_TILES;

public class GameStateManager extends Game {
    public SpriteBatch batch;
    public OrthographicCamera camera;
    public Viewport vp;
    public BitmapFont font;
    public MyGame myGame;
    public Menu menu;
    public Pause pause;
    @Override
    public void create() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        vp = new ExtendViewport(VISIBLE_HORIZONTAL_TILES, VISIBLE_VERTICAL_TILES, camera);

        font = new BitmapFont(Gdx.files.internal("default.fnt"));

        myGame = new MyGame(this);
        menu = new Menu(this);
        pause = new Pause(this);
        setScreen(menu);
    }
    public void render() {
        super.render();
    }
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
