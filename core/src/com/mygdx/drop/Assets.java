package com.mygdx.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureArray;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

// TODO: Use LibGDX's AssetManager
/**
 * Manages the loading of assets
 */
public class Assets implements Disposable {
	public final AtlasRegion dropImage;
	public final AtlasRegion bucketImage;
	public final Array<AtlasRegion> worldTileset;
	public final Array<AtlasRegion> playerWalkSheet;
	public final Array<AtlasRegion> playerIdleSheet;
	public final Sound dropSound;
	public final Music rainMusic;
	public final BitmapFont arial;

	public final TextureAtlas atlas;

	private static boolean loaded = false;

	private Assets() {
		this.atlas = new TextureAtlas(Gdx.files.internal("game.atlas"));
		this.dropImage = atlas.findRegion("GameScreen/textures/drop");
		assert dropImage != null : "Failed to load assets";

		this.bucketImage = atlas.findRegion("GameScreen/textures/bucket");
		assert bucketImage != null : "Failed to load assets";

		this.worldTileset = atlas.findRegions("GameScreen/textures/tilesets/worldTileset");
		assert this.worldTileset.size != 0 : "Failed to load assets";

		this.playerWalkSheet = atlas.findRegions("GameScreen/textures/playerWalkAnimation48x48");
		assert this.playerWalkSheet.size != 0 : "Failed to load assets";

		this.playerIdleSheet = atlas.findRegions("GameScreen/textures/playerIdleAnimation48x48");
		assert this.playerIdleSheet.size != 0 : "Failed to load assets";

		this.dropSound = Gdx.audio.newSound(Gdx.files.internal("GameScreen/sounds/drop.wav"));
		this.rainMusic = Gdx.audio.newMusic(Gdx.files.internal("GameScreen/music/rain.mp3"));
		this.arial = new BitmapFont();
	}

	public static final Assets load() {
		if (Constants.DEBUG) {
			assert !loaded : "Already loaded the assets";
			loaded = true;
		}
		return new Assets();
	}

	@Override
	public void dispose() {
		atlas.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		arial.dispose();
	}

}