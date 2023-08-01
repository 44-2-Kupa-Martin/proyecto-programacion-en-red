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
import com.mygdx.drop.game.Player.StateData.State;

public class Player extends Entity {
	protected final float density = 1 /* kg/m^2 */;
	private StateData.State previousState;
	private StateData currentStateData = new StateData(); // Simple data structure
	private EnumMap<StateData.State, Animation<TextureRegion>> animations;
	
	// The offset between the player's origin and its bottom left corner, which is needed by SpriteBatch.draw(). Cached for performance.
	private Vector2 bottomLeftVertexOffset;
	
	public Player() {
		super(Drop.tileToMt(2), Drop.tileToMt(3), 0, Drop.tileToMt(3)/2);
		this.previousState = State.IDLE;
		this.currentStateData.id = State.IDLE;
		this.currentStateData.animationTimer = 0;
		this.animations = initAnimationsMap();
		
		PolygonShape hitbox = new PolygonShape();
		hitbox.setAsBox(width/2, height/2);
		Fixture fixture = self.createFixture(hitbox, density);
		
		// Cache vertex for future use
		this.bottomLeftVertexOffset = new Vector2();
		hitbox.getVertex(0, bottomLeftVertexOffset);
		hitbox.dispose();
	}
	
	@Override
	public final boolean update(Camera camera) {
		previousState = currentStateData.id;
		currentStateData.id = State.IDLE;
		
		/*
		 * Rationale: Although the Entity class has a width and a height field, they are simply for convenience. What actually determines the width and height of the 
		 * player is the shape associated with the fixtures within the body of the player (i.e within the self field). This is a check to ensure both values are 
		 * in sync
		 */
		if (Constants.DEBUG) {
			assert self.getFixtureList().get(0).getShape().getType() == Type.Polygon : "Unexpected fixture type";
			// As you can see, retrieving the width and height of the body is tremendously inconvenient 
			PolygonShape shapeData = (PolygonShape) self.getFixtureList().get(0).getShape();
			Vector2 temp = new Vector2();
			shapeData.getVertex(2, temp); // vertex 2 should correspond to the top-right corner of the rectangle
			temp.scl(2); // the origin is at the middle of the body, hence the distances must be scaled by two
			assert temp.x == this.width && temp.y == this.height : "Player's properties are desynced from those of its body's";
		}
		
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			self.applyLinearImpulse(new Vector2(-1, 0), self.getWorldCenter(), true);
			currentStateData.id = State.WALKING;
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			self.applyLinearImpulse(new Vector2(1, 0), self.getWorldCenter(), true);
			currentStateData.id = State.WALKING;
		}
		if (Gdx.input.isKeyPressed(Keys.UP)) {
			self.applyLinearImpulse(new Vector2(0, 1), self.getWorldCenter(), true);
			currentStateData.id = State.WALKING;
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			self.applyLinearImpulse(new Vector2(0, -1), self.getWorldCenter(), true);
			currentStateData.id = State.WALKING;
		}
		
		if (currentStateData.id != previousState) {
			currentStateData.animationTimer = 0;
		}		
		return false;
	}

	public final void draw(Camera camera) {
		currentStateData.animationTimer += Gdx.graphics.getDeltaTime();
		Vector2 bottomLeftVertexWorldCoordinates = self.getWorldPoint(bottomLeftVertexOffset);
		Animation<TextureRegion> currentAnimation = animations.get(currentStateData.id);
		TextureRegion frame = currentAnimation.getKeyFrame(currentStateData.animationTimer);
		game.batch.draw(frame, bottomLeftVertexWorldCoordinates.x, bottomLeftVertexWorldCoordinates.y, width, height);
	}

	private final EnumMap<StateData.State, Animation<TextureRegion>> initAnimationsMap() {
		EnumMap<StateData.State, Animation<TextureRegion>> animations = new EnumMap<>(StateData.State.class);
		animations.put(State.IDLE, new Animation<TextureRegion>(0.05f, game.assets.playerIdleSheet, PlayMode.LOOP));
		animations.put(State.WALKING, new Animation<TextureRegion>(0.05f, game.assets.playerWalkSheet, PlayMode.LOOP));
		return animations;
	}
	
	// Simple data structure
	public static class StateData {
		public State id;
		public float animationTimer;
		
		enum State {
			IDLE,
			WALKING;
		}
	}

	public void setWidth(float width) {
		this.width = width;
		reconstructFixture();
	}

	public void setHeight(float height) {
		this.height = height;
		reconstructFixture();
	}
	
	private void reconstructFixture() {
		Fixture oldFixture = self.getFixtureList().get(0);
		PolygonShape shape = new PolygonShape(); 
		shape.setAsBox(width/2, height/2);
		Fixture newFixture = self.createFixture(shape, density);
		self.destroyFixture(oldFixture);
	}
}
