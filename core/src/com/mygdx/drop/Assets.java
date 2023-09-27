package com.mygdx.drop;

import java.util.EnumMap;

import com.badlogic.gdx.assets.AssetManager;
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
	private EnumMap<FontId, BitmapFont> fonts = new EnumMap<>(FontId.class);
	private EnumMap<SoundId, com.badlogic.gdx.audio.Sound> sounds;
	private EnumMap<MusicId, com.badlogic.gdx.audio.Music> music;
	private EnumMap<SkinId, com.badlogic.gdx.scenes.scene2d.ui.Skin> skins;
	// The texture and animations are managed by a TextureAtlas
	private EnumMap<TextureId, AtlasRegion> textures = new EnumMap<>(TextureId.class);
	private EnumMap<AnimationId, Array<AtlasRegion>> animations = new EnumMap<>(AnimationId.class);
	private EnumMap<TilesetId, Array<AtlasRegion>> tilesets = new EnumMap<>(TilesetId.class);

	private static boolean loaded = false;

	private Assets() {
		// Put the assets into the load queue. Null paths indicate that the asset is generated
		// programmatically and must be mapped in the corresponding EnumMap and be disposed manually.
		for (FontId font : FontId.values()) {
			if (font.path == null)
				continue;
		}
		fonts.put(FontId.MainMenuScreen_arial, new BitmapFont());

		for (SoundId sound : SoundId.values()) {
			if (sound.path == null)
				continue;
			manager.load(sound.path, com.badlogic.gdx.audio.Sound.class);
		}

		for (MusicId music : MusicId.values()) {
			if (music.path == null)
				continue;

			manager.load(music.path, com.badlogic.gdx.audio.Music.class);
		}

		for (SkinId skin : SkinId.values()) {
			if (skin.path == null) 
				continue;
			
			manager.load(skin.path, com.badlogic.gdx.scenes.scene2d.ui.Skin.class);
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

		for (TextureId texture : TextureId.values()) {
			if (texture.path == null)
				continue;
			AtlasRegion asset = manager.get(ATLAS, TextureAtlas.class).findRegion(texture.path);
			assert asset != null : "Failed to load texture!";
			textures.put(texture, asset);
		}

		for (AnimationId animation : AnimationId.values()) {
			if (animation.path == null)
				continue;
			Array<AtlasRegion> asset = manager.get(ATLAS, TextureAtlas.class).findRegions(animation.path);
			assert asset.size != 0 : "Failed to load animation!";
			animations.put(animation, asset);
		}

		for (TilesetId tileset : TilesetId.values()) {
			if (tileset.path == null)
				continue;
			Array<AtlasRegion> asset = manager.get(ATLAS, TextureAtlas.class).findRegions(tileset.path);
			assert asset.size != 0 : "Failed to load tileset!";
			tilesets.put(tileset, asset);
		}
	}

	public final BitmapFont get(FontId identifier) {
		if (identifier.path == null)
			return fonts.get(identifier);
		return manager.get(identifier.path);
	}

	public final com.badlogic.gdx.audio.Sound get(SoundId identifier) {
		if (identifier.path == null)
			return sounds.get(identifier);
		return manager.get(identifier.path);
	}

	public final com.badlogic.gdx.audio.Music get(MusicId identifier) {
		if (identifier.path == null)
			return music.get(identifier);
		return manager.get(identifier.path);
	}
	public final com.badlogic.gdx.scenes.scene2d.ui.Skin get(SkinId identifier) {
		if (identifier.path == null)
			return skins.get(identifier);
		return manager.get(identifier.path);
	}

	public final AtlasRegion get(TextureId identifier) { return textures.get(identifier); }

	public final Array<AtlasRegion> get(AnimationId identifier) { return animations.get(identifier); }

	public final Array<AtlasRegion> get(TilesetId identifier) { return tilesets.get(identifier); }

	@Override
	public void dispose() {
		manager.dispose();
		fonts.get(FontId.MainMenuScreen_arial).dispose();
	}

	// Assets' names are prefixed with the name of the class that they belong. An empty string in the
	// path indicates that the asset is generated programmatically and must me mapped manually
	public enum FontId {
		MainMenuScreen_arial("");

		public final String path;

		private FontId(String path) { this.path = path == "" ? null : "fonts/" + path; }

	}

	public enum SoundId {
		GameScreen_drop("GameScreen/drop.wav"),
		Player_hurt("Player/hurt.mp3");

		public final String path;

		private SoundId(String path) { this.path = path == "" ? null : "sounds/" + path; }

	}

	public enum MusicId {
		GameScreen_rain("GameScreen/rain.mp3");

		public final String path;

		private MusicId(String path) { this.path = path == "" ? null : "music/" + path; }

	}

	public enum TextureId {
		DebugBox_bucket("DebugBox/bucket"),
		GoofyItem_goofy("GoofyItem/goofy"),
		BowItem_bow("BowItem/bow"),
		Arrow_arrow("Arrow/arrow");
		public final String path;

		private TextureId(String path) { this.path = path == "" ? null : "textures/" + path; }

	}

	public enum AnimationId {
		Player_walk("Player/walk"),
		Player_idle("Player/idle");

		public final String path;

		private AnimationId(String path) { this.path = path == "" ? null : "animations/" + path; }

	}

	public enum TilesetId {
		World_world("World/world");

		public final String path;

		private TilesetId(String path) { this.path = path == "" ? null : "tilesets/" + path; }

	}

	public enum SkinId {
		Global_default("Default/uiskin.json"),
		Glassy_glassy("Glassy/skin/glassy-ui.json");
		
		public final String path;
		private SkinId(String path) { this.path = "skins/" + path; }
	}
}