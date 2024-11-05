package com.tbd.game.Entities.PlayerPackage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Align;
import com.tbd.game.Direction;
import com.tbd.game.Item;
import com.tbd.game.ItemType;
import com.tbd.game.MyGame;
import static com.tbd.game.Constants.*;

public class Inventory {
    MyGame myGame;
    public boolean open;
    Item[] inventoryItems;
    Table table;
    boolean canEscape;
    Item currentSelection;
    int selectionNumber;
    Image selectedImage;
    public ItemType getItemType(int index) {
        if (index == 0) return ItemType.Ability;
        if (index == 1) return ItemType.Armor;
        if (index == 2) return ItemType.Boots;
        if (index == 3) return ItemType.Attribute;
        return ItemType.Any;
    }
    public Texture getItemTexture(int index) {
        if (index == 0) return myGame.abilityIcon;
        if (index == 1) return myGame.armorIcon;
        if (index == 2) return myGame.bootsIcon;
        if (index == 3) return myGame.attributeIcon;
        return null;
    }
    public Inventory(MyGame myGame) {
        this.myGame = myGame;
        inventoryItems = new Item[PLAYER_INVENTORY_SPACE + PLAYER_ATTRIBUTE_SPACE];

        currentSelection = null;
        selectionNumber = -1;

        table = new Table();
        table.setFillParent(true);
        table.setDebug(true);
        //table.setName("Table");
        for (int i = 0; i < PLAYER_ATTRIBUTE_SPACE; i++) {
            Image image = new Image(myGame.slot);
            //image.setName("Image");
            image.setTouchable(Touchable.disabled);
            Stack overlay = new Stack(image);
            Image typeImage = new Image(getItemTexture(i));
            typeImage.setTouchable(Touchable.disabled);
            Container<Image> container = new Container<>(typeImage);
            container.size(50,50);
            overlay.add(container);
            overlay.setTouchable(Touchable.enabled);
            //overlay.setName("Overlay");
            overlay.setUserObject(i);
            Cell<Stack> cell = table.add(overlay).maxHeight(80).maxWidth(80);
            cell.minHeight(80).minWidth(80);
            cell.pad(5, 5, 50, 5);
        }
        table.row();
        int rows = 3;
        int cols = 4;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Image image = new Image(myGame.slot);
                //image.setName("Image");
                image.setTouchable(Touchable.disabled);
                Stack overlay = new Stack(image);
                overlay.setTouchable(Touchable.enabled);
                //overlay.setName("Overlay");
                overlay.setUserObject(i * cols + j + PLAYER_ATTRIBUTE_SPACE);
                Cell<Stack> cell = table.add(overlay).maxHeight(80).maxWidth(80);
                cell.minHeight(80).minWidth(80);
                cell.pad(5, 5, 5, 5);
            }
            table.row();
        }
        table.addListener(new ClickListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Actor actor = table.hit(x, y, true);
                if (actor == null || button != Input.Buttons.LEFT) return false;
                int selection = (int) actor.getUserObject();
                System.out.println("down " + (selection));
                if (inventoryItems[selection] != null) {
                    selectionNumber = selection;
                    currentSelection = inventoryItems[selectionNumber];
                    removeItem(currentSelection, selectionNumber);
                    selectedImage = new Image(currentSelection.itemTexture);
                    selectedImage.setSize(50, 50);
                    myGame.stage.addActor(selectedImage);
                }
                return true;
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Actor actor = table.hit(x, y, true);
                if (actor == null || button != Input.Buttons.LEFT) return;
                int selection = (int) actor.getUserObject();
                System.out.println("up " + (selection));
                if (currentSelection == null) return;
                if (inventoryItems[selection] == null && (getItemType(selection) == currentSelection.itemType || getItemType(selection) == ItemType.Any)) {
                    addItem(currentSelection, selection);
                    currentSelection = null;
                    selectionNumber = -1;
                } else {
                    addItem(currentSelection, selectionNumber);
                    currentSelection = null;
                    selectionNumber = -1;
                }
                myGame.stage.getActors().removeValue(selectedImage, true);
            }
        });
        open = false;
        canEscape = false;
    }
    public boolean addItem(Item item, int index) {
        //if (item == Item.GolemArmor) {
        //    if (attributes[ARMOR_POSITION] == null) {
        //        attributes[ARMOR_POSITION] = item;
        //    }
        //}
        if (index == -1) {
            for (int i = 0; i < PLAYER_INVENTORY_SPACE + PLAYER_ATTRIBUTE_SPACE; i++) {
                if (inventoryItems[i] == null && (getItemType(i) == item.itemType || getItemType(i) == ItemType.Any)) {
                    index = i;
                    break;
                }
            }
        }
        if (index == -1 || !(getItemType(index) == item.itemType || getItemType(index) == ItemType.Any)) return false;
        inventoryItems[index] = item;
        Image image = new Image(item.itemTexture);
        image.setTouchable(Touchable.disabled);
        Container<Image> container = new Container<>(image);
        container.size(50,50);
        item.body.setActive(false);
        ((Stack) table.getCells().get(index).getActor()).add(container);

        return true;
    }
    public boolean dropItem(Item item, int index) {
        //if (item == Item.GolemArmor) {
        //    if (attributes[ARMOR_POSITION] == null) {
        //        attributes[ARMOR_POSITION] = item;
        //    }
        //}
        if (index == -1) {
            for (int i = 0; i < PLAYER_INVENTORY_SPACE; i++) {
                if (inventoryItems[i].id == item.id) {
                    index = i;
                    break;
                }
            }
        }
        if (index == -1) return false;
        inventoryItems[index].body.setActive(true);
        inventoryItems[index].body.setTransform(myGame.player.body.getPosition().x - 1, myGame.player.body.getPosition().y, 0);
        myGame.itemMapManager.addItem(inventoryItems[index]);
        inventoryItems[index] = null;
        ((Stack) table.getCells().get(index).getActor()).getChildren().pop();
        return true;
    }
    public boolean dropItem(Item item, Direction dir) {
        item.body.setActive(true);
        if (dir == Direction.Right) {
            item.body.setTransform(myGame.player.body.getPosition().x + 1.5f, myGame.player.body.getPosition().y, 0);
        } else {
            item.body.setTransform(myGame.player.body.getPosition().x - 1.5f, myGame.player.body.getPosition().y, 0);
        }
        myGame.itemMapManager.addItem(item);
        return true;
    }
    public boolean removeItem(Item item, int index) {
        //if (item == Item.GolemArmor) {
        //    if (attributes[ARMOR_POSITION] == null) {
        //        attributes[ARMOR_POSITION] = item;
        //    }
        //}
        if (index == -1) {
            for (int i = 0; i < PLAYER_INVENTORY_SPACE; i++) {
                if (inventoryItems[i].id == item.id) {
                    index = i;
                    break;
                }
            }
        }
        if (index == -1) return false;
        inventoryItems[index] = null;
        ((Stack) table.getCells().get(index).getActor()).getChildren().pop();
        return true;
    }
    public void setOpen(boolean open) {
        if (open) {
            this.open = true;
            canEscape = false;
            myGame.stage.addActor(table);
        } else {
            this.open = false;
            table.addAction(Actions.removeActor());
        }
    }
    public void render() {
        if (!Gdx.input.isKeyPressed(Input.Keys.E)) {
            canEscape = true;
        }
        if (canEscape && Gdx.input.isKeyPressed(Input.Keys.E)) {
            if (currentSelection != null) {
                addItem(currentSelection, selectionNumber);
                currentSelection = null;
                selectionNumber = -1;
                myGame.stage.getActors().removeValue(selectedImage, true);
            }
            setOpen(false);
        }
        if (currentSelection != null) {
            Vector2 pos = myGame.stage.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            selectedImage.setPosition(pos.x - selectedImage.getWidth() / 2, pos.y - selectedImage.getHeight() / 2);
            //currentSelection.render(myGame.getMousePosition().x, myGame.getMousePosition().y);
            if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                Direction direction;
                if (myGame.getMousePosition().x > myGame.player.getBodyCenter().x) {
                    direction = Direction.Right;
                } else direction = Direction.Left;
                dropItem(currentSelection, direction);
                currentSelection = null;
                selectionNumber = -1;
                myGame.stage.getActors().removeValue(selectedImage, true);
            }
            //myGame.batch.draw(currentSelection.itemTexture, Gdx.input.getX(), Gdx.input.getY(), 50, 50);
        }

        //myGame.batch.begin();

        //myGame.batch.end();
    }
}
