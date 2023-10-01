package com.mygdx.drop.game;

import java.util.function.Supplier;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Constants.LayerId;
import com.mygdx.drop.Drop;

public abstract class Tile extends Entity {
	protected static Drop game;

	public final World world;
	public final int x_tl;
	public final int y_tl;

	protected final LayerId layerId;
	protected final TileId tileId;

	/**
	 * 
	 * @param world   A reference to the {@link World} object that will hold the tile
	 * @param layerId The layer in which the tile is to be rendered
	 * @param tileId  The tile's id
	 * @param x       Measured in tiles, origin is at the bottom left corner of the world
	 * @param y       Measured in tiles, origin is at the bottom left corner of the world
	 */
	public <T extends Tile> Tile(World world, LayerId layerId, TileId tileId, int x, int y, Class<T> subclass) {
		super(world, ((Supplier<BodyDef>) (() -> {
			BodyDef bodyDefiniton = new BodyDef();
			bodyDefiniton.type = BodyType.StaticBody;
			bodyDefiniton.position.set(Drop.tlToMt(x) - world.worldWidth_mt / 2 + Drop.tlToMt(1) / 2,
					Drop.tlToMt(y) - world.worldHeight_mt / 2 + Drop.tlToMt(1) / 2);
			bodyDefiniton.fixedRotation = true;
			return bodyDefiniton;
		})).get());
		// TODO: remove subclass parameter, it is debug only
		assert tileId != null : "tileId cannot be null!";
		if (Constants.DEBUG) {
			TileId correspondingTileId = debug_findCorrespondingId(subclass);
			assert correspondingTileId != null
					: "The " + subclass.getSimpleName() + " class doesnt have a corresponding entry in the Tile.TileId enum";
			assert correspondingTileId == tileId
					: "The provided tileId " + tileId.name() + " is not the correct id for class " + subclass.getSimpleName();
		}

		assert Drop.game != null : "Tile object created before game instance!";
		game = Drop.game;

		this.world = world;
		this.layerId = layerId;
		this.tileId = tileId;
		this.x_tl = x;
		this.y_tl = y;

		TiledMapTileLayer layer = (TiledMapTileLayer) world.tiledMap.getLayers().get(LayerId.WORLD.value);
		layer.setCell(x, y, new Cell().setTile(world.tiledMap.getTileSets().getTileSet(layerId.value).getTile(tileId.value)));

		FixtureDef fixtureDefinition = new FixtureDef();
		fixtureDefinition.filter.categoryBits = (short) (Constants.Category.WORLD.value | Constants.Category.PLAYER_COLLIDABLE.value);
		fixtureDefinition.filter.maskBits = (short) 0b1111111111111111;
		ChainShape chain = new ChainShape();

		float halfWidth = Drop.tlToMt(1) / 2;
		float halfHeight = halfWidth;
		chain.createLoop(new float[] { -halfWidth, -halfHeight, halfWidth, -halfHeight, halfWidth, halfHeight, -halfWidth, halfHeight });

		fixtureDefinition.shape = chain;
		self.createFixture(fixtureDefinition);
		chain.dispose();
	}

	/**
	 * Tile classes take the position as tiles from the bottom left corner of the world
	 *
	 * @see Entity.EntityDefinition
	 */
	public static abstract class TileDefinition<T extends Tile> extends Entity.EntityDefinition<T> {
		public TileDefinition(int x, int y) { super(x, y); }

	}

	/**
	 * Enforces the requirement for all subclasses of tile to have a matching entry in the
	 * {@link TileId} enum
	 * 
	 * @return Whether the requirement was met
	 */
	protected static final <T extends Tile> TileId debug_findCorrespondingId(Class<T> subclass) {
		assert Constants.DEBUG
				: "Debug only method " + Thread.currentThread().getStackTrace()[1].getMethodName() + " is called outside of debug build!";
		if (!Constants.DEBUG)
			throw new RuntimeException("Debug only method " + Thread.currentThread().getStackTrace()[1].getMethodName()
					+ " is called outside of debug build!");

		for (TileId id : TileId.values()) {
			if (subclass.getSimpleName().toUpperCase().equals(id.name().replaceAll("_", "")))
				return id;
		}
		return null;
	}

	/**
	 * The {@code TileId} is used to identify the tiles in a {@link TiledMapTileSet}. Each {@code Tile}
	 * subclass has a corresponding {@link TiledMapTile} object with within a given
	 * {@code TiledMapTileSet}, this object contains the tile's texture and is used by all instances of
	 * the subclass during rendering.
	 */
	public static enum TileId {
		// The constants' names must be the same as their corresponding class' name with added underscores
		// for word separation, and they must be in uppercase

		RAINBOW_TILE;

		public final int value;

		private TileId() { this.value = ordinal(); }

	}

}
