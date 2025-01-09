package com.robotrunner.gwt;

import com.badlogic.gdx.backends.gwt.preloader.DefaultAssetFilter;

public class AssetFilter extends DefaultAssetFilter {
    @Override
    public boolean preload(String file) {
        if (file.contains("map2")) return true;
        switch(file) {
            case "buttonUp.png":
            case "buttonDown.png":
            case "default.fnt":
            case "default.png":
            case "keybinds.txt":
                return true;
            default:
                return false;
        }
    }
}
