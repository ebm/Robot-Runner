package com.tbd.game.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.tbd.game.States.MyGame;
import jdk.javadoc.internal.doclets.toolkit.taglets.snippet.Style;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Keybinds {
    MyGame myGame;
    public HashMap<String, KeyType> keybinds;
    public HashMap<String, KeyType> defaultKeybinds;
    public Keybinds(MyGame myGame) {
        this.myGame = myGame;

        keybinds = new LinkedHashMap<>();
        defaultKeybinds = new LinkedHashMap<>();
        FileHandle file = myGame.assetManager.getFileHandleResolver().resolve("keybinds.txt");
        String txt = file.readString();
        String[] lines = txt.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String[] entry = lines[i].split(": ");
            String[] numbers = entry[1].replace("\r", "").split(", ");
            keybinds.put(entry[0], new KeyType(numbers[0].charAt(0), Integer.parseInt(numbers[1])));
            defaultKeybinds.put(entry[0], new KeyType(numbers[2].charAt(0), Integer.parseInt(numbers[3])));
        }
    }
    public void saveKeybinds() {
        FileHandle file = myGame.assetManager.getFileHandleResolver().resolve("keybinds.txt");
        StringBuilder res = new StringBuilder();
        for (String s : keybinds.keySet()) {
            res.append(s).append(": ").append(keybinds.get(s)).append(", ").append(defaultKeybinds.get(s)).append("\n");
        }
        file.writeString(res.toString(), false);
    }
}
