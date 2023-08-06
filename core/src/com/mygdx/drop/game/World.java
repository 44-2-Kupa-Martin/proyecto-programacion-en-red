package com.mygdx.drop.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.game.Entity.EntityDefinition;
import com.mygdx.drop.Constants.Category;
import com.mygdx.drop.Constants.LayerId;

public class World implements Disposable {
	protected static Drop game;
	
	public final float worldWidth_mt;
	public final float worldHeight_mt;

	protected com.badlogic.gdx.physics.box2d.World box2dWorld;
	protected TiledMap tiledMap;
	protected final Array<Entity> entities;
	protected final Array<Tile> tiles;
	
	private final OrthogonalTiledMapRenderer mapRenderer;
	private final Debug debug = Constants.DEBUG ? new Debug() : null;

	/**
	 * Creates the world.
	 * 
	 * @param width   World width in tiles
	 * @param height  World height in tiles
	 * @param gravity Gravity vector in SIU units
	 */
	public World(int width, int height, Vector2 gravity) {
		assert Drop.game != null : "World created before game instance!";
		if (game == null)
			game = Drop.game;

		this.worldWidth_mt = Drop.tlToMt(width);
		this.worldHeight_mt = Drop.tlToMt(height);

		this.box2dWorld = new com.badlogic.gdx.physics.box2d.World(gravity, false);

		if (Constants.DEBUG) {
			debug.debugRenderer = new Box2DDebugRenderer();
			debug.bodies = new Array<Body>();
		}

		// tiledMap initialization
		this.tiledMap = new TiledMap();
		TiledMapTileSets tilesets = tiledMap.getTileSets();
		for (LayerId layerId : LayerId.values()) {
			// init layers
			TiledMapTileLayer layer = new TiledMapTileLayer(width, height, Constants.TL_TO_PX_SCALAR, Constants.TL_TO_PX_SCALAR);
			layer.setName(layerId.getName());
			tiledMap.getLayers().add(layer);

			// init tilesets
			TiledMapTileSet tileset = new TiledMapTileSet();
			Array<AtlasRegion> textures = game.assets.atlas.findRegions("GameScreen/textures/tilesets/" + layerId.getName() + "Tileset");
			assert textures.size != 0 : "Failed to load assets";
			for (int i = 0; i < textures.size; i++) {
				tileset.putTile(i, new StaticTiledMapTile(textures.get(i)));
			}
			tileset.setName(layerId.getName());
			tilesets.addTileSet(tileset);
		}

		this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, Constants.PX_TO_MT_SCALAR, game.batch);
		this.entities = new Array<Entity>();
		this.tiles = new Array<Tile>();

		initBox2dWorld(Drop.tlToMt(width), Drop.tlToMt(height));
	}

	public final void render(Camera camera) {
		mapRenderer.setView(camera.combined, -worldWidth_mt / 2, -worldHeight_mt / 2, worldWidth_mt, worldHeight_mt);
		mapRenderer.render();

		if (Constants.DEBUG) {
			debug.debugRenderer.render(box2dWorld, camera.combined);
		}

		// Checking whether all bodies have an owner
		if (Constants.DEBUG) {
			box2dWorld.getBodies(debug.bodies);
			for (Body body : debug.bodies) {
				assert body.getUserData() != null : "Body's userData doesnt reference an Entity instance (is null)";
				// Generally dynamic bodies are entities and static bodies belong to the world (e.g the walls that
				// keep the player in bounds)
				assert body.getUserData() instanceof Entity || body.getUserData() instanceof World || body.getUserData() instanceof Tile
						: "Body's userData references an unknown object";
			}
		}
	}

	public final void draw(Camera camera) {
		for (Entity entity : entities) {
			entity.draw(camera);
		}
	}

	public final void step() { box2dWorld.step(1 / 60f, 6, 2); }

	public final <T extends Tile, D extends Tile.TileDefinition<T>> T createTile(D tileDefinition) {
		T tile = tileDefinition.createTile(this);
		this.tiles.add(tile);
		return tile;
	}

	public final <T extends Entity, D extends Entity.EntityDefinition<T>> T createEntity(D entityDefinition) {
		T entity = entityDefinition.createEntity(this);
		this.entities.add(entity);
		return entity;
	}

	private final void initBox2dWorld(float width, float height) {
		int worldWidth_tl = (int) Drop.mtToTl(worldWidth_mt);
		int worldHeight_tl = (int) Drop.mtToTl(worldHeight_mt);

		// TODO: make wall's dimension proportional to world's dimensions
		float wallHalfWidth = Drop.tlToMt(500);
		float wallHalfHeight = Drop.tlToMt(500);
		PolygonShape rectangle = new PolygonShape();
		rectangle.setAsBox(wallHalfWidth, wallHalfHeight);

		FixtureDef wallFixture = new FixtureDef();
		wallFixture.shape = rectangle;
		wallFixture.density = Float.POSITIVE_INFINITY;
		wallFixture.friction = 0;
		wallFixture.restitution = 0;
		wallFixture.filter.maskBits = Constants.Category.PLAYER.value; // Only allow collisions with the player category
		wallFixture.filter.categoryBits = Constants.Category.PLAYER_COLLIDABLE.value; // The walls belong to the player collidable category

		BodyDef wallDefinition = new BodyDef();
		wallDefinition.type = BodyType.StaticBody;

		// Left wall
		wallDefinition.position.set(-width / 2 - wallHalfWidth, 0);
		Body leftWall = box2dWorld.createBody(wallDefinition);
		leftWall.createFixture(wallFixture);

		// Right wall
		wallDefinition.position.set(width / 2 + wallHalfWidth, 0);
		Body rightWall = box2dWorld.createBody(wallDefinition);
		rightWall.createFixture(wallFixture);

		// Ceiling
		wallDefinition.position.set(0, height / 2 + wallHalfHeight);
		Body ceiling = box2dWorld.createBody(wallDefinition);
		ceiling.createFixture(wallFixture);

		// Floor
		wallDefinition.position.set(0, -height / 2 - wallHalfHeight);
		Body floor = box2dWorld.createBody(wallDefinition);
		floor.createFixture(wallFixture);

		// Indicate these bodies belong to the game itself, i.e not an entity
		leftWall.setUserData(this);
		rightWall.setUserData(this);
		ceiling.setUserData(this);
		floor.setUserData(this);

		for (int i = worldWidth_tl / 2 - 10; i < worldWidth_tl / 2 + 10; i++) {
			for (int j = worldHeight_tl / 2 - 3; j < worldHeight_tl / 2; j++) {
				tiles.add(new RainbowTile(this, i, j));
			}
		}
	}

	@Override
	public void dispose() {
		box2dWorld.dispose();
		tiledMap.dispose();
	}

	public static final class Debug extends com.mygdx.drop.Debug {
		public Box2DDebugRenderer debugRenderer;
		// This array is used to check whether all Box2D bodies have an owner. All bodies must hold a
		// reference to their owner in their user data attribute
		public Array<Body> bodies;
		private static boolean constructed = false;

		@Override
		protected boolean isConstructed() { return constructed; }

		@Override
		protected void setConstructed(boolean value) { constructed = value; }

	}

}
