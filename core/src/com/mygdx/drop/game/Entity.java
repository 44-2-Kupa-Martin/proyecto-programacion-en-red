package com.mygdx.drop.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.Drop;
import com.mygdx.drop.etc.EventEmitter;
import com.mygdx.drop.etc.events.Event;
import com.mygdx.drop.etc.events.InputEvent;
import com.mygdx.drop.etc.events.handlers.EventListener;

/**
 * This class is a wrapper for box2d's {@link Body} class
 */
public abstract class Entity implements Disposable, EventEmitter {
	//TODO: either make all game references static or non-static. Ensure consistency
	// Static because game is a singleton
	protected static Drop game;

	public final World world;
	protected Body self;
	private Array<EventListener> eventHandlers;
	/** Tasks run on each {@link #update() call} */
	protected Array<Runnable> tasks;
	private Queue<Event> eventQueue;
	private boolean firing;
	/** Hidden to classes/subclasses outside this package */
	Lifetime objectState;

	/**
	 * @param world          The {@link World} that holds the entity.
	 * @param bodyDefinition The body definition used to create the body, all attributes must be in SIU
	 *                       units. 
	 */
	protected Entity(World world, BodyDef bodyDefinition) {
		assert Drop.game != null : "Entity created before game instance!";
		if (game == null)
			game = Drop.game;
		this.objectState = Lifetime.ALIVE;
		this.world = world;
		this.self = world.box2dWorld.createBody(bodyDefinition);
		this.eventHandlers = new Array<>();
		this.eventQueue = new Queue<>();
		this.tasks = new Array<>();
		this.firing = false;
		self.setUserData(this);
	}
	
	@Override
	public void dispose() {
		this.objectState = Lifetime.TO_BE_DISPOSED;
	}
	
	public final boolean isDisposed() {
		return objectState == Lifetime.DISPOSED;
	}

	/**
	 * Updates the entity's internal state.
	 * 
	 * @param viewport Needed for projecting/unprojecting
	 * @return {@code true} if {@link Entity#dispose()} is to be called, {@code false} otherwise
	 */
	public boolean update(Viewport viewport) {
		if (objectState == Lifetime.DISPOSED) 
			throw new IllegalStateException("Calling method of disposed object");
		return objectState == Lifetime.TO_BE_DISPOSED;
	}

	/** Returns the position of the entity's center of mass in meters (the same vector each time) */
	public final Vector2 getPosition() {
		return new Vector2(self.getWorldCenter());
	}

	/** Measured in meters */
	public final float getX() { return self.getWorldCenter().x; }

	/** Measured in meters */
	public final float getY() { return self.getWorldCenter().y; }
	
	/** Returns an Array containing the entity's fixtures. DO NOT modify the array */
	public final Array<Fixture> getFixtures() { return self.getFixtureList(); } //TODO: make an immutable wrapper for the array class
	
	public final void addTask(Runnable task) { tasks.add(task); }
	
	public final boolean removeTask(Runnable task) { return tasks.removeValue(task, true); }
	
	@Override
	public boolean removeListener(EventListener handler) { return eventHandlers.removeValue(handler, false); }
	
	@Override
	public void fire(Event event) {
		//TODO perhaps blocking all events from being fired when the entity has been disposed of would be a good idea
		eventQueue.addLast(event);
		if (firing) 
			return;
		
		firing = true;
		while (eventQueue.size != 0) {
			Event queuedEvent = eventQueue.removeFirst();
			for (int i = 0; i < eventHandlers.size; i++) {
				EventListener eventHandler = eventHandlers.get(i);
				eventHandler.handle(queuedEvent);
				if (queuedEvent.isStopped()) 
					break;
			}			
		}
		firing = false;
	}
	
	@Override
	public void addListener(EventListener handler) {
		assert handler != null;
		eventHandlers.add(handler); 
	}
	
	/**
	 * Tests if the given coordinates are within the entity
	 * @param worldX_mt
	 * @param worldY_mt
	 * @return {@code true} if the entity was hit, {@code false} otherwise
	 */
	public final boolean hit(float worldX_mt, float worldY_mt) {
		for (Fixture fixture : self.getFixtureList()) {
			if (fixture.testPoint(worldX_mt, worldY_mt))
				return true;
		}
		return false;
	}
	
	/**
	 * @param worldCoordinates An arbitrary point in the world. Measured in meters
	 * @return The same point relative to this entity's center of mass. Measured in meters
	 */
	public final Vector2 getRelativeCoordinates(Vector2 worldCoordinates) { return self.getLocalPoint(worldCoordinates); }
	
	enum Lifetime {
		ALIVE,
		TO_BE_DISPOSED,
		DISPOSED;
	}
	
	/**
	 * Defines the minimum requirements for constructing an entity. This class' purpose is to provide an
	 * api that accurately reflects the high level of coupling the {@link World} and {@link Entity}
	 * classes have. By making it possible to create an entity only through a world object, it aims to
	 * make clear that the {@code Entity} and {@code World} classes are "friendly."
	 * 
	 * @param <T> The entity defined by the specific subclass. This must be a parameter of the class
	 *            itself instead of the {@link EntityDefinition#createEntity(World)} method (see <a href=
	 *            "https://stackoverflow.com/questions/15095966/overriding-generic-abstract-methods-return-type-without-type-safety-warnings">reason</a>)
	 * 
	 */
	public static abstract class EntityDefinition<T extends Entity> {
		public float x;
		public float y;

		public EntityDefinition(float x, float y) {
			this.x = x;
			this.y = y;
		}
		
		/**
		 * Creates an entity in the specified world
		 * @param world The world where the entity will live
		 * @return The entity
		 */
		protected abstract T createEntity(World world);

	}

}
