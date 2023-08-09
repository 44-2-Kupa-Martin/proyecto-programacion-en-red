package com.mygdx.drop.game;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.function.Supplier;

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

public class Player extends BoxEntity {
	private State previousState;
	private State currentState;
	private float animationTimer;
	private EnumMap<State, Animation<TextureRegion>> animations;

	/**
	 * @param x Measured in meters
	 * @param y Measured in meters
	 */
	protected Player(World world, float x, float y) {
		super(world, Drop.tlToMt(2), Drop.tlToMt(3), ((Supplier<BodyDef>) (() -> {
			BodyDef body = new BodyDef();
			body.position.set(x, Drop.tlToMt(3) / 2 + y);
			body.fixedRotation = true;
			return body;
		})).get(), ((Supplier<FixtureDef>) (() -> {
			FixtureDef fixture = new FixtureDef();
			fixture.density = 1;
			fixture.filter.categoryBits = Constants.Category.PLAYER.value;
			return fixture;
		})).get());
		this.previousState = State.IDLE;
		this.currentState = State.IDLE;
		this.animationTimer = 0;
		this.animations = initAnimationsMap();
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
			assert temp.x == getWidth() && temp.y == getHeight() : "Player's properties are desynced from those of its body's";
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
		Vector2 coords = getDrawingCoordinates();
		Animation<TextureRegion> currentAnimation = animations.get(currentState);
		TextureRegion frame = currentAnimation.getKeyFrame(animationTimer);
		game.batch.draw(frame, coords.x, coords.y, getWidth(), getHeight());
	}

	@Override
	public void dispose() {}

	private final EnumMap<State, Animation<TextureRegion>> initAnimationsMap() {
		EnumMap<State, Animation<TextureRegion>> animations = new EnumMap<>(State.class);
		animations.put(State.IDLE, new Animation<TextureRegion>(0.05f, game.assets.get(com.mygdx.drop.Assets.Animation.Player_idle), PlayMode.LOOP));
		animations.put(State.WALKING, new Animation<TextureRegion>(0.05f, game.assets.get(com.mygdx.drop.Assets.Animation.Player_walk), PlayMode.LOOP));
		return animations;
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
		/**
		 * See {@link Player#Player(World, float, float)}
		 */
		public Definition(float x, float y) { super(x, y); }

		@Override
		protected Player createEntity(World world) {
			return new Player(world, x, y);
		}

	}

}
