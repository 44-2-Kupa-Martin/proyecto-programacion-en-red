package com.mygdx.drop.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.Drop;
import com.mygdx.drop.etc.EventListener;
import com.mygdx.drop.etc.events.Event;
import com.mygdx.drop.etc.events.InputEvent;
import com.mygdx.drop.etc.events.handlers.EventHandler;

/**
 * This class is a wrapper for box2d's {@link Body} class
 */
public abstract class Entity implements Disposable, EventListener {
	//TODO: either make all game references static or non-static. Ensure consistency
	// Static because game is a singleton
	protected static Drop game;

	protected final World world;
	protected final Body self;
	private Array<EventHandler> eventHandlers;
	

	/**
	 * @param world          The {@link World} that holds the entity.
	 * @param bodyDefinition The body definition used to create the body, all attributes must be in SIU
	 *                       units. 
	 */
	protected Entity(World world, BodyDef bodyDefinition) {
		assert Drop.game != null : "Entity created before game instance!";
		if (game == null)
			game = Drop.game;
		this.world = world;
		this.self = world.box2dWorld.createBody(bodyDefinition);
		this.eventHandlers = new Array<>();
		self.setUserData(this);
	}
	
	@Override
	public void dispose() {
		world.destroyEntity(this);
	}

	/**
	 * Updates the entity's internal state.
	 * 
	 * @param viewport Needed for projecting/unprojecting
	 * @return {@code true} if {@link Entity#dispose()} is to be called, {@code false} otherwise
	 */
	public abstract boolean update(Viewport viewport);

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
	
	@Override
	public boolean removeHandler(EventHandler handler) { return eventHandlers.removeValue(handler, false); }
	
	@Override
	public boolean fire(Event event) {
		for (EventHandler eventHandler : eventHandlers) 
			eventHandler.handle(event);

		return event.isCancelled();
	}
	
	@Override
	public void addHandler(EventHandler handler) { eventHandlers.add(handler); }
	
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
