package com.mygdx.drop;

import java.util.Iterator;

import com.badlogic.gdx.Application;

@SuppressWarnings("unused")
public final class Constants {
	// Whether to include debug code. All debug code should be within an if statement checking for this flag (except logging, which is controlled by the LOG_LEVEL)
	public static final boolean DEBUG = true;
	// Determines what gets logged
	public static final int LOG_LEVEL = DEBUG ? Application.LOG_DEBUG : Application.LOG_NONE;
	// Proportionality constants
	public static final int MT_TO_PX_SCALAR = 24;
	public static final float MT_TO_TILE_SCALAR = 3/2f;
	public static final float TILE_TO_MT_SCALAR = 2/3f;
	public static final int TILE_TO_PX_SCALAR = 16;
	public static final float PX_TO_MT_SCALAR = 1/24f;
	public static final float PX_TO_TILE_SCALAR = 1/16f;
	// The width of the world in tiles
	public static final int WORLD_WIDTH_TILES = 200;
	// The height of the world in tiles
	public static final int WORLD_HEIGHT_TILES = 100;
	// The width of the world in meters
	public static final float WORLD_WIDTH_MT = Drop.tileToMt(WORLD_WIDTH_TILES);
	// The height of the world in meters
	public static final float WORLD_HEIGHT_MT = Drop.tileToMt(WORLD_HEIGHT_TILES);
	// Whether multiple threads will be used. When writing thread-unsafe code you must check whether this flag is on, and if so terminate execution
	public static final boolean MULTITHREADED = false;
	
	// The id of the world's tileset
	public static final int WORLD_TILESET_ID = 0;
	// The name of the world's tileset
	public static final String WORLD_TILESET_NAME = "world";
	
	// Used for collision filtering. See https://www.iforce2d.net/b2dtut/collision-filtering
	public static enum Category {
		// The category of all players 
		PLAYER,
		// The category of all bodies that collide with the player
		PLAYER_COLLIDABLE,
		// The category of all bodies that are part of the terrain
		WORLD;

		public final short value;
		
		private Category() {
			// The values are bit fields, hence they must be powers of two
			this.value = (short)Math.pow(2, (double)ordinal());
			assert ordinal() <= 16 : "The maximum amount of categories is 16";
		}
	}
	
	// Used for creating the layers in the tiledmap (see Drop.create), also defines the tilesets. Order matters.
	public static enum LayerId {
		WORLD("world");
		
		public final int value;
		private final String name;
		
		private LayerId(String name) {
			this.value = ordinal();
			this.name = name;
		}
		
		@Override
		public final String toString() {
			return name;
		}
		
		public final String getName() {
			return name;
		}
	}
	
	static {
		if (DEBUG) {
			assert 1 * MT_TO_PX_SCALAR * PX_TO_TILE_SCALAR * TILE_TO_MT_SCALAR == 1 : "Inconsistent proportionallity constants!";
			assert 1 * PX_TO_MT_SCALAR * MT_TO_TILE_SCALAR * TILE_TO_PX_SCALAR == 1 : "Inconsistent proportionallity constants!";
		}
	}
}
