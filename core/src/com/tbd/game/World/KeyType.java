package com.tbd.game.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class KeyType {
    public char type;
    public int keycode;
    public KeyType(char type, int keycode) {
        this.type = type;
        this.keycode = keycode;
    }
    public KeyType(KeyType keyType) {
        this.keycode = keyType.keycode;
        this.type = keyType.type;
    }
    public String getKeycodeString() {
        if (keycode == -99) return "NULL";
        if (type == 'b') {
            if (keycode == 0) {
                return "Left Mouse Button";
            } else if (keycode == 1) {
                return "Right Mouse Button";
            } else if (keycode == 2) {
                return "Middle Mouse Button";
            } else if (keycode == 3) {
                return "Back Mouse Button";
            } else if (keycode == 4) {
                return "Forward Mouse Button";
            } else return "Unknown Mouse Button.";
        } else {
            return Input.Keys.toString(keycode);
        }
    }
    @Override
    public String toString() {
        return type + ", " + keycode;
    }
}
