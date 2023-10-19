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
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Array.ArrayIterator;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.EventManager;
import com.mygdx.drop.etc.Drawable;
import com.mygdx.drop.etc.EventCapable;
import com.mygdx.drop.etc.events.ContactEvent;
import com.mygdx.drop.etc.events.Event;
import com.mygdx.drop.etc.events.InputEvent;
import com.mygdx.drop.etc.events.InputEvent.Type;
import com.mygdx.drop.etc.events.listeners.EventListener;
import com.mygdx.drop.game.Entity.EntityDefinition;
import com.mygdx.drop.game.Entity.Lifetime;
import com.mygdx.drop.game.WorldBorder.Cardinality;
import com.mygdx.drop.game.dynamicentities.Player;
import com.mygdx.drop.game.tiles.RainbowTile;

/**
 * A wrapper for box2dworld. Handles the simulation and rendering of all {@link Entity entities} and {@link InputEvent input events}
 */
public class World implements Disposable, InputProcessor, EventCapable {
	protected static Drop game;
	
	public final float worldWidth_mt;
	public final int worldWidth_tl;
	public final float worldHeight_mt;
	public final int worldHeight_tl;
	/** A quick hack for the singleplayer build. TODO when implementing multiplayer, this should be either removed or refectored */
	public final Player player;

	protected com.badlogic.gdx.physics.box2d.World box2dWorld;
	/** There is a 1 to 1 correspondence between bodies and entities. TODO consider whether it would be better to have an array on entities */
	protected final Array<Body> bodies;
	/** If an entity object implements {@link Drawable} it is placed here automatically (see {@link #createEntity(EntityDefinition)} */
	protected final Array<Drawable> toBeDrawn;
	/** Entities here are destroyed the next {@link #step()} call TODO this should be a queue not an array */
	protected final Array<Entity> toBeDestroyed;
	private final Array<EventListener> eventHandlers;
	private final Queue<Event> eventQueue;
	private boolean firing;
	
	private final Debug debug = Constants.DEBUG ? new Debug() : null;
	private final Vector2 tempCoords = new Vector2();

	private final Viewport viewport;
	
	private final Entity[] pointerOverEntities = new Entity[20];
	private final boolean[] pointerTouched = new boolean[20];
	/** reference counted, each pointer increases the reference */
	private final short[] buttonPressed = new short[5];
	private final float[] pointerWorldX = new float[20], pointerWorldY = new float[20];
	private float mouseWorldX, mouseWorldY;
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
		assert Drop.world == null : "Multiple worlds created at the same time!";
		
		Drop.world = this;
		
		if (game == null)
			game = Drop.game;
		
		
		this.viewport = viewport;
		this.worldWidth_tl = width_tl;
		this.worldHeight_tl = height_tl;
		this.worldWidth_mt = Drop.tlToMt(width_tl);
		this.worldHeight_mt = Drop.tlToMt(height_tl);

		this.box2dWorld = new com.badlogic.gdx.physics.box2d.World(gravity, false);
		
		// Pack and fire contact events. Note: it seems that box2d calls this listener from multiple threads, hence the need for ContactEvents to be semi-immutable
		box2dWorld.setContactListener(new ContactListener() {
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				ContactEvent event = new ContactEvent(World.this, contact, ContactEvent.Type.preSolve, oldManifold);
				EventManager.fire(event);
			}
			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				ContactEvent event = new ContactEvent(World.this, contact, ContactEvent.Type.postSolve, impulse);
				EventManager.fire(event);
			}
			
			@Override
			public void beginContact(Contact contact) {
				ContactEvent event = new ContactEvent(World.this, contact, ContactEvent.Type.beginContact);
				EventManager.fire(event);
			}
			
