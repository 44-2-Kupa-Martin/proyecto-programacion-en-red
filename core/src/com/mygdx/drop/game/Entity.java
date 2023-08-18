package com.mygdx.drop.game;

import java.beans.PropertyChangeSupport;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.GameScreen;
import com.mygdx.drop.etc.Event;
import com.mygdx.drop.etc.EventListener;

/**
 * This class glues together the physics api provided by Box2D and the rendering api provided by
 * SpriteBatch into a unified api. All entities are dynamic bodies.
 */
public abstract class Entity implements Disposable {
	// Static because game is a singleton
	protected static Drop game;

	protected final World world;
	protected final Body self;
	private Array<EventListener> listeners;
	

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
		this.listeners = new Array<>();
		self.setUserData(this);
	}

	/**
	 * Updates the entity's internal state.
	 * 
	 * @param camera Needed for projecting/unprojecting
	 * @return A {@code boolean} that indicates if {@link Entity#dispose()} is to be called
	 */
	public abstract boolean update(Camera camera);

	/** Draws the entity to the screen */
	public abstract void draw(Camera camera);

	/** Returns the position of the entity's center of mass in meters (the same vector each time) */
	public final Vector2 getPosition() {
		return new Vector2(self.getWorldCenter());
	}

	/** Measured in meters */
	public final float getX() { return self.getWorldCenter().x; }

	/** Measured in meters */
	public final float getY() { return self.getWorldCenter().y; }
	
	public final Array<Fixture> getFixtures() { return self.getFixtureList(); }
	
	public boolean fire(Event event) {
		event.setTarget(this);
		event.listenerOwner = this;
		for (EventListener eventListener : listeners) {
			if (eventListener.handle(event))
				break;
		}
		event.listenerOwner = null;
		return event.isCancelled();
	}
	
	public final void addListener(EventListener listener) { listeners.add(listener); } 
	public final void removeListener(EventListener listener) { listeners.removeValue(listener, false); }
	
	public final boolean hit(float worldX, float worldY) {
		for (Fixture fixture : self.getFixtureList()) {
			if (fixture.testPoint(worldX, worldY))
				return true;
		}
		return false;
	}
	
	public final Vector2 getRelativeCoordinates(Vector2 worldCoordinates) { return self.getLocalPoint(worldCoordinates); }
	
	/**
	 * Defines the minimum requirements for constructing an entity. This class' purpose is to provide an
	 * api that accurately reflects the high level of coupling the {@link World} and {@link Entity}
	 * classes have. By making it possible to create an entity only through a world object, it aims to
	 * make clear that the {@code Entity} and {@code World} classes are "friendly."
	 * 
	 * @param <T> The entity defined by the specific subclass. This must be a parameter of the class
	 *            itself instead of the {@link EntityDefinition#createEntity()} method (see <a href=
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

		protected abstract T createEntity(World world);

	}

}
