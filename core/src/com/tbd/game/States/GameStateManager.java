package com.tbd.game.States;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tbd.game.World.Map;

import static com.tbd.game.World.Constants.*;

public class GameStateManager extends Game {
    public SpriteBatch batch;
    public OrthographicCamera camera;
    public Viewport vp;
    public BitmapFont font;
    public MyGame myGame;
    public Menu menu;
    public Pause pause;
    public Options options;
    public boolean initialized;
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
        initialized = false;
        ready = false;
    }
    public void render() {
        if (myGame != null) {
            if (!initialized && myGame.assetManager.update()) {
                initialized = true;
                OrthogonalTiledMapRenderer orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(myGame.assetManager.get("map2/tilemap.tmx"), UNIT_SCALE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // do something important here, asynchronously to the rendering thread
                        GameStateManager.this.myGame.init(orthogonalTiledMapRenderer);
                        // post a Runnable to the rendering thread that processes the result
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                // process the result, e.g. add it to an Array<Result> field of the ApplicationListener.
                                ready = true;
                                pause = new Pause(GameStateManager.this);
                                options = new Options(GameStateManager.this);
                            }
                        });
                    }
                }).start();
            }
        }
        super.render();
    }
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
