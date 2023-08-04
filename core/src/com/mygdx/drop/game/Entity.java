package com.mygdx.drop.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;
import com.mygdx.drop.GameScreen;

/**
 * This class glues together the physics api provided by Box2D and the rendering api provided by
 * SpriteBatch into a unified api.
 */
public abstract class Entity implements Disposable {
	// Static because game is a singleton
	protected static Drop game;

	protected final Body self;
	protected float width_mt;
	protected float height_mt;

	/**
	 * 
	 * @param world  A reference to the {@link World} object that will hold the entity
	 * @param width  Measured in meters
	 * @param height Measured in meters
	 * @param x      Center of mass's x component measured in meters
	 * @param y      Center of mass's y component measured in meters
	 */
	protected Entity(World world, float width, float height, float x, float y) {
		assert Drop.game != null : "Entity created before game instance!";
		if (game == null)
			game = Drop.game;

		this.width_mt = width;
		this.height_mt = height;

		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.type = BodyType.DynamicBody;
		bodyDefinition.position.set(x, y);
		bodyDefinition.fixedRotation = true;

		this.self = world.box2dWorld.createBody(bodyDefinition);
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

	/** Returns the position of the player in meters (the same vector each time) */
	public final Vector2 getPosition() { return new Vector2(self.getWorldCenter()); }

	/** Measured in meters */
	public final float getX() { return self.getWorldCenter().x; }

	/** Measured in meters */
	public final float getY() { return self.getWorldCenter().y; }

	/** Measured in meters */
	public float getWidth() { return width_mt; }

	/** Measured in meters */
	public float getHeight() { return height_mt; }

	/**
	 * Defines the minimum requirements for constructing an entity. This class' purpose is to provide an
	 * api that accurately reflects the high level of coupling the {@link World} and {@code Entity}
	 * classes have. By making it possible to create an entity only through a world object, it aims to
	 * make clear that the {@code Entity} and {@code World} classes are "friendly."
	 * 
	 * @param <T> The entity the specific implementation defines. This must be a parameter of the class
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
