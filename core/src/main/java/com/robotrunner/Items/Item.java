package com.robotrunner.Items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.robotrunner.Entities.PlayerPackage.Player;
import com.robotrunner.World.Listener;
import com.robotrunner.States.MyGame;

import static com.robotrunner.World.Constants.*;

public abstract class Item {
    public MyGame myGame;
    public int id;
    public ItemType itemType;
    public Body body;
    public Body worldBody;
    public Texture itemTexture;
    public int multiplier;
    public double lastSwitch;
    float verticalVelocity;
    public Item(int id, ItemType itemType, float x, float y, Texture itemTexture, MyGame myGame) {
        this.id = id;
        this.myGame = myGame;
        this.itemType = itemType;
        createBody(x, y);
        createWorldBody(x, y);
        this.itemTexture = itemTexture;
        lastSwitch = 0;
        multiplier = 1;
        verticalVelocity = myGame.rand.nextFloat() % 0.15f + 0.05f;
    }
    public void setActive(boolean var) {
        body.setActive(var);
        worldBody.setActive(var);
    }
    public void setPosition(float x, float y) {
        body.setTransform(x, y, 0);
        worldBody.setTransform(x, y, 0);
    }
    public void createBody(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        body = myGame.world.createBody(bodyDef);

        PolygonShape polygon = new PolygonShape();
        polygon.set(new Vector2[] {new Vector2(0, 0), new Vector2(0.75f * METERS_PER_PIXEL, 0),
                new Vector2(0, 0.75f * METERS_PER_PIXEL), new Vector2(0.75f * METERS_PER_PIXEL, 0.75f * METERS_PER_PIXEL)});

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygon;
        fixtureDef.isSensor = true;
        body.setGravityScale(0);

        body.createFixture(fixtureDef).setUserData(this);
        body.setFixedRotation(true);

        polygon.dispose();

        body.setTransform(x, y + 0.25f, 0);
    }
    public void createWorldBody(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        worldBody = myGame.world.createBody(bodyDef);

        PolygonShape polygon = new PolygonShape();
        polygon.set(new Vector2[] {new Vector2(0, 0), new Vector2(0.75f * METERS_PER_PIXEL, 0),
            new Vector2(0, 0.75f * METERS_PER_PIXEL), new Vector2(0.75f * METERS_PER_PIXEL, 0.75f * METERS_PER_PIXEL)});

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygon;

        Filter filter = new Filter();
        filter.maskBits = CATEGORY_BITS_MAP;

        worldBody.createFixture(fixtureDef).setUserData(this);
        worldBody.getFixtureList().get(0).setFilterData(filter);
        worldBody.setFixedRotation(true);

        polygon.dispose();

        worldBody.setTransform(x, y + 5/*0.25f*/, 0);
    }

    public abstract void apply();
    public void render() {
        /*if (myGame.timePassed - lastSwitch > 1 && getDistance(myGame.player.getBodyCenter(), body.getPosition()) < 40) {
            body.setLinearVelocity(0, verticalVelocity * multiplier);
            multiplier *= -1;
            lastSwitch = myGame.timePassed;
        }*/
        body.setTransform(worldBody.getPosition(), 0);
        myGame.batch.draw(itemTexture, body.getPosition().x, body.getPosition().y, 0.75f * METERS_PER_PIXEL, 0.75f * METERS_PER_PIXEL);
    }
    public static void handleContact(Fixture fixtureA, Fixture fixtureB, boolean beginContact, MyGame myGame) {
        if (!beginContact) return;
        Item item = null;
        Fixture[] collision = Listener.checkContact(fixtureA, fixtureB, Item.class);
        if (collision[0] != null) {
            item = (Item) collision[0].getUserData();
        } else return;
        if (collision[1].getUserData() != null && collision[1].getUserData().getClass() == Player.class) {
            if (myGame.player.inventory.addItem(item, -1)) {
                myGame.itemMapManager.removeItem(item);
            }
        }
    }
    @Override
    public abstract String toString();
}
