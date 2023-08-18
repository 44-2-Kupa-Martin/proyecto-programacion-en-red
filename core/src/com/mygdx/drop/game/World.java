package com.mygdx.drop.game;

import java.beans.PropertyChangeSupport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.maps.MapLayer;
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
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.Stage.TouchFocus;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.game.Entity.EntityDefinition;
import com.mygdx.drop.Constants.Category;
import com.mygdx.drop.Constants.LayerId;
import com.mygdx.drop.etc.InputEvent;
import com.mygdx.drop.etc.InputEvent.Type;

public class World implements Disposable, InputProcessor {
	protected static Drop game;
	
	public final float worldWidth_mt;
	public final float worldHeight_mt;

	protected com.badlogic.gdx.physics.box2d.World box2dWorld;
	protected TiledMap tiledMap;
	protected final Array<Entity> entities;
	protected final Array<Tile> tiles;
	
	private final OrthogonalTiledMapRenderer mapRenderer;
	private final Debug debug = Constants.DEBUG ? new Debug() : null;
	private final Vector2 tempCoords = new Vector2();

	private final Viewport viewport;
	
	private final Entity[] pointerOverEntities = new Entity[20];
	private final boolean[] pointerTouched = new boolean[20];
	private final int[] pointerScreenX = new int[20], pointerScreenY = new int[20];
	private int mouseScreenX, mouseScreenY;
	private @Null Entity mouseOverEntity;
	

	/**
	 * Creates the world.
	 * 
	 * @param width   World width in tiles
	 * @param height  World height in tiles
	 * @param gravity Gravity vector in SIU units
	 */
	public World(int width, int height, Vector2 gravity, Viewport viewport) {
		assert Drop.game != null : "World created before game instance!";
		if (game == null)
			game = Drop.game;
		
		this.viewport = viewport;
		
		this.worldWidth_mt = Drop.tlToMt(width);
		this.worldHeight_mt = Drop.tlToMt(height);

		this.box2dWorld = new com.badlogic.gdx.physics.box2d.World(gravity, false);

		if (Constants.DEBUG) {
			debug.debugRenderer = new Box2DDebugRenderer();
			debug.bodies = new Array<Body>();
		}

		//TODO: move this to a World.loadTilesets() method to allow for asynchronous loading of assets while the world initializes
		
		// tiledMap initialization
		this.tiledMap = new TiledMap();
		TiledMapTileSets tilesets = tiledMap.getTileSets();
		for (LayerId layerId : LayerId.values()) {
			// init layers
			TiledMapTileLayer layer = new TiledMapTileLayer(width, height, Constants.TL_TO_PX_SCALAR, Constants.TL_TO_PX_SCALAR);
			layer.setName(layerId.name);
			tiledMap.getLayers().add(layer);

			// init tilesets
			TiledMapTileSet tileset = new TiledMapTileSet();
			Array<AtlasRegion> tilesetTextures = game.assets.get(layerId.tileset); 
			for (int i = 0; i < tilesetTextures.size; i++) {
				tileset.putTile(i, new StaticTiledMapTile(tilesetTextures.get(i)));
			}
			tileset.setName(layerId.name);
			tilesets.addTileSet(tileset);
		}

		this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, Constants.PX_TO_MT_SCALAR, game.batch);
		this.entities = new Array<Entity>();
		this.tiles = new Array<Tile>();

		initBox2dWorld(Drop.tlToMt(width), Drop.tlToMt(height));
	}

	public final void render(OrthographicCamera camera) {
		MapLayer layer = tiledMap.getLayers().get(Constants.LayerId.WORLD.value);
		float offsetX = Drop.mtToPx(camera.viewportWidth/2 - camera.position.x - worldWidth_mt/2);
		float offsetY = -Drop.mtToPx(camera.viewportHeight/2 - camera.position.y - worldHeight_mt/2);
		layer.setOffsetX(offsetX);
		layer.setOffsetY(offsetY);
		mapRenderer.setView(camera);
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
		game.batch.begin();
		for (Entity entity : entities) {
			entity.draw(camera);
		}
		game.batch.end();
		
		
	}

	public final void step() {
		box2dWorld.step(1 / 60f, 6, 2);
		// Update over actors. Done in act() because actors may change position, which can fire enter/exit without an input event.
		for (int pointer = 0, n = pointerOverEntities.length; pointer < n; pointer++) {
			Entity overLast = pointerOverEntities[pointer];
			if (pointerTouched[pointer]) {
				// Update the over actor for the pointer.
				pointerOverEntities[pointer] = fireEnterAndExit(overLast, pointerScreenX[pointer], pointerScreenY[pointer], pointer);
			} else if (overLast != null) {
				// The pointer is gone, exit the over actor for the pointer, if any.
				pointerOverEntities[pointer] = null;
				fireExit(overLast, pointerScreenX[pointer], pointerScreenY[pointer], pointer);
			}
		}
		mouseOverEntity = fireEnterAndExit(mouseOverEntity, mouseScreenX, mouseScreenY, -1);


		
	}

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

		float wallHalfWidth = width * 3/2;
		float wallHalfHeight = height * 3/2;
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

	// Interfaces
	
	// Disposable
	@Override
	public void dispose() {
		box2dWorld.dispose();
		tiledMap.dispose();
	}

	// InputProcessor
	@Override
	public boolean keyDown(int keycode) { return false; }

	@Override
	public boolean keyUp(int keycode) { return false; }

	@Override
	public boolean keyTyped(char character) { return false; }

	/** Applies a touch down event to the stage and returns true if an actor in the scene {@link Event#handle() handled} the
	 * event. */
	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
