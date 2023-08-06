package com.mygdx.drop.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.drop.Drop;

/**
 * An entity with a four-vertex {@link PolygonShape} {@link Fixture}, i.e and entity whose hitbox
 * has the shape of a box.
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
	 * @param width      Measured in meters
	 * @param height     Measured in meters
	 * @param bodyDef    The body definition used to create the body, all attributes must be in SIU
	 *                   units. The {@link BodyDef#type} attribute will be overridden to
	 *                   {@link BodyType#DynamicBody}.
	 * @param fixtureDef The fixture definition used to create the fixture, all attributes must be in
	 *                   SIU units. The {@link FixtureDef#shape} attribute will be overridden to a
	 *                   {@link PolygonShape} of an axis-aligned box with the specified dimensions.
	 */
	protected BoxEntity(World world, float width, float height, BodyDef bodyDef, FixtureDef fixtureDef) {
		super(world, bodyDef);
		this.width_mt = width;
		this.height_mt = height;

		PolygonShape hitbox = new PolygonShape();
		hitbox.setAsBox(width / 2, height / 2);
		fixtureDef.shape = hitbox;
		Fixture fixture = self.createFixture(fixtureDef);

		// Cache vertex for future use
		this.bottomLeftVertexOffset = new Vector2();
		hitbox.getVertex(0, bottomLeftVertexOffset);
		hitbox.dispose();
	}

	/** Measured in meters */
	public float getWidth() { return width_mt; }

	/** Measured in meters */
	public float getHeight() { return height_mt; }

	/**
	 * The world coordinates of the hitbox's bottom left corner measured in meters. Same {@link Vector2}
	 * is returned every time
	 */
	protected Vector2 getDrawingCoordinates() { return self.getWorldPoint(bottomLeftVertexOffset); }

}
