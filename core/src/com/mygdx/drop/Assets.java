package com.mygdx.drop;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

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
		for (Fonts font : Fonts.values) {
			// Null descriptors indicate that the asset is generated programmatically (i.e is unmanaged) and must be disposed manually.
			if (font.descriptor == null)
				continue;
			manager.load(font.descriptor);
		}

		for (Sounds sound : Sounds.values) {
			if (sound.descriptor == null)
				continue;
			manager.load(sound.descriptor);
		}

		for (Music music : Music.values) {
			if (music.descriptor == null)
				continue;
			manager.load(music.descriptor);
		}

		for (Skins skin : Skins.values) {
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

	public static final Asset<?> getById(int assetId) {
		AssetType type;
		Asset<?> asset;
		int typeIndex = (assetId & AssetType.TYPE_ID_MASK) >> AssetType.TYPE_ID_OFFSET;
		int assetIndex = assetId & ~AssetType.TYPE_ID_MASK;
		try {
			type = AssetType.values[typeIndex];
			asset = AssetType.assetValues.get(type)[assetIndex];
		} catch (IndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Invalid asset id, nonexistent asset type");
		}
		return asset;
	}
	
	public static final void dispose() {
		manager.dispose();
		for (Disposable disposable : manuallyDisposed) {
			disposable.dispose();
		}
	}
	
	public static enum AssetType {
		Fonts,
		Sounds,
		Music,
		Textures,
		Animations,
		Skins;
		
		
		public static final AssetType[] values = values();
		public static final int TYPE_ID_OFFSET = 3*Byte.SIZE;
		public static final int TYPE_ID_MASK = 0b11111111 << TYPE_ID_OFFSET;
		private static final EnumMap<AssetType, Asset<?>[]> assetValues = new EnumMap<>(AssetType.class);
		
		private AssetType() {
			assert ordinal() <= 0b11111111 : "Max number of asset types reached";
		}
		
		public static final <T extends Enum<T> & Asset<?>> byte registerNewAssetType(Class<T> assetClass) {
			AssetType type = valueOf(assetClass.getSimpleName()); 
			assetValues.put(type, assetClass.getEnumConstants());
			return (byte)type.ordinal();
		}
	}
	
	// Asset enums must be registered in the AssetType enum An empty string in the path indicates that the asset is generated programmatically and its corresponding field must be initialized manually
	public static enum Fonts implements Asset<BitmapFont> {
		arial(null);

		public static final Fonts[] values = values();
		private static final byte typeId = AssetType.registerNewAssetType(Fonts.class);
		private final AssetDescriptor<BitmapFont> descriptor;
		private BitmapFont font;

		private Fonts(String path) {
			assert ordinal() <= 0b00000000_11111111_11111111_11111111 : "Max number of assets reached";
			BitmapFontParameter params = new BitmapFontParameter();
			params.loadedCallback = (manager, fileName, type) -> {
				this.font = manager.get(fileName);
			};
			this.descriptor = path != null ? new AssetDescriptor<>("fonts/" + path, BitmapFont.class, params) : null;
		}

		@Override
		public AssetDescriptor<BitmapFont> getDescriptor() { return descriptor; }

		@Override
		public BitmapFont get() { return font; }

		@Override
		public int getId() { return ordinal() | typeId << AssetType.TYPE_ID_OFFSET; }
		
	}

	public enum Sounds implements Asset<Sound> {
		waterDrop("GameScreen/drop.wav"),
		playerHurt("Player/hurt.mp3");

		public static final Sounds[] values = values();
		private static final byte typeId = AssetType.registerNewAssetType(Sounds.class);
		private final AssetDescriptor<Sound> descriptor;
		private Sound sound;

		private Sounds(String path) {
			assert ordinal() <= 0b00000000_11111111_11111111_11111111 : "Max number of assets reached";
			SoundParameter params = new SoundParameter();
			params.loadedCallback = (manager, fileName, type) -> {
				this.sound = manager.get(fileName);
			};
			this.descriptor = path != null ? new AssetDescriptor<>("sounds/" + path, Sound.class, params) : null; 
		}

		@Override
		public AssetDescriptor<Sound> getDescriptor() { return descriptor; }

		@Override
		public Sound get() { return sound; }

		@Override
		public int getId() { return ordinal() | typeId << AssetType.TYPE_ID_OFFSET; }
		
	}

	public enum Music implements Asset<com.badlogic.gdx.audio.Music> {
		rain("GameScreen/rain.mp3");

		public static final Music[] values = values();
		private static final byte typeId = AssetType.registerNewAssetType(Music.class);
		private final AssetDescriptor<com.badlogic.gdx.audio.Music> descriptor;
		private com.badlogic.gdx.audio.Music music;

		private Music(String path) { 
			assert ordinal() <= 0b00000000_11111111_11111111_11111111 : "Max number of assets reached";
			MusicParameter params = new MusicParameter();
			params.loadedCallback = (manager, fileName, type) -> {
				this.music = manager.get(fileName);
			};
			this.descriptor = path != null ? new AssetDescriptor<>("music/" + path, com.badlogic.gdx.audio.Music.class, params) : null; 
		}

		@Override
		public AssetDescriptor<com.badlogic.gdx.audio.Music> getDescriptor() { return descriptor; }

		@Override
		public com.badlogic.gdx.audio.Music get() { return music; }

		@Override
		public int getId() { return ordinal() | typeId << AssetType.TYPE_ID_OFFSET; }
	}
	

	public enum Textures implements Asset<AtlasRegion> {
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
		
		public static final Textures[] values = values();
		private static final byte typeId = AssetType.registerNewAssetType(Textures.class);
		private final AssetDescriptor<AtlasRegion> descriptor;
		private AtlasRegion texture;

		private Textures(String path) { 
			assert ordinal() <= 0b00000000_11111111_11111111_11111111 : "Max number of assets reached";
			this.descriptor = path != null ? new AssetDescriptor<>("textures/" + path, AtlasRegion.class) : null; 
		}

		@Override
		public AssetDescriptor<AtlasRegion> getDescriptor() { return descriptor; }

		@Override
		public AtlasRegion get() { return texture; }

		@Override
		public int getId() { return ordinal() | typeId << AssetType.TYPE_ID_OFFSET; }

	}

	public enum Animations implements Asset<Array<AtlasRegion>> {
		playerWalking("Player/walk"),
		playerIdle("Player/idle"),
		menuBackground("Background/frame");

		public static final Animations[] values = values();
		private static final byte typeId = AssetType.registerNewAssetType(Animations.class);
		private final AssetDescriptor<Array<AtlasRegion>> descriptor;
		private Array<AtlasRegion> animation;

		private Animations(String path) { 
			assert ordinal() <= 0b00000000_11111111_11111111_11111111 : "Max number of assets reached";
			this.descriptor = path != null ? (AssetDescriptor<Array<AtlasRegion>>)((AssetDescriptor<?>)new AssetDescriptor<Array>("animations/" + path, Array.class)) : null;
		}

		@Override
		public AssetDescriptor<Array<AtlasRegion>> getDescriptor() { return descriptor; }

		@Override
		public Array<AtlasRegion> get() { return animation; }

		@Override
		public int getId() { return ordinal() | typeId << AssetType.TYPE_ID_OFFSET; }

	}

	public enum Skins implements Asset<Skin> {
		Global_default("Default/uiskin.json"),
		Glassy_glassy("Glassy/skin/glassy-ui.json");
		
		public static final Skins[] values = values();
		private static final byte typeId = AssetType.registerNewAssetType(Skins.class);
		private final AssetDescriptor<Skin> descriptor;
		private Skin skin;
		private Skins(String path) { 
			assert ordinal() <= 0b00000000_11111111_11111111_11111111 : "Max number of assets reached";
			SkinParameter params = new SkinParameter();
			params.loadedCallback = (manager, fileName, type) -> {
				this.skin = manager.get(fileName);
			};
			this.descriptor = path != null ? new AssetDescriptor<>("skins/" + path, Skin.class, params) : null;
		}
		@Override
		public AssetDescriptor<Skin> getDescriptor() { return descriptor; }
		@Override
		public Skin get() { return skin; }
		@Override
		public int getId() { return ordinal() | typeId << AssetType.TYPE_ID_OFFSET; }
	}
	
	public static interface Asset<AssetType> {
		public AssetDescriptor<AssetType> getDescriptor();
		public AssetType get();
		public int getId();
	}
}