//		if (!isInsideViewport(screenX, screenY)) return false;
//
		pointerTouched[pointer] = true;
		pointerScreenX[pointer] = screenX;
		pointerScreenY[pointer] = screenY;

		
		Vector2 worldCoordinates = viewport.unproject(tempCoords.set(screenX, screenY));

		InputEvent event = new InputEvent(this);
		event.setType(Type.touchDown);
		event.world = this;
		event.setWorldX(worldCoordinates.x);
		event.setWorldY(worldCoordinates.y);
		event.setPointer(pointer);
		event.setButton(button);

		Entity target = null;
		for (Entity entity : entities) {
			if (entity.hit(worldCoordinates.x, worldCoordinates.y)) {
				target = entity;
				break;
			}
		}
		
		if (target != null) {
			boolean cancelled = target.fire(event);
			Gdx.app.debug("", event.getType().toString() + " cancelled: " + cancelled);
		} else {
			if (game.heldItem != null) {
				createEntity(new DroppedItem.Definition(0, 5, game.heldItem));
				game.heldItem = null;
			}
		}
			

		boolean handled = event.isHandled();
		return handled;
	}

	/** Applies a touch up event to the stage and returns true if an actor in the scene {@link Event#handle() handled} the event.
	 * Only {@link InputListener listeners} that returned true for touchDown will receive this event. */
	public boolean touchUp (int screenX, int screenY, int pointer, int button) {
		pointerTouched[pointer] = false;
		pointerScreenX[pointer] = screenX;
		pointerScreenY[pointer] = screenY;
//
//		if (touchFocuses.size == 0) return false;

		Vector2 worldCoordinates = viewport.unproject(tempCoords.set(screenX, screenY));

		InputEvent event = new InputEvent(this);
		event.setType(Type.touchUp);
		event.world = this;
		event.setWorldX(worldCoordinates.x);
		event.setWorldY(worldCoordinates.y);
		event.setPointer(pointer);
		event.setButton(button);

		Entity target = null;
		for (Entity entity : entities) {
			if (entity.hit(tempCoords.x, tempCoords.y)) {
				target = entity;
				break;
			}
		}
		
		if (target != null) {
			boolean cancelled = target.fire(event);
			Gdx.app.debug("", event.getType().toString() + " cancelled: " + cancelled);
		}

		boolean handled = event.isHandled();
		return handled;
	}

	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) { 
		pointerScreenX[pointer] = screenX;
		pointerScreenY[pointer] = screenY;
		mouseScreenX = screenX;
		mouseScreenY = screenY;
		return false; 
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) { 
		mouseScreenX = screenX;
		mouseScreenY = screenY;
		return false; 
	}

	@Override
	public boolean scrolled(float amountX, float amountY) { return false; }
	
	private Entity fireEnterAndExit (Entity overLast, int screenX, int screenY, int pointer) {
		// Find the actor under the point.
		Vector2 worldCoordinates = viewport.unproject(tempCoords.set(screenX, screenY));
		
		Entity over = null;
		for (Entity entity : entities) {
			if (entity.hit(worldCoordinates.x, worldCoordinates.y)) {
				over = entity;
				break;
			}
		}
		
		if (over == overLast) return overLast;

		// Exit overLast.
		if (overLast != null) {
			InputEvent event = new InputEvent(this);
			event.setType(InputEvent.Type.exit);
			event.world = (this);
			event.setWorldX(worldCoordinates.x);
			event.setWorldY(worldCoordinates.y);
			event.setPointer(pointer);
			event.setRelatedEntity(over);
			overLast.fire(event);
		}

		// Enter over.
		if (over != null) {
			InputEvent event = new InputEvent(this);
			event.setType(InputEvent.Type.enter);
			event.world = (this);
			event.setWorldX(worldCoordinates.x);
			event.setWorldY(worldCoordinates.y);
			event.setPointer(pointer);
			event.setRelatedEntity(overLast);
			over.fire(event);
		}
		return over;
	}
	
	private void fireExit (Entity entity, int screenX, int screenY, int pointer) {
		Vector2 worldCoordinates = viewport.unproject(tempCoords.set(screenX, screenY));
		InputEvent event = new InputEvent(this);
		event.setType(InputEvent.Type.exit);
		event.world = (this);
		event.setWorldX(worldCoordinates.x);
		event.setWorldY(worldCoordinates.y);
		event.setPointer(pointer);
		event.setRelatedEntity(entity);
		entity.fire(event);
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
