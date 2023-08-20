package com.mygdx.drop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Array.ArrayIterator;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Constants.LayerId;
import com.mygdx.drop.Drop;
import com.mygdx.drop.etc.Drawable;
import com.mygdx.drop.etc.EventListener;
import com.mygdx.drop.etc.events.ContactEvent;
import com.mygdx.drop.etc.events.InputEvent;
import com.mygdx.drop.etc.events.InputEvent.Type;
import com.mygdx.drop.etc.events.handlers.EventHandler;
import com.mygdx.drop.game.Entity.EntityDefinition;
import com.mygdx.drop.game.WorldBorder.Cardinality;
import com.mygdx.drop.game.dynamicentities.DroppedItem;
import com.mygdx.drop.game.tiles.RainbowTile;

/**
 * A wrapper for box2dworld. Handles the simulation and rendering of all {@link Entity entities} and {@link InputEvent input events}
 */
public class World implements Disposable, InputProcessor {
	protected static Drop game;
	
	public final float worldWidth_mt;
	public final int worldWidth_tl;
	public final float worldHeight_mt;
	public final int worldHeight_tl;

	protected com.badlogic.gdx.physics.box2d.World box2dWorld;
	protected TiledMap tiledMap;
	/** There is a 1 to 1 correspondence between bodies and entities */
	protected final Array<Body> bodies;
	/** In an entity object implements {@link Drawable} it is placed here automatically (see {@link #createEntity(EntityDefinition)} */
	protected final Array<Drawable> toBeDrawn;
	/** Entities here are destroyed the next {@link #step()} call */
	protected final Array<Entity> toBeDestroyed;
	private final Array<EventHandler<ContactEvent>> contactEventHandlers;
	
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
	 * @param width_tl   World width in tiles
	 * @param height_tl  World height in tiles
	 * @param gravity Gravity vector in SIU units
	 */
	public World(int width_tl, int height_tl, Vector2 gravity, Viewport viewport) {
		assert Drop.game != null : "World created before game instance!";
		if (game == null)
			game = Drop.game;
		
		this.viewport = viewport;
		this.worldWidth_tl = width_tl;
		this.worldHeight_tl = height_tl;
		this.worldWidth_mt = Drop.tlToMt(width_tl);
		this.worldHeight_mt = Drop.tlToMt(height_tl);

		this.box2dWorld = new com.badlogic.gdx.physics.box2d.World(gravity, false);
		
		// Pack and fire contact events
		// TODO: perform hit detection and fire the events on the actors directly
		box2dWorld.setContactListener(new ContactListener() {
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				ContactEvent event = new ContactEvent(ContactEvent.Type.preSolve);
				event.setContact(contact);
				event.setManifold(oldManifold);
				asContactEventListener().fire(event);
			}
			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				ContactEvent event = new ContactEvent(ContactEvent.Type.postSolve);
				event.setContact(contact);
				event.setContactImpulse(impulse);
				asContactEventListener().fire(event);
			}
			
			@Override
			public void endContact(Contact contact) {
				ContactEvent event = new ContactEvent(ContactEvent.Type.endContact);
				event.setContact(contact);
				asContactEventListener().fire(event);
			}
			
			@Override
			public void beginContact(Contact contact) {
				ContactEvent event = new ContactEvent(ContactEvent.Type.beginContact);
				event.setContact(contact);
				asContactEventListener().fire(event);
			}	
		});

		if (Constants.DEBUG) {
			debug.debugRenderer = new Box2DDebugRenderer();
		}

		//TODO: move this to a World.loadTilesets() method to allow for asynchronous loading of assets while the world initializes
		// tiledMap initialization
		this.tiledMap = new TiledMap();
		TiledMapTileSets tilesets = tiledMap.getTileSets();
		for (LayerId layerId : LayerId.values()) {
			// init layers
			TiledMapTileLayer layer = new TiledMapTileLayer(width_tl, height_tl, Constants.TL_TO_PX_SCALAR, Constants.TL_TO_PX_SCALAR);
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
		this.bodies = new Array<Body>();
		this.toBeDrawn = new Array<Drawable>();
		this.toBeDestroyed = new Array<Entity>();
		this.contactEventHandlers = new Array<EventHandler<ContactEvent>>();

		new WorldBorder(this, Cardinality.NORTH);
		new WorldBorder(this, Cardinality.SOUTH);
		new WorldBorder(this, Cardinality.EAST);
		new WorldBorder(this, Cardinality.WEST);
		RainbowTile.Definition tileDefiniton = new RainbowTile.Definition(0,0); 
		for (int i = worldWidth_tl / 2 - 10; i < worldWidth_tl / 2 + 10; i++) {
			for (int j = worldHeight_tl / 2 - 3; j < worldHeight_tl / 2; j++) {
				tileDefiniton.x = i;
				tileDefiniton.y = j;
				createEntity(tileDefiniton);
			}
		}		
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
			box2dWorld.getBodies(bodies);
			for (Body body : bodies) {
				assert body.getUserData() != null : "Body's userData doesnt reference an Entity instance (is null)";
				// Generally dynamic bodies are entities and static bodies belong to the world (e.g the walls that
				// keep the player in bounds)
				assert body.getUserData() instanceof Entity || body.getUserData() instanceof World || body.getUserData() instanceof Tile
						: "Body's userData references an unknown object";
			}
		}
		
		game.batch.begin();
		for (Drawable drawable : toBeDrawn) {
			drawable.draw(viewport);
		}
		game.batch.end();
		
		
	}

	public final void step() {
		for (ArrayIterator<Entity> iterator = toBeDestroyed.iterator(); iterator.hasNext();) {
			Entity entity = iterator.next();
			box2dWorld.destroyBody(entity.self);
			iterator.remove();
		}
		box2dWorld.getBodies(bodies);
		
		//TODO: determine best values for box2dworld.step()
		box2dWorld.step(1 / 60f, 6, 2);
		
		for (Body body : bodies) {
			Entity entity = (Entity) body.getUserData();
			entity.update(viewport);
		}
		
		// Update over entities. Done in step() because entities may change position, which can fire enter/exit without an input event.
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
	

	public final <T extends Entity, D extends Entity.EntityDefinition<T>> T createEntity(D entityDefinition) {
		T entity = entityDefinition.createEntity(this);
		if (entity instanceof Drawable) 
			toBeDrawn.add((Drawable)entity);
		// Update bodies array
		box2dWorld.getBodies(bodies);
		return entity;
	}
	
	public final void destroyEntity(Entity entity) {
		toBeDestroyed.add(entity);
		if (entity instanceof Drawable) 
			toBeDrawn.removeValue((Drawable)entity, false);
	}

	// EventListerners
	/**
	 * Allows for registering contact event handlers
	 */
	public EventListener<ContactEvent> asContactEventListener() {
		return new EventListener<ContactEvent>() {
			
			@Override
			public boolean removeHandler(EventHandler<ContactEvent> handler) { return contactEventHandlers.removeValue(handler, false); }
			
			@Override
			public boolean fire(ContactEvent event) {
				event.setTarget(World.this);
				for (EventHandler<ContactEvent> handler : contactEventHandlers) {
					if (handler.handle(event))
						break;
				}
				return event.isCancelled(); 
			}
			
			@Override
			public void addHandler(EventHandler<ContactEvent> handler) { contactEventHandlers.add(handler); }
			
		};
	}
	
	// Disposable
	@Override
	public void dispose() {
		box2dWorld.dispose();
		tiledMap.dispose();
	}

	// InputProcessor
	//TODO: implement key events 
	@Override
	public boolean keyDown(int keycode) { return false; }

	@Override
	public boolean keyUp(int keycode) { return false; }

	@Override
	public boolean keyTyped(char character) { return false; }

	/** Applies a touch down event to the world and returns true if an entity {@link Event#handle() handled} the
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
		event.setWorldX(worldCoordinates.x);
		event.setWorldY(worldCoordinates.y);
		event.setPointer(pointer);
		event.setButton(button);

		Entity target = null;
		for (Body body : bodies) {
			Entity entity = (Entity)body.getUserData();
			if (entity.hit(worldCoordinates.x, worldCoordinates.y)) {
				target = entity;
				break;
			}
		}
		
		if (target != null) {
			boolean cancelled = target.asInputEventListener().fire(event);
			Gdx.app.debug("", event.getType().toString() + " cancelled: " + cancelled);
		} else {
			if (game.heldItem != null) {
				//TODO: make a proper implementation of the drop mechanic
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
		event.setWorldX(worldCoordinates.x);
		event.setWorldY(worldCoordinates.y);
		event.setPointer(pointer);
		event.setButton(button);

		Entity target = null;
		for (Body body : bodies) {
			Entity entity = (Entity)body.getUserData();
			if (entity.hit(tempCoords.x, tempCoords.y)) {
				target = entity;
				break;
			}
		}
		
		if (target != null) {
			boolean cancelled = target.asInputEventListener().fire(event);
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
		for (Body body : bodies) {
			Entity entity = (Entity)body.getUserData();
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
			event.setWorldX(worldCoordinates.x);
			event.setWorldY(worldCoordinates.y);
			event.setPointer(pointer);
			event.setRelatedEntity(over);
			overLast.asInputEventListener().fire(event);
		}

		// Enter over.
		if (over != null) {
			InputEvent event = new InputEvent(this);
			event.setType(InputEvent.Type.enter);
			event.setWorldX(worldCoordinates.x);
			event.setWorldY(worldCoordinates.y);
			event.setPointer(pointer);
			event.setRelatedEntity(overLast);
			over.asInputEventListener().fire(event);
		}
		return over;
	}
	
	private void fireExit (Entity entity, int screenX, int screenY, int pointer) {
		Vector2 worldCoordinates = viewport.unproject(tempCoords.set(screenX, screenY));
		InputEvent event = new InputEvent(this);
		event.setType(InputEvent.Type.exit);
		event.setWorldX(worldCoordinates.x);
		event.setWorldY(worldCoordinates.y);
		event.setPointer(pointer);
		event.setRelatedEntity(entity);
		entity.asInputEventListener().fire(event);
	}
	
	// Classes
	public static final class Debug extends com.mygdx.drop.Debug {
		public Box2DDebugRenderer debugRenderer;
		// This array is used to check whether all Box2D bodies have an owner. All bodies must hold a
		// reference to their owner in their user data attribute
		private static boolean constructed = false;
		
		@Override
		protected boolean isConstructed() { return constructed; }
		
		@Override
		protected void setConstructed(boolean value) { constructed = value; }
		
	}
}
