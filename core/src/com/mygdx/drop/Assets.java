package com.mygdx.drop;

import java.util.EnumMap;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.assets.loaders.MusicLoader.MusicParameter;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.assets.loaders.SoundLoader.SoundParameter;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader.TextureAtlasParameter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Manages the loading of assets
 */
public abstract class Assets {
	private static final String ATLAS = "game.atlas";
	/** TODO look into {@link AssetDescriptor} */
	private static final AssetManager manager = new AssetManager();
	private static final Array<Disposable> manuallyDisposed = new Array<>();

	private static boolean loaded = false;

	public static final void load() {
		if (Constants.DEBUG) {
			assert !loaded : "Already loaded the assets";
			loaded = true;
		}
		
		// Unmanaged assets
		Fonts.arial.font = new BitmapFont();
		manuallyDisposed.add(Fonts.arial.font);
		
		// Managed assets
		for (Fonts font : Fonts.values()) {
			// Null descriptors indicate that the asset is generated programmatically (i.e is unmanaged) and must be disposed manually.
			if (font.descriptor == null)
				continue;
			manager.load(font.descriptor);
		}

		for (Sounds sound : Sounds.values()) {
			if (sound.descriptor == null)
				continue;
			manager.load(sound.descriptor);
		}

		for (Music music : Music.values()) {
			if (music.descriptor == null)
				continue;
			manager.load(music.descriptor);
		}

		for (Skins skin : Skins.values()) {
			if (skin.descriptor == null) 
				continue;
			manager.load(skin.descriptor);
		}
		
		TextureAtlasParameter params = new TextureAtlasParameter();
		params.loadedCallback = (manager, fileName, type) -> {
			TextureAtlas atlas = manager.get(fileName);
			for (Textures texture : Textures.values()) 
				texture.texture = atlas.findRegion(texture.descriptor.fileName);
			
			for (Animations animation : Animations.values()) 
				animation.animation = atlas.findRegions(animation.descriptor.fileName);			
		};
		manager.load(ATLAS, TextureAtlas.class, params);
		
		// Start loading
		manager.update();
	}
	
	public static final boolean update() { return manager.update(); }

	public static final void finishLoading() {
		manager.finishLoading();
	}

	public static final void dispose() {
		manager.dispose();
		for (Disposable disposable : manuallyDisposed) {
			disposable.dispose();
		}
	}

	// Assets' names are prefixed with the name of the class that they belong. An empty string in the
	// path indicates that the asset is generated programmatically and must me mapped manually
	public static enum Fonts {
		arial(null);

		public final AssetDescriptor<BitmapFont> descriptor;
		private BitmapFont font;

		private Fonts(String path) {
			BitmapFontParameter params = new BitmapFontParameter();
			params.loadedCallback = (manager, fileName, type) -> {
				this.font = manager.get(fileName);
			};
			this.descriptor = path != null ? new AssetDescriptor<>("fonts/" + path, BitmapFont.class, params) : null;
		}
		
		public BitmapFont get() { return font; }
	}

	public enum Sounds {
		waterDrop("GameScreen/drop.wav"),
		playerHurt("Player/hurt.mp3");

		public final AssetDescriptor<Sound> descriptor;
		private Sound sound;

		private Sounds(String path) {
			SoundParameter params = new SoundParameter();
			params.loadedCallback = (manager, fileName, type) -> {
				this.sound = manager.get(fileName);
			};
			this.descriptor = path != null ? new AssetDescriptor<>("sounds/" + path, Sound.class, params) : null; 
		}
		
		public Sound get() { return sound; }
	}

	public enum Music {
		rain("GameScreen/rain.mp3");

		public final AssetDescriptor<com.badlogic.gdx.audio.Music> descriptor;
		private com.badlogic.gdx.audio.Music music;

		private Music(String path) { 
			MusicParameter params = new MusicParameter();
			params.loadedCallback = (manager, fileName, type) -> {
				this.music = manager.get(fileName);
			};
			this.descriptor = path != null ? new AssetDescriptor<>("music/" + path, com.badlogic.gdx.audio.Music.class, params) : null; 
		}
		public com.badlogic.gdx.audio.Music get() { return music; }
	}

	public enum Textures {
		DebugBox_bucket("DebugBox/bucket"),
		GoofyItem_goofy("GoofyItem/goofy"),
		BowItem_bow("BowItem/bow"),
		Arrow_arrow("Arrow/arrow"),
		Background_mainMenu("Background/mainMenu2"),
		DiamondSet_helmet("DiamondSet/helmet"),
		DiamondSet_chestplate("DiamondSet/chestplate"),
		DiamondSet_leggings("DiamondSet/leggins"),
		DiamondSet_boots("DiamondSet/boots"),
		rainbowTile("tiles/rainbowTile");

		
		public final AssetDescriptor<AtlasRegion> descriptor;
		private AtlasRegion texture;

		private Textures(String path) { 
			this.descriptor = path != null ? new AssetDescriptor<>("textures/" + path, AtlasRegion.class) : null; 
		}

		public AtlasRegion get() { return texture; }
	}

	public enum Animations {
		playerWalking("Player/walk"),
		playerIdle("Player/idle"),
		menuBackground("Background/frame");

		public final AssetDescriptor<Array<AtlasRegion>> descriptor;
		private Array<AtlasRegion> animation;

		private Animations(String path) { 
			this.descriptor = path != null ? (AssetDescriptor<Array<AtlasRegion>>)((AssetDescriptor<?>)new AssetDescriptor<Array>("animations/" + path, Array.class)) : null;
		}

		public Array<AtlasRegion> get() { return animation; }
	}

	public enum Skins {
		Global_default("Default/uiskin.json"),
		Glassy_glassy("Glassy/skin/glassy-ui.json");
		
		public final AssetDescriptor<Skin> descriptor;
		private Skin skin;
		private Skins(String path) { 
			SkinParameter params = new SkinParameter();
			params.loadedCallback = (manager, fileName, type) -> {
				this.skin = manager.get(fileName);
			};
			this.descriptor = path != null ? new AssetDescriptor<>("skins/" + path, Skin.class, params) : null; 
		}
		public Skin get() { return skin; }
	}
}