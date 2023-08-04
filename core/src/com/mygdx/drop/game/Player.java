package com.mygdx.drop.game;

import java.util.EnumMap;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.drop.Constants;
import com.mygdx.drop.Drop;

public class Player extends Entity {
	protected final float density = 1 /* kg/m^2 */;
	private State previousState;
	private State currentState;
	private float animationTimer;
	private EnumMap<State, Animation<TextureRegion>> animations;

	/**
	 * The offset between the player's origin and its bottom left corner, which is needed by
	 * {@link Player#draw()}. Cached for performance.
	 */
	private Vector2 bottomLeftVertexOffset;

	/**
	 * 
	 * @param world A reference to the {@link World} object that will hold the player
	 * @param x     Center of mass's x component measured in meters
	 * @param y     Center of mass's y component measured in meters
	 */
	public Player(World world, float x, float y) {
		super(world, Drop.tlToMt(2), Drop.tlToMt(3), x, Drop.tlToMt(3) / 2 + y);
		this.previousState = State.IDLE;
		this.currentState = State.IDLE;
		this.animationTimer = 0;
		this.animations = initAnimationsMap();

		PolygonShape hitbox = new PolygonShape();
		hitbox.setAsBox(width_mt / 2, height_mt / 2);
		Fixture fixture = self.createFixture(hitbox, density);

		// Cache vertex for future use
		this.bottomLeftVertexOffset = new Vector2();
		hitbox.getVertex(0, bottomLeftVertexOffset);
		hitbox.dispose();
	}

	@Override
	public final boolean update(Camera camera) {
		previousState = currentState;
		currentState = State.IDLE;

		// Rationale: Although the Entity class has a width and a height field, they are simply for
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
			assert temp.x == this.width_mt && temp.y == this.height_mt : "Player's properties are desynced from those of its body's";
		}

		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			self.applyLinearImpulse(new Vector2(-1, 0), self.getWorldCenter(), true);
			currentState = State.WALKING;
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			self.applyLinearImpulse(new Vector2(1, 0), self.getWorldCenter(), true);
			currentState = State.WALKING;
		}
		if (Gdx.input.isKeyPressed(Keys.UP)) {
			self.applyLinearImpulse(new Vector2(0, 1), self.getWorldCenter(), true);
			currentState = State.WALKING;
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			self.applyLinearImpulse(new Vector2(0, -1), self.getWorldCenter(), true);
			currentState = State.WALKING;
		}

		if (currentState != previousState)
			animationTimer = 0;

		return false;
	}

	@Override
	public final void draw(Camera camera) {
		animationTimer += Gdx.graphics.getDeltaTime();
		Vector2 bottomLeftVertexWorldCoordinates = self.getWorldPoint(bottomLeftVertexOffset);
		Animation<TextureRegion> currentAnimation = animations.get(currentState);
		TextureRegion frame = currentAnimation.getKeyFrame(animationTimer);
		game.batch.draw(frame, bottomLeftVertexWorldCoordinates.x, bottomLeftVertexWorldCoordinates.y, width_mt, height_mt);
	}

	@Override
	public void dispose() {}

	private final EnumMap<State, Animation<TextureRegion>> initAnimationsMap() {
		EnumMap<State, Animation<TextureRegion>> animations = new EnumMap<>(State.class);
		animations.put(State.IDLE, new Animation<TextureRegion>(0.05f, game.assets.playerIdleSheet, PlayMode.LOOP));
		animations.put(State.WALKING, new Animation<TextureRegion>(0.05f, game.assets.playerWalkSheet, PlayMode.LOOP));
		return animations;
	}

	/**
	 * @param width Measured in meters
	 */
	public void setWidth(float width) {
		this.width_mt = width;
		reconstructFixture();
	}

	/**
	 * @param height Measured in meters
	 */
	public void setHeight(float height) {
		this.height_mt = height;
		reconstructFixture();
	}

	/**
	 * In order to modify the body's shape, a new fixture must be created.
	 */
	private void reconstructFixture() {
		Fixture oldFixture = self.getFixtureList().get(0);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width_mt / 2, height_mt / 2);
		Fixture newFixture = self.createFixture(shape, density);
		self.destroyFixture(oldFixture);
	}

	/**
	 * The state of the player
	 */
	enum State {
		IDLE, WALKING;
	}
	/**
	 * See {@link Entity.EntityDefinition}
	 */
	public static class Definition extends Entity.EntityDefinition<Player> {

		public Definition() { super(0, 0); }

		@Override
		protected Player createEntity(World world) {
			// TODO Auto-generated method stub
			return new Player(world, x, y);
		}

	}

}
