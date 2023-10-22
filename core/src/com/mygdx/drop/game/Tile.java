package com.mygdx.drop.game;

import java.util.function.Supplier;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.etc.Drawable;
import com.mygdx.drop.game.PlayerManager.FrameComponent;
import com.mygdx.drop.game.dynamicentities.DroppedItem;
import com.mygdx.drop.game.items.BowItem;

public abstract class Tile extends Entity implements Drawable {
	protected static Drop game;

	public final World world;
	public final int x_tl;
	public final int y_tl;
	public final int breakageLevel;
	private int fractureLevel;
	private final int textureId;
	private final Fixture chainFixture;
	private final Vector2 bottomLeftVertex;
	
	/**
	 * 
	 * @param world   A reference to the {@link World} object that will hold the tile
	 * @param layerId The layer in which the tile is to be rendered
	 * @param tileId  The tile's id
	 * @param x       Measured in tiles, origin is at the bottom left corner of the world
	 * @param y       Measured in tiles, origin is at the bottom left corner of the world
	 */
	public <T extends Tile> Tile(World world, int x, int y, int textureId) {
		super(world, ((Supplier<BodyDef>) (() -> {
			BodyDef bodyDefiniton = new BodyDef();
			bodyDefiniton.type = BodyType.StaticBody;
			bodyDefiniton.position.set(Drop.tlToMt(x) - world.worldWidth_mt / 2 + Drop.tlToMt(1) / 2,
					Drop.tlToMt(y) - world.worldHeight_mt / 2 + Drop.tlToMt(1) / 2);
			bodyDefiniton.fixedRotation = true;
			return bodyDefiniton;
		})).get());

		assert Drop.game != null : "Tile object created before game instance!";
		game = Drop.game;

		this.world = world;
		this.x_tl = x;
		this.y_tl = y;
		this.breakageLevel = 4;
		this.fractureLevel = 0;
		this.textureId = textureId;

		FixtureDef fixtureDefinition = new FixtureDef();
		fixtureDefinition.filter.categoryBits = (short) (Constants.Category.WORLD.value | Constants.Category.PLAYER_COLLIDABLE.value);
		fixtureDefinition.filter.maskBits = (short) 0b1111111111111111;
		ChainShape chain = new ChainShape();

		float halfWidth = Drop.tlToMt(1) / 2;
		float halfHeight = halfWidth;
		chain.createLoop(new float[] { -halfWidth, -halfHeight, halfWidth, -halfHeight, halfWidth, halfHeight, -halfWidth, halfHeight });
		this.bottomLeftVertex = new Vector2(-halfWidth, -halfHeight);

		fixtureDefinition.shape = chain;
		this.chainFixture = self.createFixture(fixtureDefinition);
		chain.dispose();
	}
	
	@Override
	public FrameComponent getFrameComponent() { 
		Vector2 coords = getDrawingCoordinates();
		return new FrameComponent(textureId, coords.x, coords.y, Drop.tlToMt(1), Drop.tlToMt(1)); 
	}
	
	@Override
	public boolean hit(float worldX_mt, float worldY_mt) { 
		ChainShape chainShape = (ChainShape) chainFixture.getShape();
		Vector2 vertex1 = new Vector2();
		Vector2 vertex2 = new Vector2();
		chainShape.getVertex(0, vertex1);
		chainShape.getVertex(2, vertex2);
		vertex1.add(self.getWorldCenter());
		vertex2.add(self.getWorldCenter());
		/** TODO refactor this war crime. This code relies on the assumption that the vertices are stored in the order they're passed to {@link ChainShape#createLoop} on {@link Tile#Tile} */ 
		assert vertex1.x != vertex2.x && vertex1.y != vertex2.y;
		assert vertex1.x < vertex2.x && vertex1.y < vertex2.y;
		boolean onYRange = worldY_mt >= vertex1.y && worldY_mt <= vertex2.y;
		boolean onXRange = worldX_mt >= vertex1.x && worldX_mt <= vertex2.x;
		return onXRange && onYRange;
	}
	
	public boolean fracture() {
		fractureLevel++;
		if (fractureLevel == breakageLevel) {
			Vector2 position = self.getWorldCenter();
			world.createEntity(new DroppedItem.Definition(position.x, position.y, new BowItem()));
			this.dispose();
			return true;
		}
		return false;
	}
	
	/**
	 * The world coordinates of the hitbox's bottom left corner measured in meters. Same {@link Vector2}
	 * is returned every time
	 */
	protected Vector2 getDrawingCoordinates() { return self.getWorldPoint(bottomLeftVertex); }

	/**
	 * Tile classes take the position as tiles from the bottom left corner of the world
	 *
	 * @see Entity.EntityDefinition
	 */
	public static abstract class TileDefinition<T extends Tile> extends Entity.EntityDefinition<T> {
		public TileDefinition(int x, int y) { super(x, y); }

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
