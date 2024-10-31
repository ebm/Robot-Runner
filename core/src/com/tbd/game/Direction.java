package com.tbd.game;

public enum Direction {
    Up, Down, Left, Right;

    public static Direction parseDirection(String str) {
        if (str.equalsIgnoreCase("down")) {
            return Down;
        } else if (str.equalsIgnoreCase("left")) {
            return Left;
        } else if (str.equalsIgnoreCase("right")) {
            return Right;
        }
        return Up;
    }
}
