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
import com.mygdx.drop.game.Tile;
import com.mygdx.drop.game.World;

import java.util.function.BooleanSupplier;

public class Drop extends Game {
	public static Drop game;
	
	public Assets assets;
	public SpriteBatch batch;
	// debug is static because it is assumed that only one instance of game will exist during the application
	protected static Debug debug = Constants.DEBUG ? new Debug() : null;
	
	public Drop() {
		super();
		if (Constants.DEBUG) {
			assert !debug.gameConstructed : "Game is constructed multiple times!";
			debug.gameConstructed = true;			
		}
		// Sets up game reference for the lifetime of the application
		Drop.game = this;
	}
	
	// Utility functions for converting between units. Box2D uses the metric system and so does the game, regardless a notion of a tile is most useful.
	public static final float pxToMt(float pixels) { return pixels * Constants.PX_TO_MT_SCALAR; }
	public static final float pxToTile(float pixels) { return pixels * Constants.PX_TO_TILE_SCALAR; }
	public static final float mtToPx(float meters) { return meters * Constants.MT_TO_PX_SCALAR; }
	public static final float mtToTile(float meters) { return meters * Constants.MT_TO_TILE_SCALAR; }
	public static final float tileToPx(float tiles) { return tiles * Constants.TILE_TO_PX_SCALAR; }
	public static final float tileToMt(float tiles) { return tiles * Constants.TILE_TO_MT_SCALAR; }
	
	@Override
	public void create() {
		Box2D.init();
		
		Gdx.app.setLogLevel(Constants.LOG_LEVEL);
		this.assets = Assets.load();
		
		this.batch = new SpriteBatch();
		
		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		this.screen.dispose();
		assets.dispose();
		batch.dispose();
	}
	
	public static final class Debug extends com.mygdx.drop.Debug {
		// This flag prevents the creation of multiple game instances
		public boolean gameConstructed = false;

		private static boolean constructed = false;
		
		@Override
		protected boolean isConstructed() {
			return constructed;
		}

		@Override
		protected void setConstructed(boolean value) {
			constructed = value;
		}
	}
}
