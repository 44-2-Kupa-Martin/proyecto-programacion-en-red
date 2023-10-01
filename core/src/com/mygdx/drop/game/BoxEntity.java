package com.mygdx.drop.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.Constants;

/**
 * An entity with a four-vertex {@link PolygonShape} {@link Fixture}, i.e and entity whose hitbox
 * has the shape of a box. Note that the entity is not limited to one fixture, and is free to create
 * as many as it wants
 */
public abstract class BoxEntity extends Entity {
	private float width_mt;
	private float height_mt;

	/**
	 * The offset between the entity's origin and its bottom left corner, useful for drawing the entity
	 * as {@link SpriteBatch} doesn't draw from the center of the texture. Cached for performance.
	 */
	private Vector2 bottomLeftVertexOffset;

	/**
	 * 
	 * @param width_mt   Measured in meters
	 * @param height_mt  Measured in meters
	 * @param bodyDef    The body definition used to create the body, all attributes must be in SIU
	 *                   units.
	 * @param fixtureDef The fixture definition used to create the fixture, all attributes must be in
	 *                   SIU units. The {@link FixtureDef#shape} attribute will be overridden to a
	 *                   {@link PolygonShape} of an axis-aligned box with the specified dimensions.
	 */
	protected BoxEntity(World world, float width_mt, float height_mt, BodyDef bodyDef, FixtureDef fixtureDef) {
		super(world, bodyDef);
		this.width_mt = width_mt;
		this.height_mt = height_mt;

		PolygonShape hitbox = new PolygonShape();
		hitbox.setAsBox(width_mt / 2, height_mt / 2);
		fixtureDef.shape = hitbox;
		self.createFixture(fixtureDef);

		// Cache vertex for future use
		this.bottomLeftVertexOffset = new Vector2();
		hitbox.getVertex(0, bottomLeftVertexOffset);
		hitbox.dispose();
	}
	
	@Override
	public boolean update(Viewport viewport) {
		boolean toBeDisposed = super.update(viewport);
		
		// Rationale: Although the BoxEntity class has a width and a height field, they are simply for
		// convenience. What actually determines the width and height of the player is the shape associated
		// with the fixtures within the body of the player (i.e within the self field). This is a check to
		// ensure both values are in sync
		if (Constants.DEBUG) {
			assert self.getFixtureList().get(0).getShape().getType() == Type.Polygon : "Unexpected fixture type";
			// As you can see, retrieving the width and height of the body is tremendously inconvenient
			PolygonShape shapeData = (PolygonShape) self.getFixtureList().get(0).getShape();
			Vector2 temp = new Vector2();
			shapeData.getVertex(2, temp); // vertex 2 should correspond to the top-right corner of the rectangle
			temp.scl(2); // the origin is at the middle of the body, hence the distances must be scaled by two
			assert temp.x == getWidth() && temp.y == getHeight() : "Player's properties are desynced from those of its body's";
		}
		return toBeDisposed; 
	}
	
	/** The width measured in meters */
	public float getWidth() { return width_mt; }

	/** The height measured in meters */
	public float getHeight() { return height_mt; }

	/**
	 * The world coordinates of the hitbox's bottom left corner measured in meters. Same {@link Vector2}
	 * is returned every time
	 */
	protected Vector2 getDrawingCoordinates() { return self.getWorldPoint(bottomLeftVertexOffset); }

}
