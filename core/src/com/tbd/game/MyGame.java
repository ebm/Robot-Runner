package com.tbd.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.*;
import com.tbd.game.Entities.MapEntity;
import com.tbd.game.Entities.MonsterPackage.Bat;
import com.tbd.game.Entities.MonsterPackage.Monster;
import com.tbd.game.Entities.PlayerPackage.Player;
import com.tbd.game.Weapons.Laser;

import static com.tbd.game.Constants.*;

import java.util.ArrayList;

public class MyGame extends State {
	public SpriteBatch batch;
	//OrthographicCamera camera;
	//Viewport vp;
	public World world; // 1 block = 2 meters = 64 pixels
	Box2DDebugRenderer debugRenderer;
	double accumulator;
	public double timePassed;
	public Player player;
	public TextureAtlas atlas;
	Map map;
	Texture shadow;
	public MapEntity mapEntity;
	public Listener listener;
	public ArrayList<Monster> activeMonsters;
	public ArrayList<Laser> activeLasers;
	public Texture golem;
	public Texture bullet;
	public Texture laserVertical;
	public Texture laserHorizontal;
	public Texture bat;
	public Texture bat1;
	public Texture bat2;
	public Texture bat3;
	public TextureAtlas healthbarAtlas;
	public MyGame(GameStateManager gsm) {
		super(gsm);
		batch = gsm.batch;

		Box2D.init();
		atlas = new TextureAtlas("game_atlas.atlas");
		healthbarAtlas = new TextureAtlas("healthbar/healthbar.atlas");

		world = new World(new Vector2(0, GRAVITY), true);

		debugRenderer = new Box2DDebugRenderer();

		shadow = new Texture("player/shadow.png");
		golem = new Texture("golem.png");
		laserVertical = new Texture("laserVertical.png");
		laserHorizontal = new Texture("laserHorizontal.png");
		bullet = new Texture("bullet.png");
		bat = new Texture("bat.png");
		bat1 = new Texture("bat1.png");
		bat2 = new Texture("bat2.png");
		bat3 = new Texture("bat3.png");
		mapEntity = new MapEntity();

		activeMonsters = new ArrayList<>();
		activeLasers = new ArrayList<>();
		//activeMonsters.add(new Golem(this));
		//activeMonsters.add(new Bat(this));
		//activeMonsters.add(new Bat(this, BAT_INITIAL_X_POSITION + 1, BAT_INITIAL_Y_POSITION + 1));
		//activeMonsters.add(new Bat(this, BAT_INITIAL_X_POSITION + 2, BAT_INITIAL_Y_POSITION + 2));

		listener = new Listener(this);
		world.setContactListener(listener);
		map = new Map(this);

		if (player == null) player = new Player(this);
	}
	/*public void create () {
		Box2D.init();
		atlas = new TextureAtlas("game_atlas.atlas");
		healthbarAtlas = new TextureAtlas("healthbar/healthbar.atlas");

		batch = new SpriteBatch();

		camera = new OrthographicCamera();
		camera.setToOrtho(false);
		vp = new ExtendViewport(VISIBLE_HORIZONTAL_TILES, VISIBLE_VERTICAL_TILES, camera);

		world = new World(new Vector2(0, GRAVITY), true);

		debugRenderer = new Box2DDebugRenderer();

		shadow = new Texture("player/shadow.png");
		golem = new Texture("golem.png");
		laserVertical = new Texture("laserVertical.png");
		laserHorizontal = new Texture("laserHorizontal.png");
		bullet = new Texture("bullet.png");
		bat = new Texture("bat.png");
		bat1 = new Texture("bat1.png");
		bat2 = new Texture("bat2.png");
		bat3 = new Texture("bat3.png");
		mapEntity = new MapEntity();

		activeMonsters = new ArrayList<>();
		activeLasers = new ArrayList<>();
		//activeMonsters.add(new Golem(this));
		//activeMonsters.add(new Bat(this));
		//activeMonsters.add(new Bat(this, BAT_INITIAL_X_POSITION + 1, BAT_INITIAL_Y_POSITION + 1));
		//activeMonsters.add(new Bat(this, BAT_INITIAL_X_POSITION + 2, BAT_INITIAL_Y_POSITION + 2));

		listener = new Listener(this);
		world.setContactListener(listener);
		map = new Map(this);

		if (player == null) player = new Player(this);

		camera.position.x = player.body.getPosition().x + PLAYER_SPRITE_WIDTH / 2;
		camera.position.y = player.body.getPosition().y + PLAYER_SPRITE_HEIGHT / 2;
		camera.update();
	}*/
	public Vector3 getMousePosition() {
		return gsm.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
	}

	private void step() {
		double delta = Gdx.graphics.getDeltaTime();

		accumulator += delta;
		timePassed += delta;

		while (accumulator >= TIME_STEP) {
			world.step(TIME_STEP, 6, 2);
			accumulator -= TIME_STEP;
			listener.update();
			player.update();
		}
		for (Monster m : activeMonsters) {
			m.update();
		}
	}
	public void render () {
		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			gsm.switchState(GameState.Pause);
		}

		gsm.camera.position.x = player.getBodyCenter().x;
		gsm.camera.position.y = player.getBodyCenter().y + CAMERA_Y_OFFSET;
		gsm.camera.update();

		map.render();

		player.render();
		for (Monster m : activeMonsters) {
			m.render();
		}
		for (Laser l : activeLasers) {
			l.render();
		}

		//debugRenderer.render(world, camera.combined);

		step();
	}

	public void resize(int width, int height) {
		//vp.update(width, height);
	}
	
	public void dispose () {
		atlas.dispose();
		map.dispose();
		golem.dispose();
		bullet.dispose();
	}
}
