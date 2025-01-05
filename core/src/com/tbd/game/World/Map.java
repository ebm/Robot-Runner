package com.tbd.game.World;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.tbd.game.Entities.MonsterPackage.Bat;
import com.tbd.game.Entities.MonsterPackage.Golem;
import com.tbd.game.Entities.MonsterPackage.Spaceship;
import com.tbd.game.Entities.PlayerPackage.Player;
import com.tbd.game.Items.Armor;
import com.tbd.game.Items.Boots;
import com.tbd.game.Items.Dash;
import com.tbd.game.Items.Heart;
import com.tbd.game.States.MyGame;
import com.tbd.game.Weapons.Laser;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.tbd.game.World.Constants.*;

public class Map {
    static class Edge {
        Vector2 a;
        Vector2 b;
        public Edge(Vector2 a, Vector2 b) {
            this.a = a;
            this.b = b;
        }
        public boolean combineEdgeIfShared(Edge e) {
            if (getSlope() == e.getSlope()) {
                if (a.equals(e.a)) {
                    a = e.b;
                } else if (a.equals(e.b)) {
                    a = e.a;
                } else if (b.equals(e.a)) {
                    b = e.b;
                } else if (b.equals(e.b)) {
                    b = e.a;
                } else {
                    return false;
                }
                return true;
            }
            return false;
        }
        public float getSlope() {
            return (b.y - a.y) / (b.x - a.x);
        }
    }
    MyGame myGame;
    public OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    public Map(MyGame myGame) {
        this.myGame = myGame;

        this.orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(myGame.assetManager.get("map2/tilemap.tmx"), UNIT_SCALE);
        orthogonalTiledMapRenderer.setView(myGame.gsm.camera);

        setupMap(((TiledMap) myGame.assetManager.get("map2/tilemap.tmx")).getLayers().get("objects").getObjects());
        setupSpawns(((TiledMap) myGame.assetManager.get("map2/tilemap.tmx")).getLayers().get("points").getObjects());
    }
    private void setupSpawns(MapObjects mapObjects) {
        for (MapObject mapObject : mapObjects) {
            if (mapObject instanceof RectangleMapObject) {
                RectangleMapObject rectangleMapObject = (RectangleMapObject) mapObject;
                if (mapObject.getProperties().containsKey("Player")) {
                    myGame.player = new Player(myGame, rectangleMapObject.getRectangle().x * UNIT_SCALE, rectangleMapObject.getRectangle().y * UNIT_SCALE);
                } else if (mapObject.getProperties().containsKey("Golem")) {
                    String range = null;
                    if (mapObject.getProperties().containsKey("Range")) {
                        range = (String) mapObject.getProperties().get("Range");
                    }
                    myGame.activeMonsters.add(new Golem(myGame, rectangleMapObject.getRectangle().x * UNIT_SCALE, rectangleMapObject.getRectangle().y * UNIT_SCALE, range));
                } else if (mapObject.getProperties().containsKey("Bat")) {
                    myGame.activeMonsters.add(new Bat(myGame, rectangleMapObject.getRectangle().x * UNIT_SCALE, rectangleMapObject.getRectangle().y * UNIT_SCALE));
                } else if (mapObject.getProperties().containsKey("Laser")) {
                    myGame.activeLasers.add(new Laser(myGame, Integer.parseInt((String) mapObject.getProperties().get("Laser")), rectangleMapObject.getRectangle().x * UNIT_SCALE, rectangleMapObject.getRectangle().y * UNIT_SCALE, LASER_MAXIMUM_DISTANCE));
                } else if (mapObject.getProperties().containsKey("GolemArmorItem")) {
                    myGame.itemMapManager.addItem(new Armor(0.8f, myGame.itemMapManager.getID(),rectangleMapObject.getRectangle().x * UNIT_SCALE, rectangleMapObject.getRectangle().y * UNIT_SCALE, myGame.assetManager.get("rock_armor.png"), myGame));
                } else if (mapObject.getProperties().containsKey("BootsFastItem")) {
                    myGame.itemMapManager.addItem(new Boots(1.2f, myGame.itemMapManager.getID(),rectangleMapObject.getRectangle().x * UNIT_SCALE, rectangleMapObject.getRectangle().y * UNIT_SCALE, myGame.assetManager.get("boots_fast.png"), myGame));
                } else if (mapObject.getProperties().containsKey("HeartSmallItem")) {
                    myGame.itemMapManager.addItem(new Heart(20, myGame.itemMapManager.getID(),rectangleMapObject.getRectangle().x * UNIT_SCALE, rectangleMapObject.getRectangle().y * UNIT_SCALE, myGame.assetManager.get("heart_small.png"), myGame));
                } else if (mapObject.getProperties().containsKey("DashItem")) {
                    myGame.itemMapManager.addItem(new Dash(myGame.itemMapManager.getID(),rectangleMapObject.getRectangle().x * UNIT_SCALE, rectangleMapObject.getRectangle().y * UNIT_SCALE, myGame.assetManager.get("dash.png"), myGame));
                } else if (mapObject.getProperties().containsKey("Spaceship")) {
                    myGame.activeMonsters.add(new Spaceship(myGame, rectangleMapObject.getRectangle().x * UNIT_SCALE, rectangleMapObject.getRectangle().y * UNIT_SCALE));
                }
            }
        }
        if (myGame.player == null) myGame.player = new Player(myGame, PLAYER_INITIAL_X_POSITION, PLAYER_INITIAL_Y_POSITION);
    }
    private void setupMap(MapObjects mapObjects) {
        ArrayList<Edge> edgeList = new ArrayList<>();
        for (MapObject mapObject : mapObjects) {
            if (mapObject instanceof PolylineMapObject) {;
                float[] vertices = ((PolylineMapObject) mapObject).getPolyline().getTransformedVertices();
                addEdgeToList(new Edge(new Vector2(vertices[0] * UNIT_SCALE, vertices[1] * UNIT_SCALE), new Vector2(vertices[2] * UNIT_SCALE, vertices[3] * UNIT_SCALE)), edgeList);
            }
        }
        for (Edge e : edgeList) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;

            Body body = myGame.world.createBody(bodyDef);

            EdgeShape edgeShape = new EdgeShape();
            edgeShape.set(e.a, e.b);
             //edgeShape.set(0, 0, e.b.x - e.a.x, e.b.y - e.a.y);
            Filter filter  = new Filter();
            filter.categoryBits = CATEGORY_BITS_MAP;
            body.createFixture(edgeShape, 0.0f).setFilterData(filter);

            //body.setTransform(e.a, 0);
            edgeShape.dispose();
        }
    }
    private void addEdgeToList(Edge edge, ArrayList<Edge> edgeList) {
        DecimalFormat df = new DecimalFormat("#.##");
        edge.a.x = Float.parseFloat(df.format(edge.a.x));
        edge.a.y = Float.parseFloat(df.format(edge.a.y));

        edge.b.x = Float.parseFloat(df.format(edge.b.x));
        edge.b.y = Float.parseFloat(df.format(edge.b.y));

        for (int i = edgeList.size() - 1; i >= 0; i--) {
            if (edgeList.get(i).combineEdgeIfShared(edge)) {
                return;
            }
        }
        edgeList.add(edge);
    }
    public void render() {
        orthogonalTiledMapRenderer.setView(myGame.gsm.camera);
        orthogonalTiledMapRenderer.render();
    }
    public void dispose() {

    }
}
