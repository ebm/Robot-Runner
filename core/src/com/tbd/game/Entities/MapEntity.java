package com.tbd.game.Entities;

import com.badlogic.gdx.math.Vector2;
import com.tbd.game.Entities.MonsterPackage.Monster;
import com.tbd.game.States.MyGame;

public class MapEntity extends Monster {
    public MapEntity(MyGame myGame) {
        super(myGame, 0);

    }
    @Override
    public Vector2 getBodyCenter() {
        return null;
    }

    @Override
    public void update() {

    }

    @Override
    public void render() {

    }
}
