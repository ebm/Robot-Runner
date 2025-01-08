package com.robotrunner.States;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robotrunner.World.Map;

import static com.robotrunner.World.Constants.*;
/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameStateManager extends Game {
    public SpriteBatch batch;
    public OrthographicCamera camera;
    public Viewport vp;
    public BitmapFont font;
    public MyGame myGame;
    public Menu menu;
    public Pause pause;
    public Options options;
    public boolean ready;
    @Override
    public void create() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        vp = new ExtendViewport(VISIBLE_HORIZONTAL_TILES, VISIBLE_VERTICAL_TILES, camera);

        font = new BitmapFont(Gdx.files.internal("default.fnt"));

        myGame = new MyGame(this);
        menu = new Menu(this);

        setScreen(menu);
        ready = false;
    }
    public void render() {
        if (!ready && myGame.assetManager.update()) {
            myGame.init();
            pause = new Pause(this);
            options = new Options(this);
            ready = true;
        }
        super.render();
    }
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
