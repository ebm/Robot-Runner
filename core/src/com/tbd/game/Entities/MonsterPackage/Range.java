package com.tbd.game.Entities.MonsterPackage;

import com.tbd.game.Entities.Entity;

public class Range {
    float xMin;
    float xMax;
    float yMin;
    float yMax;
    public Range(float xMin, float xMax, float yMin, float yMax, Entity e) {
        float x = e.getBodyCenter().x;
        float y = e.getBodyCenter().y;
        this.xMin = x - xMin;
        this.xMax = x + xMax;
        this.yMin = y - yMin;
        this.yMax = y + yMax;
    }
    public Range(String range, Entity e) {
        float x = e.getBodyCenter().x;
        float y = e.getBodyCenter().y;
        String[] input = range.split(",");
        this.xMin = x - Float.parseFloat(input[0]);
        this.xMax = x + Float.parseFloat(input[1]);
        this.yMin = y - Float.parseFloat(input[2]);
        this.yMax = y + Float.parseFloat(input[3]);
    }
}
