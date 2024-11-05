package com.tbd.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.tbd.game.Entities.PlayerPackage.Player;

import java.util.ArrayList;

import static com.tbd.game.Constants.METERS_PER_PIXEL;
public class ItemMapManager {
    MyGame myGame;
    ArrayList<Item> itemsOnMap;
    int id;
    public ItemMapManager(MyGame myGame) {
        this.myGame = myGame;
        itemsOnMap = new ArrayList<>();
        id = 0;
    }
    public void createItem(ItemType itemType, float x, float y, Texture itemTexture) {
        itemsOnMap.add(new Item(id++, itemType, x, y, itemTexture, myGame));
    }
    public int getID() {
        return id++;
    }
    public void addItem(Item item) {
        itemsOnMap.add(item);
    }
    public void removeItem(Item item) {
        itemsOnMap.remove(item);
    }
    public void render() {
        for (Item i : itemsOnMap) {
            i.render();
        }
    }
}
