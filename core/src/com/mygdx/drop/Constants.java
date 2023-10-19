package com.mygdx.drop;

import java.util.Iterator;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.mygdx.drop.game.World;

@SuppressWarnings("unused")
public final class Constants {
	/**
	 * Whether to include debug code. All debug code should be within an if statement checking for this
	 * flag (except logging, which is controlled by the LOG_LEVEL)
	 */
	public static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("debug"));

	/** Determines what gets logged */
	public static final int LOG_LEVEL = DEBUG ? Application.LOG_DEBUG : Application.LOG_NONE;

	// Proportionality constants
	public static final int MT_TO_PX_SCALAR = 24;
	public static final float MT_TO_TL_SCALAR = 3 / 2f;
	public static final float TL_TO_MT_SCALAR = 2 / 3f;
	public static final int TL_TO_PX_SCALAR = 16;
	public static final float PX_TO_MT_SCALAR = 1 / 24f;
	public static final float PX_TO_TL_SCALAR = 1 / 16f;

	/** The width of the world in tiles */
	public static final int WORLD_WIDTH_tl = 400;

	/** The height of the world in tiles */
	public static final int WORLD_HEIGHT_tl = 200;

	/** The width of the world in meters */
	public static final float WORLD_WIDTH_mt = Drop.tlToMt(WORLD_WIDTH_tl);

	/** The height of the world in meters */
	public static final float WORLD_HEIGHT_mt = Drop.tlToMt(WORLD_HEIGHT_tl);

	/** The width in tiles of the default (100% zoom) field of view of the player */
	public static final int DEFAULT_FOV_WIDTH_tl = 36;

	/** The height in tiles of the default (100% zoom) field of view of the player */
	public static final int DEFAULT_FOV_HEIGHT_tl = 18;

	/**
	 * Whether multiple threads will be used. Thread-unsafe code must check for this flag in an assert
	 * statement
	 */
	public static final boolean MULTITHREADED = false;

	/** Used for collision filtering. See https://www.iforce2d.net/b2dtut/collision-filtering */
	public static enum Category {
		/** The category of all players */
		PLAYER,
		/** The category of all bodies that collide with the player */
		PLAYER_COLLIDABLE,
		/** The category of all items */
		ITEM,
		/** The category of all projectiles */
		PROJECTILE,
		/** The category of all sensors */
		SENSOR,
		/** The category of all bodies that are part of the terrain */
		WORLD;

		public final short value;

		private Category() {
			// The values are bit fields, hence they must be powers of two
			this.value = (short) Math.pow(2, (double) ordinal());
			assert ordinal() < 16 : "The maximum amount of categories is 16";
		}

	}

	static {
		// These asserts ensures consistency between units
		assert 1 * MT_TO_PX_SCALAR * PX_TO_TL_SCALAR * TL_TO_MT_SCALAR == 1 : "Inconsistent proportionallity constants!";
		assert 1 * PX_TO_MT_SCALAR * MT_TO_TL_SCALAR * TL_TO_PX_SCALAR == 1 : "Inconsistent proportionallity constants!";
		assert 1 * TL_TO_PX_SCALAR * PX_TO_MT_SCALAR * MT_TO_TL_SCALAR == 1 : "Inconsistent proportionallity constants!";
	}

}
