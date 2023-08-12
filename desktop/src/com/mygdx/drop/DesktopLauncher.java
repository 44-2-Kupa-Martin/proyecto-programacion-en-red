package com.mygdx.drop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.mygdx.drop.Drop;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Drop");
		config.setWindowedMode(800, 480);
		config.setForegroundFPS(60);
		config.useVsync(true);
		generateAssets();
		new Lwjgl3Application(new Drop(), config);
	}
	
	private static void generateAssets() {
		Settings settings = new Settings();
		settings.maxWidth = 4096;
		settings.maxHeight = 4096;
		settings.alias = true;
		TexturePacker.process(settings, "../assets", "../assets", "game");
	}
}


