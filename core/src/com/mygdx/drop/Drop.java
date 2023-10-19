package com.mygdx.drop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.utils.Null;
import com.mygdx.drop.game.Item;
import com.mygdx.drop.game.World;
import com.mygdx.drop.game.dynamicentities.Player;
import com.mygdx.drop.game.items.GoofyItem;

/**
 * The entry point of the game (not of the application). As such, this class' public attributes act
 * as a sort of global variable. Given that this class is a singleton, {@link Drop#game} holds a
 * public static reference to itself so that all classes can access the "global context."
 */
public class Drop extends Game {
	public static Drop game;
	/** If a world exists, a reference will exist here. Is there a better way to do this? */
	public static @Null World world;
	private static boolean constructed = false;

	public SpriteBatch batch;
	public float masterVolume;
	public float zoom = 1.0f;

	/** Initialization done in {@link Drop#create()} */
	public Drop() {
		assert !constructed : "Game is constructed multiple times!";
		constructed = true;
		// Sets up game reference for the lifetime of the application
		Drop.game = this;
	}

	// Utility functions for converting between units. Box2D uses the metric system and so does the
	// game, regardless a notion of a tile is most useful. All variables in these units must have a
	// suffix indicating the unit (meters = _mt, pixels = _px, tiles = _tl)
	public static final float pxToMt(float pixels) { return pixels * Constants.PX_TO_MT_SCALAR; }

	public static final float pxToTl(float pixels) { return pixels * Constants.PX_TO_TL_SCALAR; }

	public static final int mtToPx(float meters) { return (int) (meters * Constants.MT_TO_PX_SCALAR); }

	public static final float mtToTl(float meters) { return meters * Constants.MT_TO_TL_SCALAR; }

	public static final int tlToPx(float tiles) { return (int) (tiles * Constants.TL_TO_PX_SCALAR); }

	public static final float tlToMt(float tiles) { return tiles * Constants.TL_TO_MT_SCALAR; }

	@Override
	public void create() {
		Box2D.init();
		Assets.load();
		Assets.finishLoading();
		
		Gdx.app.setLogLevel(Constants.LOG_LEVEL);

		this.batch = new SpriteBatch();
		this.masterVolume = 1.0f;
		this.zoom = 1.0f;
		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render() { super.render(); }

	@Override
	public void dispose() {
		super.screen.dispose();
		Assets.dispose();
		batch.dispose();
	}
}
