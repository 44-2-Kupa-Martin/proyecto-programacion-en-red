package com.mygdx.drop;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureArray;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Manages the loading of assets
 */
public class Assets implements Disposable {
	private final String ATLAS = "game.atlas";
	private final AssetManager manager = new AssetManager();

	// These maps are for assets which are not managed by the AssetManager
	private EnumMap<Font, BitmapFont> fonts = new EnumMap<>(Font.class);
	private EnumMap<Sound, com.badlogic.gdx.audio.Sound> sounds;
	private EnumMap<Music, com.badlogic.gdx.audio.Music> music;
	// The texture and animations are managed by a TextureAtlas
	private EnumMap<Texture, AtlasRegion> textures = new EnumMap<>(Texture.class);
	private EnumMap<Animation, Array<AtlasRegion>> animations = new EnumMap<>(Animation.class);
	private EnumMap<Tileset, Array<AtlasRegion>> tilesets = new EnumMap<>(Tileset.class);

	private static boolean loaded = false;

	private Assets() {
		// Put the assets into the load queue. Null paths indicate that the asset is generated
		// programmatically and must be mapped in the corresponding EnumMap and be disposed manually.
		for (Font font : Font.values()) {
			if (font.path == null)
				continue;
		}
		fonts.put(Font.MainMenuScreen_arial, new BitmapFont());

		for (Sound sound : Sound.values()) {
			if (sound.path == null)
				continue;
			manager.load(sound.path, com.badlogic.gdx.audio.Sound.class);
		}

		for (Music music : Music.values()) {
			if (music.path == null)
				continue;

			manager.load(music.path, com.badlogic.gdx.audio.Music.class);
		}

		manager.load(ATLAS, TextureAtlas.class);
		// Start loading
		manager.update();
	}

	public static final Assets load() {
		if (Constants.DEBUG) {
			assert !loaded : "Already loaded the assets";
			loaded = true;
		}
		return new Assets();
	}

	public final boolean update() { return manager.update(); }

	public final void finishLoading() {
		manager.finishLoading();

		for (Texture texture : Texture.values()) {
			if (texture.path == null)
				continue;
			AtlasRegion asset = manager.get(ATLAS, TextureAtlas.class).findRegion(texture.path);
			assert asset != null : "Failed to load texture!";
			textures.put(texture, asset);
		}

		for (Animation animation : Animation.values()) {
			if (animation.path == null)
				continue;
			Array<AtlasRegion> asset = manager.get(ATLAS, TextureAtlas.class).findRegions(animation.path);
			assert asset.size != 0 : "Failed to load animation!";
			animations.put(animation, asset);
		}

		for (Tileset tileset : Tileset.values()) {
			if (tileset.path == null)
				continue;
			Array<AtlasRegion> asset = manager.get(ATLAS, TextureAtlas.class).findRegions(tileset.path);
			assert asset.size != 0 : "Failed to load tileset!";
			tilesets.put(tileset, asset);
		}
	}

	public final BitmapFont get(Font identifier) {
		if (identifier.path == null)
			return fonts.get(identifier);
		return manager.get(identifier.path);
	}

	public final com.badlogic.gdx.audio.Sound get(Sound identifier) {
		if (identifier.path == null)
			return sounds.get(identifier);
		return manager.get(identifier.path);
	}

	public final com.badlogic.gdx.audio.Music get(Music identifier) {
		if (identifier.path == null)
			return music.get(identifier);
		return manager.get(identifier.path);
	}

	public final AtlasRegion get(Texture identifier) { return textures.get(identifier); }

	public final Array<AtlasRegion> get(Animation identifier) { return animations.get(identifier); }

	public final Array<AtlasRegion> get(Tileset identifier) { return tilesets.get(identifier); }

	@Override
	public void dispose() {
		manager.dispose();
		fonts.get(Font.MainMenuScreen_arial).dispose();
	}

	// Assets' names are prefixed with the name of the class that they belong. An empty string in the
	// path indicates that the asset is generated programmatically and must me mapped manually
	public enum Font {
		MainMenuScreen_arial("");

		public final String path;

		private Font(String path) { this.path = path == "" ? null : "fonts/" + path; }

	}

	public enum Sound {
		GameScreen_drop("GameScreen/drop.wav");

		public final String path;

		private Sound(String path) { this.path = path == "" ? null : "sounds/" + path; }

	}

	public enum Music {
		GameScreen_rain("GameScreen/rain.mp3");

		public final String path;

		private Music(String path) { this.path = path == "" ? null : "music/" + path; }

	}

	public enum Texture {
		DebugBox_bucket("DebugBox/bucket");

		public final String path;

		private Texture(String path) { this.path = path == "" ? null : "textures/" + path; }

	}

	public enum Animation {
		Player_walk("Player/walk"),
		Player_idle("Player/idle");

		public final String path;

		private Animation(String path) { this.path = path == "" ? null : "animations/" + path; }

	}

	public enum Tileset {
		World_world("World/world");

		public final String path;

		private Tileset(String path) { this.path = path == "" ? null : "tilesets/" + path; }

	}

}