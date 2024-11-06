package com.tbd.game.States;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
	public TextureAtlas atlas;
	Map map;
	Texture shadow;
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
	public Texture slot;
	public Texture rockArmor;
	public Texture fastBoots;
	public Texture heartSmall;
	public Texture armorIcon;
	public Texture abilityIcon;
	public Texture bootsIcon;
	public Texture attributeIcon;
	public Texture textBackground;
	public Sound playerFireNoise;
	public Sound playerHitmarkerNoise;
	boolean canEscape;
	public Stage stage;
	public Table table;
	public LabelStyle labelStyle;
	public ItemMapManager itemMapManager;
	public MapEntity mapEntity;
	public Random rand;
	public MyGame(GameStateManager gsm) {
		this.gsm = gsm;
		batch = gsm.batch;
		rand = new Random();

		mapEntity = new MapEntity();
		stage = new Stage(new ScreenViewport());
		table = new Table();
		table.setFillParent(true);
		//table.setDebug(true);
		stage.addActor(table);
		labelStyle = new Label.LabelStyle();
		labelStyle.font = gsm.font;
		table.bottom();
		table.right();

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
		slot = new Texture("slot.png");
		rockArmor = new Texture("rock_armor.png");
		fastBoots = new Texture("boots_fast.png");
		heartSmall = new Texture("heart_small.png");
		armorIcon = new Texture("armorIcon.png");
		attributeIcon = new Texture("attributeIcon.png");
		bootsIcon = new Texture("bootsIcon.png");
		abilityIcon = new Texture("abilityIcon.png");
		textBackground = new Texture("textBorder.png");
		playerFireNoise = Gdx.audio.newSound(Gdx.files.internal("fire.mp3"));
		playerHitmarkerNoise = Gdx.audio.newSound(Gdx.files.internal("hitmarker.mp3"));

		itemMapManager = new ItemMapManager(this);
		activeMonsters = new ArrayList<>();
		activeLasers = new ArrayList<>();
		//activeMonsters.add(new Golem(this));
		//activeMonsters.add(new Bat(this));
		//activeMonsters.add(new Bat(this, BAT_INITIAL_X_POSITION + 1, BAT_INITIAL_Y_POSITION + 1));
		//activeMonsters.add(new Bat(this, BAT_INITIAL_X_POSITION + 2, BAT_INITIAL_Y_POSITION + 2));

		listener = new Listener(this);
		world.setContactListener(listener);
		map = new Map(this);

		if (player == null) player = new Player(this, PLAYER_INITIAL_X_POSITION, PLAYER_INITIAL_Y_POSITION);
		canEscape = false;
	}
	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		canEscape = false;
	}
	public Vector3 getMousePosition() {
		return gsm.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
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
		gsm.batch.begin();
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
		itemMapManager.render();
		gsm.batch.end();

		debugRenderer.render(world, gsm.camera.combined);

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
		atlas.dispose();
		map.dispose();
		golem.dispose();
		bullet.dispose();
	}
}
