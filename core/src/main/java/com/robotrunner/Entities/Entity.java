package com.robotrunner.Entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.robotrunner.Entities.MonsterPackage.Monster;
import com.robotrunner.Entities.PlayerPackage.Player;
import com.robotrunner.States.MyGame;
import com.robotrunner.World.ContactClass;
import com.robotrunner.World.Listener;

import static com.robotrunner.World.Constants.*;

public abstract class Entity {
    public MyGame myGame;
    public float health;
    public Body body;
    public Class<?> friendly;
    public Class<?> enemy;
    public boolean death;
    public int contactFeet;
    public int contactLeftArm;
    public int contactRightArm;
    public short categoryBits;
    public Entity(MyGame myGame, float health, Class<?> friendly, Class<?> enemy, short categoryBits) {
        this.myGame = myGame;
        this.friendly = friendly;
        this.enemy = enemy;
        this.health = health;
        this.categoryBits = categoryBits;
    }
    public void createBody(float hitboxWidth, float hitboxHeight, FixtureDef fixtureDef) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        //bodyDef.position.set(INITIAL_X_POSITION, INITIAL_Y_POSITION);

        body = myGame.world.createBody(bodyDef);

        float bottomHeight = hitboxHeight / 20;
        float bottomHorizontalHeight = hitboxWidth / 3;

        float topHeight = hitboxHeight / 5;
        float verticalWidth = hitboxWidth / 20;
        PolygonShape polygon = new PolygonShape();
        polygon.set(new Vector2[] {new Vector2(0, bottomHeight), new Vector2(0, hitboxHeight - topHeight), new Vector2(verticalWidth, hitboxHeight), new Vector2(hitboxWidth - verticalWidth, hitboxHeight), new Vector2(hitboxWidth, hitboxHeight - topHeight), new Vector2(hitboxWidth, bottomHeight), new Vector2(hitboxWidth - bottomHorizontalHeight,0), new Vector2(0 + bottomHorizontalHeight,0)});

        fixtureDef.shape = polygon;

        body.createFixture(fixtureDef).setUserData(this);

        polygon.dispose();
        body.setFixedRotation(true);
        //body.setSleepingAllowed(false);

        createFeet(hitboxWidth, hitboxHeight);
        createLeftArm(hitboxWidth, hitboxHeight);
        createRightArm(hitboxWidth, hitboxHeight);

        contactFeet = 0;
        contactLeftArm = 0;
        contactRightArm = 0;
    }

    private void createFeet(float hitboxWidth, float hitboxHeight) {
        PolygonShape shape = new PolygonShape();
        shape.set(new float[] {ENTITY_APPENDAGE_DISTANCE_FROM_EDGE, ENTITY_APPENDAGE_OFFSET, ENTITY_APPENDAGE_DISTANCE_FROM_EDGE, -ENTITY_APPENDAGE_THICKNESS, hitboxWidth - ENTITY_APPENDAGE_DISTANCE_FROM_EDGE, ENTITY_APPENDAGE_OFFSET, hitboxWidth - ENTITY_APPENDAGE_DISTANCE_FROM_EDGE, -ENTITY_APPENDAGE_THICKNESS});

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef).setUserData(new ContactClass(this, BodyPart.Feet));

        shape.dispose();
    }
    private void createLeftArm(float hitboxWidth, float hitboxHeight) {
        PolygonShape shape = new PolygonShape();
        shape.set(new float[] {-ENTITY_APPENDAGE_THICKNESS, ENTITY_APPENDAGE_DISTANCE_FROM_EDGE, -ENTITY_APPENDAGE_THICKNESS, hitboxHeight - ENTITY_APPENDAGE_DISTANCE_FROM_EDGE, ENTITY_APPENDAGE_OFFSET, hitboxHeight - ENTITY_APPENDAGE_DISTANCE_FROM_EDGE, ENTITY_APPENDAGE_OFFSET, ENTITY_APPENDAGE_DISTANCE_FROM_EDGE});

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef).setUserData(new ContactClass(this, BodyPart.LeftArm));

        shape.dispose();
    }
    private void createRightArm(float hitboxWidth, float hitboxHeight) {
        PolygonShape shape = new PolygonShape();
        shape.set(new float[] {hitboxWidth - ENTITY_APPENDAGE_OFFSET, ENTITY_APPENDAGE_DISTANCE_FROM_EDGE, hitboxWidth - ENTITY_APPENDAGE_OFFSET, hitboxHeight - ENTITY_APPENDAGE_DISTANCE_FROM_EDGE, hitboxWidth + ENTITY_APPENDAGE_THICKNESS, hitboxHeight - ENTITY_APPENDAGE_DISTANCE_FROM_EDGE, hitboxWidth + ENTITY_APPENDAGE_THICKNESS, ENTITY_APPENDAGE_DISTANCE_FROM_EDGE});

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef).setUserData(new ContactClass(this, BodyPart.RightArm));

        shape.dispose();
    }
    public void takeDamage(float damage) {
        //System.out.println("Health: " + health);
        health -= damage;
        if (health <= 0) {
            death();
        }
    }
    public void death() {
        //System.out.println("Death: " + this.getClass());
        myGame.world.destroyBody(body);
        death = true;
    }
    public abstract Vector2 getBodyCenter();
    public abstract void update();
    public abstract void render();
    public static void handleContact(Fixture fixtureA, Fixture fixtureB, boolean beginContact, MyGame myGame) {
        ContactClass c = null;
        Fixture[] collision = Listener.checkContact(fixtureA, fixtureB, ContactClass.class);
        if (collision[0] != null) {
            c = (ContactClass) collision[0].getUserData();
        }
        if (fixtureA.isSensor() && fixtureB.isSensor()) return;
        if (c != null) {
            if (c.data == BodyPart.Feet) {
                if (beginContact) {
                    ((Entity) c.owner).contactFeet++;
                } else {
                    ((Entity) c.owner).contactFeet--;
                }
            }
            if (c.data == BodyPart.LeftArm) {
                if (beginContact) {
                    ((Entity) c.owner).contactLeftArm++;
                } else {
                    ((Entity) c.owner).contactLeftArm--;
                }
            }
            if (c.data == BodyPart.RightArm) {
                if (beginContact) {
                    ((Entity) c.owner).contactRightArm++;
                } else {
                    ((Entity) c.owner).contactRightArm--;
                }
            }
        }
    }
}
