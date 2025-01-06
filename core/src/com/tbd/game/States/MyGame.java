package com.tbd.game.States;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.*;
import com.tbd.game.Entities.MapEntity;
import com.tbd.game.Entities.MonsterPackage.Monster;
import com.tbd.game.Entities.PlayerPackage.Player;
import com.tbd.game.Items.ItemMapManager;
import com.tbd.game.World.Keybinds;
import com.tbd.game.World.Listener;
import com.tbd.game.World.Map;
import com.tbd.game.Weapons.Laser;

import static com.tbd.game.World.Constants.*;

import java.util.ArrayList;
import java.util.Random;

public class MyGame implements Screen {
	public GameStateManager gsm;
	public SpriteBatch batch;
	//OrthographicCamera camera;
	//Viewport vp;
	public World world; // 1 block = 2 meters = 64 pixels
	Box2DDebugRenderer debugRenderer;
	double accumulator;
	public double timePassed;
	public Player player;
	Map map;
	public Listener listener;
	public ArrayList<Monster> activeMonsters;
	public ArrayList<Laser> activeLasers;
	boolean canEscape;
	public Stage stage;
	public Table table;
	public LabelStyle labelStyle;
	public ItemMapManager itemMapManager;
	public MapEntity mapEntity;
	public Random rand;
	public Label fpsLabel;
	public boolean cameraLocked;
	public AssetManager assetManager;
	public boolean firstLaunch;
	public Keybinds keybinds;
	public void initializeTextures() {
		System.out.println("initialize textures");
		assetManager = new AssetManager(new LocalFileHandleResolver());
		assetManager.load("robot_player/robot_character.atlas", TextureAtlas.class);
		assetManager.load("healthbar/healthbar.atlas", TextureAtlas.class);
		assetManager.load("player/shadow.png", Texture.class);
		assetManager.load("golem.png", Texture.class);
		assetManager.load("gun.png", Texture.class);
		assetManager.load("laser.png", Texture.class);
		assetManager.load("laserBeam.png", Texture.class);
		assetManager.load("bullet.png", Texture.class);
		assetManager.load("bat.png", Texture.class);
		assetManager.load("bat1.png", Texture.class);
		assetManager.load("bat2.png", Texture.class);
		assetManager.load("bat3.png", Texture.class);
		assetManager.load("slot.png", Texture.class);
		assetManager.load("spaceship.png", Texture.class);
		assetManager.load("rock_armor.png", Texture.class);
		assetManager.load("boots_fast.png", Texture.class);
		assetManager.load("heart_small.png", Texture.class);
		assetManager.load("dash.png", Texture.class);
		assetManager.load("armorIcon.png", Texture.class);
		assetManager.load("attributeIcon.png", Texture.class);
		assetManager.load("bootsIcon.png", Texture.class);
		assetManager.load("abilityIcon.png", Texture.class);
		assetManager.load("textBorder.png", Texture.class);
		assetManager.load("fire.mp3", Sound.class);
		assetManager.load("hitmarker.mp3", Sound.class);
		assetManager.setLoader(TiledMap.class, new TmxMapLoader());
		assetManager.load("map2/tilemap.tmx", TiledMap.class);
		//assetManager.load("keybinds.txt", FileHandle.class);
	}

	/**
	 * Initializes before assets are loaded. Unsafe to use the asset manager in this function.
	 * @param gsm Game State Manager
	 */
	public MyGame(GameStateManager gsm) {
		this.gsm = gsm;
		batch = gsm.batch;
		rand = new Random();

		mapEntity = new MapEntity(this);

		labelStyle = new Label.LabelStyle();
		labelStyle.font = gsm.font;

		Box2D.init();
		itemMapManager = new ItemMapManager(this);
		activeMonsters = new ArrayList<>();
		activeLasers = new ArrayList<>();

		stage = null;

		world = new World(new Vector2(0, GRAVITY), true);

		firstLaunch = true;
		initializeTextures();
	}

	/**
	 * Initializes uninitialized variables. Safe to use asset manager here.
	 */
	public void init() {
		keybinds = new Keybinds(this);
		map = new Map(this);
	}
	@Override
	public void show() {
		if (firstLaunch) {
			debugRenderer = new Box2DDebugRenderer();

			listener = new Listener(this);
			world.setContactListener(listener);

			canEscape = false;
			cameraLocked = true;

			stage = new Stage(new ScreenViewport());
			table = new Table();
			table.setFillParent(true);
			//table.setDebug(true);
			stage.addActor(table);
			table.bottom();
			table.right();
			System.out.println(labelStyle);
			fpsLabel = new Label("FPS: " + Gdx.graphics.getFramesPerSecond(), labelStyle);
			fpsLabel.setPosition(0, Gdx.graphics.getHeight() - 25);
			stage.addActor(fpsLabel);

			player.initializeOnScreenPlayerStats();
			firstLaunch = false;
		}
		Gdx.input.setInputProcessor(stage);

		canEscape = false;
	}
	public Vector3 getMousePosition() {
		return gsm.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
	}
	public boolean checkKeybind(String s) {
		if (keybinds.keybinds.get(s).keycode == -99) return false;
		if (keybinds.keybinds.get(s).type == 'b') {
			return Gdx.input.isButtonPressed(keybinds.keybinds.get(s).keycode);
		} else if (keybinds.keybinds.get(s).type == 'k') {
			return Gdx.input.isKeyPressed(keybinds.keybinds.get(s).keycode);
		}
		return false;
	}
	private void step(float delta) {
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
	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0, 1);
		gsm.vp.apply();
		gsm.batch.setProjectionMatrix(gsm.camera.combined);
		if (Gdx.input.isKeyPressed(Input.Keys.EQUALS) && !Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
			gsm.camera.zoom -= 0.01f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.MINUS) && !Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
			gsm.camera.zoom += 0.01f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP) && !Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			gsm.camera.position.y += 0.1f;
			cameraLocked = false;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && !Gdx.input.isKeyPressed(Input.Keys.UP)) {
			gsm.camera.position.y -= 0.1f;
			cameraLocked = false;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			gsm.camera.position.x -= 0.1f;
			cameraLocked = false;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			gsm.camera.position.x += 0.1f;
			cameraLocked = false;
		}
		if (cameraLocked || Gdx.input.isKeyPressed(Input.Keys.C)) {
			cameraLocked = true;
			gsm.camera.position.x = player.getBodyCenter().x;
			gsm.camera.position.y = player.getBodyCenter().y + CAMERA_Y_OFFSET;
		}
		gsm.camera.update();
		gsm.batch.begin();

		map.render();

		player.render();
		for (Monster m : activeMonsters) {
			m.render();
		}
		for (Laser l : activeLasers) {
			l.render();
		}
		itemMapManager.render();
		fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
		gsm.batch.end();

		//debugRenderer.render(world, gsm.camera.combined);\
		player.debug();

		stage.act();
		stage.draw();

		step(delta);
		if (!Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			canEscape = true;
		}
		if (canEscape && Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			gsm.setScreen(gsm.pause);
		}
	}
	@Override
	public void resize(int width, int height) {
		gsm.vp.update(width, height);
		stage.getViewport().update(width, height, true);
		fpsLabel.setPosition(0, Gdx.graphics.getHeight() - 25);
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

	public void dispose () {
		assetManager.dispose();
		map.dispose();
	}
}
