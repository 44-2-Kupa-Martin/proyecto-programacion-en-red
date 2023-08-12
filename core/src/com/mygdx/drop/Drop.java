package com.mygdx.drop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mygdx.drop.Constants.LayerId;
import com.mygdx.drop.game.DebugItem;
import com.mygdx.drop.game.GoofyItem;
import com.mygdx.drop.game.Item;
import com.mygdx.drop.game.Tile;
import com.mygdx.drop.game.World;

import java.util.function.BooleanSupplier;

/**
 * The entry point of the game (not of the application). As such, this class' public attributes act
 * as a sort of global variable. Given that this class is a singleton, {@link Drop#game} holds a
 * public static reference to itself so that all classes can access the "global context."
 */
public class Drop extends Game {
	public static Drop game;

	public Assets assets;
	public SpriteBatch batch;
	public Item heldItem;
	private static boolean constructed = false;

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

		Gdx.app.setLogLevel(Constants.LOG_LEVEL);
		this.assets = Assets.load();
		//TODO: implement an event system so that classes that need certain assets can listen for when the resource is ready
		assets.finishLoading();

		this.batch = new SpriteBatch();
		
		this.heldItem = new GoofyItem();
		
		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render() { super.render(); }

	@Override
	public void dispose() {
		super.screen.dispose();
		assets.dispose();
		batch.dispose();
	}

}