			@Override
			public void endContact(Contact contact) {
				ContactEvent event = new ContactEvent(World.this, contact, ContactEvent.Type.endContact);
				EventManager.fire(event);
			}
		});

		if (Constants.DEBUG) {
			debug.debugRenderer = new Box2DDebugRenderer();
		}

		this.bodies = new Array<Body>();
		this.toBeDrawn = new Array<Drawable>();
		this.toBeDestroyed = new Array<Entity>();
		this.eventHandlers = new Array<EventListener>();
		this.eventQueue = new Queue<Event>();
		this.firing = false;

		new WorldBorder(this, Cardinality.NORTH);
		new WorldBorder(this, Cardinality.SOUTH);
		new WorldBorder(this, Cardinality.EAST);
		new WorldBorder(this, Cardinality.WEST);
		RainbowTile.Definition tileDefiniton = new RainbowTile.Definition(0,0); 
		for (int i = worldWidth_tl / 2 - 15; i < worldWidth_tl / 2 + 15; i++) {
			for (int j = worldHeight_tl / 2 - 3; j < worldHeight_tl / 2; j++) {
				tileDefiniton.x = i;
				tileDefiniton.y = j;
				createEntity(tileDefiniton);
			}
		}
		this.player = createEntity(new Player.Definition(0,3));
	}

	public final void render(OrthographicCamera camera) {
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
			entity.objectState = Lifetime.DISPOSED;
			entity.self = null;
			iterator.remove();
		}
		
		box2dWorld.getBodies(bodies);
		//TODO find a better fix to the problem of creating bodies while iterating over getBodies
		Array<Body> bodies = new Array<Body>(this.bodies);
		//TODO: determine best values for box2dworld.step()
		box2dWorld.step(1 / 60f, 6, 2);
		for (Body body : bodies) {			
			Entity entity = (Entity) body.getUserData();
			boolean dispose = entity.update(viewport);
			if (dispose) 
				destroyEntity(entity);
		}
		
		// Update over entities. Done in step() because entities may change position, which can fire enter/exit without an input event.
		for (int pointer = 0, n = pointerOverEntities.length; pointer < n; pointer++) {
			Entity overLast = pointerOverEntities[pointer];
			if (pointerTouched[pointer]) {
				// Update the over actor for the pointer.
				pointerOverEntities[pointer] = fireEnterAndExit(overLast, pointerWorldX[pointer], pointerWorldY[pointer], pointer);
			} else if (overLast != null) {
				// The pointer is gone, exit the over actor for the pointer, if any.
				pointerOverEntities[pointer] = null;
				fireExit(overLast, pointerWorldX[pointer], pointerWorldY[pointer], pointer);
			}
		}
		mouseOverEntity = fireEnterAndExit(mouseOverEntity, mouseWorldX, mouseWorldY, -1);
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
			toBeDrawn.removeValue((Drawable)entity, true);
	}

	public final Vector2 getLastClickPosition() {
		return tempCoords.set(pointerWorldX[0], pointerWorldY[0]);
	}
	
	public final boolean isButtonPressed(int button) {
		return buttonPressed[button] > 0;
	}
	
	public final Entity hit(float x, float y) {
		Entity hit = null;
		box2dWorld.getBodies(bodies);
		for (Body body : bodies) {
			Entity entity = (Entity)body.getUserData();
			if (entity.hit(x, y)) {
				hit = entity;
				break;
			}
		}
		return hit;
	}
	
	// EventCapable
	
	@Override
	public void addListener(EventListener handler) { eventHandlers.add(handler); }

	@Override
	public boolean removeListener(EventListener handler) { return eventHandlers.removeValue(handler, false); }
	
	@Override
	public Array<EventListener> getListeners() { return eventHandlers; }
	
	// Disposable
	@Override
	public void dispose() {
		box2dWorld.dispose();
	}

	// InputProcessor
	//TODO: implement key events 
	@Override
	public boolean keyDown(int keycode) {
		if (player.isDisposed()) 
			return false;
		InputEvent inputEvent = new InputEvent(this, this, player);
		inputEvent.setType(Type.keyDown);
		inputEvent.setKeyCode(keycode);
		EventManager.fire(inputEvent);
		return inputEvent.isHandled(); 
	}

	@Override
	public boolean keyUp(int keycode) { 
		if (player.isDisposed()) 
			return false;
		InputEvent inputEvent = new InputEvent(this, this, player);
		inputEvent.setType(Type.keyUp);
		inputEvent.setKeyCode(keycode);
		EventManager.fire(inputEvent);
		return inputEvent.isHandled(); 
	}

	@Override
	public boolean keyTyped(char character) { return false; }

	/** Applies a touch down event to the world and returns true if an entity {@link Event#handle() handled} the
	 * event. */
	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		//TODO maybe implement?
