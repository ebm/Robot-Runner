package com.tbd.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.tbd.game.States.GameStateManager;

public class DesktopLauncher {
	public static int fps = 0;

	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(fps);
		config.setWindowedMode(800, 500);
		config.setTitle("TBD");
		//config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		new Lwjgl3Application(new GameStateManager(), config);
	}
}
