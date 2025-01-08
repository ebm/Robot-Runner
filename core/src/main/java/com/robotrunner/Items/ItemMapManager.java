package com.robotrunner.Items;

import com.robotrunner.States.MyGame;

import java.util.ArrayList;

public class ItemMapManager {
    MyGame myGame;
    ArrayList<Item> itemsOnMap;
    int id;
    public ItemMapManager(MyGame myGame) {
        this.myGame = myGame;
        itemsOnMap = new ArrayList<>();
        id = 0;
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
