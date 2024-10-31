package com.tbd.game.Entities;

import com.badlogic.gdx.math.Vector2;

public class MapEntity extends Entity {
    public MapEntity() {
        friendly = MapEntity.class;
        enemy = Entity.class;
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