//		if (!isInsideViewport(screenX, screenY)) return false;
//
		pointerTouched[pointer] = true;
		buttonPressed[button]++;
		Vector2 worldCoordinates = viewport.unproject(tempCoords.set(screenX, screenY));
		pointerWorldX[pointer] = worldCoordinates.x;
		pointerWorldY[pointer] = worldCoordinates.y;
	
		if (player.isDisposed()) 
			return false;
		
		/** If no entity is hit, the event will be fired on the world */
		EventCapable target = this;
		for (Body body : bodies) {
			Entity entity = (Entity)body.getUserData();
			if (entity.hit(worldCoordinates.x, worldCoordinates.y)) {
				target = entity;
				break;
			}
		}
		
		InputEvent event = new InputEvent(target, this, player);
		event.setType(Type.touchDown);
		event.setWorldX(worldCoordinates.x);
		event.setWorldY(worldCoordinates.y);
		event.setPointer(pointer);
		event.setButton(button);
		EventManager.fire(event);
		
		return event.isHandled();
	}

	/** Applies a touch up event to the stage and returns true if an actor in the scene {@link Event#handle() handled} the event.
	 * Only {@link InputListener listeners} that returned true for touchDown will receive this event. */
	public boolean touchUp (int screenX, int screenY, int pointer, int button) {
		pointerTouched[pointer] = false;
		buttonPressed[button]--;
		Vector2 worldCoordinates = viewport.unproject(tempCoords.set(screenX, screenY));
		pointerWorldX[pointer] = worldCoordinates.x;
		pointerWorldY[pointer] = worldCoordinates.y;

		if (player.isDisposed()) 
			return false;
		
		
		/** If no entity is hit, the event will be fired on the world */
		EventCapable target = this;
		for (Body body : bodies) {
			Entity entity = (Entity)body.getUserData();
			if (entity.hit(tempCoords.x, tempCoords.y)) {
				target = entity;
				break;
			}
		}

		InputEvent event = new InputEvent(target, this, player);
		event.setType(Type.touchUp);
		event.setWorldX(worldCoordinates.x);
		event.setWorldY(worldCoordinates.y);
		event.setPointer(pointer);
		event.setButton(button);
		EventManager.fire(event);

		return event.isHandled();
	}

	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) { 
		Vector2 worldPosition = viewport.unproject(tempCoords.set(screenX, screenY));
		pointerWorldX[pointer] = worldPosition.x;
		pointerWorldY[pointer] = worldPosition.y;
		mouseWorldX = worldPosition.x;
		mouseWorldY = worldPosition.y;
		if (player.isDisposed()) 
			return false;
		//TODO fire inputevent
		return false; 
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		Vector2 worldPosition = viewport.unproject(tempCoords.set(screenX, screenY));
		mouseWorldX = worldPosition.x;
		mouseWorldY = worldPosition.y;
		if (player.isDisposed()) 
			return false;
		//TODO fire inputevent
		return false; 
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		//TODO fire inputevent		
		return false; 
	}
	
	private Entity fireEnterAndExit (Entity overLast, float worldX, float worldY, int pointer) {
		//TODO think this through
		if (player.isDisposed()) 
			return null;
		// Find the actor under the point.		
		Entity over = null;
		for (Body body : bodies) {
			Entity entity = (Entity)body.getUserData();
			if (entity.hit(worldX, worldY)) {
				over = entity;
				break;
			}
		}
		
		if (over == overLast) return overLast;

		// Exit overLast.
		if (overLast != null) {
			InputEvent event = new InputEvent(overLast, this, player);
			event.setType(InputEvent.Type.exit);
			event.setWorldX(worldX);
			event.setWorldY(worldY);
			event.setPointer(pointer);
			event.setRelatedEntity(over);
			EventManager.fire(event);
		}

		// Enter over.
		if (over != null) {
			InputEvent event = new InputEvent(over, this, player);
			event.setType(InputEvent.Type.enter);
			event.setWorldX(worldX);
			event.setWorldY(worldY);
			event.setPointer(pointer);
			event.setRelatedEntity(overLast);
			EventManager.fire(event);
		}
		return over;
	}
	
	private void fireExit (Entity entity, float worldX, float worldY, int pointer) {
		if (player.isDisposed()) 
			return;
		InputEvent event = new InputEvent(entity, this, player);
		event.setType(InputEvent.Type.exit);
		event.setWorldX(worldX);
		event.setWorldY(worldY);
		event.setPointer(pointer);
		event.setRelatedEntity(null);
		EventManager.fire(event);
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
